import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
    public static ArrayList<Socket> sockets = new ArrayList<>();
    public static ArrayList<String> users = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        try {
            final int PORT = 8090;
            ServerSocket SERVER = new ServerSocket(PORT);
            System.out.println("Waiting for clients...");

            while (true) {
                Socket SOCKET = SERVER.accept();
                sockets.add(SOCKET);
                System.out.println("clientRunnable connection from: " + SOCKET.getLocalAddress().getHostName());
                addUserName(SOCKET);
                serverRunnable server = new serverRunnable(SOCKET);
                Thread t = new Thread(server);
                t.start();
            }

        } catch (Exception e) {
            System.out.println(e + " SERVER");

        }

    }

    //When a new user enters the chat add them to everyone's client
    public static void addUserName(Socket s) throws IOException {
        Scanner INPUT = new Scanner(s.getInputStream());
        String UserName = INPUT.nextLine();
        users.add(UserName);
        for (int i = 0; i < Server.sockets.size(); i++) {
            Socket TEMP_S = Server.sockets.get(i);
            PrintWriter OUT = new PrintWriter(TEMP_S.getOutputStream());
            //append #?! to let the client this is the user list
            OUT.println("#?!" + users);
            OUT.flush();
        }
       printChatHistory(Server.sockets.get(Server.sockets.size()-1));
    }

    //when a new user enters the chat load the chat history to their client
    public static void printChatHistory(Socket tempHistory) throws IOException {
        PrintWriter OUT = new PrintWriter(tempHistory.getOutputStream());
        BufferedReader reader;
        try{
            reader = new BufferedReader(new FileReader("./chat.txt"));
            String line = reader.readLine();
            while(line !=null){
                System.out.println(line);
                //append /#! to let the user know this is the chat history
                OUT.println("/#!"+ line);
                OUT.flush();
                line = reader.readLine();
            }
            reader.close();
        }catch (Exception e){

        }

    }
}

class serverRunnable implements Runnable {
    private Socket SOCKET;
    private String MESSAGE = "";
    private FileWriter fileWriter = new FileWriter("chat.txt", true);
    private int temp;

    public serverRunnable(Socket s) throws IOException {
        this.SOCKET = s;
    }

    @Override
    public void run() {
        try {
            try {
                Scanner INPUT = new Scanner(SOCKET.getInputStream());
                PrintWriter OUT = new PrintWriter(SOCKET.getOutputStream());
                while (true) {
                    //check if any users have left the server
                    if (temp != Server.sockets.size()) {
                        checkUsers();
                    }
                    temp = Server.sockets.size();

                    if (!INPUT.hasNext()) {
                        return;
                    }

                    MESSAGE = INPUT.nextLine();
                    writeToFile();
                    removeSocket();
                    sendToClients();


                }
            } finally {
                SOCKET.close();
            }
        } catch (Exception e) {
            System.out.print(e + " SERVER_RETURN");
        }
    }

    //Check if any user has left the chatroom
    private void checkUsers() throws IOException {
        for (int i = 0; i < Server.sockets.size(); i++) {
            Socket TEMP_S = Server.sockets.get(i);
            PrintWriter OUT = new PrintWriter(TEMP_S.getOutputStream());
            System.out.println(Server.users);
            OUT.println("#?!" + Server.users);
            OUT.flush();
        }
    }

    //write a log of the chatroom
    private void writeToFile() {
        System.out.println("clientRunnable said " + MESSAGE);
        try {
            if (!MESSAGE.equals("") && !MESSAGE.contains("#?!")) {
                System.out.println(MESSAGE);
                fileWriter.append(MESSAGE).append("\n");
                fileWriter.flush();
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    //Send messages to each client
    private void sendToClients() throws IOException {
        for (int i = 0; i < Server.sockets.size(); i++) {
            Socket TEMP_SOCKET = Server.sockets.get(i);
            PrintWriter TEMP_OUT = new PrintWriter(TEMP_SOCKET.getOutputStream());
            TEMP_OUT.println(MESSAGE);
            TEMP_OUT.flush();
            System.out.println("Sent to: " + TEMP_SOCKET.getLocalAddress().getHostName());
        }
    }

    //remove socket and username when user disconnects
    private void removeSocket() throws IOException {
        if (MESSAGE.contains("#?!")) {
            MESSAGE = MESSAGE.substring(3);
            for (int i = 0; i < Server.sockets.size(); i++) {
                Socket TEMP_S = Server.sockets.get(i);
                PrintWriter OUT = new PrintWriter(TEMP_S.getOutputStream());
                OUT.println("#?!" + Server.users);
                OUT.flush();
                if (Server.sockets.get(i).getPort() == SOCKET.getPort()) {
                    Server.sockets.remove(i);
                    Server.users.remove(i);

                }
            }

        }
    }
}
