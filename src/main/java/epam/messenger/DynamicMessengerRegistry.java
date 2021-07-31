package epam.messenger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import epam.messenger.model.AddNewMessengerRequest;
import epam.messenger.model.MessengerBean;
import epam.messenger.model.MessengerList;
import lombok.extern.log4j.Log4j2;

/**
 * DynamicMessengerRegistry
 */
@Log4j2
@Service
public class DynamicMessengerRegistry implements BeanFactoryAware
{
  @Autowired
  private ApplicationContext      appContext;
  
  private ConfigurableBeanFactory beanFactory;
  
  private RegistryClassLoader     registryCL = new RegistryClassLoader(new URL[] {}, this.getClass().getClassLoader());
  
  /**
   * Return all register messengers info
   */
  public MessengerList getAllRegisteredMessengers()
  {
    List<MessengerBean> beans = new ArrayList<>();    
    List<String> messengerNames = Arrays.asList(appContext.getBeanNamesForType(MesssengerService.class));
    
    messengerNames.forEach(n -> {
      Object m = beanFactory.getBean(n);
      MessengerBean bean = new MessengerBean();
      bean.setBeanName(n);
      bean.setClassName(m.getClass().getName());
      bean.setLoadFrom(m.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
      beans.add(bean);
    });
    
    MessengerList list = new MessengerList();
    list.setCount(beans.size());
    list.setMessengers(beans);
    
    return list;
  }
  
  /**
   * Register messenger service
   * 
   * @param request  definitions of messenger to be registered;
   */  
  public void registerBean(AddNewMessengerRequest request) throws Exception
  {
    MesssengerService instance = null;
    
    // Normalize location value 
    request.setLocation(request.getLocation().startsWith("/") ? request.getLocation() : "/" + request.getLocation());
    
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
                "<" + request.getClassName() + ">");
          } catch(ClassNotFoundException ex) {
            // Class not exist. Do nothing. Just continue
          }
          
          /*
             NOTE:  It is allowed here multiple registration of the same service
                    with different names
           */
          File f = new File(request.getLocation());
          URL url = f.toURI().toURL();
          registryCL.addURL(url);
          Class<? extends MesssengerService> c = null;
          
          try {
            c = (Class<MesssengerService>) registryCL.loadClass(request.getClassName());
          } catch(ClassNotFoundException ex) {
            throw new ClassNotFoundException(! f.exists() ? "Invalid location <" + request.getLocation() + ">" : 
              ex.getMessage() + ", location: " + request.getLocation());
          }

          // Validate that the desirable class loaded from the required location
          String classLocation  = c.getProtectionDomain().getCodeSource().getLocation().getFile();
          if( ! classLocation.equals(request.getLocation()) )
            throw new SubjectAlreadyLoadedException("Class already reqistered for at least one messenger: " + 
                appContext.getBeanNamesForType(c)[0] + ", from location: " + classLocation);
          
          // Create bean
          instance = c.newInstance();
          beanFactory.registerSingleton(request.getMessengerName(), instance);
          log.info("The messenger <" + request.getMessengerName() + "> is registered (loaded from \"" + classLocation + "\"");
        }
        else
        {
          validateClassLocation(request,true);
        }
      }
    }
    else
    {
      validateClassLocation(request, true);
    }
  }
  
  /**
   * Check if requested class has been loaded from proper location  
   * 
   * @param       request
   * @return      if the class required by request is loaded from the desirable location, otherwisw
   *              exception is thrown  
   * @exception   SubjectAlreadyLoadedException  
   *              if the given class has been already loaded from different location 
   */
  private void validateClassLocation(AddNewMessengerRequest request, boolean alreadyReqistered) throws Exception
  {
    String classLocation  = beanFactory.getType(request.getMessengerName()).getProtectionDomain().getCodeSource().getLocation().getFile();
    log.warn("Messenger already reqistered from location: " + classLocation);
    throw new SubjectAlreadyLoadedException("Messenger already reqistered from location: " + classLocation);
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
  
