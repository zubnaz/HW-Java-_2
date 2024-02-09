package org.example.utils;

import lombok.Getter;
import org.example.models.Category;
import org.example.models.Photo;
import org.example.models.Product;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.xml.catalog.Catalog;

public class HibernateUtil {
    @Getter
    private static SessionFactory sessionFactory;

    static {
        try {
            Configuration conf = new Configuration().configure();
            conf.addAnnotatedClass(Category.class);
            conf.addAnnotatedClass(Product.class);
            conf.addAnnotatedClass(Photo.class);
            sessionFactory = conf.buildSessionFactory();
        } catch(Throwable ex) {
            System.out.println("Помилка підключення до БД! " + ex.getMessage());
        }
    }
}
