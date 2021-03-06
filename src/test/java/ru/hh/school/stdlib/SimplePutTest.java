package ru.hh.school.stdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Test;

public class SimplePutTest extends BaseFunctionalTest {

  @Test
  public void simplePut(String key, String value) throws IOException {
    Socket s = connect();

    Writer out = new PrintWriter(s.getOutputStream());
    out.append("PUT " + key + " " + value + "\n").flush();
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    
    Assert.assertEquals("OK", in.readLine());
    
    s.close();
  }
}
