package client;

import server.Connection;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("127.0.0.1", 8188); // Создаём сокет, для подключения к серверу
            System.out.println("Успешно подключен");
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
                    String response = user.getIn().readUTF(); // Принимаем сообщение от сервера
                    System.out.println(response); //Печатаем на консоль принятое сообщение от сервера
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void printToConsoleAndSend(Connection user) throws IOException {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String request = scanner.nextLine(); // Ждём сообщение от пользователя (из консоли)
            user.getOut().writeUTF(request); // Отправляем сообщение из консоли на сервер
        }
    }

}
