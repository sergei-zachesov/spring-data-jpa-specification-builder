package io.github.szachesov.specification.builder.sample.repository;

import io.github.szachesov.specification.builder.sample.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {}
