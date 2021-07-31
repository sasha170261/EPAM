package epam.messenger;

/**
 * class ClassAlreadyLoaded
 */
public class SubjectAlreadyLoadedException extends Exception
{
  /**
   * Constructor
   * 
   * @param location  location the class was loaded from
   */
  public SubjectAlreadyLoadedException(String message)
  {
    super(message);
  }
}
