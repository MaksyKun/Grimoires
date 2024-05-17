package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.maksy.grimoires.modules.storage.Genre;
import net.maksy.grimoires.modules.storage.Grimoire;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BooksSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_books";

    public BooksSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + "("
                            + "Id numeric PRIMARY KEY,"
                            + "Book blob);")
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBookCount() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT COUNT(Id) FROM " + TABLE)) {
           ResultSet result = select.executeQuery();
            if(result.next()) {
                return result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addBook(Grimoire book) {
        if(book.getId() > -1) {
            updateBook(book);
            return;
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insert = connection.prepareStatement("INSERT INTO " + TABLE + " VALUES(?, ?)")) {
            insert.setLong(1, getBookCount());
            insert.setBytes(2, serializeObject(book));
            insert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBook(Grimoire book) {
        if(book == null || book.getId() == -1) return;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM " + TABLE + " WHERE Id=?")) {
            statement.setInt(1, book.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Grimoire book) {
        if(book == null || book.getId() == -1) return;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement update = connection.prepareStatement("UPDATE " + TABLE + " SET Book=? WHERE Id=?")) {
            update.setBytes(1, serializeObject(book));
            update.setInt(2, book.getId());
            update.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Grimoire> getBooks(Genre genre, UUID author) {
        List<Grimoire> books = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT * FROM " + TABLE)) {
            ResultSet result = select.executeQuery();
            while (result.next()) {
                books.add(parseBook(result));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(genre != null || author != null) {
            if (genre != null && author != null) {
                books.forEach(book -> {
                    if(!book.getGenres().contains(genre) || !book.getAuthors().contains(author)) {
                        books.remove(book);
                    }
                });
            }
            else if (genre != null) {
                books.forEach(book -> {
                    if(!book.getGenres().contains(genre)) {
                        books.remove(book);
                    }
                });
            }
            else {
                books.forEach(book -> {
                    if(!book.getAuthors().contains(author)) {
                        books.remove(book);
                    }
                });
            }
        }
        return books;
    }

    public Grimoire getBook(int id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE Id=?")) {
            select.setInt(1, id);
            ResultSet result = select.executeQuery();
            if (result.next()) {
                return parseBook(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Grimoire parseBook(ResultSet result) {
        try {
            byte[] bookBytes = result.getBytes("Book");
            return (Grimoire) deserializeObject(bookBytes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] serializeObject(Object obj) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object deserializeObject(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais) {
                 @Override
                 protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                     String className = desc.getName();
                     // Translate old class names to new class names
                     if (className.equals("net.maksy.grimoires.Grimoire")) {
                         className = "net.maksy.grimoires.modules.storage.Grimoire";
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
}
