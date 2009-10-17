package geo;

import java.awt.*;
import java.awt.event.*;

import java.text.DecimalFormat;


/**
* <p><code>ConvertCoordinates</code> is a simple lat/lon-UTM conversion
* tool.</p>
* @author Jerry Smith
* @version $Id: ConvertCoordinates.java 255 2006-01-18 16:40:51Z jsmith $
*/

public class ConvertCoordinates extends Frame implements ActionListener {
  //
  // Constants:
  //
  private static final String TITLE = "Convert Coordinates";
  private static final int DEGREES_WIDTH = 4;
  private static final int DECIMAL_DEGREES_WIDTH = 8;
  private static final int GRIDZONE_WIDTH = 3;
  private static final int EASTING_WIDTH = 8;
  private static final int NORTHING_WIDTH = 8;
  private static final String LINEFEED = "\n";
  private static final String NULL_SYMBOL = "(null)";
  //
  // Instance variables:
  //
  private Button convertDegrees, convertDecimalDegrees, convertUTM;
  private Button clear, clearDMS, clearDecimal, clearUTM;
  private TextField degreesLat, minutesLat, secondsLat;
  private TextField degreesLon, minutesLon, secondsLon;
  private TextField decimalDegreesLat, decimalDegreesLon;
  private TextField gridZone, easting, northing;
  private Choice ns = new Choice();
  private Choice ew = new Choice();
 

  /**
  * <p>Not for public consumption.</p>
  * <p><code>ConvertCoordinates</code> is never instantiated directly.</p>
  * @param title The title displayed in the title bar.
  */

  public ConvertCoordinates(String title) {
    super(title);
    setLayout(new BorderLayout(5, 5));
    InsetsPanel outerPanel = new InsetsPanel();
    outerPanel.setLayout(new BorderLayout(5, 5));
    add(outerPanel, BorderLayout.CENTER);
    Panel innerPanel = new Panel();
    innerPanel.setLayout(new GridLayout(4, 1));
    outerPanel.add(innerPanel, BorderLayout.CENTER);
    Panel latLonPanel = new Panel();
    innerPanel.add(latLonPanel);
    Panel latLonDecimalPanel = new Panel();
    innerPanel.add(latLonDecimalPanel);
    Panel utmPanel = new Panel();
    innerPanel.add(utmPanel);
    Panel bottomPanel = new Panel();
    innerPanel.add(bottomPanel);

    latLonPanel.add(convertDegrees = new Button("Convert"));
    convertDegrees.addActionListener(this);
    latLonPanel.add(new Label(" Lat Deg:"));
    latLonPanel.add(degreesLat = new TextField(DEGREES_WIDTH));
    latLonPanel.add(new Label(" Min:"));
    latLonPanel.add(minutesLat = new TextField(DEGREES_WIDTH));
    latLonPanel.add(new Label(" Sec:"));
    latLonPanel.add(secondsLat = new TextField(DEGREES_WIDTH));
    ns.add("N");
    ns.add("S");
    latLonPanel.add(ns);
    ns.select("N");
    latLonPanel.add(new Label(" Lon Deg:"));
    latLonPanel.add(degreesLon = new TextField(DEGREES_WIDTH));
    latLonPanel.add(new Label(" Min:"));
    latLonPanel.add(minutesLon = new TextField(DEGREES_WIDTH));
    latLonPanel.add(new Label(" Sec:"));
    latLonPanel.add(secondsLon = new TextField(DEGREES_WIDTH));
    ew.add("E");
    ew.add("W");
    latLonPanel.add(ew);
    ew.select("W");
    latLonPanel.add(clearDMS = new Button("Clear"));
    clearDMS.addActionListener(this);

    latLonDecimalPanel.add(convertDecimalDegrees = new Button("Convert"));
    convertDecimalDegrees.addActionListener(this);
    latLonDecimalPanel.add(new Label(" Lat Deg Decimal:"));
    latLonDecimalPanel.add(
      decimalDegreesLat = new TextField(DECIMAL_DEGREES_WIDTH));
    latLonDecimalPanel.add(new Label(" Lon Deg Decimal:"));
    latLonDecimalPanel.add(
      decimalDegreesLon = new TextField(DECIMAL_DEGREES_WIDTH));
    latLonDecimalPanel.add(clearDecimal = new Button("Clear"));
    clearDecimal.addActionListener(this);

    utmPanel.add(convertUTM = new Button("Convert"));
    convertUTM.addActionListener(this);
    utmPanel.add(new Label(" Grid Zone:"));
    utmPanel.add(gridZone = new TextField(GRIDZONE_WIDTH));
    utmPanel.add(new Label(" Easting:"));
    utmPanel.add(easting = new TextField(EASTING_WIDTH));
    utmPanel.add(new Label(" Northing:"));
    utmPanel.add(northing = new TextField(NORTHING_WIDTH));
    utmPanel.add(clearUTM = new Button("Clear"));
    clearUTM.addActionListener(this);

    bottomPanel.add(clear = new Button("Clear"));
    clear.addActionListener(this);

    pack();
    setSize(getPreferredSize());
    setVisible(true);
    addWindowListener(new ApplicationCloser());
  }

  /**
  * <p>Runs the coordinate converter.</p>
  * @param args No arguments are accepted.
  */

  public static void main(String[] args) {
    if (args.length > 0) {
      System.out.println("Usage: java " + ConvertCoordinates.class.getName());
      return;
    }
    new ConvertCoordinates(TITLE);
  }

