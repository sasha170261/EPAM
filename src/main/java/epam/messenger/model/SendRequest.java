package epam.messenger.model;

import lombok.Getter;
import lombok.Setter;

/**
 * class SendRequest
 * 
 * Send request structure
 */
@Getter
@Setter
public class SendRequest
{
  private String  message;        // message text to be sent
  private String  messengerName;  // the messenger name 
}
