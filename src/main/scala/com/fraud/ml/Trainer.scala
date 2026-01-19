package com.fraud.ml

import com.fraud.config.HyperparametersConfig
import com.fraud.domain.CredentialsScheme
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.{LogisticRegression, LogisticRegressionModel}
import org.apache.spark.sql.DataFrame

object Trainer {
  def train(df: DataFrame, hyperparametersConfig: HyperparametersConfig): LogisticRegressionModel = {
    val lr = new LogisticRegression()
      .setLabelCol("Class")
      .setFeaturesCol("features")
      .setWeightCol("classWeight")
      .setMaxIter(hyperparametersConfig.maxIter)
      .setRegParam(hyperparametersConfig.regParam)
      .setElasticNetParam(hyperparametersConfig.elasticNetParam)
      .setThreshold(hyperparametersConfig.threshold)
      .setStandardization(true)

    lr.fit(df)
  }
}
