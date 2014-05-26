package com.lps.commons.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import javax.persistence.EntityManager;
import java.io.Serializable;

public class LpsRepositoryFactoryBean<R extends JpaRepository<T,I>, T, I extends Serializable> extends JpaRepositoryFactoryBean<R, T, I> {

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        return new LpsRepositoryFactory(entityManager);
    }

    private static class LpsRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {

        private EntityManager entityManager;

        public LpsRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @SuppressWarnings(value="unchecked")
        public LpsJpaRepositoryImpl<T, I> getTargetRepository(RepositoryMetadata repositoryMetadata) {
            return new LpsJpaRepositoryImpl<T, I>((Class <T>) repositoryMetadata.getDomainType(), entityManager);
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return LpsJpaRepositoryImpl.class;
        }
    }
}
