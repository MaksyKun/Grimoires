package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.Config;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class SQLManager {
    private static final Config config = Grimoires.getConfiguration();

    private final HikariDataSource dataSource = new HikariDataSource();

    private static final DatabaseType dbType = config.getDatabaseType();
    private static final String host = config.getSQLHost();
    private static final String database = config.getSQLDatabase();
    private static final String username = config.getSQLUsername();
    private static final String password = config.getSQLPassword();
    private static final int port = config.getSQLPort();

    private final BooksSQL booksSQL;

    public SQLManager() {
        connect();
        booksSQL = new BooksSQL(dataSource);
    }

    public void connect() {
        if (dbType == DatabaseType.LOCALE) {
            File databaseFile = new File(Grimoires.getInstance().getDataFolder(), "Database.db");
            if (!databaseFile.exists()) {
                try {
                    databaseFile.createNewFile();
                } catch (IOException exception) {
                    Grimoires.getInstance().getLogger().log(Level.SEVERE, "Failed to created SQLite database.  Error: "
                            + exception.getMessage());
                }
            }
            dataSource.setPoolName("SQLiteConnectionPool");
            dataSource.setDriverClassName("org.sqlite.JDBC");
            dataSource.setJdbcUrl("jdbc:sqlite:" + databaseFile);
        } else {
            dataSource.setJdbcUrl(dbType.getJdbcUrl() + "//" + host + ":" + port + "/" + database);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
        }
    }

    public BooksSQL getBooksSQL() {
        return booksSQL;
    }
}
