package com.lps.commons.spring;

import java.io.Serializable;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.lps.constants.LpsConstants;

@NoRepositoryBean
public class LpsJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements LpsJpaRepository<T, ID> {

    private Class<T> visibleDomainClass;
    private EntityManager visibleEntityManager;

    protected Logger logger;

    public LpsJpaRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.visibleEntityManager = entityManager;
        this.visibleDomainClass = entityInformation.getJavaType();
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public LpsJpaRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass,em);
        this.visibleEntityManager = em;
        this.visibleDomainClass = domainClass;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    public EntityManager getEntityManager() {
        return visibleEntityManager;
    }

    @Override
    public boolean isLoaded(T instance) {
        Session hibernateSession = getEntityManager().unwrap(Session.class);
        return hibernateSession.contains(instance);
    }

    @Override
    public boolean evictFromMemory(T instance) {
        if (isLoaded(instance)) {
            Session hibernateSession = getEntityManager().unwrap(Session.class);
            hibernateSession.evict(instance);
            return true;
        }
        return false;
    }


    public T findOneForUpdate(ID id) {

        String msg = "";
        if (logger.isTraceEnabled()) {
            msg = "findForUpdate(" + visibleDomainClass.getSimpleName() + "#" + id + ") ...";
        }
        try {
            T result = visibleEntityManager.find(visibleDomainClass, id, LockModeType.PESSIMISTIC_WRITE);
            if (logger.isTraceEnabled()) {
                try {
                    Method getVersionMethod = result.getClass().getMethod("getVersion", LpsConstants.EMPTY_CLASS_ARRAY);
                    Integer version = (Integer) getVersionMethod.invoke(result, LpsConstants.EMPTY_OBJECT_ARRAY);
                    logger.trace(msg + "success (version=" + version + ")");
                } catch(Exception e) {
                    logger.trace(msg + "success (version could not be identified)");
                }
            }
            return result;
        } catch(RuntimeException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(msg + "failed with message " + e.getMessage());
            }
            throw e;
        }
    }


}
