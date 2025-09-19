package io.github.szachesov.specification.builder;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Function;

/** <a href="https://en.wikipedia.org/wiki/Where_(SQL)#LIKE">LIKE with wildcard</a> */
@Getter
@AllArgsConstructor
public enum Wildcard {
  ABSENCE("s", v -> v),
  LEADING("%s", v -> "%" + v),
  ENDING("s%", v -> v + "%"),
  MULTIPLE("%s%", v -> "%" + v + "%");

  private final String description;
  private final Function<String, String> withWildcard;
}
