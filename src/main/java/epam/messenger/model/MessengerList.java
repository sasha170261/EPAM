package epam.messenger.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * MessengerList
 */
@Getter
@Setter
public class MessengerList
{
  private int           count;
  private List<String>  messengers;
}
