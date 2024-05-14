package net.maksy.grimoires.configuration.sql;

public enum DatabaseType {
    MYSQL("jdbc:mysql:", "MySQL"),
    MARIADB("jdbc:mariadb:", "MariaDB"),
    LOCALE(null, "Locale");

    private final String jdbcURL;
    private final String name;

    DatabaseType(String jdbcURL, String name) {
        this.jdbcURL = jdbcURL;
        this.name = name;
    }

    public String getJdbcUrl() { return jdbcURL; }

    public String getName() { return name; }
}
