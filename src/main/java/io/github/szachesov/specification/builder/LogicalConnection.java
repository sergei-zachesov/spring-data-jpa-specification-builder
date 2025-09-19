package io.github.szachesov.specification.builder;

import org.springframework.data.jpa.domain.Specification;

/**
 * @author Zachesov Sergei
 * @since 2023-04-02
 */
public enum LogicalConnection {
  AND {
    @Override
    <T> Specification<T> connect(Specification<T> spec, Specification<T> connect) {
      return Specification.where(spec).and(connect);
    }
  },
  OR {
    @Override
    <T> Specification<T> connect(Specification<T> spec, Specification<T> connect) {
      return Specification.where(spec).or(connect);
    }
  };

  abstract <T> Specification<T> connect(Specification<T> spec, Specification<T> connect);
}
