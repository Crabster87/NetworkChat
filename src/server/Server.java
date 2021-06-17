package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    public static void main(String[] args) {
        Map<Socket, String> clientSockets = new ConcurrentHashMap<>();
        try {
            ServerSocket serverSocket = new ServerSocket(8188); // Создаёи серверный сокет
            System.out.println("Сервер запущен");
            while (true) { // бесконечный цикл для ожидания подключения клиентов
                System.out.println("Ожидаю подключения клиентов...");
                Socket socket = serverSocket.accept(); // Ожидаем подключения клиента
                System.out.println("Клиент подключился");
                DataInputStream in = new DataInputStream(socket.getInputStream()); // Поток ввода
                DataOutputStream out = new DataOutputStream(socket.getOutputStream()); // Поток вывода
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String request;
                        String clientName = null;
                        try {
                            out.writeUTF("Привет! Как вас зовут?");
                            clientName = in.readUTF(); // Принимает имя от клиента
                            clientSockets.put(socket, clientName);
                            out.writeUTF("Очень приятно, " + clientName + "! Присоединяйтесь к общению :)");
                        } catch (IOException e) {
                        }
                        while (true) {
                            try {
                                request = in.readUTF(); // Принимает сообщение от клиента
                                System.out.println(clientName + ": " + request);
                                for (Map.Entry<Socket, String> entry : clientSockets.entrySet()) { // Перебираем клиентов которые подключенны в настоящий момент
                                    if (entry.getKey() != socket) {
                                        DataOutputStream out = new DataOutputStream(entry.getKey().getOutputStream());
                                        out.writeUTF(clientName + " пишет: " + request);
                                    } // Рассылает принятое сообщение всем клиентам кроме автора
                                }
                            } catch (IOException ex) {
                                ex.printStackTrace();
                                clientSockets.remove(socket); // Удаление сокета, когда клиент отключился
                                break;
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
