package ru.kpfu.itis.orm.criteria;

import ru.kpfu.itis.orm.annotations.Column;
import ru.kpfu.itis.orm.annotations.Id;
import ru.kpfu.itis.orm.annotations.Transient;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class CriteriaSave<T> {

    protected StringBuilder query;
    protected StringBuilder parameters;

    public CriteriaSave(T entity) {
        this.query = new StringBuilder("INSERT INTO ");
        this.parameters = new StringBuilder("(");
        saveEntity(entity);
    }

    protected void saveEntity(T entity) {
        String tableName = CriteriaUtils.getTableName(entity.getClass());
        query.append("\"").append(tableName).append("\" (");
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            saveField(field, entity);
        }
        query.deleteCharAt(query.length() - 1).deleteCharAt(query.length() - 1);
        parameters.deleteCharAt(parameters.length() - 1).deleteCharAt(parameters.length() - 1).append(")");
        query.append(") VALUES ").append(parameters.toString());
    }

    protected void saveField(Field field, T entity){
        if (field.getAnnotation(Transient.class) == null) {
            int modifiers = field.getModifiers();
            if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                Column column = field.getAnnotation(Column.class);
                Object result = CriteriaUtils.getFieldValue(entity, field.getName(), field.getType());
                if (field.getAnnotation(Id.class) != null) {
                    query.append("id, ");
                } else {
                    query.append((column != null && !column.name().equals("")) ? column.name() : field.getName().toLowerCase()).append(", ");
                }
                if (field.getType().getSimpleName().equals("String")) {
                    parameters.append("'").append(result).append("', ");
                } else {
                    parameters.append(result).append(", ");
                }
            }
        }
    }

    public String getSql() {
        return query.toString();
    }

}
