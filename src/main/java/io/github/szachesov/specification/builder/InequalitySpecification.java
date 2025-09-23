package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class InequalitySpecification<T, P extends Comparable<? super P>>
    extends ComparisonSpecification<T, P> {

  private final Sign sign;

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> gt(
      Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.GT);
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> gte(
      Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.GTE);
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> lt(
      Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.LT);
  }

  static <T, P extends Comparable<? super P>> InequalitySpecification<T, P> lte(
      Builder<T, P> builder) {
    return new InequalitySpecification<>(builder, Sign.LTE);
  }

  protected InequalitySpecification(Builder<T, P> builder, Sign sign) {
    super(builder);
    this.sign = sign;
  }

  @Override
  Predicate toPredicate(CriteriaBuilder builder, Path<P> path) {
    return sign.toPredicate(builder, path, min, max);
  }

  /**
   * The sign of inequality.
   *
   * <p><a href="https://en.wikipedia.org/wiki/Inequality_(mathematics)">Inequality</a>
   */
  @Getter
  @AllArgsConstructor
  protected enum Sign {
    GT("greater than", ">") {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.greaterThan(path, min);
      }
    },
    GTE("greater than or equal to", ">=") {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.greaterThanOrEqualTo(path, min);
      }
    },
    LT("less than", "<") {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.lessThan(path, max);
      }
    },
    LTE("less than or equal to", "<=") {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.lessThanOrEqualTo(path, max);
      }
    };

    private final String name;
    private final String description;

    abstract <T extends Comparable<? super T>> Predicate toPredicate(
        CriteriaBuilder builder, Path<T> path, T min, T max);
  }
}
