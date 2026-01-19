package com.fraud.ml

import com.fraud.domain.CredentialsScheme
import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.classification.LogisticRegression
import org.apache.spark.sql.DataFrame

object Trainer {
  def train(df: DataFrame): PipelineModel = {
    val lr = new LogisticRegression()
      .setLabelCol("Class")
      .setFeaturesCol("features")
      .setWeightCol("classWeight")

    val pipeline = new Pipeline()
      .setStages(Array(lr))

    pipeline.fit(df)
  }
}
