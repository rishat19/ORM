package ru.kpfu.itis.orm.criteria;

public class Selection {

    protected String columnName;
    protected Integer order;

    public Selection(String columnName) {
        this.columnName = columnName;
        this.order = -1;
    }

    public Selection(String columnName, Integer order) {
        this.columnName = columnName;
        this.order = order;
    }

    public static Selection selectAll() {
        return new Selection("*");
    }

    public String getColumnName() {
        return columnName;
    }

    public Integer getOrder() {
        return order;
    }

}
