package ru.kpfu.itis.orm.criteria;

import ru.kpfu.itis.orm.exceptions.OrmException;

public class Expression {

    protected String columnName;
    protected Object value;
    protected Sign sign;
    protected StringBuilder sql;

    public Expression(String columnName, Sign sign, Object value) {
        this.columnName = columnName;
        this.sign = sign;
        this.value = value;
        this.sql = buildSql();
    }

    public Expression(Expression expression) {
        this.columnName = expression.columnName;
        this.sign = expression.sign;
        this.value = expression.value;
        this.sql = expression.sql;
    }

    public Expression merge(Operator operator, Expression expression) {
        sql.append(" ").append(operator.getValue()).append(" ").append(expression.getSql());
        return this;
    }

    public Expression bracket() {
        String oldSql = sql.toString();
        this.sql = new StringBuilder();
        sql.append("(").append(oldSql).append(")");
        return this;
    }

    public Expression negate() {
        String oldSql = sql.toString();
        this.sql = new StringBuilder();
        sql.append("NOT ").append(oldSql);
        return this;
    }

    public String getSql() {
        return sql.toString();
    }

    protected StringBuilder buildSql() {
        StringBuilder sql = new StringBuilder();
        sql.append("\"").append(columnName).append("\" ").append(sign.getValue()).append(" ");
        if (value.getClass().getSimpleName().equals("String")) {
            sql.append("\"").append(value).append("\"");
        } else {
            if (sign.equals(Sign.IN)) {
                try {
                    sql.append(convertArrayToString((Object[]) value));
                } catch (ClassCastException ex) {
                    throw new OrmException("\"IN\" operator requires array object.");
                }
            } else {
                sql.append(value);
            }
        }
        return sql;
    }

    protected String convertArrayToString(Object[] array) {
        StringBuilder result = new StringBuilder("(");
        for (Object o : array) {
            result.append(o.toString()).append(", ");
        }
        result.deleteCharAt(result.length() - 1).deleteCharAt(result.length() - 1);
        result.append(")");
        return result.toString();
    }

}