  /**
  * <p>Not for public consumption.</p>
  * <p>Processes graphically triggered operations.</p>
  * @param event The GUI's button events.
  */

  public void actionPerformed(ActionEvent event) {
    Object source = event.getSource();
    if (source == convertDegrees) {
      convertLatLonToUTM();
    }
    else if (source == convertDecimalDegrees) {
      convertDecimalLatLonToUTM();
    }
    else if (source == convertUTM) {
      convertUTMToLatLon();
    }
    else if (source == clearDMS) {
      clearDMSData();
    }
    else if (source == clearDecimal) {
      clearDecimalData();
    }
    else if (source == clearUTM) {
      clearUTMData();
    }
    else if (source == clear) {
      clearDMSData();
      clearDecimalData();
      clearUTMData();
    }
  }

  private void clearDMSData() {
    degreesLat.setText("");
    minutesLat.setText("");
    secondsLat.setText("");
    ns.select("N");
    degreesLon.setText("");
    minutesLon.setText("");
    secondsLon.setText("");
    ew.select("W");
  }

  private void clearDecimalData() {
    decimalDegreesLat.setText("");
    decimalDegreesLon.setText("");
  }

  private void clearUTMData() {
    gridZone.setText("");
    easting.setText("");
    northing.setText("");
  }

  private void convertLatLonToUTM() {
    int multiplier = ns.getSelectedIndex() == 0 ? 1 : -1;
    double latitude = multiplier *
      (getDouble(degreesLat) + getDouble(minutesLat) / 60.0 +
        getDouble(secondsLat) / 3600.0);
    multiplier = ew.getSelectedIndex() == 0 ? 1 : -1;
    double longitude = multiplier * 
      (getDouble(degreesLon) + getDouble(minutesLon) / 60.0 +
          getDouble(secondsLon) / 3600.0);
    LatLon latLon = new LatLon(latitude, longitude);
    UTM utm = new UTM("", 0, 0);
    if (!CoordinateUtil.latLonToUTM(latLon, utm, CoordinateUtil.WGS_84_DATUM)) {
      return;
    }
    DecimalFormat ldf = new DecimalFormat("###.######");
    decimalDegreesLat.setText(ldf.format(latitude));
    decimalDegreesLon.setText(ldf.format(longitude));
    DecimalFormat udf = new DecimalFormat("#######.#");
    gridZone.setText(utm.getGridZone());
    easting.setText(udf.format(utm.getX()));
    northing.setText(udf.format(utm.getY()));
  }

  private void convertDecimalLatLonToUTM() {
    LatLon latLon =
      new LatLon(getDouble(decimalDegreesLat), getDouble(decimalDegreesLon));
    UTM utm = new UTM("", 0, 0);
    if (!CoordinateUtil.latLonToUTM(latLon, utm, CoordinateUtil.WGS_84_DATUM)) {
      return;
    }
    handleSexagesimal(latLon.getLatitude(), latLon.getLongitude());
    DecimalFormat udf = new DecimalFormat("#######.#");
    gridZone.setText(utm.getGridZone());
    easting.setText(udf.format(utm.getX()));
    northing.setText(udf.format(utm.getY()));
  }

  private void convertUTMToLatLon() {
    UTM utm = new UTM(gridZone.getText().toUpperCase(),
      getDouble(easting), getDouble(northing));
    LatLon latLon = new LatLon();
    if (!CoordinateUtil.utmToLatLon(utm, latLon, CoordinateUtil.WGS_84_DATUM)) {
      return;
    }
    DecimalFormat ldf = new DecimalFormat("###.######");
    double latitude = latLon.getLatitude();
    double longitude = latLon.getLongitude();
    decimalDegreesLat.setText(ldf.format(latitude));
    decimalDegreesLon.setText(ldf.format(longitude));
    handleSexagesimal(latitude, longitude);
  }

  private void handleSexagesimal(double latitude, double longitude) {
    DecimalFormat ldf = new DecimalFormat("###.##");
    ns.select(latitude > 0 ? "N" : "S");
    latitude = Math.abs(latitude);
    DMS dms = CoordinateUtil.convertDecimalToDMS(Math.abs(latitude));
    degreesLat.setText(ldf.format(dms.getDegrees()));
    minutesLat.setText(ldf.format(dms.getMinutes()));
    secondsLat.setText(ldf.format(dms.getSeconds()));
    ew.select(longitude > 0 ? "E" : "W");
    longitude = Math.abs(longitude);
    dms = CoordinateUtil.convertDecimalToDMS(Math.abs(longitude));
    degreesLon.setText(ldf.format(dms.getDegrees()));
    minutesLon.setText(ldf.format(dms.getMinutes()));
    secondsLon.setText(ldf.format(dms.getSeconds()));
  }

  private double getDouble(TextField tf) {
    return Double.parseDouble(checkValue(tf.getText(), "0.0"));
  }

  private String checkValue(String str, String altStr) {
    return (str == null || str.length() == 0) ? altStr : str;
  }

  class InsetsPanel extends Panel {
    public Insets getInsets() {
      return new Insets(5, 5, 5, 5);
    }
  }

  class ApplicationCloser extends WindowAdapter {
    public void windowClosing(WindowEvent event) {
      if (event.getID() == WindowEvent.WINDOW_CLOSING) {
        System.exit(0);
      }
    }
  }
}
