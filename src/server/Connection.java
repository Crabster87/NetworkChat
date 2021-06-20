package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

public class Connection {

    private Socket socket;
    private String userName;
    private UUID uuid;
    private DataInputStream in;
    private DataOutputStream out;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        this.userName = "";
        this.uuid = UUID.randomUUID();
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUserName() {
        return userName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }

}