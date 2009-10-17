@rem $Id: latlon2utm.bat 247 2006-01-16 22:24:56Z JSmith $

@rem
@rem java geo.CoordinateUtil -latlon 32.28305 -106.80035
@rem

java -classpath .. geo.CoordinateUtil -latlon %1 %2
