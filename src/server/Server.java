package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static List<Connection> clients = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // ������ ��������� �����
            System.out.println("������ �������.");
            while (true) { // ����������� ���� ��� �������� ����������� ��������
                System.out.println("������ ����������� ��������...");
                Socket socket = serverSocket.accept(); // ������� ����������� �������
                System.out.println("������ �����������");
                Connection user = new Connection(socket);
                clients.add(user);
                greetingsUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void greetingsUser(Connection user) {
        new Thread(() -> {
            String clientName;
            DataInputStream in = null;
            try {
                in = new DataInputStream(user.getSocket().getInputStream()); // ����� �����
                DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream()); // ����� ������
                out.writeUTF("������! ��� ��� �����?");
                clientName = in.readUTF(); // ��������� ��� �� �������
                user.setUserName(clientName);
                out.writeUTF("����� �������, " + clientName + "! ��������������� � ������� :)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage(user, in);
        }).start();
    }

    public static void sendMessage(Connection user, DataInputStream in) {
        String request;
        while (true) {
            try {
                request = in.readUTF(); // ��������� ��������� �� �������
                System.out.println(user.getUserName() + ": " + request);
                for (Connection x :
                        clients) {
                    if (x.getSocket() != user.getSocket()) { // ���������� �������� ������� ����������� � ��������� ������
                        DataOutputStream out = new DataOutputStream(x.getSocket().getOutputStream());
                        out.writeUTF(user.getUserName() + " �����: " + request); // ��������� �������� ��������� ���� �������� ����� ������
                    }
                }
            } catch (IOException ex) {
                removeDisconnectedUsers(user);
                break;
            }
        }
    }

    public static void removeDisconnectedUsers(Connection user) {
        for (Connection x : clients) { // ���������� �������� ������� ����������� � ��������� ������
            if (user != x) {
                try {
                    DataOutputStream out = new DataOutputStream(x.getSocket().getOutputStream());
                    out.writeUTF("������������ " + user.getUserName() + " ������� ���!"); // ��������� �������� ��������� ���� ��������
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        clients.remove(user); // �������� ������, ����� ������ ����������
    }

}
