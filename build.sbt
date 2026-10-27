scalaVersion := "3.3.4"

lazy val root = (project in file("."))
  .settings(
    name := "Project<23026628>",
    fork := true,
    libraryDependencies ++= Seq(
      "org.scalafx" %% "scalafx" % "21.0.0-R32",
      "com.lihaoyi" %% "upickle" % "3.3.1"
    ),
    libraryDependencies ++= {
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _ => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics")
        .map(m => "org.openjfx" % s"javafx-$m" % "21.0.2" classifier osName)
    }
  )