# $Id: latlon2utm.sh 247 2006-01-16 22:24:56Z JSmith $

#
# java geo.CoordinateUtil -latlon 32.28305 -106.80035
#

java -classpath .. geo.CoordinateUtil -latlon $1 $2
