//SourceCode partially from: http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
//							 http://www.oracle.com/technetwork/java/socket-140484.html

package tcp_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;

class ClientThread extends Thread implements Runnable {
	private SSLSocket client;
	private ChatServer observer;
	public final int ID;
	private String User;

	ClientThread(SSLSocket client, ChatServer observer) {
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
			char[] password = new char[20];
			int i = 0;
			do {
				char c = (char) in.read();
				if (c == '\r' || c == '\n') {
					break;
				}
				password[i] = c;
				i++;
			} while (true);
			if (observer.authenticate(username, password)) {
				out.println("Authentication succesful");
				password = null;
				User = username;
			} else {
				out.println("Password or Username invalid.");
				password = null;
				observer.disconnect(ID);
				client.close();
				client = null;
				return;
			}
		}
		catch (SSLHandshakeException e) {
				System.out.println("SSLHandshake failed");
				this.interrupt();
		}
		 catch (IOException e) {
			System.out.println("in or out failed");
			this.interrupt();
		} 

		while (!this.interrupted()) {
			try {
				line = in.readLine();
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