package ru.kpfu.itis.orm.database;

import ru.kpfu.itis.orm.criteria.Expression;

import java.util.List;

public interface DatabaseQueryExecutor {

    <T> void createTable(Class<T> entityClass);
    <T> List<T> findAll(Class<T> entityClass);
    <T, P> T findById(Class<T> entityClass, P id);
    <T> List<T> find(Class<T> entityClass, Expression expression);
    <T> void insert(Class<T> entityClass, T entity);
    //<T, P> void delete(Class<T> entityClass, P id);

}
