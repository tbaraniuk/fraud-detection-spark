package com.fraud.preprocessing

import com.fraud.config.MLConfig
import com.fraud.domain.CredentialsScheme
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StandardScaler, StringIndexer, VectorAssembler}
import org.apache.spark.sql.{Dataset, Encoder}
import org.apache.spark.sql.functions.{lit, when}

object FeatureEngineering {
  def createPipeline(mlConfig: MLConfig): Pipeline = {
    val assembler = new VectorAssembler()
      .setInputCols(mlConfig.numeric.toArray)
      .setOutputCol("features_raw")
      .setHandleInvalid("skip")

    val scaler = new StandardScaler()
      .setInputCol("features_raw")
      .setOutputCol("features")
      .setWithStd(true)
      .setWithMean(true)

    new Pipeline().setStages(Array(assembler, scaler))
  }

  def addClassWeights[T <: Product : Encoder](df: Dataset[T]): Dataset[T] = {
    val fraudCount = df.filter("Class = 1").count()
    val legitCount = df.filter("Class = 0").count()

    val balancingRatio = legitCount.toDouble / fraudCount.toDouble

    import df.sparkSession.implicits._

    df.withColumn(
      "classWeight",
      when(df("Class") === 1, lit(balancingRatio)).otherwise(lit(1.0))
    ).as[T]
  }
}
