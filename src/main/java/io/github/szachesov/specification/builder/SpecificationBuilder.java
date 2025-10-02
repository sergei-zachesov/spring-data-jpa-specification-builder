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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

/**
 * A builder aggregating specifications describing all SQL predicates.
 *
 * <p><a href="https://en.wikipedia.org/wiki/SQL_syntax#Operators">SQL operators</a>
 */
@NoArgsConstructor(staticName = "builder")
public class SpecificationBuilder<S> {

  private final List<CompositeSpecification<S, ?>> specifications = new ArrayList<>();
  private boolean isDistinct = true;

  public SpecificationBuilder<S> distinct(final boolean isDistinct) {
    this.isDistinct = isDistinct;
    return this;
  }

  // inner predicate
  public SpecificationBuilder<S> inner(final CompositeSpecification<S, ?> spec) {
    specifications.add(spec);
    return this;
  }

  // Equal to(=)
  public SpecificationBuilder<S> notEqual(final String column, final Object value) {
    return equal(column, value, EqualsSpecification.Builder::not);
  }

  public SpecificationBuilder<S> equal(final String column, final Object value) {
    return equal(column, value, EqualsSpecification.Builder::self);
  }

  public SpecificationBuilder<S> equal(
      final String column,
      final Object value,
      final Function<EqualsSpecification.Builder<S>, ObjectBuilder<EqualsSpecification<S>>> fn) {
    return equal(splitColumn(column), value, fn);
  }

  public SpecificationBuilder<S> equal(final List<String> columns, final Object value) {
    return equal(columns, value, EqualsSpecification.Builder::self);
  }

  public SpecificationBuilder<S> equal(
      final List<String> columns,
      final Object value,
      final Function<EqualsSpecification.Builder<S>, ObjectBuilder<EqualsSpecification<S>>> fn) {
    if (value == null) {
      return this;
    }
    EqualsSpecification<S> spec =
        fn.apply(new EqualsSpecification.Builder<>(columns, value)).build();
    specifications.add(spec);
    return this;
  }

  // Equal to one of multiple possible values(IN)

  public <V> SpecificationBuilder<S> in(final String column, final Collection<V> values) {
    return in(column, values, InSpecification.Builder::self);
  }

  public <V> SpecificationBuilder<S> in(
      final String column,
      final Collection<V> values,
      final Function<InSpecification.Builder<S, V>, ObjectBuilder<InSpecification<S, V>>> fn) {
    return in(splitColumn(column), values, fn);
  }

  public <V> SpecificationBuilder<S> in(final List<String> columns, final Collection<V> values) {
    return in(columns, values, InSpecification.Builder::self);
  }

  public <V> SpecificationBuilder<S> in(
      final List<String> columns,
      final Collection<V> values,
      final Function<InSpecification.Builder<S, V>, ObjectBuilder<InSpecification<S, V>>> fn) {
    if (values == null || values.isEmpty()) {
      return this;
    }

    InSpecification<S, V> spec = fn.apply(new InSpecification.Builder<>(columns, values)).build();
    specifications.add(spec);
    return this;
  }

  // Character pattern(LIKE)
  public SpecificationBuilder<S> like(final String column, final String value) {
    return like(column, value, LikeSpecification.Builder::self);
  }

  public SpecificationBuilder<S> like(
      final String column,
      final String value,
      final Function<LikeSpecification.Builder<S>, ObjectBuilder<LikeSpecification<S>>> fn) {
    return like(splitColumn(column), value, fn);
  }

  public SpecificationBuilder<S> like(final List<String> columns, final String value) {
    return like(columns, value, LikeSpecification.Builder::self);
  }

  public SpecificationBuilder<S> like(
      final List<String> columns,
      final String value,
      final Function<LikeSpecification.Builder<S>, ObjectBuilder<LikeSpecification<S>>> fn) {
    if (value == null || value.isEmpty()) {
      return this;
    }

    LikeSpecification<S> spec = fn.apply(new LikeSpecification.Builder<>(columns, value)).build();

    String trimValue = value.trim();
    if (trimValue.length() < spec.getMinChar()) {
      return this;
    }

    specifications.add(spec);
    return this;
  }

  // Comparison: BETWEEN, >, <, >=, <=

  public SpecificationBuilder<S> between(final String column) {
    return between(column, ComparisonSpecification.Builder::self);
  }

  public <T extends Comparable<? super T>> SpecificationBuilder<S> between(
      final String column,
      final Function<ComparisonSpecification.Builder<S, T>, ComparisonSpecification.Builder<S, T>>
          fn) {
    return between(splitColumn(column), fn);
  }

  public SpecificationBuilder<S> between(final List<String> columns) {
    return between(columns, ComparisonSpecification.Builder::self);
  }

  // https://stackoverflow.com/questions/22588518/lambda-expression-and-generic-defined-only-in-method
  public <T extends Comparable<? super T>> SpecificationBuilder<S> between(
      final List<String> columns,
      final Function<ComparisonSpecification.Builder<S, T>, ComparisonSpecification.Builder<S, T>>
          fn) {
    ComparisonSpecification.Builder<S, T> builder =
        fn.apply(new ComparisonSpecification.Builder<>(columns));

    if (builder.isEmptyValues()) return this;

    specifications.addAll(builder.build());

    return this;
  }

  // Compare to null

  public SpecificationBuilder<S> isNotNull(final String column) {
    return isNull(column, true, CompositeSpecification.Builder::not);
  }

  public SpecificationBuilder<S> isNull(final String column) {
    return isNull(column, true);
  }

  public SpecificationBuilder<S> isNull(final String column, final Boolean isActive) {
    return isNull(column, isActive, NullSpecification.Builder::self);
  }

  public <T> SpecificationBuilder<S> isNull(
      final String column,
      final boolean isActive,
      final Function<NullSpecification.Builder<S, T>, ObjectBuilder<NullSpecification<S, T>>> fn) {
    return isNull(splitColumn(column), isActive, fn);
  }

  public SpecificationBuilder<S> isNull(final List<String> columns) {
    return isNull(columns, true);
  }

  public SpecificationBuilder<S> isNull(final List<String> columns, final Boolean isActive) {
    return isNull(columns, isActive, NullSpecification.Builder::self);
  }

  public <T> SpecificationBuilder<S> isNull(
      final List<String> columns,
      final Boolean isActive,
      final Function<NullSpecification.Builder<S, T>, ObjectBuilder<NullSpecification<S, T>>> fn) {
    if (!Boolean.TRUE.equals(isActive)) {
      return this;
    }

    NullSpecification<S, T> spec = fn.apply(new NullSpecification.Builder<>(columns)).build();

    specifications.add(spec);
    return this;
  }

  private List<String> splitColumn(final String column) {
    return Arrays.asList(column.split("\\."));
  }

  public Specification<S> build() {
    if (specifications.isEmpty()) {
      return null;
    }

    if (!isDistinct) {
      specifications.forEach(s -> s.setDistinct(isDistinct));
    }

    Specification<S> result = specifications.get(0);
    for (int i = 1; i < specifications.size(); i++) {
      result = specifications.get(i).connection.connect(result, specifications.get(i));
    }

    return result;
  }
}
