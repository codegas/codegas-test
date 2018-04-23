package org.codegas.test.spock

import org.codegas.commons.domain.entity.DomainEntity
import org.codegas.service.api.Repository
import org.springframework.transaction.annotation.Transactional

import javax.inject.Inject
import java.util.stream.Collectors

@Transactional
abstract class RepositoryImplPersistenceTest<T extends DomainEntity> extends PersistenceTest {

   @Inject
   protected Repository<T> repository

   protected T testEntity

   def setup() {
      testEntity = testEntityManager.save(createTestEntity())
      testEntityManager.flush()
      testEntityManager.clear()
   }

   def "The Entity-under-test can be saved without throwing any Exceptions and will be present in our persistence mechanism"() {
      given: "An Entity"
      def entity = createTestEntity()

      when: "The Entity is saved"
      repository.add(entity)

      and: "We flush within our Transaction"
      testEntityManager.flush()

      then: "No Exception is thrown"
      notThrown(Exception.class)

      and: "It is present in the persistence mechanism"
      testEntityManager.contains(entity)
   }

   def "The Entity-under-test can be deleted by ID"() {
      when: "We remove our Test Entity by ID"
      repository.remove(testEntity.getId())

      then: "The Test Entity will be gone"
      !testEntityManager.contains(testEntity)
   }

   def "The Entity-under-test can be deleted directly"() {
      when: "We remove our Test Entity"
      repository.remove(testEntityManager.getManagedEntity(testEntity))

      then: "The Test Entity will be gone"
      !testEntityManager.contains(testEntity)
   }

   def "The Entity-under-test can be found by ID"() {
      expect:
      repository.get(testEntity.getId()) == testEntity
   }

   def "All instances of the Entity-under-test can be retrieved"() {
      when:
      def result = repository.get().collect(Collectors.toList())

      then:
      result.size() >= 1

      and:
      result.contains(testEntity)
   }

   abstract T createTestEntity()
}
