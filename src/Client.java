import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Scanner;

public class Client {
    private static clientRunnable client;
    public static String userName = "Anonymous";
    //Chatroom GUI
    private static JFrame mainWindow = new JFrame();
    private static JButton B_CONNECT = new JButton();
    private static JButton B_DISCONNECT = new JButton();
    private static JButton B_SEND = new JButton();
    private static JLabel L_Message = new JLabel();
    static JTextField TF_Message = new JTextField(20);
    private static JLabel L_Conversation = new JLabel();
    static JTextArea TA_Conversation = new JTextArea();
    private static JScrollPane SP_Conversation = new JScrollPane();
    private static JLabel L_ONLINE = new JLabel();
    static JList JL_ONLINE = new JList();
    static JScrollPane SP_ONLINE = new JScrollPane();
    private static JLabel L_LoggedInAsBox = new JLabel();
    //Login GUI
    static JFrame LoginWindow = new JFrame();
    static JTextField TF_Username = new JTextField(20);
    private static JButton B_ENTER = new JButton("ENTER");
    private static JLabel L_EnterUsername = new JLabel("Enter Username: ");
    private static JPanel P_Login = new JPanel();

    public static void main(String args[]) {
        buildMainWindow();
    }

    //This is what connects the client to the server
    public static void Connect() {
        try {
            final int PORT = 8090;
            final String HOST = "127.0.0.1";
            Socket s = new Socket(HOST, PORT);
            System.out.println("You are connected to: " + HOST);
            client = new clientRunnable(s);
            //send Name to add to online list
            PrintWriter OUT = new PrintWriter(s.getOutputStream());
            OUT.println(userName);
            OUT.flush();
            Thread thread = new Thread(client);
            thread.start();
        } catch (Exception e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Unable to connect to server");
            System.exit(0);
        }
    }

    //Initialize the GUI and build the login window
    public static void buildLoginWindow() {
        LoginWindow.setTitle("What's your name?");
        LoginWindow.setSize(400, 100);
        LoginWindow.setLocation(250, 200);
        LoginWindow.setResizable(false);
        P_Login = new JPanel();
        P_Login.add(L_EnterUsername);
        P_Login.add(TF_Username);
        P_Login.add(B_ENTER);
        LoginWindow.add(P_Login);

        loginAction();
        LoginWindow.setVisible(true);
    }

    //build the main chatroom window
    public static void buildMainWindow() {
        mainWindow.setTitle(userName + "'s Chat Box");
        mainWindow.setSize(750, 800);
        mainWindow.setLocation(320, 180);
        mainWindow.setResizable(false);
        configureMainWindow();
        MainWindowAction();
        mainWindow.setVisible(true);
        B_SEND.setEnabled(false);
        B_DISCONNECT.setEnabled(false);
        B_CONNECT.setEnabled(true);
    }

