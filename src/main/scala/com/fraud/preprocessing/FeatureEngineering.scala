package com.fraud.preprocessing

import com.fraud.config.MLConfig
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.feature.{StandardScaler, VectorAssembler}
import org.apache.spark.sql.{Dataset, Encoder}
import org.apache.spark.sql.functions.{lit, when}

import scala.language.postfixOps

object FeatureEngineering {
  def createPipeline(mlConfig: MLConfig): Pipeline = {
    val numeric_assembler = new VectorAssembler()
      .setInputCols(mlConfig.numericFeatures.toArray)
      .setOutputCol("numeric_features")
      .setHandleInvalid("skip")

    val scaler = new StandardScaler()
      .setInputCol("numeric_features")
      .setOutputCol("numeric_features_scaled")
      .setWithStd(true)
      .setWithMean(true)

    val assembler = new VectorAssembler()
      .setInputCols(("numeric_features_scaled" +: mlConfig.pcaFeatures).toArray)
      .setOutputCol("features")
      .setHandleInvalid("skip")

    new Pipeline().setStages(Array(numeric_assembler, scaler, assembler))
  }

  def addClassWeights[T <: Product : Encoder](df: Dataset[T]): Dataset[T] = {
    val fraudCount = df.filter("Class = 1").count()
    val legitCount = df.filter("Class = 0").count()

    val balancingRatio = legitCount.toDouble / fraudCount.toDouble

    df.withColumn(
      "classWeight",
      when(df("Class") === 1, lit(balancingRatio)).otherwise(lit(1.0))
    ).as[T]
  }

  def iqrFiltering[T <: Product : Encoder](df: Dataset[T], colNames: List[String]): Dataset[T] = {
    val probabilities = Array(0.25, 0.75)
    val relativeError = 0.01

    val combinedFilter = colNames.map { colName =>
      val quantiles = df.stat.approxQuantile(colName, probabilities, relativeError)

      val q1 = quantiles(0)
      val q3 = quantiles(1)
      val iqr = q3 - q1

      val lowerBound = q1 - 3 * iqr
      val upperBound = q3 + 3 * iqr

      (df(colName) >= lowerBound) && (df(colName) <= upperBound)
    }.reduce(_.and(_))

    df.filter(combinedFilter).as[T]
  }
}
