package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.maksy.grimoires.Genre;
import net.maksy.grimoires.Grimoire;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BooksSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_books";

    public BooksSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + "("
                            + "Id numeric PRIMARY KEY,"
                            + "Book blob,"
                            + "Published bool,"
                            + "PublishedOn numeric,"
                            + "Authors varchar(1000),"
                            + "Genres varchar(500));")
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getBookCount() {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(Id) FROM " + TABLE)) {
            return statement.executeQuery().getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void addBook(Grimoire book, boolean published) {
        String authorsString = book.getAuthors().stream().map(UUID::toString).reduce((a, b) -> a + "," + b).orElse("");
        String genresString = book.getGenres().stream().map(Genre::getId).reduce((a, b) -> a + "," + b).orElse("");
        if(book.getId() > -1) {
            updateBook(book, published, authorsString, genresString);
            return;
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insert = connection.prepareStatement("INSERT INTO " + TABLE + " VALUES(?, ?, ?, ?, ?, ?)")) {
            insert.setLong(1, getBookCount());
            // Add the Grimoire variable book as a blob than can be parsed back later
            Blob newB = connection.createBlob();
            newB.setBytes(1, book.toString().getBytes());
            insert.setBlob(2, newB);
            insert.setBoolean(3, published);
            insert.setLong(4, published ? System.currentTimeMillis() : -1);
            insert.setString(5, authorsString);
            insert.setString(6, genresString);
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

    public void updateBook(Grimoire book, boolean published, String authors, String genres) {
        if(book == null || book.getId() == -1) return;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement update = connection.prepareStatement("UPDATE " + TABLE + " SET Book=?, Published=?, PublishedOn=?, Authors=?, Genres=? WHERE Id=?")) {
            Blob newB = connection.createBlob();
            newB.setBytes(1, book.toString().getBytes());
            update.setBlob(1, newB);
            update.setBoolean(2, published);
            update.setLong(3, published ? System.currentTimeMillis() : -1);
            update.setString(4, authors);
            update.setString(5, genres);
            update.setInt(6, book.getId());
            update.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Grimoire> getBooks(Genre genre, UUID author) {
        StringBuilder whereClause = new StringBuilder();
        if(genre != null || author != null) {
            whereClause.append("WHERE ");
            // If both are not null, append both to the where clause through joins of the BookAuthorsSQL and BookGenresSQL tables.
            // Else If genre is not null, append the genre to the where clause.
            // Else, author is not null, append the author to the where clause.
            if (genre != null && author != null) whereClause.append("Genre LIKE '%").append(genre.getName()).append("%' AND Author LIKE '%").append(author).append("%'");
            else if (genre != null) whereClause.append("Genre LIKE '%").append(genre.getName()).append("%'");
            else whereClause.append("Author LIKE '%").append(author).append("%'");
        }

        List<Grimoire> books = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT * FROM " + TABLE + " " + whereClause)) {
            ResultSet result = select.executeQuery();
            while (result.next()) {
                books.add(parseBook(result));
            }
        } catch (SQLException e) {

        }
        return null;
    }

    public Grimoire parseBook(ResultSet result) {
        try {
            Blob bookBlob = result.getBlob("Book");
            byte[] bookBytes = bookBlob.getBytes(1, (int) bookBlob.length());
            Grimoire book = Grimoire.fromString(new String(bookBytes));

            book.setId(result.getInt("Id"));
            book.setAuthors(List.of(result.getString("Authors").split(",").stream().map(UUID::fromString).collect(Collectors.toList()));
            book.setGenres(List.of(result.getString("Genres").split(",").stream().map(Genre::fromId).collect(Collectors.toList()));
            return book;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
