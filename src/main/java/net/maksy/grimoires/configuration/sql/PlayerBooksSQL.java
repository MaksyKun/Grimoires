package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerBooksSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_PlayerBooks";

    public PlayerBooksSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection connection = dataSource.getConnection()) {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + "("
                            + "PlayerUUID VARCHAR(36) NOT NULL,"
                            + "BookId numeric NOT NULL,"
                            + "PRIMARY KEY (PlayerUUID, BookId));")
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(UUID playerUUID, int bookId) {
        if (hasBook(playerUUID, bookId)) return;
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insert = connection.prepareStatement(
                     "INSERT INTO " + TABLE + " (PlayerUUID, BookId) VALUES(?, ?)")) {
            insert.setString(1, playerUUID.toString());
            insert.setInt(2, bookId);
            insert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeBook(UUID playerUUID, int bookId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "DELETE FROM " + TABLE + " WHERE PlayerUUID=? AND BookId=?")) {
            stmt.setString(1, playerUUID.toString());
            stmt.setInt(2, bookId);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasBook(UUID playerUUID, int bookId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement(
                     "SELECT COUNT(*) FROM " + TABLE + " WHERE PlayerUUID=? AND BookId=?")) {
            select.setString(1, playerUUID.toString());
            select.setInt(2, bookId);
            ResultSet result = select.executeQuery();
            if (result.next()) return result.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Integer> getBooks(UUID playerUUID) {
        List<Integer> books = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement(
                     "SELECT BookId FROM " + TABLE + " WHERE PlayerUUID=?")) {
            select.setString(1, playerUUID.toString());
            ResultSet result = select.executeQuery();
            while (result.next()) books.add(result.getInt("BookId"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}
