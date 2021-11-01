package math.plot;

import java.awt.Color;
import java.awt.geom.Point2D;

public class Node {
    /* A node is a point with a label */
    Point2D.Double pNode;
    String lbl;
    Color col;
    
    public Node(Point2D.Double p, String str, Color c) {
        this.lbl = str;
        this.pNode = p;
        this.col = c;
    }
}
