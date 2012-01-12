package ru.hh.school.stdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;

import org.junit.Assert;
import org.junit.Test;

public class ComplexTest extends BaseFunctionalTest {
  public ComplexTest(int port)
  {
    super(port);
  }

  private void Put(String key, String value) throws IOException {
    Socket s = connect();

    Writer out = new PrintWriter(s.getOutputStream());
    out.append("PUT " + key + " " + value + "\n").flush();
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    
    Assert.assertEquals("OK", in.readLine());
    
    s.close();
  }

  @Test
  private void Get(String key, String value) throws IOException {
    Socket s = connect();

    Writer out = new PrintWriter(s.getOutputStream());
    out.append("GET " + key + "\n").flush();
    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    
    Assert.assertEquals("VALUE", in.readLine());
    Assert.assertEquals(value, in.readLine());
    
    s.close();
  }  
  
  @Test
  public void Test() throws IOException {
    Put("k1", "one");
    Get("k1", "one");
    Put("keys", "k1 = ${k1}, k2 = ${k2}");
    Get("keys", "k1 = one, k2 = ");
    Put("k2", "two");
    Get("keys", "k1 = one, k2 = two");
  }
}
