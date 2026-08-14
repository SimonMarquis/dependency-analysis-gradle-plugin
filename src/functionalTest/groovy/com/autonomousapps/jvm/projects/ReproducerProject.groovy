// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.jvm.projects

import com.autonomousapps.AbstractProject
import com.autonomousapps.kit.GradleProject
import com.autonomousapps.kit.Source
import com.autonomousapps.model.Advice
import com.autonomousapps.model.ProjectAdvice

import static com.autonomousapps.AdviceHelper.*
import static com.autonomousapps.kit.gradle.Dependency.project

final class ReproducerProject extends AbstractProject {

  final GradleProject gradleProject

  ReproducerProject() {
    this.gradleProject = build()
  }

  private GradleProject build() {
    return newGradleProjectBuilder(GradleProject.DslKind.KOTLIN)
      .withSubproject('app') { c ->
        c.sources = appSources
        c.withBuildScript { bs ->
          bs.plugins = kotlin
          bs.dependencies = [
            project('implementation', ':lib'),
          ]
        }
      }
      .withSubproject('lib') { c ->
        c.sources = libSources
        c.withBuildScript { bs ->
          bs.plugins = kotlin
          bs.dependencies = [
            project('api', ':transitive'),
          ]
        }
      }
      .withSubproject('transitive') { c ->
        c.sources = libTransitiveSources
        c.withBuildScript { bs ->
          bs.plugins = kotlin
        }
      }
      .write()
  }

  private appSources = [
    Source.kotlin(
      """\
      package com.example.app

      import com.example.lib.ColorProvider

      private class App {
        fun run() {
          val color = ColorProvider().getColor()
          println(color)
        }
      }
      """
    )
      .withPath('com.example.app', 'App')
      .build(),
    Source.kotlin(
      """\
      package com.example.app

      import com.example.lib.ColorProvider

      private class AppTest {
        fun test() {
          val color = ColorProvider().getColor()
          println(color)
        }
      }
      """
    )
      .withSourceSet('test')
      .withPath('com.example.app', 'AppTest')
      .build(),
  ]

  private libSources = [
    Source.kotlin(
      """\
      package com.example.lib

      import com.example.transitive.Color

      class ColorProvider {
        fun getColor(): Color = Color.RED
      }
      """
    )
      .withPath('com.example.lib', 'ColorProvider')
      .build(),
  ]

  private libTransitiveSources = [
    Source.kotlin(
      """\
      package com.example.transitive

      enum class Color {
        RED, GREEN, BLUE
      }
      """
    )
      .withPath('com.example.transitive', 'Color')
      .build(),
  ]

  Set<ProjectAdvice> actualProjectAdvice() {
    return actualProjectAdvice(gradleProject)
  }

  private final Set<Advice> appAdvice = [
    Advice.ofAdd(projectCoordinates(':transitive'), 'implementation'),
  ]

  final Set<ProjectAdvice> expectedProjectAdvice = [
    projectAdviceForDependencies(':app', appAdvice),
    emptyProjectAdviceFor(':lib'),
    emptyProjectAdviceFor(':transitive'),
  ]
}
