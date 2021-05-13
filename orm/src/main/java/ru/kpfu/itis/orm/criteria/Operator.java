package ru.kpfu.itis.orm.criteria;

import ru.kpfu.itis.orm.exceptions.OrmException;

public enum Operator {

    AND("AND"),
    OR("OR"),
    NOT("NOT");

    protected String value;

    Operator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
