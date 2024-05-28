package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.maksy.grimoires.Grimoires;
import net.maksy.grimoires.modules.book_management.storage.Genre;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BooksSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_Books";

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
             PreparedStatement select = connection.prepareStatement("SELECT COUNT(*) FROM " + TABLE)) {
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
        int count = getBookCount();
        book.setId(count);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insert = connection.prepareStatement("INSERT INTO " + TABLE + " VALUES(?, ?)")) {
            insert.setLong(1, count);
            insert.setBytes(2, SQLManager.serializeObject(book));
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
            update.setBytes(1, SQLManager.serializeObject(book));
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
            Grimoire grimoire = (Grimoire) SQLManager.deserializeObject(bookBytes);
            grimoire.setId(result.getInt("Id"));
            return grimoire;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
