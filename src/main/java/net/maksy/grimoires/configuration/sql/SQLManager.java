package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.configuration.Config;

import java.io.*;
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
    private final MysteriesSQL mysteriesSQL;

    public SQLManager() {
        connect();
        booksSQL = new BooksSQL(dataSource);
        mysteriesSQL = new MysteriesSQL(dataSource);
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


    public static byte[] serializeObject(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserializeObject(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais) {
                 @Override
                 protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                     String className = desc.getName();
                     // Translate old class names to new class names
                     if (className.equals("net.maksy.grimoires.Grimoire") || className.equals("net.maksy.grimoires.modules.storage.Grimoire")) {
                         className = "net.maksy.grimoires.modules.book_management.storage.Grimoire";
                     }
                     try {
                         return Class.forName(className, false, getClass().getClassLoader());
                     } catch (ClassNotFoundException ex) {
                         return super.resolveClass(desc);
                     }
                 }
             }) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public BooksSQL books() {
        return booksSQL;
    }

    public MysteriesSQL mysteries() {
        return mysteriesSQL;
    }
}
