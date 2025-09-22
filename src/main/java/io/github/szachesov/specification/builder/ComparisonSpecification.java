package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class ComparisonSpecification<S, T extends Comparable<? super T>>
    extends AbstractSpecification<S, T> {

  private final T min;
  private final T max;
  private final ComparisonOperation operation;

  @Override
  Predicate toCriteriaPredicate(Root<S> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Path<T> path = getPath(root);
    return operation.toPredicate(builder, path, min, max);
  }

  public static class Builder<S, T extends Comparable<? super T>>
      extends AbstractSpecification.Builder<Builder<S, T>>
      implements ObjectBuilder<ComparisonSpecification<S, T>> {

    private T min;
    private T max;

    private Envelope minEnvelope = Envelope.INCLUSIVE;
    private Envelope maxEnvelope = Envelope.INCLUSIVE;
    private ComparisonOperation comparisonOperation = ComparisonOperation.BETWEEN;

    Builder(List<String> columns) {
      super(columns);
    }

    Builder(List<String> columns, T min, T max) {
      super(columns);
      this.min = min;
      this.max = max;
    }

    private Builder<S, T> min(T min) {
      this.min = min;
      return self();
    }

    private Builder<S, T> max(T max) {
      this.max = max;
      return self();
    }

    public Builder<S, T> minEnvelope(Envelope minEnvelope) {
      this.minEnvelope = minEnvelope;
      return self();
    }

    public Builder<S, T> maxEnvelope(Envelope maxEnvelope) {
      this.maxEnvelope = maxEnvelope;
      return self();
    }

    private Builder<S, T> comparisonOperation(Envelope minEnvelope, Envelope maxEnvelope) {
      this.comparisonOperation = ComparisonOperation.define(minEnvelope, maxEnvelope);
      return self();
    }

    @Override
    protected Builder<S, T> self() {
      return this;
    }

    @Override
    public ComparisonSpecification<S, T> build() {
      return new ComparisonSpecification<>(this);
    }
  }

  private ComparisonSpecification(Builder<S, T> builder) {
    super(builder);
    this.min = builder.min;
    this.max = builder.max;
    this.operation = builder.comparisonOperation;
  }

  public static <S, T extends Comparable<? super T>>
      List<ComparisonSpecification<S, T>> buildListOf(Builder<S, T> builder) {
    if (builder.min == null) {
      builder.minEnvelope = null;
    }
    if (builder.max == null) {
      builder.maxEnvelope = null;
    }

    Interval interval = Interval.define(builder.minEnvelope, builder.maxEnvelope);
    return interval.getCountOperation().specificationsOf(builder);
  }

  /** <a href="https://en.wikipedia.org/wiki/Inequality_(mathematics)">Inequality</a> */
  @Getter
  @AllArgsConstructor
  private enum ComparisonOperation {
    GT("greater than", ">", (iMin, iMax) -> Envelope.EXCLUSIVE.equals(iMin) && iMax == null) {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.greaterThan(path, min);
      }
    },
    GTE(
        "greater than or equal to",
        ">=",
        (iMin, iMax) -> Envelope.INCLUSIVE.equals(iMin) && iMax == null) {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.greaterThanOrEqualTo(path, min);
      }
    },
    LE("less than", "<", (iMin, iMax) -> iMin == null && Envelope.EXCLUSIVE.equals(iMax)) {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.lessThan(path, max);
      }
    },
    LEE(
        "less than or equal to",
        "<=",
        (iMin, iMax) -> iMin == null && Envelope.INCLUSIVE.equals(iMax)) {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.lessThanOrEqualTo(path, max);
      }
    },
    BETWEEN(
        "Between an inclusive range",
        "a<=x<=b",
        (iMin, iMax) -> Envelope.INCLUSIVE.equals(iMin) && Envelope.INCLUSIVE.equals(iMax)) {
      @Override
      <T extends Comparable<? super T>> Predicate toPredicate(
          CriteriaBuilder builder, Path<T> path, T min, T max) {
        return builder.between(path, min, max);
      }
    };

    private final String name;
    private final String description;
    private final BiPredicate<Envelope, Envelope> isThisOperation;

    public static ComparisonOperation define(Envelope iMin, Envelope iMax) {
      return Arrays.stream(values())
          .filter(i -> i.isThisOperation.test(iMin, iMax))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Comparison operation didn't define"));
    }

    abstract <T extends Comparable<? super T>> Predicate toPredicate(
        CriteriaBuilder builder, Path<T> path, T min, T max);
  }

  /**
   * <a
   * href="https://en.wikipedia.org/wiki/Interval_(mathematics)#Classification_of_intervals">Classification
   * of intervals</a>
   */
  @Getter
  @AllArgsConstructor
  private enum Interval {
    NOT_INTERVAL(
        "a<x||a<=x||x<b||x<=b", CountOperation.ONE, (iMin, iMax) -> iMin == null || iMax == null),
    OPEN(
        "a<x<b",
        CountOperation.TWO,
        (iMin, iMax) -> Envelope.EXCLUSIVE.equals(iMin) && Envelope.EXCLUSIVE.equals(iMax)),
    CLOSED(
        "a<=x<=b",
        CountOperation.ONE,
        (iMin, iMax) -> Envelope.INCLUSIVE.equals(iMin) && Envelope.INCLUSIVE.equals(iMax)),
    LEFT_CLOSED_RIGHT_OPEN(
        "a<=x<b",
        CountOperation.TWO,
        (iMin, iMax) -> Envelope.INCLUSIVE.equals(iMin) && Envelope.EXCLUSIVE.equals(iMax)),
    LEFT_OPEN_RIGHT_CLOSED(
        "a<x<=b",
        CountOperation.TWO,
        (iMin, iMax) -> Envelope.EXCLUSIVE.equals(iMin) && Envelope.INCLUSIVE.equals(iMax));

    private final String description;
    private final CountOperation countOperation;
    private final BiPredicate<Envelope, Envelope> isThisInterval;

    public static Interval define(Envelope iMin, Envelope iMax) {
      return Arrays.stream(values())
          .filter(i -> i.isThisInterval.test(iMin, iMax))
          .findFirst()
          .orElseThrow(
              () -> new IllegalArgumentException("Comparison criteria didn't define interval"));
    }

    private enum CountOperation {
      ONE {
        @Override
        <S, T extends Comparable<? super T>> List<ComparisonSpecification<S, T>> specificationsOf(
            Builder<S, T> builder) {
          ComparisonSpecification<S, T> spec =
              new Builder<S, T>(builder.columns)
                  .min(builder.min)
                  .max(builder.max)
                  .comparisonOperation(builder.minEnvelope, builder.maxEnvelope)
                  .build();

          return List.of(spec);
        }
      },
      TWO {
        @Override
        <S, T extends Comparable<? super T>> List<ComparisonSpecification<S, T>> specificationsOf(
            Builder<S, T> builder) {

          ComparisonSpecification<S, T> minSpec =
              new Builder<S, T>(builder.columns)
                  .min(builder.min)
                  .comparisonOperation(builder.minEnvelope, null)
                  .build();

          ComparisonSpecification<S, T> maxSpec =
              new Builder<S, T>(builder.columns)
                  .min(builder.max)
                  .comparisonOperation(null, builder.maxEnvelope)
                  .build();

          return List.of(minSpec, maxSpec);
        }
      };

      abstract <S, T extends Comparable<? super T>>
          List<ComparisonSpecification<S, T>> specificationsOf(Builder<S, T> builder);
    }
  }
}
