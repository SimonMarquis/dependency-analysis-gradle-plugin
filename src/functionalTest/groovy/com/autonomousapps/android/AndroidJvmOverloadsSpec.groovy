// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.android

import com.autonomousapps.android.projects.AndroidJvmOverloadsProject
import com.autonomousapps.utils.Colors

import static com.autonomousapps.utils.Runner.build
import static com.google.common.truth.Truth.assertThat

@SuppressWarnings("GroovyAssignabilityCheck")
final class AndroidJvmOverloadsSpec extends AbstractAndroidSpec {

  def "reproducer for JvmOverload (#gradleVersion AGP #agpVersion)"() {
    given:
    def project = new AndroidJvmOverloadsProject(agpVersion)
    gradleProject = project.gradleProject

    when:
    def result = build(gradleVersion, gradleProject.rootDir, 'buildHealth', ':consumer:reason', '--id', ':producer')

    then:
    assertThat(Colors.decolorize(result.output)).contains(project.expectedReason())

    where:
    [gradleVersion, agpVersion] << gradleAgpMatrix()
  }
}
