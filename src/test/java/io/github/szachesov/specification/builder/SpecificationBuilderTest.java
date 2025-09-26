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

package io.github.szachesov.specification.builder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.github.szachesov.specification.builder.sample.entity.Group;
import io.github.szachesov.specification.builder.sample.entity.Group_;
import io.github.szachesov.specification.builder.sample.repository.GroupRepository;
import io.github.szachesov.specification.builder.testutils.TestConstants;
import io.github.szachesov.specification.builder.testutils.TestData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpecificationBuilderTest {

  @Autowired private GroupRepository groupRepository;

  @BeforeAll
  static void init(@Autowired GroupRepository groupRepository) {
    groupRepository.saveAll(TestData.croupsAll());
  }

  @Test
  void equal_getEntity_byVarchar() {
    String groupName = TestConstants.GROUP_USER;
    Specification<Group> spec =
        SpecificationBuilder.<Group>builder().equal(Group_.NAME, groupName).build();

    List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(1);
    assertThat(entities.getFirst().getName()).isEqualTo(groupName);
  }

  @Test
  void equal_notFound_byVarchar() {
    Specification<Group> spec =
        SpecificationBuilder.<Group>builder().equal(Group_.NAME, "Unknown").build();

    List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(0);
  }

  @Test
  void equal_getOtherEntity_byNotVarchar() {
    String groupName = TestConstants.GROUP_USER;
    Specification<Group> spec =
        SpecificationBuilder.<Group>builder().equal(Group_.NAME, groupName, b -> b.not()).build();

    List<Group> entities = groupRepository.findAll(spec);

    List<String> names = entities.stream().map(Group::getName).toList();
    assertThat(names).doesNotContain(groupName);
  }
}
