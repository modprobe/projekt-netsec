// Original Source http://stackoverflow.com/questions/10556829/sending-and-receiving-udp-packets-using-java
// Modified for own use

import java.io.IOException;
import java.net.*;

public class UDPReciever {

  public static void main(String[] args) {
    new UDPReciever().run(9999);
  }

  public void run(int port) {
    try {
      DatagramSocket serverSocket = new DatagramSocket(port);
      byte[] receiveData = new byte[1024];

      System.out.printf("Listening on udp:%S%D%n",InetAdress.getLocalHost().getHostAdress,port);
      DatagramPacket receivePacket = new DatagramPacket(receiveData,receiveData.length);

      while (true) {
        serverSocket.receive(receivePacket);
        String sentence = new String( receivePacket.getData(), 0,receivePacket.getLength() );
        System.out.println("RECEIVED: " + sentence);
      }
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
