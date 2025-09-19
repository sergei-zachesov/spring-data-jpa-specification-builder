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
