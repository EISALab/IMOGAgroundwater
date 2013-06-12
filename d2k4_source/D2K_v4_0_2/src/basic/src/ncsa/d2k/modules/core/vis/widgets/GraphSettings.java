package ncsa.d2k.modules.core.vis.widgets;

public class GraphSettings implements java.io.Serializable {
	public String title, xaxis, yaxis;

	public Integer xminimum, xmaximum;
	public Integer yminimum, ymaximum;

	/** this is the forced max and min data value to plot in x. */
	public Double xdataminimum, xdatamaximum;

	/** this is the forced max and min data value to plot in y direction. */
	public Double ydataminimum, ydatamaximum;

	public int gridsize;

	public boolean displaygrid;
	public boolean displayscale;
	public boolean displaylegend;
	public boolean displaytickmarks;
	public boolean displaytitle;
	public boolean displayaxislabels;
	public boolean displayaxis;

	public GraphSettings() {
		title = "";
		xaxis = "";
		yaxis = "";

		xminimum = null;
		xmaximum = null;
		yminimum = null;
		ymaximum = null;

		gridsize = 10;

		displaygrid = true;
		displayscale = true;
		displaylegend = true;
		displaytickmarks = true;
		displaytitle = true;
		displayaxislabels = true;
		displayaxis = true;
	}
}
