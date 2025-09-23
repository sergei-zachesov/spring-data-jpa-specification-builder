package io.github.szachesov.specification.builder;

import lombok.Getter;

@Getter
public enum Bound {
  INCLUSIVE("<=", ">=") {
    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> min(
        ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.gte(builder);
    }

    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> max(
        ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.lte(builder);
    }
  },
  EXCLUSIVE(">", "<") {
    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> min(
        ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.gt(builder);
    }

    @Override
    <T, P extends Comparable<? super P>> InequalitySpecification<T, P> max(
        ComparisonSpecification.Builder<T, P> builder) {
      return InequalitySpecification.lt(builder);
    }
  };
  private final String[] descriptions;

  Bound(String... description) {
    this.descriptions = description;
  }

  abstract <T, P extends Comparable<? super P>> InequalitySpecification<T, P> min(
      ComparisonSpecification.Builder<T, P> builder);

  abstract <T, P extends Comparable<? super P>> InequalitySpecification<T, P> max(
      ComparisonSpecification.Builder<T, P> builder);
}
