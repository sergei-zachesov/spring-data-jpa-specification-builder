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

  protected ComparisonSpecification(Builder<T, P> builder) {
    super(builder);
    this.min = builder.min;
    this.max = builder.max;
  }

  @Override
  protected Predicate toCriteriaPredicate(
      Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Path<P> path = getPath(root);
    return toPredicate(builder, path);
  }

  abstract Predicate toPredicate(CriteriaBuilder builder, Path<P> path);

  public static class Builder<S, T extends Comparable<? super T>>
      extends CompositeSpecification.Builder<Builder<S, T>>
      implements ObjectBuilder<List<ComparisonSpecification<S, T>>> {

    private T min;
    private T max;
    private Bound minBound = Bound.INCLUSIVE;
    private Bound maxBound = Bound.INCLUSIVE;

    Builder(List<String> columns) {
      super(columns);
    }

    private Builder<S, T> min(T min) {
      this.min = min;
      return self();
    }

    private Builder<S, T> max(T max) {
      this.max = max;
      return self();
    }

    public Builder<S, T> minBound(Bound minBound) {
      this.minBound = minBound;
      return self();
    }

    public Builder<S, T> maxBound(Bound maxBound) {
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
