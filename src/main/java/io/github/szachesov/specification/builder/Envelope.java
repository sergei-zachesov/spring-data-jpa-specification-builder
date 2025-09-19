package io.github.szachesov.specification.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Zachesov Sergei
 * @since 2023-04-17
 */
@Getter
@AllArgsConstructor
public enum Envelope {
  INCLUSIVE("<="),
  EXCLUSIVE("<");

  private final String description;
}
