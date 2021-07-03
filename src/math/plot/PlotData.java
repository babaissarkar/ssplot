package math.plot;

import java.awt.Color;
import java.util.Vector;

public class PlotData {
    /* This class is the Model.*/
    public enum PlotType { LINES, POINTS, LP, VECTORS, THREED, TRLINE };
    public enum PointType { SQUARE, CIRCLE };
    
    public Vector<Vector<Double>> data;
    public PlotType pltype;
    public Color fgColor, fgColor2;
    public PointType pttype;
    public int ptX, ptY;

    public PlotData() {
        this(new Vector<Vector<Double>>());
    }

    public PlotData(Vector<Vector<Double>> extData) {
        data = extData;
        pltype = PlotType.LINES;
        pttype = PointType.SQUARE;
        ptX = 2; ptY = 2;
        fgColor = Color.RED;
    }
}
