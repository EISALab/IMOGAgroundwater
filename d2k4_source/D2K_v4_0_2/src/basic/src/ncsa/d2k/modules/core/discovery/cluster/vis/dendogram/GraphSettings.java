package ncsa.d2k.modules.core.discovery.cluster.vis.dendogram;

public class GraphSettings {
        public String title, xaxis, yaxis;

        public Integer xminimum, xmaximum;
        public Integer yminimum, ymaximum;

        public int gridsize;

        public boolean displaygrid;
        public boolean displayscale;
        public boolean displaylegend;
        public boolean displaytickmarks;
        public boolean displaytitle;
        public boolean displayaxislabels;

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
        }
}
