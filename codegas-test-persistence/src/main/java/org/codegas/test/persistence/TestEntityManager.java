package org.codegas.test.persistence;

import org.codegas.commons.domain.entity.DomainEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.Callable;

public final class TestEntityManager {

   private final EntityManager entityManager;

   private final TransactionTemplate transactionTemplate;

   public TestEntityManager(EntityManager entityManager, TransactionTemplate transactionTemplate) {
      this.entityManager = entityManager;
      this.transactionTemplate = transactionTemplate;
   }

   public <T> T save(T entity) {
      return runInTransaction(new Callable<T>() {
         @Override
         public T call() throws Exception {
            entityManager.persist(entity);
            return entity;
         }
      });
   }

   public void flush() {
      entityManager.flush();
   }

   public void clear() {
      entityManager.clear();
   }

   public boolean contains(Object entity) {
      return entityManager.contains(entity);
   }

   public List query(String qlString) {
      return entityManager.createQuery(qlString).getResultList();
   }

   public <T extends DomainEntity> T getManagedEntity(T detachedEntity) {
      List<T> results = entityManager.createQuery(" SELECT entity" +
                                                  " FROM " + detachedEntity.getClass().getSimpleName() + " entity" +
                                                  " WHERE entity.id = :id")
                                     .setParameter("id", detachedEntity.getId().toJpaQueryObject())
                                     .getResultList();
      if (results.size() > 1) {
         throw new IllegalStateException("There was more than one result when trying to fetch a Managed Entity for this Detached Entity: " + detachedEntity);
      }
      else {
         return results.isEmpty() ? null : results.get(0);
      }
   }

   private <T> T runInTransaction(Callable<T> callable) {
      return transactionTemplate.execute(new TransactionCallback<T>() {
         @Override
         public T doInTransaction(TransactionStatus status) {
            try {
               return callable.call();
            }
            catch (Exception e) {
               throw new IllegalStateException("Error running transaction in Integration Test.", e);
            }
         }
      });
   }
}
