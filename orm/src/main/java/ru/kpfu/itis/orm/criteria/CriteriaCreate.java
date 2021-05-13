package ru.kpfu.itis.orm.criteria;

import ru.kpfu.itis.orm.annotations.Column;
import ru.kpfu.itis.orm.annotations.Id;
import ru.kpfu.itis.orm.annotations.Transient;
import ru.kpfu.itis.orm.exceptions.OrmException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class CriteriaCreate {

    protected StringBuilder query;

    public CriteriaCreate() {
        this.query = new StringBuilder("CREATE ");
    }

    public <T> CriteriaCreate createTable(Class<T> entityClass) {
        Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.getAnnotation(Id.class) != null)
                .findAny()
                .orElseThrow(() -> new OrmException("There is no id field, use @Id annotation."));
        query.append("TABLE IF NOT EXISTS ").append(CriteriaUtils.getTableName(entityClass)).append("(");
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            createColumnForField(field);
        }
        query.deleteCharAt(query.length() - 2);
        query.append(")");
        return this;
    }

    protected void createColumnForField(Field field) {
        if (field.getAnnotation(Transient.class) == null) {
            int modifiers = field.getModifiers();
            if (!Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers)) {
                Column column = field.getAnnotation(Column.class);
                if (field.getAnnotation(Id.class) != null) {
                    query.append("\"id\"");
                } else {
                    query.append("\"")
                            .append((column != null && !column.name().equals("")) ? column.name() : field.getName().toLowerCase())
                            .append("\"");
                }
                query.append(" ");
                query.append(CriteriaUtils.getFieldType(field.getType().getSimpleName()));
                if (field.getAnnotation(Id.class) != null) {
                    query.append(" PRIMARY KEY");
                }
                query.append(", ");
            }
        }
    }

    public String getSql() {
        return query.toString();
    }

}
