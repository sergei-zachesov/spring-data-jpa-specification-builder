package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
/**
 * Predicate of equal to(=).
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 */
public class EqualsSpecification<T> extends CompositeSpecification<T, Object> {

  private final Object value;

  @Override
  Predicate toCriteriaPredicate(
          Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    Path<Object> path = getPath(root);

    return isNot ? criteriaBuilder.notEqual(path, value) : criteriaBuilder.equal(path, value);
  }

  public static class Builder<S> extends CompositeSpecification.Builder<Builder<S>>
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

  private EqualsSpecification(Builder<T> builder) {
    super(builder);
    this.value = builder.value;
  }
}
