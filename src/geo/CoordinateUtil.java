package geo;

import java.text.DecimalFormat;


/**
* <p><code>CoordinateUtil</code> provides class-level convenience methods for
* coordinate-related operations.  The lat/lon and UTM conversion operations
* are Java modifications of the C routines provided by Peter Daly from MIT
* Ocean Acoustics.  As such, they have a C flavor.  The objective was to
* minimize code porting modifications in order to minimize the possibility
* of introducing errors.</p>
* <p>The following credits appear in Daly's <code>convert_datum.c</code>:</p>
* <p>These routines convert UTM to Lat/Longitude and vice-versa,
* using the WGS-84 (GPS standard) or Clarke 1866 Datums.</p>
* 
* <p>The formulae for these routines were originally taken from
* Chapter 10 of "GPS: Theory and Practice," by B. Hofmann-Wellenhof,
* H. Lictenegger, and J. Collins. (3rd ed) ISBN: 3-211-82591-6,
* however, several errors were present in the text which
* made their formulae incorrect.</p>
*
* <p>Instead, the formulae for these routines was taken from
* "Map Projections: A Working Manual," by John P. Snyder
* (US Geological Survey Professional Paper 1395).</p>
*
* <p>Copyright (C) 1998 Massachusetts Institute of Technology
*               All Rights Reserved</p>
*
* @author Jerry Smith
* @version $Id: CoordinateUtil.java 255 2006-01-18 16:40:51Z jsmith $
*/

public final class CoordinateUtil {
  /**
  * <p>A supported datum.</p>
  */

  public static final int CLARKE_1866_DATUM = 0;
  /**
  * <p>A supported datum.</p>
  */

  public static final int GRS_80_DATUM = 1;
  /**
  * <p>A supported datum.</p>
  */

  public static final int WGS_84_DATUM = 2;
  private static final double LOWER_EPS_LIMIT = 1e-14;
  private static final double M_PI_2 = Math.PI / 2;


  private CoordinateUtil() {
  }

  //
  // Both gridZone and lambda0 are modified!
  //

  private static void
  getGridZone(LatLon latLon, GridZone gridZone, Lambda0 lambda0) {
    int zoneLong;
    char zoneLat;

    /* Solve for the grid zone, returns the central meridian */

    /* First, let's take care of the polar regions */

    if (latLon.getLatitude() < -80) {
      if (latLon.getLongitude() < 0) {
        gridZone.gridZone = "30A";
        lambda0.lambda0 = 0 * Math.PI / 180.0;
      }
      else {
        gridZone.gridZone = "31B";
        lambda0.lambda0 = 0 * Math.PI / 180.0;
      }
      return;
    }
    else if (latLon.getLatitude() > 84) {
      if (latLon.getLongitude() < 0) {
        gridZone.gridZone = "30Y";
        lambda0.lambda0 = 0 * Math.PI / 180.0;
      }
      else {
        gridZone.gridZone = "31Z";
        lambda0.lambda0 = 0 * Math.PI / 180.0;
      }
      return;
    }

    /* Now the special "X" grid */

    if (latLon.getLatitude() > 72 && latLon.getLongitude() > 0 &&
        latLon.getLongitude() < 42) {
      if (latLon.getLongitude() < 9) {
        lambda0.lambda0 = 4.5;
        gridZone.gridZone = "31X";
      }
      else if (latLon.getLongitude() < 21) {
        lambda0.lambda0 = 15 * Math.PI / 180.0;
        gridZone.gridZone = "33X";
      }
      else if (latLon.getLongitude() < 33) {
        lambda0.lambda0 = 27 * Math.PI / 180.0;
        gridZone.gridZone = "35X";
      }
      else if (latLon.getLongitude() < 42) {
        lambda0.lambda0 = 37.5 * Math.PI / 180.0;
        gridZone.gridZone = "37X";
      }
      return;
    }

    /* Handle the special "V" grid */

    if (latLon.getLatitude() > 56 && latLon.getLatitude() < 64 &&
        latLon.getLongitude() > 0 && latLon.getLongitude() < 12) {
      if (latLon.getLongitude() < 3) {
        lambda0.lambda0 = 1.5 * Math.PI / 180.0;
        gridZone.gridZone = "31V";
      }
      else if (latLon.getLongitude() < 12) {
        lambda0.lambda0 = 7.5 * Math.PI / 180.0;
        gridZone.gridZone = "32V";
      }
      return;
    }

    /* The remainder of the grids follow the standard rule */

    zoneLong = (int) ((latLon.getLongitude() - (-180.0)) / 6.0) + 1;
    lambda0.lambda0 = ((zoneLong - 1) * 6.0 + (-180.0) + 3.0) * Math.PI / 180.0;

    int offset = (int) ((latLon.getLatitude() - (-80.0)) / 8.0);
    zoneLat = UnsignedChar.getCharPlusOffset('C', offset);
    if (zoneLat > 'H') {
      zoneLat = UnsignedChar.getNextChar(zoneLat);
    }
    if (zoneLat > 'N') {
      zoneLat = UnsignedChar.getNextChar(zoneLat);
    }
    if (latLon.getLatitude() > 80) {
      zoneLat = 'X';
    }
    gridZone.gridZone = "" + (zoneLong / 10) + (zoneLong % 10) + zoneLat;
  }

