package com.fraud.ingestion

import com.fraud.config.DataSourceConfig
import org.apache.spark.sql.{Dataset, Encoder, SparkSession}

object DataLoader {
  def load[T <: Product : Encoder](spark: SparkSession, config: DataSourceConfig): Dataset[T] = spark.read
      .format(config.format)
      .options(config.options)
      .option("inferSchema", "true")
      .load(config.path)
      .as[T]
}
