package me.serbob.devroomsimpledungeon.DungeonSystem.Managers;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private Connection connection;
    private String tableName;
    private String dbName;
    public static DatabaseManager instance;
    public DatabaseManager(String dbUrl, String dbUser, String dbPassword, String dbName, String dbTableName) throws SQLException {
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        this.tableName = dbTableName;
        this.dbName = dbName;
        init();
    }

    public void init() throws SQLException {
        Statement dbStatement = connection.createStatement();
        dbStatement.execute("CREATE DATABASE IF NOT EXISTS " + dbName);
        Statement useStatement = connection.createStatement();
        useStatement.execute("USE " + dbName);
        Statement tableStatement = connection.createStatement();
        tableStatement.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (uuid VARCHAR(36) PRIMARY KEY, kills INT, deaths INT, sessions INT)");
    }
    public void close() throws SQLException {
        connection.close();
    }
    public void addPlayer(Player player) throws SQLException {
        Statement checkStatement = connection.createStatement();
        checkStatement.execute("SELECT * FROM " + tableName + " WHERE uuid = '" + player.getUniqueId() + "'");
        if (!checkStatement.getResultSet().next()) {
            Statement addStatement = connection.createStatement();
            addStatement.execute("INSERT INTO " + tableName + " (uuid, kills, deaths, sessions) VALUES ('" + player.getUniqueId() + "', 0, 0, 0)");
        }
    }

    public void addKill(Player player) throws SQLException {
        Statement addStatement = connection.createStatement();
        addStatement.execute("UPDATE " + tableName + " SET kills = kills + 1 WHERE uuid = '" + player.getUniqueId() + "'");
    }

    public void addDeath(Player player) throws SQLException {
        Statement addStatement = connection.createStatement();
        addStatement.execute("UPDATE " + tableName + " SET deaths = deaths + 1 WHERE uuid = '" + player.getUniqueId() + "'");
    }

    public void addSession(Player player) throws SQLException {
        Statement addStatement = connection.createStatement();
        addStatement.execute("UPDATE " + tableName + " SET sessions = sessions + 1 WHERE uuid = '" + player.getUniqueId() + "'");
    }

    public int getKills(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT kills FROM " + tableName + " WHERE uuid = '" + player.getUniqueId() + "'");
        statement.getResultSet().next();
        return statement.getResultSet().getInt("kills");
    }

    public int getDeaths(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT deaths FROM " + tableName + " WHERE uuid = '" + player.getUniqueId() + "'");
        statement.getResultSet().next();
        return statement.getResultSet().getInt("deaths");
    }

    public int getSessions(Player player) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("SELECT sessions FROM " + tableName + " WHERE uuid = '" + player.getUniqueId() + "'");
        statement.getResultSet().next();
        return statement.getResultSet().getInt("sessions");
    }
}
