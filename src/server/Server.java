package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static void main(String[] args) {
        Map<Socket, String> clientSockets = new ConcurrentHashMap<>();
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // ������ ��������� �����
            System.out.println("������ �������");
            while (true) { // ����������� ���� ��� �������� ����������� ��������
                System.out.println("������ ����������� ��������...");
                Socket socket = serverSocket.accept(); // ������� ����������� �������
                System.out.println("������ �����������");
                DataInputStream in = new DataInputStream(socket.getInputStream()); // ����� �����
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // ����� ������
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String request;
                        String clientName = null;
                        try {
                            out.writeUTF("������! ��� ��� �����?");
                            clientName = in.readUTF(); // ��������� ��� �� �������
                            clientSockets.put(socket, clientName);
                            out.writeUTF("����� �������, " + clientName + "! ��������������� � ������� :)");
                        } catch (IOException e) {
                        }
                        while (true) {
                            try {
                                request = in.readUTF(); // ��������� ��������� �� �������
                                System.out.println(clientName + ": " + request);
                                for (Map.Entry<Socket, String> entry : clientSockets.entrySet()) { // ���������� �������� ������� ����������� � ��������� ������
                                    if (entry.getKey() != socket) {
                                        DataOutputStream out = new DataOutputStream(entry.getKey().getOutputStream());
                                        out.writeUTF(clientName + " �����: " + request);
                                    } // ��������� �������� ��������� ���� �������� ����� ������
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                clientSockets.remove(socket); // �������� ������, ����� ������ ����������
                                break;
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
