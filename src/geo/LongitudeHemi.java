package geo;

/**
* <p><code>LongitudeHemi</code> provides types for the latitude hemisphere.</p>
* @author Jerry Smith
* @version $Id: LongitudeHemi.java 253 2006-01-18 16:13:39Z jsmith $
*/

public class LongitudeHemi {
  /**
  * <p>Longitude hemisphere:  E or W.</p>
  */

  public static final LongitudeHemi E = new LongitudeHemi();

  /**
  * <p>Longitude hemisphere:  E or W.</p>
  */

  public static final LongitudeHemi W = new LongitudeHemi();

  private LongitudeHemi() {
  }
}
