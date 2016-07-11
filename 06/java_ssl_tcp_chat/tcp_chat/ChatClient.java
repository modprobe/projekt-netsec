/*
	SourceCode partially from: https://stackoverflow.com/questions/7872846/how-to-read-from-standard-input-non-blocking
*/
package tcp_chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.SSLSocket;

public class ChatClient {

	private static final BlockingQueue<String>  socketlines = new LinkedBlockingQueue<String>();
	private static final BlockingQueue<String>  systemlines = new LinkedBlockingQueue<String>();
	
  public static void main(String[] args) throws Exception {
    SocketFactory sf = SSLSocketFactory.getDefault();
    SSLSocket s = (SSLSocket) sf.createSocket("localhost", 4444);
        
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader sin = new BufferedReader(new InputStreamReader(s.getInputStream()));
    PrintWriter sout = new PrintWriter(s.getOutputStream(),true);
    
    Thread socketReaderThread = null; 
    socketReaderThread = new Thread(new Runnable() {
    	@Override
    		public void run() {
    			try {
    				while (!Thread.interrupted()) {
    					String line = sin.readLine();
    					socketlines.add(line);
    				}
    			} catch (Exception e) {
    				// TODO: handle exception
    			}
    		}
    });
    socketReaderThread.setDaemon(true);
    socketReaderThread.start();
    
    Thread systemReaderThread = null; 
    systemReaderThread = new Thread(new Runnable() {
    	@Override
    		public void run() {
    			try {
    				while (!Thread.interrupted()) {
    					String line = in.readLine();
    					systemlines.add(line);
    				}
    			} catch (Exception e) {
    				// TODO: handle exception
    			}
    		}
    });
    systemReaderThread.setDaemon(true);
    systemReaderThread.start();
    
    String line;
    while (!s.isClosed()) {
    	if ((line = socketlines.poll(10L, TimeUnit.MILLISECONDS))!= null){
    		System.out.println(line);
    	}
    	if ((line = systemlines.poll(10L, TimeUnit.MILLISECONDS))!= null){
    		sout.println(line);
    		if (line.equals("quit")) {
    			s.close();
    		}
    	}
    }
    s.close();
    socketReaderThread.interrupt();
    systemReaderThread.interrupt();
  }
}