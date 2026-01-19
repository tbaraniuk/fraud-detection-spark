package com.fraud.config

case class DataSourceConfig(
                           path: String,
                           format: String,
                           options: Map[String, String],
                           )
