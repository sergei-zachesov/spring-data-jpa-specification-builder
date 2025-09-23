package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;

public class BetweenSpecification<T, P extends Comparable<? super P>>
    extends ComparisonSpecification<T, P> {

  protected BetweenSpecification(Builder<T, P> builder) {
    super(builder);
  }

  @Override
  Predicate toPredicate(CriteriaBuilder builder, Path<P> path) {
    return builder.between(path, min, max);
  }
}
