package com.fraud.app

import com.fraud.config.AppConfig
import com.fraud.domain.{CredentialsScheme, RawCredentialsScheme}
import com.fraud.ingestion.DataLoader
import com.fraud.ml.{Evaluator, Trainer}
import com.fraud.preprocessing.FeatureEngineering
import com.fraud.session.SparkSessionBuilder
import com.fraud.tracker.MlflowTracker
import com.fraud.utils.StratifiedSplitter
import org.apache.spark.sql.Dataset
import org.apache.spark.sql.functions.lit
import org.mlflow.api.proto.Service.RunStatus

object Main {
  def main(args: Array[String]): Unit = {
    val config = AppConfig.load()

    MlflowTracker.initialize(
      config.mlflow,
      experimentName = "fraud-detection"
    )

    MlflowTracker.setTag("environment", "development")

    val spark = SparkSessionBuilder.build(config.spark)
    import spark.implicits._
    println("Spark Session Initialized!")

    val rawData = DataLoader.load[RawCredentialsScheme](spark, config.input)
    MlflowTracker.logDatasetInfo[RawCredentialsScheme](rawData, "raw_data")
    println("The data was loaded!")

    val processedData: Dataset[CredentialsScheme] = rawData
      .dropDuplicates()
      .withColumn("classWeight", lit(null).cast("double"))
      .as[CredentialsScheme]

    val (trainingData, testData) = StratifiedSplitter.split(processedData, "Class", 0.8)

    val pipeline = FeatureEngineering.createPipeline(config.ml)
    val featureModel = pipeline.fit(trainingData)

    val trainingWithWeights = FeatureEngineering.addClassWeights(trainingData)
    val trainProcessed = featureModel.transform(trainingWithWeights)

    val testProcessed = featureModel.transform(testData)

    val model = Trainer.train(trainProcessed, config.ml.hyperparameters)

    val metrics = Evaluator.evaluate(model, testProcessed)
    MlflowTracker.logMetrics(metrics)

    model.write.overwrite().save(config.output.modelPath)
    MlflowTracker.logModel(model, config.output.modelPath)
    println(s"Model saved to ${config.output.modelPath}")

    MlflowTracker.endRun(RunStatus.FINISHED)

    spark.stop()
  }
}