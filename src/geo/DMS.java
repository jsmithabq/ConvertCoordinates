package geo;


/**
* <p><code>DMS</code> represents a sexagesimal coordinate in degrees,
* minutes, and seconds.</p>
* @author Jerry Smith
* @version $Id: DMS.java 253 2006-01-18 16:13:39Z jsmith $
*/

public class DMS {
  private int degrees = 0;
  private int minutes = 0;
  private double seconds = 0;


  /**
  * <p>Constructs a coordinate object initialized to the
  * default values (0,0,0).</p>
  */

  public DMS() {
  }

  /**
  * <p>Constructs a coordinate object initialized to the
  * specified coordinates.</p>
  * @param degrees The degrees.
  * @param minutes The minutes.
  * @param seconds The seconds.
  */

  public DMS(int degrees, int minutes, double seconds) {
    this.degrees = degrees;
    this.minutes = minutes;
    this.seconds = seconds;
  }

  /**
  * <p>Sets the degrees.</p>
  * @param degrees The degrees.
  */

  public void setDegrees(int degrees) {
    this.degrees = degrees;
  }

  /**
  * <p>Gets the degrees.</p>
  * @return The degrees.
  */

  public int getDegrees() {
    return degrees;
  }

  /**
  * <p>Sets the minutes.</p>
  * @param minutes The minutes.
  */

  public void setMinutes(int minutes) {
    this.minutes = minutes;
  }

  /**
  * <p>Gets the minutes.</p>
  * @return The minutes.
  */

  public int getMinutes() {
    return minutes;
  }

  /**
  * <p>Sets the seconds.</p>
  * @param seconds The seconds.
  */

  public void setMinutes(double seconds) {
    this.seconds = seconds;
  }

  /**
  * <p>Gets the seconds.</p>
  * @return The seconds.
  */

  public double getSeconds() {
    return seconds;
  }

  /**
  * <p>Gets a summary of the lat/lon instance.</p>
  * @return The summary.
  */

  public String toString() {
    return "[degrees = " + degrees + ", minutes = " + minutes +
      ", seconds = " + seconds + "]";
  }
}
