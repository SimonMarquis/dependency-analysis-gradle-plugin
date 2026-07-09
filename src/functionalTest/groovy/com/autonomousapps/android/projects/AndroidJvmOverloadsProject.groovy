// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.android.projects

import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.Source
import com.autonomousapps.kit.gradle.kotlin.Kotlin

import static com.autonomousapps.kit.gradle.Dependency.project

final class AndroidJvmOverloadsProject extends AbstractAndroidProject {

  final String agpVersion
  final GradleProject gradleProject

    AndroidJvmOverloadsProject(String agpVersion) {
      super(agpVersion)
      this.agpVersion = agpVersion
      this.gradleProject = build()
  }

  private GradleProject build() {
    return newAndroidGradleProjectBuilder()
      .withAndroidLibProject('alt') { s ->
        s.manifest = libraryManifest('com.example.alt')
        s.sources = altSources
        s.withBuildScript { bs ->
          bs.plugins(androidLib())
          bs.android = defaultAndroidLibBlock(true, 'com.example.alt')
          bs.kotlin = Kotlin.DEFAULT
          bs.dependencies(
            project('testImplementation', ':producer'),
          )
        }
      }
      .withAndroidLibProject('consumer') { s ->
        s.manifest = libraryManifest('com.example.consumer')
        s.sources = consumerSources
        s.withBuildScript { bs ->
          bs.plugins(androidLib())
          bs.android = defaultAndroidLibBlock(true, 'com.example.consumer')
          bs.kotlin = Kotlin.DEFAULT
          bs.dependencies(
            project('testImplementation', ':producer'),
          )
        }
      }
      .withSubproject('producer') { c ->
        c.sources = producerSources
        c.withBuildScript { bs ->
          bs.plugins = kotlin
        }
      }
      .write()
  }

  private altSources = [
    Source.kotlin(
      """\
      package com.example.alt
      import com.example.producer.nextNumericalString
      import kotlin.random.Random

      class TestAlt {
        private val id = Random.nextNumericalString()
        fun test(context: android.content.Context) = println()
      }
      """
    )
      .withSourceSet("test")
      .withPath('com.example.alt', 'TestAlt')
      .build(),
  ]

  private consumerSources = [
    Source.kotlin(
      """\
      package com.example.consumer
      import com.example.producer.nextNumericalString
      import kotlin.random.Random

      class TestConsumer {
        private val id = Random.nextNumericalString(16)
        fun test(context: android.content.Context) = println()
      }
      """
    )
      .withSourceSet("test")
      .withPath('com.example.consumer', 'TestConsumer')
      .build(),
  ]

  private producerSources = [
    Source.kotlin(
      """\
      package com.example.producer
      import kotlin.random.Random

      @JvmOverloads
      fun Random.nextNumericalString(size: Int? = null): String = "..."
      """
    )
      .withPath('com.example.producer', 'Random')
      .build(),
  ]

  String expectedReason() {
    return '''\
      You have been advised to remove this dependency from 'testImplementation\'.'''.stripIndent()
  }
}
