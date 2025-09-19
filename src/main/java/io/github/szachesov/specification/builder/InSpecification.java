package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.*;

import java.util.Collection;
import java.util.List;

/**
 * @author Zachesov Sergei
 * @since 2023-11-08
 */
public class InSpecification<S, T> extends AbstractSpecification<S, T> {

  private final Collection<T> values;

  @Override
  Predicate toCriteriaPredicate(
      Root<S> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    return getPath(root).in(values);
  }

  public static class Builder<S, T>
      extends AbstractSpecification.Builder<Builder<S, T>>
      implements ObjectBuilder<InSpecification<S, T>> {

    private final Collection<T> values;

    Builder(List<String> columns, Collection<T> values) {
      super(columns);
      this.values = values;
    }

    @Override
    public InSpecification<S, T> build() {
      return new InSpecification<>(this);
    }

    @Override
    protected Builder<S, T> self() {
      return this;
    }
  }

  private InSpecification(Builder<S, T> builder) {
    super(builder);
    this.values = builder.values;
  }
}
