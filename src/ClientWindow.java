import javax.accessibility.AccessibleContext;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;


public class ClientWindow extends JFrame
{
    static Generate generate = new Generate();

    private static final String SERVER_HOST = "localhost"; // порт
    private static final int SERVER_PORT = 8080; // клиентский сокет
    private Socket clientSocket;

    private Scanner inMessage; // входящее сообщение
    private PrintWriter outMessage; // исходящее сообщение

    // следующие поля отвечают за элементы формы
    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;

    private String clientName = "";
    private String startMessage = "Введите ваше сообщение: ";

    public static Key client_key;

    public OutputStream sendBit;

    public void setClientName(String clientName)
    {
        this.clientName = clientName;
    }

    public static Key getClient_key()
    {
        return client_key;
    }

    public ClientWindow(boolean flag, int lengthOfNumBit)//, Server server) // конструктор
    {
        if (flag) {
            try {

                clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
                inMessage = new Scanner(clientSocket.getInputStream());
                outMessage = new PrintWriter(clientSocket.getOutputStream());
                sendBit = clientSocket.getOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(sendBit);
                dataOutputStream.writeUTF(String.valueOf(lengthOfNumBit));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void reg()
    {
        RegistrationClient registrationClient = new RegistrationClient();
    }

    public void createPanel()
    {
        setBounds(600, 300, 600, 500);

        setTitle(clientName);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        JScrollPane jsp = new JScrollPane(jtaTextAreaMessage);
        add(jsp, BorderLayout.CENTER);

        // label, который будет отражать количество клиентов в чате
        JLabel jlNumberOfClients = new JLabel("Количество клиентов в чате: ");
        //add(jlNumberOfClients, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);

        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);

        jtfMessage = new JTextField(startMessage);
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);

        jtfName = new JTextField(clientName);
        jtfName.setEditable(false);
        bottomPanel.add(jtfName, BorderLayout.WEST);

        // обработчик события нажатия кнопки отправки сообщения
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // если имя клиента, и сообщение непустые, то отправляем сообщение
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    String message =  jtfName.getText() + ": " + jtfMessage.getText();
                    System.out.println(message);
                    sendMessage(message);

                    // фокус на текстовое поле с сообщением
                    jtfMessage.grabFocus();
                }
            }
        });
        // при фокусе поле сообщения очищается
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });

        // в отдельном потоке начинаем работу с сервером
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // бесконечный цикл
                    while (true) {
                        // если есть входящее сообщение
                        if (inMessage.hasNext()) {
                            // считываем его
                            String inMes = inMessage.nextLine();
                           //String s = generate.decryptionText(inMes, client_key);
                            String clientsInChat = "Клиентов в чате = ";

                            if (inMes.indexOf(clientsInChat) == 0) {
                                System.out.println(inMes);
                                jlNumberOfClients.setText(inMes);
                            } else {
                                if(!inMes.contains("Новый участник вошёл в чат!")) {
                                    // выводим сообщение
                                    jtaTextAreaMessage.append(inMes);
                                    // добавляем строку перехода
                                    jtaTextAreaMessage.append("\n");
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        // добавляем обработчик события закрытия окна клиентского приложения
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        setVisible(true);
    }

    public void sendMessage(String message) {
        // формируем сообщение для отправки на сервер
            String messageSTR = message;
            outMessage.println(messageSTR);
            outMessage.flush();
            jtfMessage.setText("");
        }
        // отправляем сообщение
}
