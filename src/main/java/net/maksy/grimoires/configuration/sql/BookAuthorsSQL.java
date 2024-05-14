package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class BookAuthorsSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_book_authors";

    public BookAuthorsSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + "("
                            + "BookId numeric,"
                            + "Author varchar(36));")
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
