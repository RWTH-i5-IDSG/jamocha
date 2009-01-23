#!/usr/bin/python

import sys,os
from subprocess import *


if len(sys.argv)==1:
    print "Usage: messer.py MEASUREMENT-CLASS TIME-MAX TIME-STEP PROBLEMSIZE OUTPUTFILENAMES OUTPUTDIR JAMOCHA-BINDIR"


basedir=sys.argv[6]+"/"
measurement=sys.argv[1]
timemax=sys.argv[2]
timestep=sys.argv[3]
sizemax=sys.argv[4]
path=sys.argv[5]
jamochabindir=sys.argv[7]
start=sys.argv[8]

maxi={}

for strat in ("TRIGGER_FACT","SEPARATE_RETE"):
#if 1==1:
#	strat="TIME_FACT"
	maxi[strat]=1
	for size in range(int(start),int(sizemax)+1):
		print "jetzt kommt "+str(size)
		outputfile=basedir+path+"-"+strat+"-"+str(size)
		os.system("cd "+jamochabindir+" && java -Xmx2048m -Xms2048m  org.jamocha.benchmarking.MeasureCaller "+strat+" "+timemax+" "+timestep+" "+str(size)+" "+measurement+" "+outputfile)
		fifi = open(outputfile+"-max",'r')
		m = int(fifi.readlines()[0])
		fifi.close
		if (m > maxi[strat]): maxi[strat]=m

#X TIME     Y SIZE       Z LAG


for strat in ("TRIGGER_FACT","SEPARATE_RETE"):
#if 1==1:
#	strat="TIME_FACT"
	header="""
        set terminal postscript eps
        set xrange [0:%s]
        set yrange [0:%s]
        set ytics 5,5,%s
        #set zrange [0:%s]
        set label "Zeit in ms" at screen 0.2, screen 0.08 rotate by -7
        set label "Lag in ms" at screen 0.01, screen 0.30 rotate by 90
        set label "Anzahl temp. Elemente" at screen 0.80, screen 0.05 rotate by 45
        #set xlabel "Zeit in s" offset -1,0 rotate by -10.0
        #set ylabel "Anzahl temp. Elemente" rotate by 30.0
        #set zlabel "Lag in ms" rotate by 90.0
        set  style data lines
        set ticslevel 0.0
        set dgrid3d %d,%d,1
        set view 60,20,1.0,1.4
        set hidden3d
	""" % (timemax,sizemax,sizemax,maxi[strat],int(sizemax)+1,(int(timemax)/int(timestep))+1)

	ffile = open(basedir+path+"-"+strat+"-ALL",'w')
	for size in range(1,int(sizemax)+1):
		efile = open(basedir+path+"-"+strat+"-"+str(size),'r')
		for l in efile.readlines(): ffile.write(l)
		efile.close
	ffile.close
	
	ofile = os.popen("gnuplot",'w')
	ofile.write(header)
	ofile.write("set terminal postscript eps\n")
	ofile.write("set output \"%s\"\n" % (basedir+"graph-"+path+"-"+strat+".eps"))
			
	ofile.write("splot \"%s\" notitle \n" % (basedir+path+"-"+strat+"-ALL") )
	#ofile.write("replot\n\n")
	ofile.flush
	ofile.close

	cmd="gnuplot "+basedir+path+"-"+strat
	
	#print (cmd)

	#os.system("gnome-terminal -e '  bash -c \"    %s ; sleep 1   \"  '   " % (cmd) )
	
#os.system ("sleep 5; rm "+basedir+path+"*")
	
