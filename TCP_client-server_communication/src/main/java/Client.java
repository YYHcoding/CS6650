import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java Client <server ip> <port number>");
            return;
        }
        String serverIp = args[0];
        int portNumber = Integer.parseInt(args[1]);
        try (Socket socket = new Socket(serverIp, portNumber);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.print("Enter text: ");
            String userInput = stdIn.readLine();
            out.println(userInput);

            String serverResponse = in.readLine();
            System.out.println("Response from server: " + serverResponse);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host: " + serverIp);
        } catch (IOException e) {
            System.out.println("Couldn't get I/O for the connection to " + serverIp);
        }
    }
}
