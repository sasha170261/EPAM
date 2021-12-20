package epam.messenger.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import epam.messenger.SubjectAlreadyLoadedException;
import epam.messenger.DynamicMessengerRegistry;
import epam.messenger.MesssengerService;
import epam.messenger.model.AddNewMessengerRequest;
import epam.messenger.model.MessengerList;
import epam.messenger.model.SendRequest;
import lombok.extern.log4j.Log4j2;

/**
 * class MessengerController
 * 
 * test-4
 */
@Log4j2
@RestController
public class MessengerController
{
  @Autowired
  private DynamicMessengerRegistry  registry;
  
  @Autowired
  private ApplicationContext        appContext;  
  
  
  /**
   * Send message
   * 
   * @param request
   * @return ResponseEntity
   */
  @PostMapping(value = "/messengers/send")
  public ResponseEntity<String> sendMessage(@RequestBody SendRequest request)
  {
    ResponseEntity<String> res = null;
    
    try
    {
      MesssengerService service = registry.getMessenger(request.getMessengerName());
      if(service != null )
      {
        service.send(request.getMessage());
        res = new ResponseEntity<>("OK", HttpStatus.OK);
      }
      else
      {
        log.error("Unknown messenger <" + request.getMessengerName() + ">");
        res = new ResponseEntity<>("Unknown messenger <" + request.getMessengerName() + ">", HttpStatus.BAD_REQUEST);
      }
    }
    catch(Exception ex)
    {
      log.error("send mesage failure: " + ex);      
      res = new ResponseEntity<>("Failure", HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    return res;
  }
  
  /**
   * Add a new Messenger service
   * 
   * @param request
   */
  @PostMapping(value = "/messengers/add")
  public ResponseEntity<String> addNewMessenger(@RequestBody AddNewMessengerRequest request)
  {
    ResponseEntity<String> res = null;
    
    try
    {      
      registry.registerBean(request);
      res = new ResponseEntity<>("OK", HttpStatus.OK);
    }
    catch(ClassNotFoundException ex)
    {
      log.error("add messenger failure: " + ex);
      res = new ResponseEntity<>("Failure: Class not found: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    catch(SubjectAlreadyLoadedException ex)
    {
      log.error("add messenger failure: " + ex);
      res = new ResponseEntity<>("Failure: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    catch(Exception ex)
    {
      log.error("add messenger failure: " + ex);
      res = new ResponseEntity<>("Failure: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    return res;
  }
  
  /**
   * Return all registered messengers 
   */
  @GetMapping(value = "/messengers/all")
  public ResponseEntity<MessengerList> getAllMessengers()
  {
    HttpStatus status = HttpStatus.OK;
    ResponseEntity<MessengerList> res = null;
    
//    List<String> messengers = Arrays.asList(appContext.getBeanNamesForType(MesssengerService.class));
//    MessengerList list = new MessengerList();
//    list.setCount(messengers.size());
//    list.setMessengers(messengers);    
//    res = new ResponseEntity<>(list, status);   
    
    MessengerList list  = registry.getAllRegisteredMessengers();
    res = new ResponseEntity<>(list, status);
    
    return res;
  }
  
}
