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
        boolean success = checkUserPassword(user);
        if(success) {
            this.updateAccountLastLoginDate(user);
        }
        return success;
    }

    public boolean checkUserPassword(User user) throws SQLException {

        // user is sent by client, we want to check,
        // if the password for this username is correct.

        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.GET_USER.query(
                        manager.getData().getDatabase()
                )
        );

        ps.setString(1, user.getUsername());
        ResultSet resultSet = ps.executeQuery();

        if(resultSet.next()) {

            String salt = resultSet.getString("salt");
            String password = resultSet.getString("password");

            user.setSalt(salt);
            return user.hash().equalsIgnoreCase(password);
        }
        return false;
    }

    public boolean updateAccountLastLoginDate(User user) throws SQLException {
        PreparedStatement ps = manager.getConnection().prepareStatement(
                Command.LOGIN.query(
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
                Command.GET_USER.query(
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
