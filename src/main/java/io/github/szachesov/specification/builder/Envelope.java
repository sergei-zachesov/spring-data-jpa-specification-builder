package io.github.szachesov.specification.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Envelope {
  INCLUSIVE("<="),
  EXCLUSIVE("<");

  private final String description;
}
