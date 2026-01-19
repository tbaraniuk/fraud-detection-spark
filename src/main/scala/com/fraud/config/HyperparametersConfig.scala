package com.fraud.config

case class HyperparametersConfig(
                                  maxIter: Int = 100,
                                  regParam: Double = 0.01,
                                  elasticNetParam: Double = 0.0,
                                  threshold: Double = 0.5
                                )