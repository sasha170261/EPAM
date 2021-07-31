package epam.messenger.impl;

import epam.messenger.MesssengerService;

/**
 * class Messenger_1
 */
public class Messenger_1 implements MesssengerService
{
  /**
   * Send message
   * 
   * @param msg   message to be send;
   */
  public void send(String msg)
  {
    System.out.println("The message \" + msg + \" is sent by <" + this.getClass().getName() + "> updated");
  }

}
