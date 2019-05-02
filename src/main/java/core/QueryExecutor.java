package core;

import pojos.Command;
import pojos.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class QueryExecutor {

    private DBConnectionManager manager;

    public QueryExecutor(DBConnectionManager manager) {
        this.manager = manager;
    }

    // USERS

    public boolean createUser(User user) throws SQLException {
        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.CREATE_USER.query(
                        manager.getData().getDatabase()
                )
        );
        ps.setString(1, user.getUsername());
        ps.setString(2, user.hash());
        ps.setString(3, user.getSalt());
        return ps.executeUpdate() == 1;
    }

    public boolean loginUser(User user) throws SQLException {

        // TODO: actually test if the password is correct...


        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.UPDATE_USER_LOGIN_DATE.query(
                        manager.getData().getDatabase()
                )
        );
        ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(2, user.getUsername());
        return ps.executeUpdate() == 1;
    }

    public boolean changePassword(User user) throws SQLException {
        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.CHANGE_PASSWORD.query(
                        manager.getData().getDatabase()
                )
        );
        ps.setString(1, user.hash());
        ps.setString(2, user.getUsername());
        return ps.executeUpdate() == 1;
    }

    public boolean removeUser(User user) throws SQLException {
        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.REMOVE_USER.query(
                        manager.getData().getDatabase()
                )
        );
        ps.setString(1, user.getUsername());
        return ps.executeUpdate() == 1;
    }

    public Integer getUserId(User user) throws SQLException {
        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.GET_USER_ID.query(
                        manager.getData().getDatabase()
                )
        );
        ps.setString(1, user.getUsername());
        ResultSet rs = ps.executeQuery();
        return rs.next() ? rs.getInt("id") : null;
    }

    // CHARACTER

    public boolean createCharacter(Character character) throws SQLException {


        return false;
    }

}