  //
  // lambda0 is modified!
  //

  private static int getLambda0(String gridZone, Lambda0 lambda0) {
    int zoneLong;
    char zoneLat;

    /* given the grid zone, sets the central meridian, lambda0 */

    /* Check the grid zone format */

    if (!Character.isDigit(gridZone.charAt(0)) ||
        !Character.isDigit(gridZone.charAt(1))) {
      System.out.println(
        "Invalid grid zone format: " + gridZone + ".");
      return(-1);
    }

    zoneLong = Integer.parseInt(gridZone.substring(0, 2), 10);
    zoneLat = gridZone.charAt(2);

    /* Take care of special cases */

    switch(zoneLat) {
    case 'A': case 'B': case 'Y': case 'Z':
      lambda0.lambda0 = 0;
      return(0);
      //break;
    case 'V':
      switch (zoneLong) {
      case 31:
        lambda0.lambda0 = 1.5 * Math.PI / 180.0;
        return(0);
        //break;
      case 32:
        lambda0.lambda0 = 7.5 * Math.PI / 180.0;
        return(0);
        //break;
      //break;
      }
    case 'X':
      switch (zoneLong) {
      case 31:
        lambda0.lambda0 = 4.5 * Math.PI / 180.0;
        return(0);
        //break;
      case 33:
        lambda0.lambda0 = 15 * Math.PI / 180.0;
        return(0);
        //break;
      case 35:
        lambda0.lambda0 = 27 * Math.PI / 180.0;
        return(0);
        //break;
      case 37:
        lambda0.lambda0 = 37.5 * Math.PI / 180.0;
        return(0);
        //break;
      case 32: case 34: case 36:
        System.out.println(
          "Zone " + gridZone + " does not exist!");
        return(-1);
        //break;
      }
      break;
    }

    /* Now handle standard cases */

    lambda0.lambda0 = ((zoneLong - 1) * 6.0 + (-180.0) + 3.0) * Math.PI / 180.0;
    return(0);
  }

  /**
  * <p>Converts a lat/lon coordinate to a UTM coordinate.</p>
  * @param latLon The lat/lon coordinate object.
  * @param utm The UTM coordinate object (modified call-by-reference).
  * @param datum The datum.
  * @return Whether or not the operation was successful.
  */

