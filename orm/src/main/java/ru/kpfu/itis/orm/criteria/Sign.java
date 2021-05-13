package ru.kpfu.itis.orm.criteria;

import ru.kpfu.itis.orm.exceptions.OrmException;

public enum Sign {

    EQUALS("="),
    MORE_THAN(">"),
    LESS_THAN("<"),
    MORE_OR_EQUALS_THAN(">="),
    LESS_OR_EQUALS_THAN("<="),
    IN("IN");

    protected String value;

    Sign(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
