package com.fraud.ml

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.sql.DataFrame

object Evaluator {
  def evaluate(model: PipelineModel, testData: DataFrame): Map[String, Double] = {
    val predictions = model.transform(testData)

    val binaryMetrics = evaluateBinaryMetrics(predictions)

    binaryMetrics
  }

  private def evaluateBinaryMetrics(predictions: DataFrame): Map[String, Double] = {
    val binaryEvaluator = new BinaryClassificationEvaluator()
      .setLabelCol("Class")
      .setRawPredictionCol("rawPrediction")

    val areaUnderROC = binaryEvaluator
      .setMetricName("areaUnderROC")
      .evaluate(predictions)

    val areaUnderPR = binaryEvaluator
      .setMetricName("areaUnderPR")
      .evaluate(predictions)

    Map(
      "auc_roc" -> areaUnderROC,
      "acu_pr" -> areaUnderPR
    )
  }
}
