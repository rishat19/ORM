package ru.kpfu.itis.orm;

import ru.kpfu.itis.orm.criteria.CriteriaBuilder;
import ru.kpfu.itis.orm.criteria.Operator;
import ru.kpfu.itis.orm.criteria.Sign;
import ru.kpfu.itis.orm.database.EntityManager;
import ru.kpfu.itis.orm.entities.User;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        User user1 = new User((long) 1, "Rishat", "rishat@gmail.com", 9, true);
        User user2 = new User((long) 2, "Alina", "alina@gmail.com", 7, true);
        User user3 = new User((long) 3, "Alexander", "alex@gmail.com", 5, false);
        User user4 = new User((long) 4, "Azat", "azat@gmail.com", 3, true);

        EntityManager em = new EntityManager();

        em.createTable(User.class);
        System.out.println();

        em.insert(User.class, user1);
        em.insert(User.class, user2);
        em.insert(User.class, user3);
        em.insert(User.class, user4);
        System.out.println();

        System.out.println(em.findById(User.class, 2).toString());
        System.out.println();

        List<User> list = em.findAll(User.class);
        for (User user : list) {
            System.out.println(user.toString());
        }
        System.out.println();

        CriteriaBuilder criteriaBuilder = new CriteriaBuilder();
        list = em.find(
                User.class,
                criteriaBuilder
                        .createExpression("rating", Sign.MORE_OR_EQUALS_THAN, 9)
                        .bracket()
                        .negate()
                        .merge(Operator.AND, criteriaBuilder.createExpression("id", Sign.IN, new Integer[]{2, 4}))
        );
        for (User user : list) {
            System.out.println(user.toString());
        }

    }

}