  public static boolean latLonToUTM(LatLon latLon, UTM utm, int datum) {
    double a, b, f, e, e2, e4, e6;
    double phi, lambda, phi0, k0;
    double t, rho, m, x, y, k, mm, mm0;
    double aa, aa2, aa3, aa4, aa5, aa6;
    double ep2, nn, tt, cc;
    Lambda0 lambda0 = new Lambda0();
    GridZone gridZone = new GridZone();

    /* Converts lat/long to UTM, using the specified datum */

    switch (datum) {
    case CLARKE_1866_DATUM:
      a = 6378206.4;
      b = 6356583.8;
      break;
    case GRS_80_DATUM:
      a = 6378137;
      b = 6356752.3;
      break;
    case WGS_84_DATUM:
      a = 6378137.0;    /* semimajor axis of ellipsoid (meters) */
      b = 6356752.31425;    /* semiminor axis of ellipsoid (meters) */
      break;
    default:
      System.out.println("Unknown datum: " + datum + ".");
      return false;
    }

    /* Calculate flatness and eccentricity */

    f = 1 - (b / a);
    e2 = 2 * f - f * f;
    e = Math.sqrt(e2);
    e4 = e2 * e2;
    e6 = e4 * e2;

    /* Convert latitude/longitude to radians */
  
    phi = (latLon.getLatitude()) * Math.PI / 180.0;
    lambda = (latLon.getLongitude()) * Math.PI / 180.0;

    /* Figure out the UTM zone, as well as lambda0 */

    getGridZone(latLon, gridZone, lambda0);
    phi0 = 0.0;

    /* See if this will use UTM or UPS */

    if (latLon.getLatitude() > 84.0) {

      /* use Universal Polar Stereographic Projection (north polar aspect) */

      k0 = 0.994;
      t = Math.sqrt(((1 - Math.sin(phi)) / (1 + Math.sin(phi))) *
         Math.pow((1 + e * Math.sin(phi))/(1 - e * Math.sin(phi)), e));
      rho = 2 * a * k0 * t /
        Math.sqrt(Math.pow(1 + e, 1 + e) * Math.pow(1 - e, 1 - e));
      m = Math.cos(phi) / Math.sqrt (1 - e2 * Math.sin(phi) * Math.sin(phi));
      x = rho * Math.sin(lambda - lambda0.lambda0);
      y = -rho * Math.cos(lambda - lambda0.lambda0);
      k = rho * a * m;

      /* Apply false easting/northing */

      x += 2000000;
      y += 2000000;
    }
    else if (latLon.getLatitude() < -80.0) {

      /* use Universal Polar Stereographic Projection (south polar aspect) */

      phi = -phi;
      lambda = -lambda;
      lambda0.lambda0 = -lambda0.lambda0;

      k0 = 0.994;

      t = Math.sqrt(((1 - Math.sin(phi)) / (1 + Math.sin(phi))) *
         Math.pow((1 + e * Math.sin(phi)) / (1 - e * Math.sin(phi)), e));
      rho = 2 * a * k0 * t /
      Math.sqrt(Math.pow(1 + e, 1 + e) * Math.pow(1 - e, 1-e));
      m = Math.cos(phi) / Math.sqrt (1 - e2 * Math.sin(phi) * Math.sin(phi));

      x = rho * Math.sin(lambda - lambda0.lambda0);
      y = -rho * Math.cos(lambda - lambda0.lambda0);
      k = rho * a * m;

      x = -x;
      y = -y;

      /* Apply false easting/northing */

      x += 2000000;
      y += 2000000;
    }
    else {
      /* Use UTM */

      /* set scale on central median (0.9996 for UTM) */
    
      k0 = 0.9996;
      mm = a * ((1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256) * phi -
          (3 * e2 / 8 + 3 * e4 / 32 + 45 * e6 / 1024) * Math.sin(2 * phi) +
          (15 * e4 / 256 + 45 * e6 / 1024) * Math.sin(4 * phi) -
          (35 * e6 / 3072) * Math.sin(6 * phi));
      mm0 = a * ((1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256) * phi0 -
           (3 * e2 / 8 + 3 * e4 / 32 + 45 * e6 / 1024) * Math.sin(2 * phi0) +
           (15 * e4 / 256 + 45 * e6 / 1024) * Math.sin(4 * phi0) -
           (35 * e6 / 3072) * Math.sin(6 * phi0));
      aa = (lambda - lambda0.lambda0) * Math.cos(phi);
      aa2 = aa * aa;
      aa3 = aa2 * aa;
      aa4 = aa2 * aa2;
      aa5 = aa4 * aa;
      aa6 = aa3 * aa3;
      ep2 = e2 / (1 - e2);
      nn = a / Math.sqrt(1 - e2 * Math.sin(phi) * Math.sin(phi));
      tt = Math.tan(phi) * Math.tan(phi);
      cc = ep2 * Math.cos(phi) * Math.cos(phi);

      k = k0 * 
        (1 + (1 + cc) * aa2 / 2 +
          (5 - 4 * tt + 42 * cc + 13 * cc * cc - 28 * ep2) * aa4 / 24.0 +
          (61 - 148 * tt + 16 * tt * tt) * aa6 / 720.0
        );
      x = k0 * nn * (aa + (1 - tt + cc) * aa3 / 6 +
         (5 - 18 * tt + tt * tt + 72 * cc - 58 * ep2) * aa5 / 120.0);
      y = k0 * (mm - mm0 + nn * Math.tan(phi) * 
          (aa2 / 2 + (5 - tt + 9 * cc + 4 * cc * cc) * aa4 / 24.0
           + (61 - 58 * tt + tt * tt + 600 * cc - 330 * ep2) * aa6 / 720));

      /* Apply false easting and northing */

      x += 500000.0;
      if (y < 0.0) {
        y += 10000000;
      }
    }

    /* Set entries in UTM structure */

    utm.setGridZone(gridZone.gridZone);
    utm.setX(x);
    utm.setY(y);
    return true;
  }

