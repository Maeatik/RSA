import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegistrationClient extends JFrame implements ActionListener
{

    private JTextArea jtaTextAreaMessage;
    private Container c;
    private JLabel name;
    private JLabel bit;
    private JTextField tbit;
    private JTextField tname;
    private JButton btn;
    public static String nameOfUser;
    public static int LengthOfNumBit;

    public RegistrationClient()
    {
        setBounds(600, 300, 500, 300);
        setTitle("Регистрация пользователя");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        c = getContentPane();
        c.setLayout(null);

        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);

        //поле ввода для имени
        name = new JLabel("Имя пользователя");
        name.setFont(new Font("Arial", Font.PLAIN, 12));
        name.setSize(150, 20);
        name.setLocation(70, 50);
        c.add(name);

        tname = new JTextField();
        tname.setFont(new Font("Arial", Font.PLAIN, 12));
        tname.setSize(190, 20);
        tname.setLocation(200, 50);
        c.add(tname);

        //поле ввода для размера длины бита
        bit = new JLabel("Длина бита");
        bit.setFont(new Font("Arial", Font.PLAIN, 12));
        bit.setSize(100, 20);
        bit.setLocation(100, 100);
        c.add(bit);

        tbit = new JTextField();
        tbit.setFont(new Font("Arial", Font.PLAIN, 12));
        tbit.setSize(190, 20);
        tbit.setLocation(200, 100);
        c.add(tbit);

        btn = new JButton("Регистрация");
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setSize(150, 20);
        btn.setLocation(200, 200);
        btn.addActionListener(this);

        c.add(btn);

        setVisible(true);
    }
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btn)
        {
            nameOfUser = tname.getText();
            LengthOfNumBit = Integer.parseInt(tbit.getText());
            ClientWindow clientWindow = new ClientWindow(true, LengthOfNumBit);
            clientWindow.setClientName(nameOfUser);
            clientWindow.createPanel();
            setVisible(false);
        }
    }
}
