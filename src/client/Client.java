package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8188); // ������ �����, ��� ����������� � �������
            System.out.println("������� ���������");
            getMessageFromServer(socket);
            printToConsoleAndSend(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getMessageFromServer(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream()); // ������ �����, ��� ����� ��������� �� �������
        new Thread(() -> {
            String response;
            while (true) {
                try {
                    response = in.readUTF(); // ��������� ��������� �� �������
                    System.out.println(response); //�������� �� ������� �������� ��������� �� �������
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void printToConsoleAndSend(Socket socket) throws IOException {
        Scanner scanner = new Scanner(System.in);
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String request;
        while (true) {
            request = scanner.nextLine(); // ��� ��������� �� ������������ (�� �������)
            out.writeUTF(request); // ���������� ��������� �� ������� �� ������
        }
    }

}
