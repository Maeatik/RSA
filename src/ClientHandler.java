import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

    public class ClientHandler implements Runnable {

        Generate generate = new Generate();
        // экземпляр нашего сервера
        private static Server server;
        // исходящее сообщение
        private PrintWriter outMessage;
        // входящее собщение
        private Scanner inMessage;
        // клиентский сокет
        private Socket clientSocket = null;
        // количество клиента в чате, статичное поле
        private static int clients_count = 0;
        public static Key server_key;
        public static Key client_key;

        public int lengthOfBit;

        // конструктор, который принимает клиентский сокет и сервер
        public ClientHandler(Socket socket, Server server) {
            try {
                clients_count++;
                this.server = server;
                this.clientSocket = socket;
                this.outMessage = new PrintWriter(socket.getOutputStream());
                this.inMessage = new Scanner(socket.getInputStream());
                this.server_key = server.getKey();

                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                String line = dataInputStream.readUTF();
                System.out.println(line);
                lengthOfBit = Integer.parseInt(line);
                System.out.println(lengthOfBit);
                this.client_key = generate.generationKeys(lengthOfBit);
                System.out.println(client_key.E);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        // Переопределяем метод run(), который вызывается когда
        // мы вызываем new Thread(client).start();
        @Override
        public void run() {
            try {
//                while (true) {
//                    // сервер отправляет сообщение
//                    break;
//                }

                while (true) {
                    // Если от клиента пришло сообщение
                    if (inMessage.hasNext()) {
                        String message = inMessage.nextLine();
                        String clientMessage = generate.encryptionText(message, server_key);
                        // если клиент отправляет данное сообщение, то цикл прерывается и клиент выходит из чата
                        if (clientMessage.equalsIgnoreCase("##session##end##")) {
                            break;
                        }
                        System.out.println("---"+clientMessage);
                        // отправляем данное сообщение всем клиентам
                        server.sendMessageToAllClients(clientMessage);
                    }
                    Thread.sleep(100);
                }
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            finally {
                this.close();
            }
        }

        // отправляем сообщение
        public void sendMsg(String message) {
            try {
                String decMessageToClients = generate.decryptionText(message, client_key);
                outMessage.println(decMessageToClients);
                outMessage.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // клиент выходит из чата
        public void close() {
            // удаляем клиента из списка
            server.removeClient(this);
            clients_count--;
        }
}
