package ru.kpfu.itis.orm.database;

import ru.kpfu.itis.orm.criteria.CriteriaBuilder;
import ru.kpfu.itis.orm.criteria.Expression;

import java.util.List;

public class EntityManager {

    private final DatabaseQueryExecutor databaseQueryExecutor;

    public EntityManager() {
        databaseQueryExecutor = new DatabaseQueryExecutorImpl(new CriteriaBuilder());
    }

    public <T> void createTable(Class<T> entityClass) {
        databaseQueryExecutor.createTable(entityClass);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        return databaseQueryExecutor.findAll(entityClass);
    }

    public <T, P> T findById(Class<T> entityClass, P id) {
        return databaseQueryExecutor.findById(entityClass, id);
    }

    public <T> List<T> find(Class<T> entityClass, Expression expression) {
        return databaseQueryExecutor.find(entityClass, expression);
    }

    public <T> void insert(Class<T> entityClass, T entity) {
        databaseQueryExecutor.insert(entityClass, entity);
    }

    //public <T, P> void delete(Class<T> entityClass, P id) {
    //    databaseQueryExecutor.delete(entityClass, id);
    //}

}
