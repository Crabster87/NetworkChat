package server;

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
            try {
                user.getOut().writeUTF("������! ��� ��� �����?");
                clientName = user.getIn().readUTF(); // ��������� ��� �� �������
                user.setUserName(clientName);
                user.getOut().writeUTF("����� �������, " + clientName + "! ��������������� � ������� :)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage(user);
        }).start();
    }

    public static void sendMessage(Connection user) {
        while (true) {
            try {
                String request = user.getIn().readUTF(); // ��������� ��������� �� �������
                System.out.println(user.getUserName() + ": " + request);
                for (Connection x :
                        clients) {
                    if (x != user) { // ���������� �������� ������� ����������� � ��������� ������
                        x.getOut().writeUTF(user.getUserName() + " �����: " + request); // ��������� �������� ��������� ���� �������� ����� ������
                    }
                }
            } catch (IOException e) {
                removeDisconnectedUsers(user);
                break;
            }
        }
    }

    public static void removeDisconnectedUsers(Connection user) {
        for (Connection x : clients) { // ���������� �������� ������� ����������� � ��������� ������
            if (user != x) {
                try {
                    x.getOut().writeUTF("������������ " + user.getUserName() + " ������� ���!"); // ��������� �������� ��������� ���� ��������
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        clients.remove(user); // �������� �������, ����� ������ ����������
    }

}
