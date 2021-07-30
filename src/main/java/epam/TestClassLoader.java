package epam;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import epam.messenger.MesssengerService;

public class TestClassLoader
{
  static public void main(String[] args)
  {
    
    ClassLoader cl1 = TestClassLoader.class.getClassLoader();
    
    Class c1 = null;
    Class c2 = null;
    
    try {
      c1 =  cl1.loadClass("epam.sender.impl.Sender_1");
      MesssengerService s = (MesssengerService) c1.newInstance();
      boolean y = s instanceof MesssengerService;
      s.send("KUKU-1");
    } catch(Exception ex) {
      System.err.println(ex);
    }
    
    try {
      File f = new File("C:/sasha/projects/epam/sender1.jar");
      boolean x = f.exists();
      URL u = f.toURI().toURL();
      
      
      //URLClassLoader ucl = new URLClassLoader(new URL[] {new URL("file:///C:/sasha/projects/epam/classpath")}, null);
      URLClassLoader ucl = new URLClassLoader(new URL[] {f.toURL()});
      c2 = ucl.loadClass("epam.sender.impl.Sender_2");
      //Sender s = (Sender) c2.newInstance();
      Object s = c2.newInstance();
      boolean y = s instanceof MesssengerService;
      
      ((MesssengerService) s).send("KUKU-2");
    } catch(Exception ex) {
      System.err.println(ex);
    }
    
    System.out.println(c1 == c2 ? "the same" : "not the same");
    
  }
}
