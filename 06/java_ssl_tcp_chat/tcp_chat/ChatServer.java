//SourceCode partially from: http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
//							 http://www.oracle.com/technetwork/java/socket-140484.html
// 							 http://www.java2s.com/Tutorial/Java/0490__Security/SSLServerSession.htm
package tcp_chat;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;

import passwd_save_java.Useradmin;

public class ChatServer {

	public static void main(String[] args) {
		System.setProperty("javax.net.ssl.trustStore", "/home/patrick/workspace2/project_netsec/src/tcp_chat/servertest.truststore"); 
		System.setProperty("javax.net.ssl.keyStore", "/home/patrick/workspace2/project_netsec/src/tcp_chat/servertest.keystore"); 
		System.setProperty("javax.trustStorePassword", "123456");
		System.setProperty("javax.net.ssl.keyStorePassword", "123456");
		
		ChatServer chatserver = new ChatServer();
		chatserver.listenSocket();
	}
	
	private SSLServerSocketFactory sslf;
	private SSLServerSocket server;
	private List<ClientThread> connections;
	private List<String> loggedInUsers;
	private Useradmin useradmin;
	
	public ChatServer() {
		this.sslf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		this.connections = new LinkedList<ClientThread>();
		this.useradmin = new Useradmin();
		this.loggedInUsers = new LinkedList<String>();		
	}

	public void listenSocket() {
		try {
			server = (SSLServerSocket) sslf.createServerSocket(4444);
			server.setNeedClientAuth(true);
			//server.setEnableSessionCreation(true);
		} catch (IOException e) {
			System.out.println("Could not listen on port 4444");
			System.exit(-1);
		}
		while (true) {
			try {
				ClientThread t = new ClientThread((SSLSocket)server.accept(),this);
				System.out.println("new Connection");
				t.start();
				connections.add(t);
			} catch (IOException e) {
				System.out.println("Accept failed: 4444");
			}
		}
	}

	protected void finalize() {
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