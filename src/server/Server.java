package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
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
            DataInputStream in = null;
            try {
                in = new DataInputStream(user.getSocket().getInputStream()); // Поток ввода
                DataOutputStream out = new DataOutputStream(user.getSocket().getOutputStream()); // Поток вывода
                out.writeUTF("Привет! Как вас зовут?");
                clientName = in.readUTF(); // Принимает имя от клиента
                user.setUserName(clientName);
                out.writeUTF("Очень приятно, " + clientName + "! Присоединяйтесь к общению :)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage(user, in);
        }).start();
    }

    public static void sendMessage(Connection user, DataInputStream in) {
        String request;
        while (true) {
            try {
                request = in.readUTF(); // Принимает сообщение от клиента
                System.out.println(user.getUserName() + ": " + request);
                for (Connection x :
                        clients) {
                    if (x.getSocket() != user.getSocket()) { // Перебираем клиентов которые подключенны в настоящий момент
                        DataOutputStream out = new DataOutputStream(x.getSocket().getOutputStream());
                        out.writeUTF(user.getUserName() + " пишет: " + request); // Рассылает принятое сообщение всем клиентам кроме автора
                    }
                }
            } catch (IOException ex) {
                removeDisconnectedUsers(user);
                break;
            }
        }
    }

    public static void removeDisconnectedUsers(Connection user) {
        for (Connection x : clients) { // Перебираем клиентов которые подключенны в настоящий момент
            if (user != x) {
                try {
                    DataOutputStream out = new DataOutputStream(x.getSocket().getOutputStream());
                    out.writeUTF("Пользователь " + user.getUserName() + " покинул чат!"); // Рассылает принятое сообщение всем клиентам
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        clients.remove(user); // Удаление сокета, когда клиент отключился
    }

}
