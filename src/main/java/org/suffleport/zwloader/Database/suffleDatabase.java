package org.suffleport.zwloader.Database;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


/**
 *  Component создаёт bean со scope singleton — один объект на ApplicationContext (SINGLTON) используем чтобы подключитьяс к бд
 */
@Component
public class suffleDatabase {
    private DataSource dataSource;

    // по документации spring сам подставит сюда DataSource из настроек spring.datasource
    public CreateDatabase(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Метод @PostConstruct вызывается автоматически после создания бина.
     * Здесь мы инициализируем схему БД.
     */
    @PostConstruct
    public void init() {
        System.out.println("Колдуем над бд");

        try ( Connection connect = dataSource.getConnection();
              Statement st = connect.createStatement()) {



            System.out.println("Инициализация бд прошла успешно!");
        } catch (Exception e) {
            System.out.println("Ошибка");
            e.printStackTrace();
        }
    }
    private void createTypes(Statement st) throws SQLException {

    }
}
