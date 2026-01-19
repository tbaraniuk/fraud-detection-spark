package com.fraud.config

import pureconfig._
import pureconfig.generic.auto._


object AppConfig {
  def load(): ServiceConfig = {
    ConfigSource.default.loadOrThrow[ServiceConfig]
  }

  def load(path: String): ServiceConfig = {
    ConfigSource.file(path).loadOrThrow[ServiceConfig]
  }
}
