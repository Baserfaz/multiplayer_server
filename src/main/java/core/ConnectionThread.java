package core;

import pojos.Command;
import pojos.GenericResultPojo;
import pojos.User;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;

/**
 * This class represents a client connection.
 * It will handle all messaging between client and a server.
 * Starts a new thread that will die, when the user either logs out or the connection dies.
 */
public class ConnectionThread extends Thread {

    private Integer userId;

    private Socket socket;
    private DBConnectionManager manager;
    private QueryExecutor executor;

    public ConnectionThread(Socket socket, DBConnectionManager manager) {
        super();

        if(socket == null) {
            throw new IllegalStateException(
                    "Client socket cannot be null.");
        }

        this.manager = manager;
        this.executor = manager.getExecutor();
        this.socket = socket;
        this.run();
    }

    @Override
    public void run() {

        ObjectInputStream in;
        ObjectOutputStream out;

        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

        // server logic per client
        while(true) {

            try {

                boolean success = false;
                String message = null;
                Object o;

                try { o = in.readObject(); }
                catch (ClassNotFoundException e) { continue; }

                if(o == null) { continue; }

                GenericResultPojo pojo = new GenericResultPojo();

                if (o instanceof User) {

                    User user = (User) o;
                    Command command = user.getCommand();

                    switch (command) {
                        case LOGIN:
                            success = executor.loginUser(user);
                            if(success) { userId = executor.getUserId(user); }
                            break;
                        case CREATE_USER:
                            success = executor.createUser(user);
                            break;
                        case CHANGE_PASSWORD:
                            success = executor.changePassword(user);
                            break;
                        default:
                            message = "Unsupported command for given pojo.";
                    }

                    pojo.setSuccess(success);
                    pojo.setCommand(command);
                    pojo.setMessage(message);

                    // close thread
                    if (command.equals(Command.LOGOUT)) {
                        this.userId = null;
                        break;
                    }

                } else {
                    pojo.setSuccess(false);
                    pojo.setCommand(null);
                    pojo.setMessage("Failed to read object.");
                }

                // send pojo
                out.writeObject(pojo);

            } catch (SQLException e) {

                // catch SQL exception, and send error message to client
                try {
                    out.writeObject(new GenericResultPojo(e, null, false, null));
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            } catch (IOException e) {
                // Goes here if the client disconnects from the server,
                // we can then just kill this thread.
                break;
            }

            // save cpu cycles
            try { Thread.sleep(1000); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}
