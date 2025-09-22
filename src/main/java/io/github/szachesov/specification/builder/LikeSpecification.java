package io.github.szachesov.specification.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.Locale;
import lombok.Getter;

public class LikeSpecification<S> extends AbstractSpecification<S, String> {

  private String value;
  private final boolean isIgnoreCase;
  private final Wildcard wildcard;
  @Getter private final int minChar;

  @Override
  Predicate toCriteriaPredicate(Root<S> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
    Path<String> path = getPath(root);

    Expression<String> expression;
    if (isIgnoreCase) {
      expression = builder.upper(path);
      value = value.toUpperCase(Locale.ROOT);
    } else {
      expression = path;
    }

    return builder.like(expression, wildcard.getWithWildcard().apply(value));
  }

  public static class Builder<S> extends AbstractSpecification.Builder<Builder<S>>
      implements ObjectBuilder<LikeSpecification<S>> {

    private final String value;
    private boolean isIgnoreCase = true;
    private Wildcard wildcard = Wildcard.ABSENCE;
    private int minChar = 3;

    Builder(List<String> columns, String value) {
      super(columns);
      this.value = value;
    }

    public Builder<S> ignoreCase() {
      this.isIgnoreCase = true;
      return this;
    }

    public Builder<S> wildcard(Wildcard wildcard) {
      this.wildcard = wildcard;
      return this;
    }

    public Builder<S> minChar(int minChar) {
      this.minChar = minChar;
      return this;
    }

    @Override
    public LikeSpecification<S> build() {
      return new LikeSpecification<>(this);
    }

    @Override
    protected Builder<S> self() {
      return this;
    }
  }

  private LikeSpecification(Builder<S> builder) {
    super(builder);
    this.value = builder.value;
    this.isIgnoreCase = builder.isIgnoreCase;
    this.wildcard = builder.wildcard;
    this.minChar = builder.minChar;
  }
}