  /**
  * <p>Converts a UTM coordinate to a lat/lon coordinate.</p>
  * @param utm The UTM coordinate object.
  * @param latLon The lat/lon coordinate object (modified call-by-reference).
  * @param datum The datum.
  * @return Whether or not the operation was successful.
  */

  public static boolean utmToLatLon(UTM utm, LatLon latLon, int datum) {
    double a, b, f, e, e2, e4, e6, e8;
    double x, y, k0, rho, t, chi, phi, phi1, phit;
    double lambda, phi0, e1, e12, e13, e14;
    double mm, mm0, mu, ep2, cc1, tt1, nn1, rr1;
    double dd, dd2, dd3, dd4, dd5, dd6;
    Lambda0 lambda0 = new Lambda0();

//    System.out.println("UTM = " + utm);

    int zoneLong;
    char zoneLat;

    /* Converts UTM to lat/long, using the specified datum */

    switch (datum) {
    case CLARKE_1866_DATUM:
      a = 6378206.4;
      b = 6356583.8;
      break;
    case GRS_80_DATUM:
      a = 6378137;
      b = 6356752.3;
      break;
    case WGS_84_DATUM:
      a = 6378137.0;    /* semimajor axis of ellipsoid (meters) */
      b = 6356752.31425;    /* semiminor axis of ellipsoid (meters) */
      break;
    default:
      System.out.println("Unknown datum: " + datum + ".");
      return false;
    }

    /* Calculate flatness and eccentricity */

    f = 1 - (b / a);
    e2 = 2 * f - f * f;
    e = Math.sqrt(e2);
    e4 = e2 * e2;
    e6 = e4 * e2;
    e8 = e4 * e4;

    /* Given the UTM grid zone, generate a baseline lambda0 */

    if (getLambda0(utm.getGridZone(), lambda0) < 0) {
      System.out.println("unable to translate UTM to lat/lon.");
      return false;
    }
//    System.out.println("lambda0 = " + lambda0.lambda0);

    zoneLong = Integer.parseInt(utm.getGridZone().substring(0, 2), 10);
    zoneLat = utm.getGridZone().charAt(2);

    /* Take care of the polar regions first. */
//    System.out.println("zoneLat = " + zoneLat);

    switch(zoneLat) {
    case 'Y': case 'Z':   /* north polar aspect */
      /* Subtract the false easting/northing */

      x = utm.getX() - 2000000;
      y = utm.getY() - 2000000;

      /* Solve for inverse equations */

      k0 = 0.994;
      rho = Math.sqrt (x * x + y * y);
      t = rho * Math.sqrt(
        Math.pow(1 + e, 1 + e) * Math.pow(1 - e, 1 - e)) / (2 * a * k0);

      /* Solve for latitude and longitude */

      chi = M_PI_2 - 2 * Math.atan(t);
      phit = chi +
        (e2 / 2 + 5 * e4 / 24 + e6 / 12 + 13 * e8 / 360) * Math.sin(2 * chi) +
        (7 * e4 / 48 + 29 * e6 / 240 + 811 * e8 / 11520) * Math.sin(4 * chi) +
        (7 * e6 / 120 + 81 * e8 / 1120) * Math.sin(6 * chi) +
        (4279 * e8 / 161280) * Math.sin(8 * chi);

      do {
        phi = phit;
        phit = M_PI_2 -
          2 * 
          Math.atan(
            t *
            Math.pow((1 - e * Math.sin(phi)) / (1 + e * Math.sin(phi)), e / 2)
          );
      } while (Math.abs(phi - phit) > LOWER_EPS_LIMIT); // was fabs()

      lambda = lambda0.lambda0 + Math.atan2(x, -y);
      break;

    case 'A': case 'B':   /* south polar aspect */

      /* Subtract the false easting/northing */

      x = -(utm.getX() - 2000000);
      y = -(utm.getY() - 2000000);

      /* Solve for inverse equations */

      k0 = 0.994;
      rho = Math.sqrt (x * x + y * y);
      t = rho * Math.sqrt(
        Math.pow(1 + e, 1 + e) * Math.pow(1 - e, 1 - e)) / (2 * a * k0);

      /* Solve for latitude and longitude */

      chi = M_PI_2 - 2 * Math.atan(t);
      phit = chi + (e2 / 2 + 5 * e4 / 24 + e6 / 12 + 13 * e8 / 360) *
          Math.sin(2 * chi) +
        (7 * e4 / 48 + 29 * e6 / 240 + 811 * e8 / 11520) * Math.sin(4 * chi) +
        (7 * e6 / 120 + 81 * e8 / 1120) * Math.sin(6 * chi) +
        (4279 * e8 / 161280) * Math.sin(8 * chi);

      do {
        phi = phit;
        phit = M_PI_2 - 2 *
          Math.atan(t *
            Math.pow((1 - e * Math.sin(phi)) / (1 + e * Math.sin(phi)), e / 2)
          );
      } while (Math.abs(phi-phit) > LOWER_EPS_LIMIT); // was fabs()

      phi = -phi;
      lambda = -(-lambda0.lambda0 + Math.atan2(x,-y));

      break;

    default:

      /* Now take care of the UTM locations */

      k0 = 0.9996;

      /* Remove false eastings/northings */

      x = utm.getX() - 500000;
      y = utm.getY();

      int zoneLatValue = UnsignedChar.getASCIIValue(zoneLat);
      if (zoneLatValue > UnsignedChar.getASCIIValue('B') &&
          zoneLatValue < UnsignedChar.getASCIIValue('N')) { // southern hemi.
        y -= 10000000;
      }

      /* Calculate the footpoint latitude */

      phi0 = 0.0;
      e1 = (1 - Math.sqrt(1 - e2)) / (1 + Math.sqrt(1 - e2));
      e12 = e1 * e1;
      e13 = e1 * e12;
      e14 = e12 * e12;

      mm0 = a * ((1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256) * phi0 -
           (3 * e2/ 8 + 3 * e4 / 32 + 45 * e6 / 1024) * Math.sin(2 * phi0) +
           (15 * e4 / 256 + 45 * e6 / 1024) * Math.sin(4 * phi0) -
           (35 * e6 / 3072) * Math.sin(6 * phi0));
      mm = mm0 + y/k0;
      mu = mm / (a * (1 - e2 / 4 - 3 * e4 / 64 - 5 * e6 / 256));

      phi1 = mu + (3 * e1 / 2 - 27 * e13 / 32) * Math.sin(2 * mu) +
        (21 * e12 / 16 - 55 * e14 / 32) * Math.sin(4 * mu) +
        (151 * e13 / 96) * Math.sin(6 * mu) +
        (1097 * e14 / 512) * Math.sin(8 * mu);

      /* Now calculate lambda and phi */

      ep2 = e2 / (1 - e2);
      cc1 = ep2 * Math.cos(phi1) * Math.cos(phi1);
      tt1 = Math.tan(phi1) * Math.tan(phi1);
      nn1 = a / Math.sqrt(1 - e2 * Math.sin(phi1) * Math.sin(phi1));
      rr1 = a * (1 - e2) /
        Math.pow(1 - e2 * Math.sin(phi1) * Math.sin(phi1), 1.5);
      dd = x / (nn1 * k0);

      dd2 = dd * dd;
      dd3 = dd * dd2;
      dd4 = dd2 * dd2;
      dd5 = dd3 * dd2;
      dd6 = dd4 * dd2;

      phi = phi1 - (nn1 * Math.tan(phi1) / rr1) *
        (dd2 / 2 - (5 + 3 * tt1 + 10 * cc1 - 4 * cc1 * cc1 - 9 * ep2) * 
          dd4 / 24 +
         (61 + 90 * tt1 + 298 * cc1 + 45 * tt1 * tt1 - 252 * ep2 - 3 *
           cc1 * cc1) *
           dd6 / 720
        );

      lambda = lambda0.lambda0 +
        (dd - (1 + 2 * tt1 + cc1) * dd3 / 6 +
          (5 - 2 * cc1 + 28 * tt1 - 3 * cc1 * cc1 + 8 * ep2 + 24 * tt1 * tt1) *
          dd5 / 120) / Math.cos(phi1);
    }

    /* Convert phi/lambda to degrees */
  
    latLon.setLatitude(phi * 180.0 / Math.PI);
    latLon.setLongitude(lambda * 180.0 / Math.PI);
  
//    System.out.println("Latitude = " + latLon.getLatitude());
//    System.out.println("Longitude = " + latLon.getLongitude());
    return true;
  }

