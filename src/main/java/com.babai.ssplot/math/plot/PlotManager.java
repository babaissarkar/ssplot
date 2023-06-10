package math.plot;

import java.util.ArrayList;
import java.util.List;

public class PlotManager {
	private List<PlotData> plots;
	//private PlotData curPlot;
	
	public PlotManager() {
		plots = new ArrayList<PlotData>();
	}
	
	public void add(PlotData pdata) {
		plots.add(pdata);
	}
	
	public PlotData get(int pos) {
		return plots.get(pos);
	}
	
	public PlotData last() {
		return plots.get(plots.size());
	}
	
	public void remove() {
		plots.remove(plots.size());
	}
}
