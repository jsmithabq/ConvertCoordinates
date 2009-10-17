# $Id: utm2latlon.sh 244 2006-01-16 21:33:22Z jsmith $

#
# java geo.CoordinateUtil -utm 13S 330459 3573233
#

java -classpath .. geo.CoordinateUtil -utm $1 $2 $3
