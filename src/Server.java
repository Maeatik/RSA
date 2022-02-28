import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server
{
    static Generate generate = new Generate();

    public Key server_key;
    static final int PORT = 8080;

    // список клиентов, которые будут подключаться к серверу
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public Server()
    {
            server_key = generate.key;
            // сокет клиента, это некий поток, который будет подключаться к серверу по адресу и порту
            Socket clientSocket = null;
            ServerSocket serverSocket = null;
            try {
                // создаём серверный сокет на определенном порту
                serverSocket = new ServerSocket(PORT);
                System.out.println("Сервер запущен!");
                while (true) {
                    // ждём подключений от сервера
                    clientSocket = serverSocket.accept();
                    // создаём обработчик клиента, который подключился к серверу
                    ClientHandler client = new ClientHandler(clientSocket, this);
                    clients.add(client);
                    // каждое подключение клиента обрабатываем в новом потоке
                    new Thread(client).start();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    // закрываем подключение
                    clientSocket.close();
                    System.out.println("Сервер остановлен");
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

    }
    // отправляем сообщение всем клиентам
    public void sendMessageToAllClients(String msg)
    {
        for (ClientHandler o : clients)
        {
            String messageToClients = generate.decryptionText(msg, server_key);
            System.out.println(messageToClients);
            String EncMessageToClients = generate.encryptionText(messageToClients, o.client_key);
            o.sendMsg(EncMessageToClients);
        }

//        if ((!msg.contains("Новый участник вошёл в чат!")) && (!msg.contains("Клиентов в чате")))
//            for (ClientHandler o : clients)
//            {
//                String messageToClients = generate.decryptionText(msg, server_key);
//                System.out.println(messageToClients);
//                String EncMessageToClients = generate.encryptionText(messageToClients, o.client_key);
//                o.sendMsg(EncMessageToClients);
//            }
//        else
//            for (ClientHandler o : clients)
//            {
//                System.out.println(msg);
//                o.sendMsg(msg);
//            }
    }
    // удаляем клиента из коллекции при выходе из чата
    public void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    public Key getKey()
    {
        return this.server_key;
    }
}