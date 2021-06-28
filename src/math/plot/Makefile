all : jar

jar : compile
	cd ../../../bin && fastjar -cv -f ../jar/SSPlot.jar -m ../src/math/plot/META-INF/MANIFEST.MF math
	
compile : DBViewer.java NumParse.java ODEData.java PlotView.java ODEInputFrame.java PlotterFrame.java
	javac -14 -cp ../../../jar/math.jar -d ../../../bin/ *.java

run : jar
	java -jar ../SSPlot.jar

clean :
#	rm -rf ${PWD}/math ${PWD}/SSPlot.jar
