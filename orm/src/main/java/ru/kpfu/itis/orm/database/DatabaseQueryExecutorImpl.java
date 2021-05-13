package ru.kpfu.itis.orm.database;

import ru.kpfu.itis.orm.criteria.*;
import ru.kpfu.itis.orm.exceptions.OrmException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseQueryExecutorImpl implements DatabaseQueryExecutor {

    protected CriteriaBuilder criteriaBuilder;

    public DatabaseQueryExecutorImpl(CriteriaBuilder criteriaBuilder) {
        this.criteriaBuilder = criteriaBuilder;
    }

    @Override
    public <T> void createTable(Class<T> entityClass) {
        CriteriaCreate criteriaCreate = criteriaBuilder.criteriaCreate().createTable(entityClass);
        System.out.println(criteriaCreate.getSql());
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(criteriaCreate.getSql())) {
            statement.execute();
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage());
        }
    }

    @Override
    public <T, P> T findById(Class<T> entityClass, P id) {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.criteriaQuery(entityClass)
                .where(criteriaBuilder.createExpression("id", Sign.EQUALS, id))
                .select(criteriaBuilder.createSelection())
                .build();
        System.out.println(criteriaQuery.getSql());
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(criteriaQuery.getSql())) {
            ResultSet resultSet = statement.executeQuery();
            T entity = null;
            if (resultSet.next()) {
                entity = CriteriaUtils.getEntityFromResultSet(entityClass, resultSet);
            }
            return entity;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage());
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.criteriaQuery(entityClass)
                .select(criteriaBuilder.createSelection())
                .build();
        System.out.println(criteriaQuery.getSql());
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(criteriaQuery.getSql())) {
            ResultSet resultSet = statement.executeQuery();
            List<T> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(CriteriaUtils.getEntityFromResultSet(entityClass, resultSet));
            }
            return entities;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage());
        }
    }

    @Override
    public <T> List<T> find(Class<T> entityClass, Expression expression) {
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.criteriaQuery(entityClass)
                .where(expression)
                .select(criteriaBuilder.createSelection())
                .build();
        System.out.println(criteriaQuery.getSql());
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(criteriaQuery.getSql())) {
            ResultSet resultSet = statement.executeQuery();
            List<T> entities = new ArrayList<>();
            while (resultSet.next()) {
                entities.add(CriteriaUtils.getEntityFromResultSet(entityClass, resultSet));
            }
            return entities;
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage());
        }
    }

    @Override
    public <T> void insert(Class<T> entityClass, T entity) {
        CriteriaSave<T> criteriaSave = criteriaBuilder.criteriaSave(entity);
        System.out.println(criteriaSave.getSql());
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(criteriaSave.getSql())) {
            statement.execute();
        } catch (SQLException ex) {
            throw new OrmException(ex.getMessage());
        }
    }

    /*
    @Override
    public <T> void createTable(Class<T> entityClass) throws SQLException {
        String sql = "CREATE TABLE " + getTableName(entityClass) + " ("
                + addFieldsNamesAndTypes(entityClass.getDeclaredFields()) + ")";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) throws SQLException {
        String sql = "SELECT * FROM " + getTableName(entityClass);
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<T> objects = new ArrayList<>();
            while (resultSet.next()) {
                objects.add(getObjectFromResultSet(entityClass, resultSet));
            }
            return objects;
        }
    }

    @Override
    public <T, P> T find(Class<T> entityClass, P id) throws NoSuchFieldException, SQLException {
        Field field = entityClass.getDeclaredField("id");
        String sql = "SELECT * FROM " + getTableName(entityClass) + " WHERE " + field.getName() + " = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            ResultSet resultSet = statement.executeQuery();
            T object = null;
            if (resultSet.next()) {
                object = getObjectFromResultSet(entityClass, resultSet);
            }
            return object;
        }
    }

    @Override
    public <T> void insert(Class<T> entityClass, T entity) throws IllegalAccessException, SQLException {
        String sql = "INSERT INTO " + getTableName(entityClass) + " (" + addFieldsNames(entityClass.getDeclaredFields())
                + ") " + "VALUES" + " (" + addFieldsValues(entityClass.getDeclaredFields(), entity) + ")";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.execute();
        }
    }

    @Override
    public <T, P> void delete(Class<T> entityClass, P id) throws SQLException, NoSuchFieldException {
        Field field = entityClass.getDeclaredField("id");
        String sql = "DELETE FROM " + getTableName(entityClass) + " WHERE " + field.getName() + " = ?";
        try (Connection connection = DatabaseConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, id);
            statement.execute();
        }
    }

    private <T>String getTableName(Class<T> entityClass) {
        String[] temp = entityClass.getName().split("\\.");
        return temp[temp.length - 1];
    }

    private String addFieldType(Field field) throws IllegalArgumentException {
        String type;
        if (Integer.class.equals(field.getType())) {
            type = "INT";
        } else if (Long.class.equals(field.getType())) {
            type = "BIGINT";
        } else if (String.class.equals(field.getType()) || Character.class.equals(field.getType())) {
            type = "VARCHAR";
        } else if (Boolean.class.equals(field.getType())) {
            type = "BOOLEAN";
        } else {
            throw new IllegalArgumentException("Unknown field.");
        }
        return type;
    }

    private String addFieldsNamesAndTypes(Field[] fields) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fields.length - 1; i++) {
            stringBuilder.append(fields[i].getName());
            stringBuilder.append(" ");
            stringBuilder.append(addFieldType(fields[i]));
            stringBuilder.append(", ");
        }
        stringBuilder.append(fields[fields.length - 1].getName());
        stringBuilder.append(" ");
        stringBuilder.append(addFieldType(fields[fields.length - 1]));
        return stringBuilder.toString();
    }

    private String addFieldsNames(Field[] fields) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fields.length - 1; i++) {
            stringBuilder.append(fields[i].getName());
            stringBuilder.append(", ");
        }
        stringBuilder.append(fields[fields.length - 1].getName());
        return stringBuilder.toString();
    }

    private String addFieldsValues(Field[] fields, Object object) throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < fields.length - 1; i++) {
            fields[i].setAccessible(true);
            stringBuilder.append("'").append(fields[i].get(object)).append("'");
            stringBuilder.append(",");
            fields[i].setAccessible(false);
        }
        fields[fields.length - 1].setAccessible(true);
        stringBuilder.append("'").append(fields[fields.length - 1].get(object)).append("'");
        fields[fields.length - 1].setAccessible(false);
        return stringBuilder.toString();
    }

    private <T> T getObjectFromResultSet(Class<T> entityClass, ResultSet resultSet) {
        T object;
        try {
            object = entityClass.getConstructor().newInstance();
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                field.set(object, resultSet.getObject(field.getName()));
                field.setAccessible(false);
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException | SQLException e) {
            throw new IllegalArgumentException(e);
        }
        return object;
    }
     */

}
