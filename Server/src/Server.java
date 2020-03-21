import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 88;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            System.out.println("Server is listening on port " + PORT);
            System.out.println("Server ip address is " + InetAddress.getLocalHost().getHostAddress());

            while (true) {
                Socket socket = serverSocket.accept();
                String client = socket.getInetAddress().getHostAddress();
                System.out.println("New client " + client + " was connected");

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                writer.println(client + ": connected");

                String text;
                do {
                    text = reader.readLine();
                    System.out.println("Message from " + client + ": " + text);
                    writer.println(client + ": " + text);
                } while (!text.equals("disconnect"));

                System.out.println("Client " + client + " was disconnected");
                socket.close();
            }

        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}
