package geo;

/**
* <p><code>LatitudeHemi</code> provides types for the latitude hemisphere.</p>
* @author Jerry Smith
* @version $Id: LatitudeHemi.java 253 2006-01-18 16:13:39Z jsmith $
*/

public class LatitudeHemi {
  /**
  * <p>Latitude hemisphere:  N or S.</p>
  */

  public static final LatitudeHemi N = new LatitudeHemi();

  /**
  * <p>Latitude hemisphere:  N or S.</p>
  */

  public static final LatitudeHemi S = new LatitudeHemi();

  private LatitudeHemi() {
  }
}
