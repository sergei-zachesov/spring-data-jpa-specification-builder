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

package io.github.szachesov.specification.builder.sample.repository;

import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaRepository;
import com.cosium.spring.data.jpa.entity.graph.repository.EntityGraphJpaSpecificationExecutor;
import io.github.szachesov.specification.builder.sample.entity.Post;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository
    extends EntityGraphJpaRepository<Post, Integer>, EntityGraphJpaSpecificationExecutor<Post> {

  @Query(
      nativeQuery = true,
      value =
          """
                   select distinct
                   p1_0.id,
                   p1_0.author_id,
                   p1_0.content,
                   p1_0.created_at,
                   p1_0.rating,
                   p1_0.title,
                   p1_0.word_count
                   from posts p1_0 where p1_0.rating>1
                  """)
  List<Post> test();
}
