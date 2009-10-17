package geo;


/**
* <p><code>UTM</code> represents a UTM coordinate.</p>
* @author Jerry Smith
* @version $Id: UTM.java 237 2006-01-16 18:23:47Z jsmith $
*/

public class UTM {
  private String gridZone = new String("");
  private double x = 0;
  private double y = 0;


  /**
  * <p>Constructs a UTM coordinate object initialized to the
  * default values (0, 0, "").</p>
  */

  public UTM() {
  }

  /**
  * <p>Constructs a UTM coordinate object initialized to the
  * specified coordinates.</p>
  * @param gridZone The UTM grid zone.
  * @param x The easting.
  * @param y The northing.
  */

  public UTM(String gridZone, double x, double y) {
    this.gridZone = new String(gridZone);
    this.x = x;
    this.y = y;
  }

  /**
  * <p>Sets the grid zone (deep copy).</p>
  * @param gridZone The grid zone.
  */

  public void setGridZone(String gridZone) {
    this.gridZone = new String(gridZone);
  }

  /**
  * <p>Gets the grid zone.</p>
  * @return The grid zone.
  */

  public String getGridZone() {
    return gridZone;
  }

  /**
  * <p>Sets the easting.</p>
  * @param x The easting.
  */

  public void setX(double x) {
    this.x = x;
  }

  /**
  * <p>Gets the easting.</p>
  * @return The easting.
  */

  public double getX() {
    return x;
  }

  /**
  * <p>Sets the northing.</p>
  * @param y The northing.
  */

  public void setY(double y) {
    this.y = y;
  }

  /**
  * <p>Gets the northing.</p>
  * @return The northing.
  */

  public double getY() {
    return y;
  }

  /**
  * <p>Gets a summary of the UTM instance.</p>
  * @return The summary.
  */

  public String toString() {
    return "[gridZone = " + gridZone + ", x = " + x + ", y = " + y + "]";
  }
}
