package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {

    private static Map<Socket, String> clientSockets = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // Создаёи серверный сокет
            while (true) { // бесконечный цикл для ожидания подключения клиентов
                System.out.println("Сервер запущен. Ожидаю подключения клиентов...");
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
                connection(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void connection(Socket socket) {
        new Thread(() -> {
            String clientName = null;
            DataInputStream in = null;
            try {
                in = new DataInputStream(socket.getInputStream()); // Поток ввода
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Поток вывода
                out.writeUTF("Привет! Как вас зовут?");
                clientName = in.readUTF(); // Принимает имя от клиента
                clientSockets.put(socket, clientName);
                out.writeUTF("Очень приятно, " + clientName + "! Присоединяйтесь к общению :)");
            } catch (IOException e) {
            }
            sendMessage(socket, clientName, in);
        }).start();
    }

    public static void sendMessage(Socket socket, String clientName, DataInputStream in) {
        String request;
        while (true) {
            try {
                request = in.readUTF(); // Принимает сообщение от клиента
                System.out.println(clientName + ": " + request);
                for (Map.Entry<Socket, String> entry : clientSockets.entrySet()) { // Перебираем клиентов которые подключенны в настоящий момент
                    if (entry.getKey() != socket) {
                        DataOutputStream out = new DataOutputStream(entry.getKey().getOutputStream());
                        out.writeUTF(clientName + " пишет: " + request); // Рассылает принятое сообщение всем клиентам кроме автора
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                clientSockets.remove(socket); // Удаление сокета, когда клиент отключился
                break;
            }
        }
    }
}
