import org.jetbrains.changelog.*
import org.jetbrains.intellij.tasks.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  idea apply true
  kotlin("jvm") version "1.5.0-M2"
  id("org.jetbrains.intellij") version "0.7.2"
  id("org.jetbrains.changelog") version "1.1.2"
  id("com.github.ben-manes.versions") version "0.38.0"
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
  }

  named<Zip>("buildPlugin") {
    dependsOn("test")
    archiveFileName.set("AceJump.zip")
  }

  withType<RunIdeTask> {
    dependsOn("test")
    findProperty("luginDev")?.let { args = listOf(projectDir.absolutePath) }
  }

  withType<PublishTask> {
    val intellijPublishToken: String? by project
    token(intellijPublishToken)
  }

  patchPluginXml {
    sinceBuild("203.7717.56")
    changeNotes(closure {
      changelog.getAll().values.take(2).last().toHTML()
    })
  }
}

changelog {
  version = "3.7.0"
  path = "${project.projectDir}/CHANGES.md"
  header = closure { "[${project.version}] - ${date()}" }
  itemPrefix = "-"
  unreleasedTerm = "Unreleased"
}

dependencies {
  // gradle-intellij-plugin doesn't attach sources properly for Kotlin :(
  compileOnly(kotlin("stdlib-jdk8"))
  // https://github.com/promeG/TinyPinyin
  implementation("com.github.promeg:tinypinyin:2.0.3")
}

repositories {
  mavenCentral()
  jcenter()
}

intellij {
  version = "211.6693-EAP-CANDIDATE-SNAPSHOT"
  pluginName = "AceJump"
  updateSinceUntilBuild = false
  setPlugins("java")

}

group = "org.acejump"
version = "3.7.0"
