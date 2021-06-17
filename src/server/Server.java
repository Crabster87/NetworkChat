package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<Socket, String> clientSockets = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // ������ ��������� �����
            while (true) { // ����������� ���� ��� �������� ����������� ��������
                System.out.println("������ �������. ������ ����������� ��������...");
                Socket socket = serverSocket.accept(); // ������� ����������� �������
                System.out.println("������ �����������");
                connection(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connection(Socket socket) {
        new Thread(() -> {
            String clientName = null;
            DataInputStream in = null;
            try {
                in = new DataInputStream(socket.getInputStream()); // ����� �����
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // ����� ������
                out.writeUTF("������! ��� ��� �����?");
                clientName = in.readUTF(); // ��������� ��� �� �������
                clientSockets.put(socket, clientName);
                out.writeUTF("����� �������, " + clientName + "! ��������������� � ������� :)");
            } catch (IOException e) {
            }
            sendMessage(socket, clientName, in);
        }).start();
    }

    public static void sendMessage(Socket socket, String clientName, DataInputStream in) {
        String request;
        while (true) {
            try {
                request = in.readUTF(); // ��������� ��������� �� �������
                System.out.println(clientName + ": " + request);
                for (Map.Entry<Socket, String> entry : clientSockets.entrySet()) { // ���������� �������� ������� ����������� � ��������� ������
                    if (entry.getKey() != socket) {
                        DataOutputStream out = new DataOutputStream(entry.getKey().getOutputStream());
                        out.writeUTF(clientName + " �����: " + request); // ��������� �������� ��������� ���� �������� ����� ������
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                clientSockets.remove(socket); // �������� ������, ����� ������ ����������
                break;
            }
        }
    }
}
