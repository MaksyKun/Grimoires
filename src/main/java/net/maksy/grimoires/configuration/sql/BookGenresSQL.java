package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class BookGenresSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_book_genres";

    public BookGenresSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + "("
                            + "BookId numeric,"
                            + "Genre varchar(50));")
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
