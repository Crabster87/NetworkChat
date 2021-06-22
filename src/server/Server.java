package server;

import client.Connection;

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
            ServerConsole.print("Сервер запущен.", MessageStatus.WAITING);
            while (true) { // бесконечный цикл для ожидания подключения клиентов
                ServerConsole.print("Ожидаю подключения клиентов...", MessageStatus.WAITING);
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                Connection user = new Connection(socket);
                ServerConsole.print("Клиент подключился", MessageStatus.ENTERING);
                clients.add(user);
                greetingsUser(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method sends greeting message to each user & receives user's name
     * @param user (all users)
     *  */

    public static void greetingsUser(Connection user) {
        new Thread(() -> {
            try {
                user.getOut().writeUTF("Привет! Как вас зовут?");
                String clientName = user.getIn().readUTF(); // Принимает имя от клиента
                user.setUserName(clientName);
                user.getOut().writeUTF("Очень приятно, " + clientName + "! Присоединяйтесь к общению :)");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sendMessage(user);
        }).start();
    }

    /**
     * Method sends & receives messages to / from every sides
     * @param user (all users)
     *  */

    public static void sendMessage(Connection user) {
        while (true) {
            try {
                String request = user.getIn().readUTF(); // Принимает сообщение от клиента
                ServerConsole.print(user.getUserName() + " пишет: " + request, MessageStatus.MESSAGING);
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

    /**
     * Method removes disconnected users & sends notifications to printer's console
     * @param user (all users)
     *  */

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
        ServerConsole.print("Пользователь " + user.getUserName() + " покинул чат!", MessageStatus.DISCONNECT);
        clients.remove(user); // Удаление клиента, когда клиент отключился
    }

}
