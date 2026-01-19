package com.fraud.session

import com.fraud.config.SparkConfig
import org.apache.spark.sql.SparkSession

object SparkSessionBuilder {
  def build(sparkConfig: SparkConfig) = {
    SparkSession
      .builder
      .appName(sparkConfig.appName)
      .master(sparkConfig.master)
      .getOrCreate()
  }
}
