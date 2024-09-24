import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java Server <port number>");
            return;
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            System.out.println("Server is listening on port " + portNumber);

            try (Socket clientSocket = serverSocket.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                System.out.println("Client connected.");

                String inputLine = in.readLine();
                if (inputLine != null) {
                    String modifiedString = processString(inputLine);
                    out.println(modifiedString);
                    System.out.println("Processed string sent back to client.");
                }
            }

        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber);
            System.out.println(e.getMessage());
        }
    }

    // Function to reverse the string and change the case
    private static String processString(String input) {
        StringBuilder reversedString = new StringBuilder();
        for (int i = input.length() - 1; i >= 0; i--) {
            char c = input.charAt(i);
            if (Character.isLowerCase(c)) {
                reversedString.append(Character.toUpperCase(c));
            } else if (Character.isUpperCase(c)) {
                reversedString.append(Character.toLowerCase(c));
            } else {
                reversedString.append(c);
            }
        }
        return reversedString.toString();
    }
}






