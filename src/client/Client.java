package client;

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

    /**
     * Method gets message from server in multithreading mode
     *
     * @param user (all users)
     */

    public static void getMessageFromServer(Connection user) throws IOException {
        new Thread(() -> {
            while (true) {
                try {
                    String response = user.getIn().readUTF(); // ��������� ��������� �� �������
                    System.out.println(response); // �������� �� ������� �������� ��������� �� �������
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Method reads message from users & sends them to server
     *
     * @param user (all users)
     */

    public static void printToConsoleAndSend(Connection user) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String request = scanner.nextLine(); // ��� ��������� �� ������������ (�� �������)
            user.getOut().writeUTF(request); // ���������� ��������� �� ������� �� ������
        }
    }

}

