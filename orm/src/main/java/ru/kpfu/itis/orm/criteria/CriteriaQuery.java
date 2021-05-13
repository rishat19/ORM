package ru.kpfu.itis.orm.criteria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CriteriaQuery<T> {

    protected StringBuilder query;
    protected Expression expression;
    protected List<Selection> selections;
    protected Class<T> entityClass;

    public CriteriaQuery(Class<T> entityClass) {
        this.query = new StringBuilder("SELECT ");
        this.selections = new ArrayList<>();
        this.entityClass = entityClass;
    }

    public CriteriaQuery<T> select(Selection selection) {
        selections.add(selection);
        return this;
    }

    public CriteriaQuery<T> where(Expression restriction) {
        expression = new Expression(restriction);
        return this;
    }

    public CriteriaQuery<T> build() {
        if (selections.size() == 0) {
            Selection selection = Selection.selectAll();
            query.append(selection.getColumnName()).append(" ");
        } else {
            selections = selections.stream()
                    .sorted((Comparator.comparing(Selection::getOrder)))
                    .collect(Collectors.toList());
            for (Selection selection : selections) {
                query.append(selection.getColumnName()).append(", ");
            }
            query.deleteCharAt(query.length() - 1).deleteCharAt(query.length() - 1);
        }
        query.append(" FROM \"").append(CriteriaUtils.getTableName(entityClass)).append("\"");
        if (expression != null) {
            query.append(" WHERE ").append(expression.getSql());
        }
        return this;
    }

    public String getSql() {
        return query.toString();
    }

    public CriteriaQuery<T> multiselect(List<Selection> list) {
        this.selections = list;
        return this;
    }

    public CriteriaQuery<T> multiselect(Selection[] selections) {
        this.selections = Arrays.asList(selections);
        return this;
    }

}
