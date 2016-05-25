//SourceCode partially from: http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
//							 http://www.oracle.com/technetwork/java/socket-140484.html

package tcp_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientThread extends Thread implements Runnable {
	private Socket client;
	private ChatServer observer;
	public final int ID;
	private String User;

	// Constructor
	ClientThread(Socket client, ChatServer observer) {
		this.client = client;
		ID = this.hashCode();
		this.observer = observer;
		User = "";
	}

	public void run() {
		String line;
		BufferedReader in = null;
		try {
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					client.getInputStream()));
			out.println("Username: ");
			String username = in.readLine();
			out.println("Password:");
			List<Character> l = new ArrayList<Character>();
			do {
				char c = (char) in.read();
				if (c == '\r') {
					break;
				}
				l.add(c);
			} while (true);
			char[] password = new char[l.size()];
			int i = 0;
			for (Character c : l) {
				password[i] = c.charValue();
				i++;
			}
			l = null;

			if (observer.authenticate(username, password)) {
				out.println("Authentication succesful");
				password = null;
				User = username;
			} else {
				out.println("Password or Username invalid.");
				observer.disconnect(ID);
				client.close();
				client = null;
				return;
			}
		} catch (IOException e) {
			System.out.println("in or out failed");
			System.exit(-1);
		}

		while (true) {
			try {
				line = in.readLine();
				// Send data back to client
				// out.println(line);
				// Append data to text area
				if (line.equals("quit")) {
					observer.disconnect(ID);
					client.close();
					client = null;
					return;
				}
				observer.notify(line, ID, User);
			} catch (IOException e) {
				System.out.println("Read failed");
				System.exit(-1);
			}
		}
	}

	public synchronized void print(String message) {
		try {
			PrintWriter out = null;
			out = new PrintWriter(client.getOutputStream(), true);
			out.println(message);
		} catch (IOException e) {
			System.out.println("out failed");
			System.exit(-1);
		}
	}

	public String getUser() {
		return User;
	}
}