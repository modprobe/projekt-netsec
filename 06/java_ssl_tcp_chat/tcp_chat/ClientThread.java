//SourceCode partially from: http://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
//							 http://www.oracle.com/technetwork/java/socket-140484.html

package tcp_chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			Certificate[] certs = client.getSession().getPeerCertificates();
			for (Certificate cert : certs) {
				X509Certificate xcert = (X509Certificate)cert;
				String cert_name = xcert.getSubjectDN().getName();
				System.out.println(cert_name);
				
				Pattern name = Pattern.compile("CN=((?:[\\pL\\p{Nd}_]{1,20}\\s*)*),.*");
				
				Matcher mname = name.matcher(cert_name);
				if (mname.matches()) {
					User=mname.group(1);
				}
			}
		}
		catch (SSLHandshakeException e) {
				System.err.println("SSLHandshake failed");
				this.interrupt();
		}
		 catch (IOException e) {
			System.err.println("in or out failed");
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
				System.err.println("Read failed");
				this.interrupt();
			}
		}
	}
	
	public synchronized void print(String message) {
		try {
			PrintWriter out = null;
			out = new PrintWriter(client.getOutputStream(), true);
			out.println(message);
		} catch (IOException e) {
			System.err.println("out failed");
		}
	}

	public String getUser() {
		return User;
	}
}