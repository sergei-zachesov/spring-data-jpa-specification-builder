package io.github.szachesov.specification.builder;

import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** LIKE with wildcard. */
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
