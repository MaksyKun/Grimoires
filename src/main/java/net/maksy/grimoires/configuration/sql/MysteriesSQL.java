package net.maksy.grimoires.configuration.sql;

import com.zaxxer.hikari.HikariDataSource;
import net.maksy.grimoires.modules.book_management.storage.Grimoire;
import net.maksy.grimoires.modules.mysteries.DecryptionProcess;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MysteriesSQL {

    private final HikariDataSource dataSource;
    private final String TABLE = "Grimoires_Mysteries";

    public MysteriesSQL(HikariDataSource dataSource) {
        this.dataSource = dataSource;
        try {
            Connection connection = dataSource.getConnection();
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE + "("
                            + "UUID varchar(36),"
                            + "Id numeric,"
                            + "Process blob);")
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addProcess(UUID uuid, DecryptionProcess process) {
        if(hasProcess(uuid, process)) {
            updateProcess(uuid, process);
            return;
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insert = connection.prepareStatement("INSERT INTO " + TABLE + " VALUES(?, ?, ?)")) {
            insert.setString(1, uuid.toString());
            insert.setInt(2, process.grimoire.getId());
            insert.setBytes(3, SQLManager.serializeObject(process));
            insert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeProcess(UUID uuid, DecryptionProcess process) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM " + TABLE + " WHERE UUID=? AND Id=?")) {
            statement.setString(1, uuid.toString());
            statement.setInt(2, process.grimoire.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasProcess(UUID uuid, DecryptionProcess process) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE UUID=? AND Id=?")) {
            select.setString(1, uuid.toString());
            select.setInt(2, process.grimoire.getId());
            ResultSet result = select.executeQuery();
            if (result.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void updateProcess(UUID uuid, DecryptionProcess process) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement update = connection.prepareStatement("UPDATE " + TABLE + " SET Process=? WHERE UUID=? And Id=?")) {
            update.setBytes(1, SQLManager.serializeObject(process));
            update.setString(2, uuid.toString());
            update.setInt(3, process.grimoire.getId());
            update.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DecryptionProcess getProcess(Player player, Grimoire grimoire) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement select = connection.prepareStatement("SELECT * FROM " + TABLE + " WHERE UUID=? AND Id=?")) {
            select.setString(1, player.getUniqueId().toString());
            select.setInt(2, grimoire.getId());
            ResultSet result = select.executeQuery();
            if (result.next()) {
                return parseProcess(result);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DecryptionProcess(player, grimoire);
    }

    public DecryptionProcess parseProcess(ResultSet result) {
        try {
            byte[] bookBytes = result.getBytes("Process");
            return (DecryptionProcess) SQLManager.deserializeObject(bookBytes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
