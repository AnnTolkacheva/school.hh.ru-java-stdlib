package ru.hh.school.stdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {
  InetSocketAddress socketAddress;
  Substitutor3000 substitutor;
  ServerSocket serverSocket;
  public static List<Pattern> operations;
  AtomicInteger sleepTime;

  public Server(InetSocketAddress addr) throws IOException{
    socketAddress = addr;
    substitutor = new Substitutor3000();
    sleepTime = new AtomicInteger(0);
    serverSocket = new ServerSocket(addr.getPort(), 0, addr.getAddress());
    operations = new LinkedList<Pattern>();
    operations.add(Pattern.compile("PUT "));
    operations.add(Pattern.compile("GET "));
    operations.add(Pattern.compile("SET SLEEP "));
  }

  public void run() throws IOException {
    while (true) {
      final Socket socket = serverSocket.accept();
      Runnable runnable = new Runnable() {
        public void run() {
          try {
            Writer out = new PrintWriter(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader
                (socket.getInputStream()));            
            Thread.sleep(sleepTime.intValue());
            String request = in.readLine();;
            String response = getResponse(request);
            out.write(response);
            out.flush();
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

  private String getResponse(String request) {
    String rezult = null;
    // PUT operation
    if (operations.get(0).matcher(request).lookingAt()) {
      String argument = operations.get(0).matcher(request).replaceFirst("");
      String[] argumentParsing = argument.split(" ", 2);
      if (argumentParsing.length == 2) {
        this.substitutor.put(argumentParsing[0], argumentParsing[1]);
        rezult = "OK";
      }
      else {
        rezult = "WRONG REQUEST";
      }
    }
    // GET operation
    else if (operations.get(1).matcher(request).lookingAt()) {
      Matcher m = operations.get(1).matcher(request);
      rezult = "VALUE" + "\n" + this.substitutor.get(m.replaceFirst(""));
    }
    // SET SLEEP operation
    else if (operations.get(2).matcher(request).lookingAt()) {
      Integer time = Integer.parseInt(
          operations.get(2).matcher(request).replaceFirst(""));
      if (time != null) {
        sleepTime.set(time);
        rezult = "OK";
      }
      else {
        rezult = "WRONG REQUEST";
      }
    }
    else {
      rezult = "WRONG REQUEST";
    }
     return rezult;
  }

  public int getPort() {
    return socketAddress.getPort();
  }
}
