package ru.hh.school.stdlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Server {
  InetSocketAddress socketAddress;
  Substitutor3000 substitutor;
  ServerSocket serverSocket;
  int sleepTime;
  
  public Server(InetSocketAddress addr) {
    socketAddress = addr;
    substitutor = new Substitutor3000();
    sleepTime = 0;
    try {
      serverSocket = new ServerSocket(addr.getPort(), 0, addr.getAddress());
    } catch (IOException e) {
      e.printStackTrace();
    }
    
   // throw new UnsupportedOperationException();
  }

  public void run() throws IOException {
    while (true) {
      final Socket socket = serverSocket.accept();
      Runnable runnable = new Runnable() {
        public void run() { 
          try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();
            byte bufer[] = new byte[1024*16];
            int buferSize = in.read(bufer);
            String request = new String(bufer, 0, buferSize);
            String response = getResponse(request);
            out.write(response.getBytes());
            socket.close();
          }
          catch (IOException e) {
            e.printStackTrace();
          }
        };
      };
      Thread t = new Thread(runnable);
      try {
        Thread.sleep(sleepTime);
      } 
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      t.start();
    }
  }
  
  private List<String> parse(String request) {
    List<String> rezult = Arrays.asList(request.split(" ", 0));
    if (rezult == null || rezult.size() < 2) {
      rezult = null;
    } 
    else if (rezult.get(0) == "GET") {
      for (int index = 2; index < rezult.size(); index++) {
        rezult.remove(index);
      }
    }
    else if (rezult.get(0) == "PUT" && rezult.size() > 2) {
      if (rezult.get(2).charAt(0) == '$') {
        rezult.remove(0);
        rezult.add("SET KEYS");
        rezult.add(rezult.remove(1));
        while (rezult.get(0) != "SET KEYS") {
          int lenth = rezult.get(0).length() - 1;
          rezult.add(rezult.remove(0).substring(2, lenth));
        }
      }
      else {
        for (int index = 3; index < rezult.size(); index++) {
          rezult.remove(index);
        }
      }
    } 
    else if (rezult.get(0) != "SET SLEEP") {
      rezult = null;
    }
    return rezult;
  }
  
  private String getResponse(String request) {
    String rezult = null;
    List<String> parsedRequest = this.parse(request);
    if (parsedRequest == null) {
      rezult = "WRONG REQUEST";
 //     throw new UnsupportedOperationException();
    }
    else if (parsedRequest.get(0) == "GET") {
      rezult = "VALUE" + "\n" + this.substitutor.get(parsedRequest.get(1)) + "\n" + "connection closed";
    }
    else if (parsedRequest.get(0) == "PUT") {
      this.substitutor.put(parsedRequest.get(1), parsedRequest.get(2));
      rezult = "OK" + "\n" + "connection closed";
    }
    else if (parsedRequest.get(0) == "SET KEYS") {
      String key = parsedRequest.get(1);
      parsedRequest.remove(0);
      parsedRequest.remove(1);
      this.substitutor.setSuperKeys(key, parsedRequest);
      rezult = "OK" + "\n" + "connection closed";
    }
    else if (parsedRequest.get(0) == "SET SLEEP") {
      sleepTime = Integer.getInteger(parsedRequest.get(1));
      rezult = "OK" + "\n" + "connection closed";
    }
    return rezult;
  }
  public int getPort() {
	return socketAddress.getPort();
  }
}
