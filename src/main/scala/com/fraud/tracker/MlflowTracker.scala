package com.fraud.tracker

import com.fraud.config.MlflowConfig
import org.apache.spark.ml.classification.LogisticRegressionModel
import org.apache.spark.sql.{Dataset, Encoder}
import org.mlflow.api.proto.Service.{CreateRun, RunStatus}
import org.mlflow.tracking.MlflowClient

object MlflowTracker {
  private var client: Option[MlflowClient] = None
  private var runId: Option[String] = None

  def initialize(config: MlflowConfig, experimentName: String): Unit = {
    client = Some(new MlflowClient(config.trackingUri))

    val optExperiment = client.get.getExperimentByName(experimentName)

    val experimentId = if (optExperiment.isPresent) {
      val exp = optExperiment.get()
      println(s"Using existing experiment: ${exp.getName}")
      exp.getExperimentId
    } else {
      println(s"Creating new experiment: $experimentName")
      client.get.createExperiment(experimentName)
    }

    val createRunRequest = CreateRun.newBuilder()
      .setExperimentId(experimentId)
      .build()

    val run = client.get.createRun(createRunRequest)
    runId = Some(run.getRunId)
    println(s"Started Mlflow run: ${run.getRunId}")
  }

  def logParam(key: String, value: Any): Unit = {
    runId.foreach{ id =>
        client.foreach(_.logParam(id, key, value.toString))
    }
  }

  def logParams(params: Map[String, Any]): Unit = {
    params.foreach{ case (key, value) => logParam(key, value) }
  }

  def logMetric(key: String, value: Double, step: Long = 0): Unit = {
    runId.foreach{ id =>
      client.foreach(_.logMetric(id, key, value, System.currentTimeMillis(), step))
    }
  }

  def logMetrics(metrics: Map[String, Double]): Unit = {
    metrics.foreach{ case (key, value) => logMetric(key, value) }
  }

  def logArtifact(localPath: String): Unit = {
    runId.foreach{ id =>
      client.foreach(_.logArtifact(id, new java.io.File(localPath)))
    }
  }

  def logModel(model: Any, modelPath: String): Unit = {
    runId.foreach{ id =>
      val modelType = model.getClass.getSimpleName
      logParam("model_type", modelType)

      model match {
        case lr: LogisticRegressionModel =>
          logParam("max_iter", lr.getMaxIter)
          logParam("reg_param", lr.getRegParam)
          logParam("elastic_net_param", lr.getElasticNetParam)
          logParam("threshold", lr.getThreshold)
        case _ =>
          println(s"Unknown model type: $modelType")
      }

//      logArtifact(modelPath)
    }
  }

  def logDatasetInfo[T <: Product : Encoder](df: Dataset[T], name: String): Unit = {
    logParam(s"${name}_count", df.count())
    logParam(s"${name}_num_features", df.columns.length)

    if (df.columns.contains("Class")) {
      val classDict = df.groupBy("Class").count().collect()
      classDict.foreach { row =>
        val classLabel = row.getAs[Int](0)
        val classCount = row.getAs[Long](1)
        logParam(s"${name}_class_${classLabel}_count", classCount)
      }
    }
  }

  def setTag(key: String, value: String): Unit = {
    runId.foreach{ id =>
      client.foreach(_.setTag(id, key, value))
    }
  }

  def endRun(status: RunStatus = RunStatus.FINISHED): Unit = {
    runId.foreach { id =>
      client.foreach(_.setTerminated(id, status))
      println(s"Ended Mlflow run: $id with status: $status")
    }
    runId = None
  }
}
