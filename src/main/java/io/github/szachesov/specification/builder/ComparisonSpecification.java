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
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Predicate of comparison operators.
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public abstract class ComparisonSpecification<T, P extends Comparable<? super P>>
    extends CompositeSpecification<T, P> {

  protected final P min;
  protected final P max;

  protected ComparisonSpecification(final Builder<T, P> builder) {
    super(builder);
    this.min = builder.min;
    this.max = builder.max;
  }

  @Override
  protected Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
    Path<P> path = getPath(root);
    return toPredicate(builder, path);
  }

  abstract Predicate toPredicate(final CriteriaBuilder builder, final Path<P> path);

  /** Builder for {@link ComparisonSpecification}. */
  public static class Builder<S, T extends Comparable<? super T>>
      extends CompositeSpecification.Builder<Builder<S, T>>
      implements ObjectBuilder<List<ComparisonSpecification<S, T>>> {

    private T min;
    private T max;
    private Bound minBound = Bound.INCLUSIVE;
    private Bound maxBound = Bound.INCLUSIVE;

    Builder(final List<String> columns) {
      super(columns);
    }

    private Builder<S, T> min(final T min) {
      this.min = min;
      return self();
    }

    private Builder<S, T> max(final T max) {
      this.max = max;
      return self();
    }

    public Builder<S, T> minBound(final Bound minBound) {
      this.minBound = minBound;
      return self();
    }

    public Builder<S, T> maxBound(final Bound maxBound) {
      this.maxBound = maxBound;
      return self();
    }

    @Override
    protected Builder<S, T> self() {
      return this;
    }

    @Override
    public List<ComparisonSpecification<S, T>> build() {
      if (isEmptyValues()) {
        throw new IllegalArgumentException(
            "One or both of the MIN or MAX parameters must not be null.");
      }

      if (isBetween()) {
        return List.of(new BetweenSpecification<>(this));
      }

      return buildInequalitySpecification();
    }

    boolean isEmptyValues() {
      return min == null && max == null;
    }

    private boolean isBetween() {
      if (this.min == null || this.max == null) return false;
      return !Bound.INCLUSIVE.equals(this.minBound) && !Bound.INCLUSIVE.equals(this.maxBound);
    }

    private List<ComparisonSpecification<S, T>> buildInequalitySpecification() {
      List<ComparisonSpecification<S, T>> specifications = new ArrayList<>(2);
      if (min != null) {
        specifications.add(minBound.min(this));
      }
      if (max != null) {
        specifications.add(maxBound.max(this));
      }
      return specifications;
    }
  }
}
