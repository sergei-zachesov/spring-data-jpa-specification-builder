package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;

/**
 * Predicate of equal to one of multiple possible values(IN).
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public class InSpecification<T, P> extends CompositeSpecification<T, P> {

  private final Collection<P> values;

  @Override
  Predicate toCriteriaPredicate(
          Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    return getPath(root).in(values);
  }

  public static class Builder<S, T> extends CompositeSpecification.Builder<Builder<S, T>>
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

  private InSpecification(Builder<T, P> builder) {
    super(builder);
    this.values = builder.values;
  }
}
