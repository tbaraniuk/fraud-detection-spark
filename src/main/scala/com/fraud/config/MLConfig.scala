package com.fraud.config

case class MLConfig(
                   pcaFeatures: List[String],
                   numericFeatures: List[String],
                   label: String,
                   hyperparameters: HyperparametersConfig
                   )