    //Initialize the GUI of the chatroom
    public static void configureMainWindow() {
        mainWindow.setBackground(new java.awt.Color(255, 255, 255));
        mainWindow.setSize(530, 520);
        mainWindow.getContentPane().setLayout(null);
        B_SEND.setBackground(new java.awt.Color(0, 0, 255));
        B_SEND.setForeground(new java.awt.Color(255, 255, 255));
        B_SEND.setText("SEND");
        mainWindow.getContentPane().add(B_SEND);
        B_SEND.setBounds(250, 400, 81, 25);

        B_DISCONNECT.setBackground(new java.awt.Color(0, 0, 255));
        B_DISCONNECT.setForeground(new java.awt.Color(255, 255, 255));
        B_DISCONNECT.setText("DISCONNECT");
        mainWindow.getContentPane().add(B_DISCONNECT);
        B_DISCONNECT.setBounds(10, 400, 110, 25);

        B_CONNECT.setBackground(new java.awt.Color(0, 0, 255));
        B_CONNECT.setForeground(new java.awt.Color(255, 255, 255));
        B_CONNECT.setText("CONNECT");
        B_CONNECT.setToolTipText("");
        mainWindow.getContentPane().add(B_CONNECT);
        B_CONNECT.setBounds(130, 400, 110, 25);

        L_Message.setText("Message: ");
        mainWindow.getContentPane().add(L_Message);
        L_Message.setBounds(10, 360, 60, 20);

        TF_Message.setForeground(new java.awt.Color(0, 0, 255));
        TF_Message.requestFocus();
        mainWindow.getContentPane().add(TF_Message);
        TF_Message.setBounds(70, 360, 260, 30);

        L_Conversation.setHorizontalAlignment(SwingConstants.CENTER);
        L_Conversation.setText("Conversation");
        mainWindow.getContentPane().add(L_Conversation);
        L_Conversation.setBounds(100, 10, 140, 16);

        TA_Conversation.setColumns(20);
        TA_Conversation.setFont(new java.awt.Font("Tahoma", 0, 12));
        TA_Conversation.setForeground(new java.awt.Color(0, 0, 255));
        TA_Conversation.setLineWrap(true);
        TA_Conversation.setRows(5);
        TA_Conversation.setEditable(false);

        SP_Conversation.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        SP_Conversation.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        SP_Conversation.setViewportView(TA_Conversation);
        mainWindow.getContentPane().add(SP_Conversation);
        SP_Conversation.setBounds(10, 30, 330, 320);

        L_ONLINE.setHorizontalAlignment(SwingConstants.CENTER);
        L_ONLINE.setText("Currently Online");
        L_ONLINE.setToolTipText("");
        mainWindow.getContentPane().add(L_ONLINE);
        L_ONLINE.setBounds(350, 10, 130, 16);

//        String [] TestNames = {"Bob","Sue","John","Anna"};
        JL_ONLINE.setForeground(new java.awt.Color(0, 0, 255));
//        JL_ONLINE.setListData(TestNames);

        SP_ONLINE.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        SP_ONLINE.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        SP_ONLINE.setViewportView(JL_ONLINE);
        mainWindow.getContentPane().add(SP_ONLINE);
        SP_ONLINE.setBounds(350, 30, 150, 320);

        L_LoggedInAsBox.setText("name");
        L_LoggedInAsBox.setHorizontalAlignment(SwingConstants.CENTER);
        L_LoggedInAsBox.setBackground(new Color(255,255,255));
        L_LoggedInAsBox.setForeground(new java.awt.Color(255, 0, 0));
        L_LoggedInAsBox.setBorder(BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        mainWindow.getContentPane().add(L_LoggedInAsBox);
        L_LoggedInAsBox.setBounds(350, 360, 150, 29);
    }

    //Set the actions of each button in the chatroom window
    public static void MainWindowAction() {
        B_SEND.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Action_B_SEND();
            }
        });
        B_DISCONNECT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Action_B_DISCONNECT();
            }
        });
        B_CONNECT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                buildLoginWindow();
            }
        });
        mainWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                try {
                    if (client != null) {
                        client.DISCONNECT();
                    }
                    System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    //Set the login action and allow user to connect to server socket
    public static void loginAction() {
        B_ENTER.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!TF_Username.getText().equals("")) {
                    userName = TF_Username.getText().trim();
                    L_LoggedInAsBox.setText(userName);
                    mainWindow.setTitle(userName + "'s Chat Box");
                    LoginWindow.setVisible(false);
                    B_SEND.setEnabled(true);
                    B_DISCONNECT.setEnabled(true);
                    B_CONNECT.setEnabled(false);
                    Connect();
                } else {
                    JOptionPane.showMessageDialog(null, "Please enter a name");
                }
            }
        });
    }

    //What to do when the send button is clicked
    public static void Action_B_SEND() {
        if (!TF_Message.getText().equals("")) {
            client.SEND(TF_Message.getText());
            TF_Message.requestFocus();
        }
    }

    //What to do when the disconnect button is clicked
    public static void Action_B_DISCONNECT() {
        try {
            client.DISCONNECT();
        } catch (Exception e) {
            System.out.print(e);
        }
    }
}

class clientRunnable implements Runnable{
    private Socket SOCKET;
    private Scanner INPUT;
    private PrintWriter OUT;

    public clientRunnable(Socket s){
        this.SOCKET = s;
    }

    @Override
    public void run() {
        try{
            try{
                INPUT = new Scanner(SOCKET.getInputStream());
                OUT = new PrintWriter(SOCKET.getOutputStream());
                OUT.flush();
                checkStream();
            }
            finally {
                SOCKET.close();
            }
        }catch (Exception e){
            System.out.print(e);
        }
    }

    //Handles disconnecting from the server
    public void DISCONNECT() throws IOException {
        //appending #?! lets the server know that this is a command and not plain text.
        OUT.println("#?!"+ Client.userName+" has disconnected");
        OUT.flush();
        SOCKET.close();
        JOptionPane.showMessageDialog(null,"You have disconected");
        System.exit(0);
    }

    //Watches to see if the Server says anything
    public void checkStream(){
        while (true){
            RECEIVE();
        }
    }

    //Handles receiving messages from the server
    public void RECEIVE(){
        if(INPUT.hasNext()){
            String MESSAGE = INPUT.nextLine();
            System.out.println(MESSAGE);
            if(MESSAGE.contains("/#!")){
                String history = MESSAGE.substring(3);
                Client.TA_Conversation.append(history+"\n");
            }
            else if(MESSAGE.contains("#?!")){
                String TEMP1 = MESSAGE.substring(3);
                TEMP1= TEMP1.replace("[","");
                TEMP1 = TEMP1.replace("]","");
                String[] CurrentUsers = TEMP1.split(", ");
                Client.JL_ONLINE.setListData(CurrentUsers);
            }
            else{

                Client.TA_Conversation.append(MESSAGE+"\n");
            }
        }
    }

    //What to do when sending a message
    public void SEND(String text){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        OUT.println(Client.userName+"["+hours+":"+minutes+"]: "+text);
        OUT.flush();
        Client.TF_Message.setText("");
    }

}