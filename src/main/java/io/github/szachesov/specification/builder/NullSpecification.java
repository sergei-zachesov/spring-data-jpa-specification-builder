package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.*;

import java.util.List;

/**
 * @author Zachesov Sergei
 * @since 2023-11-09
 */
public class NullSpecification<S, T> extends AbstractSpecification<S, T> {

  @Override
  Predicate toCriteriaPredicate(
      Root<S> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

    Path<?> path = getPath(root, JoinType.LEFT);
    return isNot ? path.isNotNull() : path.isNull();
  }

  public static class Builder<S, T> extends AbstractSpecification.Builder<Builder<S, T>>
      implements ObjectBuilder<NullSpecification<S, T>> {

    Builder(List<String> columns) {
      super(columns);
    }

    @Override
    public NullSpecification<S, T> build() {
      return new NullSpecification<>(this);
    }

    @Override
    protected Builder<S, T> self() {
      return this;
    }
  }

  private NullSpecification(Builder<S, T> builder) {
    super(builder);
  }
}
