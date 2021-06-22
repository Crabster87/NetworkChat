package server;

import client.Connection;

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
            ServerConsole.print("������ �������.", MessageStatus.WAITING);
            while (true) { // ����������� ���� ��� �������� ����������� ��������
                ServerConsole.print("������ ����������� ��������...", MessageStatus.WAITING);
                Socket socket = serverSocket.accept(); // ������� ����������� �������
                Connection user = new Connection(socket);
                ServerConsole.print("������ �����������", MessageStatus.ENTERING);
                clients.add(user);
                greetingsUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method sends greeting message to each user & receives user's name
     * @param user (all users)
     *  */

    public static void greetingsUser(Connection user) {
        new Thread(() -> {
            try {
                user.getOut().writeUTF("������! ��� ��� �����?");
                String clientName = user.getIn().readUTF(); // ��������� ��� �� �������
                user.setUserName(clientName);
                user.getOut().writeUTF("����� �������, " + clientName + "! ��������������� � ������� :)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage(user);
        }).start();
    }

    /**
     * Method sends & receives messages to / from every sides
     * @param user (all users)
     *  */

    public static void sendMessage(Connection user) {
        while (true) {
            try {
                String request = user.getIn().readUTF(); // ��������� ��������� �� �������
                ServerConsole.print(user.getUserName() + " �����: " + request, MessageStatus.MESSAGING);
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

    /**
     * Method removes disconnected users & sends notifications to printer's console
     * @param user (all users)
     *  */

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
        ServerConsole.print("������������ " + user.getUserName() + " ������� ���!", MessageStatus.DISCONNECT);
        clients.remove(user); // �������� �������, ����� ������ ����������
    }

}
