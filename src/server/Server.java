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
            ServerSocket serverSocket = new ServerSocket(8188); // Создаёи серверный сокет
            System.out.println("Сервер запущен.");
            while (true) { // бесконечный цикл для ожидания подключения клиентов
                System.out.println("Ожидаю подключения клиентов...");
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
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
                user.getOut().writeUTF("Привет! Как вас зовут?");
                clientName = user.getIn().readUTF(); // Принимает имя от клиента
                user.setUserName(clientName);
                user.getOut().writeUTF("Очень приятно, " + clientName + "! Присоединяйтесь к общению :)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage(user);
        }).start();
    }

    public static void sendMessage(Connection user) {
        while (true) {
            try {
                String request = user.getIn().readUTF(); // Принимает сообщение от клиента
                System.out.println(user.getUserName() + ": " + request);
                for (Connection x :
                        clients) {
                    if (x != user) { // Перебираем клиентов которые подключенны в настоящий момент
                        x.getOut().writeUTF(user.getUserName() + " пишет: " + request); // Рассылает принятое сообщение всем клиентам кроме автора
                    }
                }
            } catch (IOException e) {
                removeDisconnectedUsers(user);
                break;
            }
        }
    }

    public static void removeDisconnectedUsers(Connection user) {
        for (Connection x : clients) { // Перебираем клиентов которые подключенны в настоящий момент
            if (user != x) {
                try {
                    x.getOut().writeUTF("Пользователь " + user.getUserName() + " покинул чат!"); // Рассылает принятое сообщение всем клиентам
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        clients.remove(user); // Удаление клиента, когда клиент отключился
    }

}
