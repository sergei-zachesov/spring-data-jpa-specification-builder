package io.github.szachesov.specification.builder;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import io.github.szachesov.specification.builder.sample.entity.Group;
import io.github.szachesov.specification.builder.sample.entity.Group_;
import io.github.szachesov.specification.builder.sample.repository.GroupRepository;
import io.github.szachesov.specification.builder.testutils.TestConstants;
import io.github.szachesov.specification.builder.testutils.TestData;

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
  void equals_getEntity_byVarchar() {
    String groupName = TestConstants.GROUP_USER;
    Specification<Group> spec =
        SpecificationBuilder.<Group>builder().equal(Group_.NAME, groupName).build();

    List<Group> entities = groupRepository.findAll(spec);

    assertThat(entities).hasSize(1);
    assertThat(entities.getFirst().getName()).isEqualTo(groupName);
  }
}
