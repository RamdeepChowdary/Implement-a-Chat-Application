package implementchatapplication;

	import java.io.*;
	import java.net.*;
	import java.util.*;

	public class ChatServer {
	    private ServerSocket serverSocket;
	    private List<ClientHandler> clients;

	    public ChatServer(int port) throws IOException {
	        serverSocket = new ServerSocket(port);
	        clients = new ArrayList<>();
	    }

	    public void start() {
	        System.out.println("Chat server started. Waiting for clients...");
	        while (true) {
	            try {
	                Socket clientSocket = serverSocket.accept();
	                System.out.println("Client connected: " + clientSocket.getInetAddress());
	                ClientHandler clientHandler = new ClientHandler(clientSocket);
	                clients.add(clientHandler);
	                clientHandler.start();
	            } catch (IOException e) {
	                System.out.println("Error accepting client connection: " + e.getMessage());
	            }
	        }
	    }

	    private class ClientHandler extends Thread {
	        private Socket clientSocket;
	        private BufferedReader reader;
	        private PrintWriter writer;

	        public ClientHandler(Socket clientSocket) {
	            this.clientSocket = clientSocket;
	        }

	        public void run() {
	            try {
	                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	                writer = new PrintWriter(clientSocket.getOutputStream(), true);
	                String message;
	                while ((message = reader.readLine()) != null) {
	                    System.out.println("Received message: " + message);
	                    broadcastMessage(message);
	                }
	            } catch (IOException e) {
	                System.out.println("Error handling client: " + e.getMessage());
	            } finally {
	                try {
	                    clientSocket.close();
	                    clients.remove(this);
	                } catch (IOException e) {
	                    System.out.println("Error closing client socket: " + e.getMessage());
	                }
	            }
	        }

	        public void sendMessage(String message) {
	            writer.println(message);
	        }
	    }

	    private void broadcastMessage(String message) {
	        for (ClientHandler client : clients) {
	            client.sendMessage(message);
	        }
	    }

	    public static void main(String[] args) {
	        try {
	            ChatServer server = new ChatServer(8000);
	            server.start();
	        } catch (IOException e) {
	            System.out.println("Error starting server: " + e.getMessage());
	        }
	    }
	}