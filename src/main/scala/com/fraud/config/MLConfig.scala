package com.fraud.config

case class MLConfig(
                   numeric: List[String],
                   label: String,
                   hyperparameters: HyperparametersConfig
                   )