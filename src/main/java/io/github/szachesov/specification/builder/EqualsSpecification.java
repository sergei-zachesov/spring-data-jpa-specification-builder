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
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Predicate of equal to(=).
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
public class EqualsSpecification<T> extends CompositeSpecification<T, Object> {

  private final Object value;

  @Override
  Predicate toCriteriaPredicate(
      Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Path<Object> path = getPath(root);

    return isNot ? criteriaBuilder.notEqual(path, value) : criteriaBuilder.equal(path, value);
  }

  public static class Builder<S> extends CompositeSpecification.Builder<Builder<S>>
      implements ObjectBuilder<EqualsSpecification<S>> {
    private final Object value;

    Builder(List<String> columns, Object value) {
      super(columns);
      this.value = value;
    }

    @Override
    public EqualsSpecification<S> build() {
      return new EqualsSpecification<>(this);
    }

    @Override
    protected Builder<S> self() {
      return this;
    }
  }

  private EqualsSpecification(Builder<T> builder) {
    super(builder);
    this.value = builder.value;
  }
}
