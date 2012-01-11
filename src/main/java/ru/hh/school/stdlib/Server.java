package ru.hh.school.stdlib;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Server {
  InetSocketAddress socketAddress;
  Substitutor3000 substitutor;
  ServerSocket serverSocket;
  int sleepTime;

  public Server(InetSocketAddress addr) throws IOException{
    socketAddress = addr;
    substitutor = new Substitutor3000();
    sleepTime = 0;
    serverSocket = new ServerSocket(addr.getPort(), 0, addr.getAddress());
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
            Thread.sleep(sleepTime);
            String request = new String(bufer, 0, buferSize);
            String response = getResponse(request);
            out.write(response.getBytes());
          }
          catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
          }
          finally {
            try {
              socket.close();
            }
            catch (IOException e) {
              System.out.println(e.getLocalizedMessage());
            }
          }
        };
      };
      Thread t = new Thread(runnable);
      t.start();
    }
  }

  private List<String> parse(String request) {
    List<String> rezult = new LinkedList<String>();
    if (request.startsWith(String.valueOf("SET SLEEP"))
        && request.length() > 9) {
      rezult.add(request.substring(0, 9));
      rezult.add(request.substring(10, request.length()));
    }
    else if (request.startsWith(String.valueOf("GET"))
        && request.length() > 3) {
      rezult.add(request.substring(0, 3));
      rezult.add(request.substring(4, request.length()));
    }
    else if (request.startsWith(String.valueOf("PUT"))
        && request.length() > 3) {
      rezult.add(request.substring(0, 3));
      String[] binParsing = request.substring(4, 
          request.length()).split(" ", 2);
      if (binParsing.length < 2) {
        rezult = null;
      }
      else {
        rezult.add(binParsing[0]);
        rezult.add(binParsing[1]);
      }
    }
    else {
      rezult = null;
    }
    return rezult;
  }

  private String getResponse(String request) {
    String rezult = null;
    if (request == null) {
      rezult = "WRONG REQUEST";
    }
    else {
      List<String> parsedRequest = this.parse(request);
      if (parsedRequest == null) {
        rezult = "WRONG REQUEST";
      }
      else if (parsedRequest.get(0).contentEquals("GET")
          && parsedRequest.size() > 1) {
        rezult = "VALUE" + "\n" + this.substitutor.get(
            parsedRequest.get(1));
      }
      else if (parsedRequest.get(0).contentEquals("PUT")
          && parsedRequest.size() > 2) {
        this.substitutor.put(parsedRequest.get(1),
            parsedRequest.get(2));
        rezult = "OK";
      }
      else if (parsedRequest.get(0).contentEquals("SET SLEEP")
          && parsedRequest.size() > 1) {
        Integer time = Integer.parseInt(parsedRequest.get(1));
        if (time != null) {
          sleepTime = time;
          rezult = "OK";
        }
        else {
          rezult = "WRONG REQUEST";
        }
      }
      else {
        rezult = "WRONG REQUEST";
      }
    }
    return rezult;
  }

  public int getPort() {
    return socketAddress.getPort();
  }
}
