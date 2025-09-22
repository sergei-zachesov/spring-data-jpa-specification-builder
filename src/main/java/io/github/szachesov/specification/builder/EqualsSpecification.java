package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class EqualsSpecification<S> extends AbstractSpecification<S, Object> {

  private final Object value;

  @Override
  Predicate toCriteriaPredicate(
      Root<S> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Path<Object> path = getPath(root);

    return isNot ? criteriaBuilder.notEqual(path, value) : criteriaBuilder.equal(path, value);
  }

  public static class Builder<S> extends AbstractSpecification.Builder<Builder<S>>
      implements ObjectBuilder<EqualsSpecification<S>> {
    private final Object value;

    Builder(List<String> columns, Object value) {
      super(columns);
      this.value = value;
    }

    @Override
    public EqualsSpecification<S> build() {
      return new EqualsSpecification<>(this);
    }

    @Override
    protected Builder<S> self() {
      return this;
    }
  }

  private EqualsSpecification(Builder<S> builder) {
    super(builder);
    this.value = builder.value;
  }
}
