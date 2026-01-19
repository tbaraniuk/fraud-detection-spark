package com.fraud.utils

import org.apache.spark.sql.{Dataset, Encoder}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.{col, percent_rank, rand}

object StratifiedSplitter {
  def split[T <: Product : Encoder](df: Dataset[T], colName: String, trainRatio: Double): (Dataset[T], Dataset[T]) = {
    val w = Window.partitionBy(colName).orderBy(rand(seed = 1000L))

    val ranked = df.withColumn("rank", percent_rank().over(w))

    val train = ranked.filter(col("rank") < trainRatio).drop("rank").as[T]
    val test = ranked.filter(col("rank") >= trainRatio).drop("rank").as[T]

    (train, test)
  }
}
