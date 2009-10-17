package geo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.text.DecimalFormat;


/**
* <p><code>JConvertCoordinates</code> is a simple lat/lon-UTM conversion
* tool.</p>
* @author Jerry Smith
* @version $Id: JConvertCoordinates.java 255 2006-01-18 16:40:51Z jsmith $
*/

public class JConvertCoordinates extends JFrame implements ActionListener {
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
  private JButton convertDegrees, convertDecimalDegrees, convertUTM;
  private JButton clear, clearDMS, clearDecimal, clearUTM;
  private JTextField degreesLat, minutesLat, secondsLat;
  private JTextField degreesLon, minutesLon, secondsLon;
  private JTextField decimalDegreesLat, decimalDegreesLon;
  private JTextField gridZone, easting, northing;
  private JComboBox ns = new JComboBox(new String[] {"N", "S"});
  private JComboBox ew = new JComboBox(new String[] {"E", "W"});
 

  /**
  * <p>Not for public consumption.</p>
  * <p><code>JConvertCoordinates</code> is never instantiated directly.</p>
  * @param title The title displayed in the title bar.
  */

  public JConvertCoordinates(String title) {
    super(title);
    Container content = getContentPane();
    content.setLayout(new BorderLayout(5, 5));
    JInsetsPanel outerPanel = new JInsetsPanel();
    outerPanel.setLayout(new BorderLayout(5, 5));
    content.add(outerPanel, BorderLayout.CENTER);
    JPanel innerPanel = new JPanel();
    innerPanel.setLayout(new GridLayout(4, 1));
    outerPanel.add(innerPanel, BorderLayout.CENTER);
    JPanel latLonPanel = new JPanel();
    innerPanel.add(latLonPanel);
    JPanel latLonDecimalPanel = new JPanel();
    innerPanel.add(latLonDecimalPanel);
    JPanel utmPanel = new JPanel();
    innerPanel.add(utmPanel);
    JPanel bottomPanel = new JPanel();
    innerPanel.add(bottomPanel);

    latLonPanel.add(convertDegrees = new JButton("Convert"));
    convertDegrees.addActionListener(this);
    latLonPanel.add(new JLabel(" Lat Deg:"));
    latLonPanel.add(degreesLat = new JTextField(DEGREES_WIDTH));
    latLonPanel.add(new JLabel(" Min:"));
    latLonPanel.add(minutesLat = new JTextField(DEGREES_WIDTH));
    latLonPanel.add(new JLabel(" Sec:"));
    latLonPanel.add(secondsLat = new JTextField(DEGREES_WIDTH));
    latLonPanel.add(ns);
    ns.setSelectedIndex(0);
    latLonPanel.add(new JLabel(" Lon Deg:"));
    latLonPanel.add(degreesLon = new JTextField(DEGREES_WIDTH));
    latLonPanel.add(new JLabel(" Min:"));
    latLonPanel.add(minutesLon = new JTextField(DEGREES_WIDTH));
    latLonPanel.add(new JLabel(" Sec:"));
    latLonPanel.add(secondsLon = new JTextField(DEGREES_WIDTH));
    latLonPanel.add(ew);
    ew.setSelectedIndex(1);
    latLonPanel.add(clearDMS = new JButton("Clear"));
    clearDMS.addActionListener(this);

    latLonDecimalPanel.add(convertDecimalDegrees = new JButton("Convert"));
    convertDecimalDegrees.addActionListener(this);
    latLonDecimalPanel.add(new JLabel(" Lat Deg Decimal:"));
    latLonDecimalPanel.add(
      decimalDegreesLat = new JTextField(DECIMAL_DEGREES_WIDTH));
    latLonDecimalPanel.add(new JLabel(" Lon Deg Decimal:"));
    latLonDecimalPanel.add(
      decimalDegreesLon = new JTextField(DECIMAL_DEGREES_WIDTH));
    latLonDecimalPanel.add(clearDecimal = new JButton("Clear"));
    clearDecimal.addActionListener(this);

    utmPanel.add(convertUTM = new JButton("Convert"));
    convertUTM.addActionListener(this);
    utmPanel.add(new JLabel(" Grid Zone:"));
    utmPanel.add(gridZone = new JTextField(GRIDZONE_WIDTH));
    utmPanel.add(new JLabel(" Easting:"));
    utmPanel.add(easting = new JTextField(EASTING_WIDTH));
    utmPanel.add(new JLabel(" Northing:"));
    utmPanel.add(northing = new JTextField(NORTHING_WIDTH));
    utmPanel.add(clearUTM = new JButton("Clear"));
    clearUTM.addActionListener(this);

    bottomPanel.add(clear = new JButton("Clear"));
    clear.addActionListener(this);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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
      System.out.println("Usage: java " + JConvertCoordinates.class.getName());
      return;
    }
    new JConvertCoordinates(TITLE);
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
    ns.setSelectedIndex(0);
    degreesLon.setText("");
    minutesLon.setText("");
    secondsLon.setText("");
    ew.setSelectedIndex(1);
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
    ns.setSelectedIndex(latitude > 0 ? 0 : 1);
    latitude = Math.abs(latitude);
    DMS dms = CoordinateUtil.convertDecimalToDMS(Math.abs(latitude));
    degreesLat.setText(ldf.format(dms.getDegrees()));
    minutesLat.setText(ldf.format(dms.getMinutes()));
    secondsLat.setText(ldf.format(dms.getSeconds()));
    ew.setSelectedIndex(longitude > 0 ? 0 : 1);
    longitude = Math.abs(longitude);
    dms = CoordinateUtil.convertDecimalToDMS(Math.abs(longitude));
    degreesLon.setText(ldf.format(dms.getDegrees()));
    minutesLon.setText(ldf.format(dms.getMinutes()));
    secondsLon.setText(ldf.format(dms.getSeconds()));
  }

  private double getDouble(JTextField tf) {
    return Double.parseDouble(checkValue(tf.getText(), "0.0"));
  }

  private String checkValue(String str, String altStr) {
    return (str == null || str.length() == 0) ? altStr : str;
  }

  class JInsetsPanel extends JPanel {
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
