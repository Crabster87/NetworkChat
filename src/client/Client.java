package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8188); // Создаём сокет, для подключения к серверу
            System.out.println("Успешно подключен");
            getMessageFromServer(socket);
            printToConsoleAndSend(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getMessageFromServer(Socket socket) throws IOException {
        DataInputStream in = new DataInputStream(socket.getInputStream()); // Создаём поток, для приёма сообщений от сервера
        new Thread(() -> {
            String response;
            while (true) {
                try {
                    response = in.readUTF(); // Принимаем сообщение от сервера
                    System.out.println(response); //Печатаем на консоль принятое сообщение от сервера
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
            request = scanner.nextLine(); // Ждём сообщение от пользователя (из консоли)
            out.writeUTF(request); // Отправляем сообщение из консоли на сервер
        }
    }

}
