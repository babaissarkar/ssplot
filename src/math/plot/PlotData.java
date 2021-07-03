/*
 * PlotData.java
 * 
 * Copyright 2021 Subhraman Sarkar <subhraman@subhraman-Inspiron>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

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
