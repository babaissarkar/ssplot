all : jar

jar : compile
	cd ../../../bin && fastjar -cv -f ../jar/math.jar math/prim

compile :
	javac -14 -d ../../../bin/ *.java
