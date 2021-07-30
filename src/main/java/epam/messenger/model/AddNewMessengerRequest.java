package epam.messenger.model;

import lombok.Getter;
import lombok.Setter;

/**
 * AddNewMessengerRequest
 * 
 * Add new messenger request struvture
 */
@Getter
@Setter
public class AddNewMessengerRequest
{
  private String  location;       // where the messenger to load from
  private String  className;      // the messenger class name
  private String  messengerName;  // the name to be registered for the given messenger
}
