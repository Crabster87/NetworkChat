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
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            new Thread(new Runnable() { // ������ �����, ��� ����� ��������� �� �������
                @Override
                public void run() {
                    String response;
                    while (true) {
                        try {
                            response = in.readUTF(); // ��������� ��������� �� �������
                            System.out.println(response); //�������� �� ������� �������� ��������� �� �������
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            Scanner scanner = new Scanner(System.in);
            String request;
            while (true) {
                request = scanner.nextLine(); // ��� ��������� �� ������������ (�� �������)
                out.writeUTF(request); // ���������� ��������� �� ������� �� ������
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
