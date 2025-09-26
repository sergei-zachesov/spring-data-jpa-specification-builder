/*
 * Copyright 2025 Sergei Zachesov and others.
 * https://github.com/sergei-zachesov/spring-data-jpa-specification-builder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.szachesov.specification.builder.testutils;

import static io.github.szachesov.specification.builder.testutils.TestConstants.GROUP_ADMIN;
import static io.github.szachesov.specification.builder.testutils.TestConstants.GROUP_USER;

import java.util.List;
import lombok.experimental.UtilityClass;
import io.github.szachesov.specification.builder.sample.entity.Group;

@UtilityClass
public class TestData {

  public static List<Group> croupsAll() {
    return List.of(croupAdmin(), croupUser());
  }

  public static Group croupAdmin() {
    return new Group(GROUP_ADMIN);
  }

  public static Group croupUser() {
    return new Group(GROUP_USER);
  }
}
