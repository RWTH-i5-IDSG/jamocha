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

maxi={}

for strat in ("TIME_FACT","TRIGGER_FACT","SEPARATE_RETE"):
	maxi[strat]=1
	for size in range(1,int(sizemax)+1):
		outputfile=basedir+path+"-"+strat+"-"+str(size)
		os.system("cd "+jamochabindir+" && java org.jamocha.benchmarking.MeasureCaller "+strat+" "+timemax+" "+timestep+" "+str(size)+" "+measurement+" "+outputfile)
		fifi = open(outputfile+"-max",'r')
		m = int(fifi.readlines()[0])
		fifi.close
		if (m > maxi[strat]): maxi[strat]=m

#X TIME     Y SIZE       Z LAG


for strat in ("TIME_FACT","TRIGGER_FACT","SEPARATE_RETE"):

	header="""
        set terminal postscript eps
        set xrange [0:%s]
        set yrange [0:%s]
        #set zrange [0:%d]
        #set zrange [*:*] 
        set data style lines
        #set contour base
        set dgrid3d %d,%d,1
        set hidden3d
        #show contour
        #set view 45,20,1.0,2.5
	""" % (timemax,sizemax,maxi[strat],int(sizemax)+1,(int(timemax)/int(timestep))+1)

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
			
	ofile.write("splot \"%s\" title 'Lag in ms' \n" % (basedir+path+"-"+strat+"-ALL") )
	#ofile.write("replot\n\n")
	ofile.flush
	ofile.close

	cmd="gnuplot "+basedir+path+"-"+strat
	
	#print (cmd)

	#os.system("gnome-terminal -e '  bash -c \"    %s ; sleep 1   \"  '   " % (cmd) )
	
#os.system ("sleep 5; rm "+basedir+path+"*")
	