package io.github.szachesov.specification.builder;

import org.springframework.data.jpa.domain.Specification;

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
