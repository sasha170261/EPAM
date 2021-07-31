package epam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

/**
 * class EpamMessengerApplication
 */
@SpringBootApplication
public class EpamMessengerApplication extends AsyncConfigurerSupport
{
  public static void main(String[] args) 
  {
    SpringApplication.run(EpamMessengerApplication.class, args);
  }

}
