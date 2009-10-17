package geo;


/**
* <p><code>LatLon</code> represents a lat/lon coordinate.</p>
* @author Jerry Smith
* @version $Id: LatLon.java 237 2006-01-16 18:23:47Z jsmith $
*/

public class LatLon {
  private double latitude = 0;
  private double longitude = 0;


  /**
  * <p>Constructs a lat/lon coordinate object initialized to the
  * default values (0,0).</p>
  */

  public LatLon() {
  }

  /**
  * <p>Constructs a lat/lon coordinate object initialized to the
  * specified coordinates.</p>
  * @param latitude The latitude.
  * @param longitude The longitude.
  */

  public LatLon(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  /**
  * <p>Sets the latitude.</p>
  * @param latitude The latitude.
  */

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  /**
  * <p>Gets the latitude.</p>
  * @return The latitude.
  */

  public double getLatitude() {
    return latitude;
  }

  /**
  * <p>Sets the longitude.</p>
  * @param longitude The longitude.
  */

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  /**
  * <p>Gets the longitude.</p>
  * @return The longitude.
  */

  public double getLongitude() {
    return longitude;
  }

  /**
  * <p>Gets a summary of the lat/lon instance.</p>
  * @return The summary.
  */

  public String toString() {
    return "[latitude = " + latitude + ", longitude = " + longitude + "]";
  }
}
