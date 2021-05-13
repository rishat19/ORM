package ru.kpfu.itis.orm.criteria;

public class CriteriaBuilder {

    public CriteriaCreate criteriaCreate() {
        return new CriteriaCreate();
    }

    public <T> CriteriaQuery<T> criteriaQuery(Class<T> entityClass) {
        return new CriteriaQuery<>(entityClass);
    }

    public <T> CriteriaSave<T> criteriaSave(T entity) {
        return new CriteriaSave<>(entity);
    }

    public <T> String getTableName(Class<T> entityClass) {
        return CriteriaUtils.getTableName(entityClass);
    }

    public Expression createExpression(String columnName, Sign sign, Object value) {
        return new Expression(columnName, sign, value);
    }

    public Selection createSelection(String columnName) {
        return new Selection(columnName);
    }

    public Selection createSelection(String columnName, Integer order) {
        return new Selection(columnName, order);
    }

    public Selection createSelection() {
        return Selection.selectAll();
    }

}
