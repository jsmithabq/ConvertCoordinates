@rem $Id: utm2latlon.bat 244 2006-01-16 21:33:22Z jsmith $

@rem
@rem java geo.CoordinateUtil -utm 13S 330459 3573233
@rem

java -classpath .. geo.CoordinateUtil -utm %1 %2 %3
