// Copyright (c) 2026. Tony Robalik.
// SPDX-License-Identifier: Apache-2.0
package com.autonomousapps.jvm


import com.autonomousapps.jvm.projects.Reproducer1747Project

import static com.autonomousapps.utils.Runner.build
import static com.google.common.truth.Truth.assertThat

final class Reproducer1747Spec extends AbstractJvmSpec {

  def "reproducer for issue 1747 (#gradleVersion)"() {
    given:
    def project = new Reproducer1747Project()
    gradleProject = project.gradleProject

    when:
    build(gradleVersion, gradleProject.rootDir, 'buildHealth')

    then:
    assertThat(project.actualProjectAdvice()).containsExactlyElementsIn(project.expectedProjectAdvice)

    where:
    gradleVersion << gradleVersions()
  }
}
