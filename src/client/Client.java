package client;

import server.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8188); // ������ �����, ��� ����������� � �������
            System.out.println("������� ���������");
            Connection user = new Connection(socket);
            getMessageFromServer(user);
            printToConsoleAndSend(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getMessageFromServer(Connection user) throws IOException {
        new Thread(() -> {
            while (true) {
                try {
                    String response = user.getIn().readUTF(); // ��������� ��������� �� �������
                    System.out.println(response); //�������� �� ������� �������� ��������� �� �������
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void printToConsoleAndSend(Connection user) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String request = scanner.nextLine(); // ��� ��������� �� ������������ (�� �������)
            user.getOut().writeUTF(request); // ���������� ��������� �� ������� �� ������
        }
    }

}
