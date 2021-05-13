package ru.kpfu.itis.orm.criteria;

import ru.kpfu.itis.orm.annotations.Column;
import ru.kpfu.itis.orm.annotations.Id;
import ru.kpfu.itis.orm.annotations.Table;
import ru.kpfu.itis.orm.annotations.Transient;
import ru.kpfu.itis.orm.exceptions.OrmException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CriteriaUtils {

    public static <T> String getTableName(Class<T> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        String name = (table == null) ? "" : table.name();
        if (name.equals(name.toLowerCase()) && !name.contains(" ")) {
            return (name.equals("")) ? entityClass.getSimpleName().toLowerCase() : name;
        } else {
            throw new OrmException("Incorrect table name, don't use uppercase or spaces");
        }
    }

    public static String getFieldType(String fieldType) {
        String type;
        switch (fieldType) {
            case "byte":
            case "short":
            case "int":
            case "Byte":
            case "Short":
            case "Integer":
                type = "INTEGER";
                break;
            case "long":
            case "Long":
                type = "BIGINT";
                break;
            case "boolean":
            case "Boolean":
                type = "BOOLEAN";
                break;
            case "float":
            case "Float":
                type = "DOUBLE PRECISION";
                break;
            case "double":
            case "Double":
                type = "REAL";
                break;
            case "String":
                type = "VARCHAR";
                break;
            default:
                throw new OrmException("Cannot use " + fieldType + " type.");
        }
        return type;
    }

    public static Object getFieldValue(Object entity, String fieldName, Class<?> fieldType) {
        Method[] methods = entity.getClass().getDeclaredMethods();
        String methodName = "get" + fieldName;
        methodName = methodName.toLowerCase();
        Object result = null;
        boolean isValue = false;
        for (Method method : methods) {
            if (method.getName().toLowerCase().equals(methodName) && method.getReturnType().equals(fieldType)) {
                try {
                    result = method.invoke(entity);
                } catch (Exception ex) {
                    throw new OrmException("Failed to get the field. Check your getter methods.", ex);
                }
                isValue = true;
            }
        }
        if (isValue) {
            return result;
        } else {
            throw new OrmException("Getter for " + fieldName + " field is not exist.");
        }
    }

    public static <T> T getEntityFromResultSet(Class<T> entityClass, ResultSet resultSet) {
        try {
            T entity = entityClass.getConstructor().newInstance();
            Field[] fields = entity.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(Transient.class)
                        && (field.isAnnotationPresent(Column.class) || field.isAnnotationPresent(Id.class))) {
                    String methodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
                    Method method = entityClass.getMethod(methodName, field.getType());
                    method.invoke(entity, resultSet.getObject(field.getName()));
                }
            }
            return entity;
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (SQLException ex) {
            throw new OrmException("Entity format error.");
        }
    }

}
