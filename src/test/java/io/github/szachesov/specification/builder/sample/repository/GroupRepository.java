package io.github.szachesov.specification.builder.sample.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import io.github.szachesov.specification.builder.sample.entity.Group;

public interface GroupRepository
    extends JpaRepository<Group, Integer>, JpaSpecificationExecutor<Group> {}
