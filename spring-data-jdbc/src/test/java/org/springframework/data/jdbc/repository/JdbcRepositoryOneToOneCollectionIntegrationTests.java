/*
 * Copyright 2019-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jdbc.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.jdbc.testing.DatabaseType;
import org.springframework.data.jdbc.testing.EnabledOnDatabase;
import org.springframework.data.jdbc.testing.IntegrationTest;
import org.springframework.data.jdbc.testing.TestConfiguration;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testing one-to-one relations with different id-types using {@link MappedCollection} annotation in Entities.
 *
 * @author Johan Blomgren
 */
@IntegrationTest
@EnabledOnDatabase(DatabaseType.POSTGRES)
public class JdbcRepositoryOneToOneCollectionIntegrationTests {

    @Configuration
    @Import(TestConfiguration.class)
    static class Config {

        @Bean
        DummyEntityRepository dummyEntityRepository(JdbcRepositoryFactory factory) {
            return factory.getRepository(DummyEntityRepository.class);
        }

    }

    @Autowired
    DummyEntityRepository repository;

    @Test // GH-1684
    public void saveAndLoadAnEntity() {

        DummyEntity entity = repository.save(createDummyEntity());

        assertThat(repository.findById(entity.getId())).hasValueSatisfying(it -> {
            assertThat(it.getId()).isEqualTo(entity.getId());
            assertThat(it.getTest()).isEqualTo(entity.getTest());
            assertThat(it.getDummyEntity2().getId()).isEqualTo(entity.getDummyEntity2().getId());
        });
    }

    private static DummyEntity createDummyEntity() {
        DummyEntity entity = new DummyEntity();
        entity.setTest("test");

        final DummyEntity2 dummyEntity2 = new DummyEntity2();

        entity.setDummyEntity2(dummyEntity2);

        return entity;
    }

    interface DummyEntityRepository extends CrudRepository<DummyEntity, UUID> {
    }


    static class DummyEntity implements Persistable<UUID> {

        @Id
        UUID id = UUID.randomUUID();

        @MappedCollection(idColumn = "dummy_entity_id")
        DummyEntity2 dummyEntity2;

        String test;

        // Simply setting it to new not having to add `DEFAULT uuid_generate_v4()` in db-script
        @Override
        public boolean isNew() {
            return true;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public DummyEntity2 getDummyEntity2() {
            return dummyEntity2;
        }

        public void setDummyEntity2(DummyEntity2 dummyEntity2) {
            this.dummyEntity2 = dummyEntity2;
        }

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    static class DummyEntity2 {
        @Id
        Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
