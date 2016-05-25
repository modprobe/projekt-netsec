//SourceCode partially from:
//http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-
// optional/
//http://www.oracle.com/technetwork/java/socket-140484.html


package tcp_chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import passwd_save_java.*;

public class ChatServer {

	public static void main(String[] args) {
		ChatServer chatserver = new ChatServer();
		chatserver.listenSocket();
	}

	private ServerSocket server;
	private List<ClientThread> connections;
	private final Useradmin useradmin;
	private List<String> loggedInUsers;

	public ChatServer() {
		this.connections = new LinkedList<ClientThread>();
		this.useradmin = new Useradmin();
		this.loggedInUsers = new LinkedList<String>();
	}

	public void listenSocket() {
		try {
			server = new ServerSocket(4444);
		} catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			System.exit(-1);
		}
		while (true) {
			try {
				// server.accept returns a client connection
				//w = new ClientWorker(server.accept(),this);
				ClientThread t = new ClientThread(server.accept(),this);
				System.out.println("new Connection");
				t.start();
				connections.add(t);
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
				System.exit(-1);
			}
		}
	}

	protected void finalize() {
		// Objects created in run method are finalized when
		// program terminates and thread exits
		try {
			server.close();
		} catch (IOException e) {
			System.out.println("Could not close socket");
			System.exit(-1);
		}
	}

	public void notify(String message, int ID, String User) {
		for (ClientThread t : connections) {
			if (t.ID != ID) {
				t.print(User + ":" + message);
			}
		}
	}

	public void disconnect(int ID) {
		for (ClientThread t : connections) {
			if (t.ID == ID) {
				loggedInUsers.remove(t.getUser());
				connections.remove(t);
				System.out.println("disconnected");
			}
		}
	}

	public boolean authenticate(String username, char[] password) {
		if (loggedInUsers.contains(username)) {
			return false;
		}
		if (useradmin.checkUser(username, password)) {
			loggedInUsers.add(username);
			return true;
		}
		return false;
	}
}
