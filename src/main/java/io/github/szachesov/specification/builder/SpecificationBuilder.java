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

  public SpecificationBuilder<S> distinct(boolean isDistinct) {
    this.isDistinct = isDistinct;
    return this;
  }

  // inner predicate
  public SpecificationBuilder<S> inner(CompositeSpecification<S, ?> spec) {
    specifications.add(spec);
    return this;
  }

  // Equal to(=)
  public SpecificationBuilder<S> equal(String column, Object value) {
    return equal(column, value, EqualsSpecification.Builder::self);
  }

  public SpecificationBuilder<S> equal(
      String column,
      Object value,
      Function<EqualsSpecification.Builder<S>, ObjectBuilder<EqualsSpecification<S>>> fn) {
    return equal(splitColumn(column), value, fn);
  }

  public SpecificationBuilder<S> equal(List<String> columns, Object value) {
    return equal(columns, value, EqualsSpecification.Builder::self);
  }

  public SpecificationBuilder<S> notEqual(String column, Object value) {
    return equal(column, value, EqualsSpecification.Builder::not);
  }

  public SpecificationBuilder<S> equal(
      List<String> columns,
      Object value,
      Function<EqualsSpecification.Builder<S>, ObjectBuilder<EqualsSpecification<S>>> fn) {
    if (value == null) {
      return this;
    }
    EqualsSpecification<S> spec =
        fn.apply(new EqualsSpecification.Builder<>(columns, value)).build();
    specifications.add(spec);
    return this;
  }

  // Equal to one of multiple possible values(IN)

  public <V> SpecificationBuilder<S> in(String column, Collection<V> values) {
    return in(column, values, InSpecification.Builder::self);
  }

  public <V> SpecificationBuilder<S> in(
      String column,
      Collection<V> values,
      Function<InSpecification.Builder<S, V>, ObjectBuilder<InSpecification<S, V>>> fn) {
    return in(splitColumn(column), values, fn);
  }

  public <V> SpecificationBuilder<S> in(List<String> columns, Collection<V> values) {
    return in(columns, values, InSpecification.Builder::self);
  }

  public <V> SpecificationBuilder<S> in(
      List<String> columns,
      Collection<V> values,
      Function<InSpecification.Builder<S, V>, ObjectBuilder<InSpecification<S, V>>> fn) {
    if (values == null || values.isEmpty()) {
      return this;
    }

    InSpecification<S, V> spec = fn.apply(new InSpecification.Builder<>(columns, values)).build();
    specifications.add(spec);
    return this;
  }

  // Character pattern(LIKE)
  public SpecificationBuilder<S> like(String column, String value) {
    return like(column, value, LikeSpecification.Builder::self);
  }

  public SpecificationBuilder<S> like(
      String column,
      String value,
      Function<LikeSpecification.Builder<S>, ObjectBuilder<LikeSpecification<S>>> fn) {
    return like(splitColumn(column), value, fn);
  }

  public SpecificationBuilder<S> like(List<String> columns, String value) {
    return like(columns, value, LikeSpecification.Builder::self);
  }

  public SpecificationBuilder<S> like(
      List<String> columns,
      String value,
      Function<LikeSpecification.Builder<S>, ObjectBuilder<LikeSpecification<S>>> fn) {
    if (value == null || value.isEmpty()) {
      return this;
    }

    LikeSpecification<S> spec = fn.apply(new LikeSpecification.Builder<>(columns, value)).build();

    value = value.trim();
    if (value.length() < spec.getMinChar()) {
      return this;
    }

    specifications.add(spec);
    return this;
  }

  // Comparison: BETWEEN, >, <, >=, <=

  public SpecificationBuilder<S> between(String column) {
    return between(column, ComparisonSpecification.Builder::self);
  }

  public <T extends Comparable<? super T>> SpecificationBuilder<S> between(
      String column,
      Function<ComparisonSpecification.Builder<S, T>, ComparisonSpecification.Builder<S, T>> fn) {
    return between(splitColumn(column), fn);
  }

  public SpecificationBuilder<S> between(List<String> columns) {
    return between(columns, ComparisonSpecification.Builder::self);
  }

  // https://stackoverflow.com/questions/22588518/lambda-expression-and-generic-defined-only-in-method
  public <T extends Comparable<? super T>> SpecificationBuilder<S> between(
      List<String> columns,
      Function<ComparisonSpecification.Builder<S, T>, ComparisonSpecification.Builder<S, T>> fn) {
    ComparisonSpecification.Builder<S, T> builder =
        fn.apply(new ComparisonSpecification.Builder<>(columns));

    if (builder.isEmptyValues()) return this;

    specifications.addAll(builder.build());

    return this;
  }

  // Compare to null

  public SpecificationBuilder<S> isNull(String column) {
    return isNull(column, true);
  }

  public SpecificationBuilder<S> isNotNull(String column) {
    return isNull(column, true, CompositeSpecification.Builder::not);
  }

  public SpecificationBuilder<S> isNull(String column, Boolean isActive) {
    return isNull(column, isActive, NullSpecification.Builder::self);
  }

  public <T> SpecificationBuilder<S> isNull(
      String column,
      boolean isActive,
      Function<NullSpecification.Builder<S, T>, ObjectBuilder<NullSpecification<S, T>>> fn) {
    return isNull(splitColumn(column), isActive, fn);
  }

  public SpecificationBuilder<S> isNull(List<String> columns) {
    return isNull(columns, true);
  }

  public SpecificationBuilder<S> isNull(List<String> columns, Boolean isActive) {
    return isNull(columns, isActive, NullSpecification.Builder::self);
  }

  public <T> SpecificationBuilder<S> isNull(
      List<String> columns,
      Boolean isActive,
      Function<NullSpecification.Builder<S, T>, ObjectBuilder<NullSpecification<S, T>>> fn) {
    if (!Boolean.TRUE.equals(isActive)) {
      return this;
    }

    NullSpecification<S, T> spec = fn.apply(new NullSpecification.Builder<>(columns)).build();

    specifications.add(spec);
    return this;
  }

  private List<String> splitColumn(String column) {
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
