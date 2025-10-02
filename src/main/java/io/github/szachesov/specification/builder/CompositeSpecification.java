/*
 * Copyright 2025 Sergei Zachesov and others.
 * https://github.com/sergei-zachesov/spring-data-jpa-specification-builder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.szachesov.specification.builder;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
 * An abstract aggregating class that describes the basic properties and behaviors of predicates.
 *
 * <p><a href="https://martinfowler.com/apsupp/spec.pdf">Specifications pattern</a>
 *
 * @param <T> the type of the {@link Root} the resulting {@literal Specification} operates on.
 * @param <P> target predicate type, maybe {@link Join}
 */
public abstract class CompositeSpecification<T, P> implements Specification<T> {

  @Setter(AccessLevel.PACKAGE)
  private boolean distinct = true;

  private final JoinType joinType;
  private final boolean isFetch;
  BooleanOperator connection;
  protected final List<String> columns;
  protected final boolean isNot;

  @Override
  public final Predicate toPredicate(
      final Root<T> root,
      @Nullable final CriteriaQuery<?> query,
      final CriteriaBuilder criteriaBuilder) {
    query.distinct(distinct);
    return toCriteriaPredicate(root, query, criteriaBuilder);
  }

  abstract Predicate toCriteriaPredicate(
      final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder criteriaBuilder);

  protected Path<P> getPath(final Root<T> root) {
    return getPath(root, joinType);
  }

  @SuppressWarnings("unchecked")
  protected Path<P> getPath(final Root<T> root, final JoinType joinType) {
    Path<P> path = null;
    From<?, ?> from = root;
    Class<?> javaType = root.getJavaType();

    for (String column : columns) {
      if (isObjectAssociation(column, javaType)) {
        Optional<Join<?, ?>> joinOpt = getJoin(root.getJoins(), column, joinType);

        from = joinOpt.isPresent() ? joinOpt.get() : joinFetch(from, column, joinType);
        javaType = from.getJavaType();

      } else if (isElementCollection(column, javaType)) {
        Optional<Join<?, ?>> joinOpt = getJoin(root.getJoins(), column, joinType);
        path = joinOpt.isPresent() ? (Path<P>) joinOpt.get() : from.join(column, joinType);
        break;
      } else {
        path = from.get(column);
        break;
      }
    }
    if (path == null) {
      path = (Path<P>) from;
    }

    return path;
  }

  private boolean isObjectAssociation(final String column, final Class<?> javaType) {
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

  private boolean isElementCollection(final String column, final Class<?> javaType) {
    Field[] fields = javaType.getDeclaredFields();
    Field field =
        Arrays.stream(fields).filter(f -> f.getName().equals(column)).findFirst().orElse(null);
    if (field == null) return false;

    return field.isAnnotationPresent(ElementCollection.class);
  }

  protected Join<?, ?> joinFetch(final From<?, ?> from, final String column, final JoinType type) {
    if (isFetch) {
      return (Join<?, ?>) from.fetch(column, type);
    } else {
      return from.join(column, type);
    }
  }

  private Optional<Join<?, ?>> getJoin(
      final Set<? extends Join<?, ?>> joins, final String column, final JoinType joinType) {
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
    private BooleanOperator connection = BooleanOperator.AND;
    private boolean isNot = false;
    private JoinType joinType = JoinType.INNER;
    private boolean isFetch = false;

    Builder(final List<String> columns) {
      this.columns = columns;
    }

    public BuilderT connection(final BooleanOperator connection) {
      this.connection = connection;
      return self();
    }

    public BuilderT join(final JoinType joinType) {
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

  protected <BuilderT extends Builder<BuilderT>> CompositeSpecification(
      final Builder<BuilderT> builder) {
    this.columns = builder.columns;
    this.connection = builder.connection;
    this.isNot = builder.isNot;
    this.joinType = builder.joinType;
    this.isFetch = builder.isFetch;
  }
}
