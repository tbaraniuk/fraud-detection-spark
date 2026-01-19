package com.fraud.config

case class ServiceConfig(
                        spark: SparkConfig,
                        input: DataSourceConfig,
                        ml: MLConfig,
                        mlflow: MlflowConfig,
                        output: OutputConfig
                        )
