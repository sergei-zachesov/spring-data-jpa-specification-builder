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

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;
import lombok.Getter;

/**
 * Predicate of like.
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
public class LikeSpecification<T> extends CompositeSpecification<T, String> {

  private String value;
  private final boolean isIgnoreCase;
  private final Wildcard wildcard;
  @Getter private final int minChar;

  @Override
  Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
    Path<String> path = getPath(root);

    Expression<String> expression;
    if (isIgnoreCase) {
      expression = builder.upper(path);
      value = value.toUpperCase(Locale.ROOT);
    } else {
      expression = path;
    }

    return builder.like(expression, wildcard.getWithWildcard().apply(value));
  }

  /** Builder for {@link LikeSpecification}. */
  public static class Builder<S> extends CompositeSpecification.Builder<Builder<S>>
      implements ObjectBuilder<LikeSpecification<S>> {

    private final String value;
    private boolean isIgnoreCase = true;
    private Wildcard wildcard = Wildcard.ABSENCE;
    private int minChar = 3;

    Builder(final List<String> columns, final String value) {
      super(columns);
      this.value = value;
    }

    public Builder<S> ignoreCase() {
      this.isIgnoreCase = true;
      return this;
    }

    public Builder<S> wildcard(final Wildcard wildcard) {
      this.wildcard = wildcard;
      return this;
    }

    public Builder<S> minChar(final int minChar) {
      this.minChar = minChar;
      return this;
    }

    @Override
    public LikeSpecification<S> build() {
      return new LikeSpecification<>(this);
    }

    @Override
    protected Builder<S> self() {
      return this;
    }
  }

  private LikeSpecification(final Builder<T> builder) {
    super(builder);
    this.value = builder.value;
    this.isIgnoreCase = builder.isIgnoreCase;
    this.wildcard = builder.wildcard;
    this.minChar = builder.minChar;
  }
}
