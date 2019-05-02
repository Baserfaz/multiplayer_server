package core;

import java.sql.*;

/**
 * Connects the server to a database.
 * Creates an instance of query executor.
 */
public class DBConnectionManager {

    private final String DRIVER_NAME = "jdbc:mysql://";

    private QueryExecutor executor;
    private ConnectionData data;
    private java.sql.Connection connection;

    public DBConnectionManager(ConnectionData data) {
        this.executor = new QueryExecutor(this);
        this.data = data;
        this.connection = null;
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        this.connect();
    }

    public void connect() {

        if(this.connection != null) {
            this.disconnect();
        }

        try {
            this.connection = DriverManager.getConnection(
                    this.createUrl(data),
                    this.data.getUsername(),
                    this.data.getPassword());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try { this.connection.close(); }
        catch (SQLException e) { e.printStackTrace(); }
        this.connection = null;
    }

    private String createUrl(ConnectionData data) {
        StringBuilder b = new StringBuilder();
        b.append(DRIVER_NAME)
                .append(data.getHost()).append(":")
                .append(data.getPort()).append("/")
                .append(data.getDatabase())
                .append("?serverTimezone=GMT%2B2");
        return b.toString();
    }

    public QueryExecutor getExecutor() {
        return executor;
    }

    public ConnectionData getData() {
        return data;
    }

    public void setData(ConnectionData data) {
        this.data = data;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