  /**
  * <p>Converts a sexagesimal coordinate to a decimal coordinate.</p>
  * @param degrees The degrees.
  * @param minutes The minutes.
  * @param seconds The seconds.
  * @return The decimal coordinate.
  */

  public static double converDMSToDecimal(int degrees, int minutes,
      double seconds) {
    return degrees + minutes / 60.0 + seconds / 3600.0;
  }

  /**
  * <p>Converts a decimal coordinate to a sexagesimal coordinate.</p>
  * @param coordinate The decimal coordinate.
  * @return The degrees-minutes-seconds object.
  */

  public static DMS convertDecimalToDMS(double coordinate) {
    return new DMS(getWholeDegrees(coordinate), getWholeMinutes(coordinate),
      getSeconds(coordinate));
  }

  /**
  * <p>Gets the integer component from a decimal coordinate.</p>
  * @param coordinate The decimal coordinate.
  * @return The degrees.
  */

  public static int getWholeDegrees(double coordinate) {
    return (int) coordinate;
  }

  /**
  * <p>Gets the minutes component from a decimal coordinate.</p>
  * @param coordinate The decimal coordinate.
  * @return The minutes.
  */

  public static int getWholeMinutes(double coordinate) {
    return (int) (
      ((coordinate - getWholeDegrees(coordinate)) * 60)
    );
  }

