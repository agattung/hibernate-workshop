package com.lps.commons.spring;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.io.Serializable;

@NoRepositoryBean
public interface LpsJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    T findOneForUpdate(ID id);

    EntityManager getEntityManager();

    boolean isLoaded(T instance);

    boolean evictFromMemory(T instance);
    
    void clear();
    
    T merge(T instance);
}
