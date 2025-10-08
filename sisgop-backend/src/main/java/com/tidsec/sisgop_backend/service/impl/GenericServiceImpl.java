package com.tidsec.sisgop_backend.service.impl;

import com.tidsec.sisgop_backend.exception.ModelNotFoundException;
import com.tidsec.sisgop_backend.repository.IGenericRepository;
import com.tidsec.sisgop_backend.service.IGenericService;

import java.lang.reflect.Method;
import java.util.List;

public abstract class GenericServiceImpl<T, ID> implements IGenericService<T, ID> {

    protected abstract IGenericRepository<T, ID> getRepo();
    @Override
    public List<T> findAll() {
        return getRepo().findByStatusNot(0);    }

    @Override
    public T findById(ID id) throws Exception {
        T entity = getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID NOT FOUND: " + id));
        try {
            Integer status = (Integer) entity.getClass().getMethod("getStatus").invoke(entity);
            if (status == 0) {
                throw new ModelNotFoundException("ID NOT FOUND: " + id);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error processing status field: " + e.getMessage());
        }
        return entity;
    }

    @Override
    public T save(T t) {
        return getRepo().save(t);
    }

    @Override
    public T update(T t, ID id) throws Exception {
        getRepo().findById(id).orElseThrow(() -> new ModelNotFoundException("ID NOT FOUND: " + id));
        Class<?> clazz = t.getClass();
        String className = clazz.getSimpleName();

        String methodName = "setId" + className;
        Method setIdMethod = clazz.getMethod(methodName, id.getClass());
        setIdMethod.invoke(t, id);

        return getRepo().save(t);
    }

    @Override
    public boolean delete(ID id) throws Exception{
        T entity = getRepo().findById(id)
                .orElseThrow(() -> new ModelNotFoundException("ID NOT FOUND: " + id));
        try {
            Integer status = (Integer) entity.getClass().getMethod("getStatus").invoke(entity);
            if (status == 0) {
                throw new ModelNotFoundException("ID NOT FOUND: " + id);
            }
            entity.getClass().getMethod("setStatus", Integer.class).invoke(entity, 0);
        } catch (Exception e) {
            throw new IllegalStateException("Error processing status field: " + e.getMessage());
        }
        getRepo().save(entity);
        return true;
    }

    @Override
    public T saveWithSoftDeleteValidation(T t) throws Exception {
        throw new UnsupportedOperationException("This entity does not implement validation for soft delete");
    }

    @Override
    public boolean softDeleteTransactional(ID id) throws Exception {
        throw new UnsupportedOperationException("Transactional soft delete not supported for this entity");
    }
}