  /**
  * <p>Gets the (real-value) seconds component from a decimal coordinate.</p>
  * @param coordinate The decimal coordinate.
  * @return The seconds.
  */

  public static double getSeconds(double coordinate) {
    return 
    (
      ((coordinate - getWholeDegrees(coordinate)) * 60) -
      getWholeMinutes(coordinate)
    ) * 60;
  }

  /**
  * <p>Gets the (integer-value) seconds component from a decimal coordinate.</p>
  * @param coordinate The decimal coordinate.
  * @return The whole seconds.
  */

  public static int getWholeSeconds(double coordinate) {
    return (int) getSeconds(coordinate);
  }

  /**
  * <p>Runs the coordinate converter as a command-line utility.  In this
  * form it accepts the following arguments:</p>
  * <ul>
  * <li><code>CoordinateUtil [[[no args -- runs a fixed test case]]]</code>
  * <li><code>CoordinateUtil -latlon 32.28305 -106.80035</code>
  * <li><code>CoordinateUtil -utm 13S 330459 3573233</code>
  * </ul>
  * @param args The command-line arguments.
  */

  public static void main(String[] args) {
    if (usageOnly(args)) {
      return;
    }
    DecimalFormat ldf = new DecimalFormat("###.######");
    DecimalFormat udf = new DecimalFormat("#######.#");
    String latStr, lonStr, xStr, yStr;
    if (args.length == 0) { // just run a test case
      double latitude = converDMSToDecimal(34, 6, 41.03);
      double longitude = -1 * converDMSToDecimal(119, 19, 49.9);
      LatLon latLon = new LatLon(latitude, longitude);
      UTM utm = new UTM("", 0, 0);

      latLonToUTM(latLon, utm, WGS_84_DATUM);
/*
      System.out.println(
        "Lat/lon: " + latLon.getLatitude() + " " + latLon.getLongitude() +
        " UTM: " + utm.getGridZone() + " " + utm.getX() + "," + utm.getY());
*/
      latStr = ldf.format(latLon.getLatitude());
      lonStr = ldf.format(latLon.getLongitude());
      xStr = udf.format(utm.getX());
      yStr = udf.format(utm.getY());
      System.out.println(
        "Lat/lon: " + latStr + " " + lonStr +
        " UTM: " + utm.getGridZone() + " " + xStr + "," + yStr);

      latLon = new LatLon(0, 0);
      utmToLatLon(utm, latLon, WGS_84_DATUM);
/*
      System.out.println(
        "Lat/lon: " + latLon.getLatitude() + " " + latLon.getLongitude() +
        " UTM: " + utm.getGridZone() + " " + utm.getX() + "," + utm.getY());
*/
      latStr = ldf.format(latLon.getLatitude());
      lonStr = ldf.format(latLon.getLongitude());
      System.out.println(
        "Lat/lon: " + latStr + " " + lonStr +
        " UTM: " + utm.getGridZone() + " " + xStr + "," + yStr);
    }
    else if (args[0].equals("-utm")) {
      LatLon latLon = new LatLon();
      UTM utm = new UTM(args[1].toUpperCase(),
        new Double(args[2]).doubleValue(), new Double(args[3]).doubleValue());
      utmToLatLon(utm, latLon, WGS_84_DATUM);
      latStr = ldf.format(latLon.getLatitude());
      lonStr = ldf.format(latLon.getLongitude());
      xStr = udf.format(utm.getX());
      yStr = udf.format(utm.getY());
      System.out.println(
        "Lat/lon: " + latStr + " " + lonStr +
        " UTM: " + utm.getGridZone() + " " + xStr + "," + yStr);
      
    }
    else if (args[0].equals("-latlon")) {
      LatLon latLon = new LatLon(new Double(args[1]).doubleValue(),
        new Double(args[2]).doubleValue());
      UTM utm = new UTM();
      latLonToUTM(latLon, utm, WGS_84_DATUM);
      latStr = ldf.format(latLon.getLatitude());
      lonStr = ldf.format(latLon.getLongitude());
      xStr = udf.format(utm.getX());
      yStr = udf.format(utm.getY());
      System.out.println(
        "Lat/lon: " + latStr + " " + lonStr +
        " UTM: " + utm.getGridZone() + " " + xStr + "," + yStr);
      }
  }

  private static boolean usageOnly(String[] args) {
    if (args.length > 1 && args[0].equalsIgnoreCase("--help")) {
      System.out.println("Usage:  java " +
        CoordinateUtil.class.getName() + " -utm 13S 330459 3573233");
      System.out.println("Usage:  java " +
        CoordinateUtil.class.getName() + " -latlon 32.28305 -106.80035");
      return true;
    }
    return false;
  }
}

/**
* <p>This class simply wraps a <code>double</code> to allow a
* call-by-reference operation.</p>
*/

class Lambda0 {
  double lambda0 = 0;
}

/**
* <p>This class simply wraps a <code>String</code> to allow a
* call-by-reference operation.</p>
*/

class GridZone {
  String gridZone = "";
}
