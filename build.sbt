ThisBuild / version := "1.0.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.10"

lazy val root = (project in file("."))
  .settings(
    name := "fraud-detection-spark"
  )

val pureconfigVersion = "0.17.1"
val mlflowVersion = "2.14.1"
val mlflowScalaVersion = "3.5.1"
val sparkVersion = "3.5.0"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig-core" % pureconfigVersion,
  "com.github.pureconfig" %% "pureconfig-generic" % pureconfigVersion,
  "org.mlflow" % "mlflow-client" % mlflowVersion,
  "org.mlflow" %% "mlflow-spark" % mlflowScalaVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-mllib" % sparkVersion,
  "org.apache.spark" %% "spark-streaming" % sparkVersion
)

