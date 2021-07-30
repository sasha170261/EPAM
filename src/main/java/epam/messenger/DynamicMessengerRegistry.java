package epam.messenger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import epam.messenger.model.AddNewMessengerRequest;
import lombok.extern.log4j.Log4j2;

/**
 * DynamicMessengerRegistry
 */
@Log4j2
@Service
public class DynamicMessengerRegistry implements BeanFactoryAware
{
  private ConfigurableBeanFactory beanFactory;
  
  private RegistryClassLoader registryCL = new RegistryClassLoader(new URL[] {}, this.getClass().getClassLoader());
  
  
  /**
   * Register messenger service
   * 
   * @param request  definitions of messenger to be registered;
   */  
  public void registerBean(AddNewMessengerRequest request) throws Exception
  {
    MesssengerService instance = null;
    
    if( ! beanFactory.containsBean(request.getMessengerName()) )
    {
      synchronized(DynamicMessengerRegistry.class)
      {
        if( ! beanFactory.containsBean(request.getMessengerName()) )
        {
          try
          { // Check if the class is already loaded by the application 
            // default class loader
            
            Class<?> c = Class.forName(request.getClassName());
            /*
                The given state actually mean that the required class was 
                already loaded by the default application class loader.
                The class just was in the application class-path at the 
                application start
               
                Just warn about that issue (there is no exact behavior policy for such a case)
             */
            log.warn(
                "The messenger implementation for <" + request.getMessengerName() + "> is found in the default application classpath " +
                "<" + request.getClassName() + ">. Ignored");
          } catch(ClassNotFoundException ex) {
            // Class not exist. Do nothing. Just continue
          }
          
          /*
             NOTE:  It is allowed here multiple registration of the same service
                    with different names
           */          
          URL url = new File(request.getLocation()).toURI().toURL();
          registryCL.addURL(url);
          Class<? extends MesssengerService> c = (Class<? extends MesssengerService>) registryCL.loadClass(request.getClassName());
          instance = c.newInstance();
          beanFactory.registerSingleton(request.getMessengerName(), instance);
          log.info("The messenger <" + request.getMessengerName() + "> is reistered (loaded from \"" + url + "\"");
        }
        else
          log.warn("The messenger <" + request.getMessengerName() + "> is already registered");
      }
    }
    else
      log.warn("The messenger <" + request.getMessengerName() + "> is already registered");
  }
  
  /**
   * Return messenger service by the given name
   * 
   * @param serviceName
   * @return messenger server if found otherwise null;
   */
  public MesssengerService getMessenger(String serviceName)
  {
    Object o = beanFactory.getSingleton(serviceName);    
    return (MesssengerService) beanFactory.getSingleton(serviceName);
  }
  
  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException
  {
      this.beanFactory = (ConfigurableBeanFactory) beanFactory;
  }
  
  /**
   * class RegistryUrl
   */
  static class RegistryClassLoader extends URLClassLoader
  {
    // Constructor
    RegistryClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
    }
    
    @Override
    protected void addURL(URL url) {
      super.addURL(url);
    }
  }
}
  
