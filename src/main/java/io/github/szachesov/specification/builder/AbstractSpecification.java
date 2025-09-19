package io.github.szachesov.specification.builder;

import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;

/**
 * @param <S> the source type
 * @param <T> the target type
 */
public abstract class AbstractSpecification<S, T> implements Specification<S> {

  @Setter(AccessLevel.PACKAGE)
  private boolean distinct = true;

  private final JoinType joinType;
  private final boolean isFetch;
  LogicalConnection connection;
  protected final List<String> columns;
  protected final boolean isNot;

  @Override
  public final Predicate toPredicate(
      Root<S> root, @Nullable CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
    query.distinct(distinct);
    return toCriteriaPredicate(root, query, criteriaBuilder);
  }

  abstract Predicate toCriteriaPredicate(
      Root<S> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder);

  protected Path<T> getPath(Root<S> root) {
    return getPath(root, joinType);
  }

  @SuppressWarnings("unchecked")
  protected Path<T> getPath(Root<S> root, JoinType joinType) {
    Path<T> path = null;
    From<?, ?> from = root;
    Class<?> javaType = root.getJavaType();

    for (String column : columns) {
      if (isObjectAssociation(column, javaType)) {
        Optional<Join<?, ?>> joinOpt = getJoin(root.getJoins(), column, joinType);

        from = joinOpt.isPresent() ? joinOpt.get() : joinFetch(from, column, joinType);
        javaType = from.getJavaType();

      } else if (isElementCollection(column, javaType)) {
        Optional<Join<?, ?>> joinOpt = getJoin(root.getJoins(), column, joinType);
        path = joinOpt.isPresent() ? (Path<T>) joinOpt.get() : from.join(column, joinType);
        break;
      } else {
        path = from.get(column);
        break;
      }
    }
    if (path == null) {
      path = (Path<T>) from;
    }

    return path;
  }

  private boolean isObjectAssociation(String column, Class<?> javaType) {
    Field[] fields = javaType.getDeclaredFields();
    Field field =
        Arrays.stream(fields).filter(f -> f.getName().equals(column)).findFirst().orElse(null);
    if (field == null) {
      return false;
    }
    return field.isAnnotationPresent(OneToOne.class)
        || field.isAnnotationPresent(ManyToOne.class)
        || field.isAnnotationPresent(OneToMany.class)
        || field.isAnnotationPresent(ManyToMany.class);
  }

  private boolean isElementCollection(String column, Class<?> javaType) {
    Field[] fields = javaType.getDeclaredFields();
    Field field =
        Arrays.stream(fields).filter(f -> f.getName().equals(column)).findFirst().orElse(null);
    if (field == null) {
      return false;
    }
    return field.isAnnotationPresent(ElementCollection.class);
  }

  protected Join<?, ?> joinFetch(From<?, ?> from, String column, JoinType type) {
    if (isFetch) {
      return (Join<?, ?>) from.fetch(column, type);
    } else {
      return from.join(column, type);
    }
  }

  private Optional<Join<?, ?>> getJoin(
      Set<? extends Join<?, ?>> joins, String column, JoinType joinType) {
    if (joins == null || joins.isEmpty()) {
      return Optional.empty();
    }

    Optional<Join<?, ?>> result = Optional.empty();
    for (Join<?, ?> join : joins) {
      if (join.getAttribute().getName().equals(column)) {
        if (join.getJoinType().equals(joinType)) {
          return Optional.of(join);
        } else if (join.getJoinType() == JoinType.LEFT && joinType == JoinType.INNER) {
          return Optional.of(join);
        }
      }
      result = getJoin(join.getJoins(), column, joinType);
      if (result.isPresent()) {
        return result;
      }
    }

    return result;
  }

  abstract static class Builder<BuilderT extends Builder<BuilderT>> {

    protected final List<String> columns;
    private LogicalConnection connection = LogicalConnection.AND;
    private boolean isNot = false;
    private JoinType joinType = JoinType.INNER;
    private boolean isFetch = false;

    Builder(List<String> columns) {
      this.columns = columns;
    }

    public BuilderT connection(LogicalConnection connection) {
      this.connection = connection;
      return self();
    }

    public BuilderT join(JoinType joinType) {
      this.joinType = joinType;
      return self();
    }

    public BuilderT fetch() {
      this.isFetch = true;
      return self();
    }

    public BuilderT not() {
      this.isNot = true;
      return self();
    }

    protected abstract BuilderT self();
  }

  protected <BuilderT extends Builder<BuilderT>> AbstractSpecification(Builder<BuilderT> builder) {
    this.columns = builder.columns;
    this.connection = builder.connection;
    this.isNot = builder.isNot;
    this.joinType = builder.joinType;
    this.isFetch = builder.isFetch;
  }
}
