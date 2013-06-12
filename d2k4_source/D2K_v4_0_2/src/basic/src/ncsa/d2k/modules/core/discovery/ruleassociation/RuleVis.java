package ncsa.d2k.modules.core.discovery.ruleassociation;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import ncsa.d2k.core.modules.*;
import java.util.*;
import ncsa.gui.*;
import java.io.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.core.discovery.ruleassociation.*;


/**
	RuleVis.java
*/
public class RuleVis extends ncsa.d2k.core.modules.VisModule
{
	static final int BAR_HEIGHT = 56;
	static final Color RULE_VIS_BACKGROUND = new Color (238, 237,237);
	static final Color RULE_VIS_CONFIDENCE = new Color (196, 195, 26);
	static final Color RULE_VIS_SUPPORT = new Color (87, 87, 100);
	static final Color RULE_VIS_HIGHLIGHT = new Color (247, 247, 247);
        private static final Dimension buttonsize = new Dimension(22, 22);
        private static final Color yellowish = new Color(255, 255, 240);
        private static final String filtericon = File.separator+"images"+File.separator+"filter.gif";
        private static final String printicon = File.separator+"images"+File.separator+"printit.gif";
        private static final String refreshicon = File.separator+"images"+File.separator+"home.gif";
        private static final String helpicon = File.separator+"images"+File.separator+"help.gif";
        private static final String abcicon = File.separator+"images"+File.separator+"abc.gif";
        private static final String rankicon = File.separator+"images"+File.separator+"rank.gif";


	/**	This method returns an array of strings that contains the data types for the inputs.
		@return the data types of all inputs.
	*/
	public String[] getInputTypes () {
		String[] types = {"ncsa.d2k.modules.core.discovery.ruleassociation.RuleTable"};
		return types;
	}

	/**
	 * Return the human readable name of the indexed input.
	 * @param index the index of the input.
	 * @return the human readable name of the indexed input.
	 */
	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "Rule Table";
			default:
				return "No such input";
		}
	}
	/**
		This method returns the description of the various inputs.
		@return the description of the indexed input.
	*/
	public String getInputInfo(int index) {
		switch (index) {
		    case 0:
			return "A representation of association rules to be displayed. ";
		    default:
			return "No such input";
		}
	}

	/**	This method returns an array of strings that contains the data types for the outputs.
		@return the data types of all outputs.
	*/
	public String[] getOutputTypes () {
		return null;
        }

	/**	This method returns the description of the outputs.
		@return the description of the indexed output.
	*/
	public String getOutputInfo (int index) {
		switch (index) {
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the indexed output.
	 * @param index the index of the output.
	 * @return the human readable name of the indexed output.
	 */
	public String getOutputName(int index) {
		switch(index) {
			default: return "No such output";
		}
	}

	/**
	 * Return the human readable name of the module.
	 * @return the human readable name of the module.
	 */
	public String getModuleName() {
		return "Rule Visualization";
	}

	/**	This method returns the description of the module.
		@return the description of the module.
	*/
       	public String getModuleInfo () {
	  StringBuffer sb = new StringBuffer( "<p>Overview: ");
          sb.append( "This module provides a visual representation of the association rules encapsulated in the ");
          sb.append("input <i>Rule Table<i>. ");

          sb.append("</p><p>Detailed Description: ");
          sb.append("This module presents a visual representation of association rules identified by ");
          sb.append("a discovery algorithm. ");
          sb.append("D2K includes several modules that implement association rule discovery algorithms, ");
          sb.append("all of which save their results in a <i>Rule Table</i> structure that can be used as ");
          sb.append("input to this module. ");

          sb.append("</p><p> ");
          sb.append("The main region of the display contains a matrix that visually depicts the rules. ");
          sb.append("Each numbered column in the matrix corresponds to an association rule  ");
          sb.append("that met the minimum support and confidence requirements specified by the user in the ");
          sb.append("rule discovery modules. ");
          sb.append("Items used in the rules, that is attribute-value pairs, are listed along the left  ");
          sb.append("side of the matrix. ");
          sb.append("Note that some items in the original data set may not be included in any rule ");
          sb.append("because there was insufficient support and/or confidence to consider the item ");
          sb.append("significant. ");

          sb.append("</p><p> ");
          sb.append("An icon in the matrix cell corresponding to ( row = <i>item i</i>, column = <i>rule r</i>) ");
          sb.append("indicates that <i>item i</i> is included in <i>rule r</i>. ");
          sb.append("If the matrix cell icon is a box, then the item is part of the rule antecedent. If ");
          sb.append("the icon is a check mark, then the item is part of the rule consequent. ");
          sb.append("For example, if the rules being displayed indicate whether or not a mushroom is edible, ");
          sb.append("a rule might be &quot;odor=none&quot; and &quot;ring_number=one&quot; then &quot;edibility=edible&quot;. ");
          sb.append("This rule would be displayed in a column with a box in the row for the item &quot;odor=none&quot; ");
          sb.append("and a box in the row for &quot;ring_number=one&quot;, and there would be a check in the ");
          sb.append("row for &quot;edibility=edible&quot;. ");

          sb.append("</p><p> ");
          sb.append("Above the main matrix are two rows of bars labeled <i>Confidence</i> and <i>Support</i>. ");
          sb.append("These bars align with the corresponding rule columns in the main matrix.  For any given rule, ");
          sb.append("the confidence and support values for that rule are represented by the degree to which the ");
          sb.append("bars above the rule column are filled in.   Brushing the mouse on a confidence or support ");
          sb.append("bar displays the exact value that is graphically represented by the bar height. ");

          sb.append("</p><p>");
          sb.append("The rules can be ordered by confidence or by support. ");
          sb.append("To sort the rules, click either the support or the confidence label -- ");
          sb.append("these labels are clickable radio buttons. ");
          sb.append("If support is selected, rules will be sorted using support as the primary key and confidence as the secondary key. ");
          sb.append("Conversely, if the confidence button is chosen, confidence is the primary sort key and support is the secondary key.  ");

          sb.append("</p><p>");
          sb.append("Directly above the confidence and support display is a toolbar that provides additional functionality.  ");
          sb.append("On the left side of the toolbar are two buttons that allow the rows of the table to be displayed ");
          sb.append("according to different sorting schemes. One of the buttons is active at all times. ");
          sb.append("The <i>Alphabetize</i> button sorts the attribute-value pairs alphabetically.  ");
          sb.append("The <i>Rank</i> button sorts the rows based on the current Confidence/Support selection, moving the ");
          sb.append("consequents and antecedents of the highest ranking rules to the ");
          sb.append("top of the attribute-value list. ");
          sb.append("</p><p>");
          sb.append("On the right side of the toolbar are four additional buttons:<br> ");
          sb.append("Restore Original reverts ");
          sb.append("back to the original table that was displayed before any sorting was done. ");
          sb.append("<br>Filter provides an interface that allows the user to display a subset of the generated rules. ");
          sb.append("<br>Print prints a screen capture of the visual display. ");
          sb.append("The print output contains only the cells that are visible in the display window, not all the cells ");
          sb.append("in the rule table.  The user can scroll to different part of the matrix and print multiple times ");
          sb.append("to get the full picture of a large matrix. Printing is also accessible via the Options menu. ");
          sb.append("<br>Help displays information describing the visualization. ");
          sb.append("</p><p>Scalability: ");
          sb.append("While the visualization can display a large number of items and rules, there can be a noticeable delay " );
	  sb.append("in opening the visualization when a large number of cells are involved. " );
          sb.append("Also, as the number of cells increases beyond ");
          sb.append("a certain point, it is difficult to gain insights from the display.  Advanced features to help ");
          sb.append("in these cases are being discussed for a future release. ");

	  return sb.toString();
	}


        public PropertyDescription[] getPropertiesDescriptions() {
		// hide properties that the user shouldn't udpate
       		return new PropertyDescription[0];
   	}


	/**	This method is called by D2K to get the UserView for this module.
		@return the UserView.
	*/
	protected UserView createUserView() {
          return new RuleVisView();
	}

	/**	This method returns an array with the names of each DSComponent in the UserView
		that has a value.  These DSComponents are then used as the outputs of this module.
	*/
	public String[] getFieldNameMapping() {
		String[] fieldMap = {};
		return fieldMap;
	}

	/**	RuleVisView
		This is the UserView class.
	*/
	private class RuleVisView extends ncsa.d2k.userviews.swing.JUserPane implements ActionListener, Printable {

		java.util.List itemLabels = null;

		RuleTable ruleTable;
		int numExamples;
		ValueVisDataModel vvdm;
		RuleVisDataModel rvdm;
		JMenuItem print;
                JPanel header;
                JButton filterButton;
                JButton refreshButton;
                JButton printButton;
                JButton helpButton;
                JToggleButton abcButton;
                JToggleButton rankButton;
                HelpWindow help;
		JMenuItem pmml;
		JMenuBar menuBar;

                JTable rjt;
                JTable vjt;
		/** this identifies the order the rules are displayed in, also what rules are
		 * displayed. */
		int [] order = null;


		/**	This method adds the components to a Panel and then adds the Panel
			to the view.
		*/
		public void initView(ViewModule mod) {
			//module = mod;
			menuBar = new JMenuBar();
			JMenu options = new JMenu("Options");
			print = new JMenuItem("Print...");
			print.addActionListener(this);

                        //load the images one by one
                        Image im = mod.getImage(filtericon);
                        ImageIcon icon = null;
                        if(im != null)
                           icon = new ImageIcon(im);
                        if(icon != null) {
                           filterButton = new JButton(icon);
                           filterButton.setMaximumSize(buttonsize);
                           filterButton.setPreferredSize(buttonsize);
                        }
                        else
                          filterButton = new JButton("F");
                        filterButton.addActionListener(this);
                        filterButton.setToolTipText("Filter");

                        im = mod.getImage(printicon);
                        icon = null;
                        if(im != null)
                          icon = new ImageIcon(im);
                        if(icon != null) {
                          printButton = new JButton(icon);
                          printButton.setMaximumSize(buttonsize);
                          printButton.setPreferredSize(buttonsize);
                        }
                        else
                          printButton = new JButton("P");
                        printButton.addActionListener(this);
                        printButton.setToolTipText("Print");

                        im = mod.getImage(helpicon);
                        icon = null;
                        if(im != null)
                          icon = new ImageIcon(im);
                        if(icon != null) {
                          helpButton = new JButton(icon);
                          helpButton.setMaximumSize(buttonsize);
                          helpButton.setPreferredSize(buttonsize);
                        }
                        else
                          helpButton = new JButton("H");
                        helpButton.addActionListener(this);
                        helpButton.setToolTipText("Help");

                        im = null;
                        icon = null;
                        im = mod.getImage(refreshicon);
                        if(im != null)
                          icon = new ImageIcon(im);
                        if(icon != null) {
                          refreshButton = new JButton(icon);
                          refreshButton.setMaximumSize(buttonsize);
                          refreshButton.setPreferredSize(buttonsize);
                        }
                        else
                          refreshButton = new JButton("R");
                        refreshButton.addActionListener(this);
                        refreshButton.setToolTipText("Restore Original");

                        im = null;
                        icon = null;
                        im = mod.getImage(abcicon);
                        if(im != null)
                          icon = new ImageIcon(im);
                        if(icon != null) {
                          abcButton = new JToggleButton(icon,true);
                          abcButton.setMaximumSize(buttonsize);
                          abcButton.setPreferredSize(buttonsize);
                        }
                        else
                          abcButton = new JToggleButton("A");
                        abcButton.addActionListener(this);
                        abcButton.setToolTipText("Alphabetize");

                        im = null;
                        icon = null;
                        im = mod.getImage(rankicon);
                        if(im != null)
                          icon = new ImageIcon(im);
                        if(icon != null) {
                          rankButton = new JToggleButton(icon);
                          rankButton.setMaximumSize(buttonsize);
                          rankButton.setPreferredSize(buttonsize);
                        }
                        else
                          rankButton = new JToggleButton("R");
                        rankButton.addActionListener(this);
                        rankButton.setToolTipText("Rank");

                        //set up the tool bar
                        JPanel tools_right = new JPanel();
                        tools_right.setLayout(new GridLayout(1,4));
                        JPanel tools_left = new JPanel();
                        tools_left.setLayout(new GridLayout(1,2));
                        tools_left.add(abcButton);
                        tools_left.add(rankButton);
                        tools_right.add(refreshButton);
                        tools_right.add(filterButton);
                        tools_right.add(printButton);
                        tools_right.add(helpButton);
                        JPanel east = new JPanel();
                        JPanel west = new JPanel();
                        west.setLayout(new BoxLayout(west, BoxLayout.Y_AXIS));
                        east.setLayout(new BoxLayout(east, BoxLayout.Y_AXIS));
                        west.add(Box.createGlue());
                        east.add(Box.createGlue());
                        west.add(tools_left);
                        east.add(tools_right);
                        header = new JPanel();
                        header.setLayout(new BorderLayout());
                        header.add(west, BorderLayout.WEST);
                        header.add(east, BorderLayout.EAST);
                        help = new HelpWindow();
			options.add(print);

			// Commented out for Bsic as PMML not working right.  - Ruth
			// pmml = new JMenuItem("Save as PMML..");
			// pmml.addActionListener(this);
			// options.add(pmml);

			menuBar.add(options);
		}

		public void endExecution () {
			itemLabels = null;
			 ruleTable = null;
		}

		public Object getMenu() {
			return menuBar;
		}




		public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
                  //System.out.println("The current pi is " + pi);
                  Graphics2D g2=(Graphics2D)g;

                  if (!pageinfoCalculated) //Checks to see if page info has been calculated-gathered yet
                  {
                    getPageInfo(g, pf);
                  }
                  g2.setColor(Color.black); //Sets printing color to black

                  //if page index is greater than or equal to number of pages in table
                  //numHorizontalPages+ ((numHorizontalPages-1)*(numVerticalPages-1)) +numVerticalPages is allows for
                  //first section of table (top 12 rows, 15 columns);  + next section (first left side row, first left side column)
                  //on every vertical section of table + last section of table (all horizontal sections on second vertical pages to last)
              if (pi >= (int)(numHorizontalPages+ ((numHorizontalPages-1)*(numVerticalPages-1)) +numVerticalPages))
                 {
                  return Printable.NO_SUCH_PAGE;
                  }

              pf.setOrientation(PageFormat.LANDSCAPE);
              g2.translate((int)pf.getImageableX(), (int)pf.getImageableY());

               printTablePartAll(g2, pf, pi);
                return Printable.PAGE_EXISTS;

		}



                      JTableHeader tableHeader;
                      boolean pageinfoCalculated=false;

                  //WIDTH INFORMATION
                      double pageWidth;    //The width available for each printed page
                      int fontDesent;      //Font depth
                      double tableHeaderWidth;  //The width of tableHeader
                      double verticalTableWidth = 0;
                      int columnWidth;     //The width of each column of main table
                  //WIDTH INFORMATION

                  //HEIGHT INFORMATION
                      double tableHeaderHeight; //The height of tableHeader
                      double pageHeight;    //The height available for each printed page
                      int fontHeight;       //Font height
                      double barHeaderHeight = 0; //The height of bar graph table (header of top-most pages)
                      int oneRowHeight;     //The height of each row of main table
                 //HEIGHT INFORMATION

                 //NUMBER OF PAGES INFORMATION
                      int numVerticalPages = 0; //Total number of pages needed for rows
                      int numHorizontalPages = 0;//Total number of pages needed for columns
                  //NUMBER OF PAGES INFORMATION

                  //COLUMN AND ROW INFORMATION
                      int numRowsOnAPage = 0;//Number of rows per printed page
                      int numColumnOnAPage = 0;//Number of columns per printed page
                      int currentHorizontalPage = 2; //Current page that horizontal shift is on
                      int currentVerticalPage = 2;   //Current page that vertical shift is on
                      int pageRight = 450;           //Page Edge on right side, printed to this max X value
                      TableColumnModel tableColumnModel;
                      int tableWidthPerPage;  //The width of table printed per page (after page 1)
                      int pageLeft;          //Page Edge on left side, printed from this min X value
                      double topPageLeft = 0; //Page Edge at top of vertical shift
                      int onLevelCount = 0;   //Tracks horizontal shift on all vertical-horizontally shifting pages
                      int printHorizontalCount = 0; //Prevents premature changing of horizontal values when Printing Index refers to a method twice
                      int printVerticalCount = 0;    //Prevents premature changing of vertical values when Printing Index refers to a method twice
                      int printHorizontalVerticalCount = 0; //Prevents premature changing of horizontal and vertical values when Printing Index refers to a method twice
                      int bottomLeft = 450;   //Page Edge on bottom of page, printed to this max Y value
                      int currentHorizontalPage2; //Current page that horizontal page of vertical shift is on
                 //COLUMN AND ROW INFORMATION


                  //Gathers general information about one page and the 4 different tables
                         public void getPageInfo(Graphics g, PageFormat pageFormat)
                         {

                           //determine font size, height and depth
                           fontHeight = g.getFontMetrics().getHeight();
                           fontDesent = g.getFontMetrics().getDescent();

                           //header information gathered
                           tableHeader = rjt.getTableHeader();
                           tableHeaderWidth = tableHeader.getWidth();
                           tableHeaderHeight = tableHeader.getHeight() + rjt.getRowMargin();
                           barHeaderHeight = vjt.getHeight();

                           //If-then table width gathered
                           verticalTableWidth = rhjt.getWidth();

                           //Height and Width avaliable for printed page gathered
                           pageHeight = pageFormat.getImageableHeight();
                           pageWidth = pageFormat.getImageableWidth();

                           //Individual Column information gathered
                           tableColumnModel = rjt.getColumnModel();
                           columnWidth = 0;
                           columnWidth = tableColumnModel.getColumn(0).getWidth();

                           //Row Calculations
                           oneRowHeight = rjt.getRowHeight();
                           numRowsOnAPage=(int)((pageHeight-tableHeaderHeight + barHeaderHeight)/oneRowHeight)/2;
                           numVerticalPages= (int)Math.ceil(((double)rjt.getRowCount())/numRowsOnAPage);

                           //Column Calculations
                           numColumnOnAPage = (int) (pageWidth)/(columnWidth);
                           numHorizontalPages =(int) Math.ceil(((double)rjt.getColumnCount())/numColumnOnAPage);
                           tableWidthPerPage =  numColumnOnAPage * columnWidth + rhjt.getWidth();

                           pageinfoCalculated = true;


                        }




                      //All part of RuleVis printed (If-Then column, Button Table, Bar Graph Table, TableHeader, and Main Table)
                      public void printTablePartAll(Graphics2D g2, PageFormat pageFormat, int pi)
                      {
                      //Printing Left-Right-Width-Height's calculated
                       int printablePageLeft = 0;
                       int printablePageRight = numColumnOnAPage * columnWidth;
                       int printablePageWidth =  printablePageRight-printablePageLeft;
                       int newPageHeight = numRowsOnAPage * oneRowHeight;

                       //moves graphic object to starting location of current page
                       g2.translate(-printablePageLeft, 0);

                      if (pi == 0) //IF FIRST PAGE, THEN PRINT ALL ITEMS
                      {


                            String pageNumber1 = "Page: " + "1 - 1";
                            g2.drawString(pageNumber1,  (int)pageWidth/2, (int)(500));
                           // System.out.println("The page number for first page is " + pageNumber1);

                        //All Set Clip Code goes to X,Y position, then cuts out from there a Width,Height section

                          //BAR-GRAPH TABLE (Header part to all first row-horizontal pages)
                           g2.translate(rhjt.getWidth(), 0);
                           g2.setClip(printablePageLeft, 0, printablePageWidth, (int) barHeaderHeight);
                           vjt.paint(g2); // draw's the graph bars table

                          //BUTTON TABLE
                           g2.translate( -rhjt.getWidth(), 0);
                           g2.setClip(printablePageLeft, 0, vhjt.getWidth(), (int) barHeaderHeight);
                           vhjt.paint(g2); //draws the radio-button table

                           //IF-THEN TABLE
                           g2.translate(pageLeft, barHeaderHeight+tableHeaderHeight);
                           g2.setClip(printablePageLeft, 0, (int) verticalTableWidth, (int) newPageHeight); //modified, goes to same height as main table
                           rhjt.paint(g2); //draws if-then table

                           //TABLE HEADER OF MAIN TABLE
                           g2.translate(rhjt.getWidth(),-tableHeaderHeight );
                           g2.setClip(printablePageLeft, 0, printablePageWidth, (int) tableHeaderHeight);
                           tableHeader.paint(g2); //draws header for rjt table (numbers)

                           // CHECK AND BOX TABLE (MAIN TABLE)
                           g2.translate(0, tableHeaderHeight);
                           g2.setClip(printablePageLeft, 0, printablePageWidth, (int) newPageHeight);
                           rjt.paint(g2); //draws check and box table


                      }

                      else //ELSE PRINT CERTAIN PARTS OF TABLE, BASED ON PAGE POSITION CONDITIONS
                      {
                        g2.translate(0, -40);

                        /*
                         This Code prints the complete first visible rows of table with all necessary headers;
                         Once that is complete, the next set of rowso f the table are printed by shifting the graphics objected downwards to
                         the last printed row of previous page and clip from there.  Then the following
                         else if statement involving onLevelCount, indicates that the printer
                         should continued printing on same Y, but change X (Shifting horizontally)
                         When onLevelCount hits max, sets back to zero and then shifts downward to next row
                         And that process continues until all rows printed.  The printing being done is one in which
                         the top visible rows are printed with headers and all, then we first shift horizontally, then shift down one whole page and print that by shifting
                         horizontally on that set of rows and continuing on as such.

                         *Note* The use of onLevelCount variable, allows for the horizontal shifting on vertical pages 2 -> end.
                         Also the use of printCounts is to deal with the fact that printIndex is used twice by printing, meaning methods are
                         called twice and inside methods, we only want values that shift for clipping, to change once.

                         */
                          //all top horizontal sections
                           if (currentHorizontalPage <= numHorizontalPages)
                           {

                             String pageNumber2 = "Page: 1" + "-" + currentHorizontalPage;

                             g2.drawString(pageNumber2,  (int)pageWidth / 2, (int)500);

                               printTablePart2(g2, pageFormat, currentHorizontalPage, pi);
                               if (printHorizontalCount == 1) //If second call to print index made, move to next page, reset print count
                               {
                                   currentHorizontalPage = currentHorizontalPage + 1;
                                   printHorizontalCount = 0;
                               }
                               else //First print index call, increment print count
                                   printHorizontalCount++;
                            }
                            //top-left side section, all vertical, executed once for each vertical page
                           else if (currentVerticalPage <= numVerticalPages || currentHorizontalPage2 <= numHorizontalPages)
                            {
                                //g2.translate(0, -40);
                                if (onLevelCount == 0) //If zero then at new row so, print it first page of it
                                {

                                     String pageNumber3 = "Page:" + currentVerticalPage + "-" + "1";
                                     g2.drawString(pageNumber3,  (int)pageWidth/2, (int)(500));
                                     printTablePart3(g2, pageFormat, currentVerticalPage, pi);

                                    if (printVerticalCount == 1)//If second call to print index made, move to next page, reset print count
                                    {
                                        currentVerticalPage = currentVerticalPage + 1;
                                        printVerticalCount = 0;
                                        onLevelCount++;
                                        currentHorizontalPage2 = 2;
                                    }
                                    else//First print index call, increment print count
                                        printVerticalCount++;

                                }
                                //At same set of visible rows as above, shift page horizontally, printing columnsPerPage and rowsPerPage on each page

                               else if (onLevelCount <= numHorizontalPages)
                               {

                                 String pageNumber4 = "Page:" + (currentVerticalPage-1) + "-" + (onLevelCount+1);
                                 g2.drawString(pageNumber4,  (int)pageWidth/2, (int)(500));
                                 printTablePart4(g2, pageFormat, currentVerticalPage, currentHorizontalPage, pi);

                                   if (printHorizontalVerticalCount == 1)//If second call to print index made, move to next page, reset print count
                                   {
                                      onLevelCount++;
                                      printHorizontalVerticalCount = 0;
                                      currentHorizontalPage2++;
                                   }
                                   else//First print index call, increment print count
                                      printHorizontalVerticalCount++;

                               }
                                else
                                 onLevelCount = 0;

                            }


                         }


              }
                      //Part of RuleVis printed (Bar Graph Table, TableHeader, and Main Table  )
                      public void printTablePart2(Graphics2D g2, PageFormat pageFormat, int currentHorizontalPage, int pi)
                      {

                          pageLeft = pageRight;

                          int newPageHeight = numRowsOnAPage * oneRowHeight;


                          g2.translate(-pageLeft, 0); //moves graphic object to starting location of current page
                          g2.setColor(Color.black);

                         //BAR GRAPH TABLE



                          g2.setClip(pageLeft, 0,(int) tableWidthPerPage , (int) barHeaderHeight);
                          vjt.paint(g2); // draw's the graph bars table

                          //TABLE HEADER OF MAIN TABLE
                          g2.translate(0, barHeaderHeight);
                          g2.setClip(pageLeft, 0, (int)tableWidthPerPage , (int) tableHeaderHeight);
                          tableHeader.paint(g2); //draws header for rjt table (numbers)


                          // CHECK AND BOX TABLE (MAIN TABLE)
                          g2.translate(0, tableHeaderHeight);
                          g2.setClip(pageLeft, 0,(int) tableWidthPerPage , (int) newPageHeight);
                          rjt.paint(g2); //draws check and box table



                          if (printHorizontalCount == 1)
                          {


                            pageRight = pageLeft + (int) tableWidthPerPage;
                          }

              }

                     //Part of RuleVis printed (If-Then column and Main Table)
                     public void printTablePart3(Graphics2D g2, PageFormat pageFormat, int currentVerticalPage, int pi)
                     {
                         int printablePageLeft = (int)topPageLeft;
                         int printablePageRight = (numColumnOnAPage * columnWidth);
                         int printablePageWidth =  printablePageRight-printablePageLeft;
                         int newPageHeight = numRowsOnAPage * oneRowHeight;




                         g2.translate(-printablePageLeft, -newPageHeight); //moves graphic object to starting location of current page

                         //IF-THEN TABLE
                         g2.setClip(printablePageLeft,newPageHeight, (int) verticalTableWidth, newPageHeight); //modified, goes to same height as main table
                         rhjt.paint(g2);

                         // CHECK AND BOX TABLE (MAIN TABLE)
                         g2.translate((int) verticalTableWidth, 0);
                         g2.setClip(printablePageLeft, newPageHeight, (int)printablePageWidth,  newPageHeight);
                         rjt.paint(g2); //draws check and box table

                         if (printVerticalCount == 1)
                              topPageLeft = topPageLeft + (int)newPageHeight + vjt.getHeight() + tableHeader.getHeight();
                }




                    //Part of RuleVis printed (Main Table)
                    public void printTablePart4(Graphics2D g2, PageFormat pageFormat, int currentVerticalPage, int currentHorizontalPage, int pi)
                    {
                        int printablePageLeft = bottomLeft;
                        int newPageHeight = numRowsOnAPage * oneRowHeight;
                        g2.translate(-bottomLeft, -newPageHeight); //moves graphic object to starting location of current page

                        // CHECK AND BOX TABLE (MAIN TABLE)
                        g2.setClip(printablePageLeft, newPageHeight, tableWidthPerPage, (int) newPageHeight);
                        rjt.paint(g2); //draws check and box table

                        if (printHorizontalVerticalCount == 1)
                        {
                           bottomLeft = bottomLeft + tableWidthPerPage;

                        }
                }



                private class RowHeadTable extends JTable {
                  RowHeadTable(TableModel tm) {
                    super(tm);
                  }

                  public String getToolTipText(MouseEvent e) {
                    Point p = e.getPoint();
                    int row = this.rowAtPoint(p);
                    String tip = getTipForRow(row);
                    return tip;
                  }

                  private String getTipForRow(int r) {
                    String tp = (String)getModel().getValueAt(r, 0);
                    return tp;
                  }

                }


		/**
                 * construct a table for the row heads.
		*/
		JTable rhjt = null;
		private JViewport getRowHeadTable (int imageHeight)
		{
			itemLabels = ruleTable.getNamesList();

                        String [][] rowNames = new String [itemLabels.size()][1];
			String [] cn = {"attributes"};

                    for (int i = 0; i < itemLabels.size(); i++)
                            rowNames  [i][0] = ((String)itemLabels.get(i));

                    JViewport jv = new JViewport ();
                    DefaultTableModel dtm = new DefaultTableModel (rowNames, cn);
                    //rhjt = new JTable (dtm);
                    rhjt = new RowHeadTable (dtm);


			rhjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			jv.setBackground (RuleVis.RULE_VIS_BACKGROUND);

			// Set the width of the heads.
			rhjt.createDefaultColumnsFromModel ();
			jv.setView (rhjt);

			// value label cell renderer specifies the height to
			// be that of the value cells
			rhjt.setRowHeight (imageHeight + rhjt.getIntercellSpacing ().height*2);
			rhjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);


			return jv;
		}

		/** construct a table for the row heads.
		*/
		JTable vhjt = null;
		private JViewport getConfSupHeadTable ()
                {
			String [][] rowNames = new String[ruleTable.getNumColumns()-2][1];
			for(int i=2;i<ruleTable.getNumColumns();i++)
				rowNames[i-2][0]=ruleTable.getColumnLabel(i);
			JViewport jv = new JViewport();
			String [] cn = {""};
			DefaultTableModel dtm = new DefaultTableModel (rowNames, cn);
			vhjt = new JTable (dtm);
			vhjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			jv.setBackground (RuleVis.RULE_VIS_BACKGROUND);
			vhjt.createDefaultColumnsFromModel ();

			// set the view.
			jv.setView (vhjt);

			// value label cell renderer specifies the height to
			// be that of the value cells
			vhjt.setShowGrid (false);
			vhjt.setRowHeight (RuleVis.BAR_HEIGHT + vhjt.getIntercellSpacing ().height*2);
			vhjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
			return jv;
		}

		/**	Create a panel which will contain the buttons that sort by the various
			means, confidence and support.
		*/
		//JButton conf = new JButton("Confidence");
		//JButton  sup = new JButton("Support");
      		JRadioButton conf = new JRadioButton("Confidence", false);
		JRadioButton sup = new JRadioButton("Support", false);
		final int BSIZE = 13;
		private JPanel getSortPanel () {
			JPanel buttons = new JPanel ();
			return buttons;
		}



                /**
                 * Update the row headers after a change to their order in the
                 * RuleTable
                 */
                private void setAttributes(){
                  itemLabels = ruleTable.getNamesList();
                  String [][] rowNames = new String [itemLabels.size()][1];
                  String [] cn = {"attributes"};
                  for (int i = 0; i < itemLabels.size(); i++)
                    rowNames  [i][0] = ((String)itemLabels.get(i));
                  DefaultTableModel dtm = new DefaultTableModel (rowNames, cn);
                  rhjt.setModel(dtm);
                  setupRowHeaders(ruleTable.getNamesList());
                  // Set the cell renderer for the rule labels
                  TableColumnModel tcm = rhjt.getColumnModel();
                  tcm = rhjt.getColumnModel();
                  LabelCellRenderer lcr = new LabelCellRenderer ();
                  TableColumn col = tcm.getColumn (0);
                  lcr.setBackground (Color.white);
                  col.setCellRenderer (lcr);
                }

                /**
                 * update the body of the table after a change in the set of rules
                 * we want to see
                 */
                private void setBody(){
                  rvdm = new RuleVisDataModel (ruleTable);
                  Image a = /*module.*/getImage ("/images/rulevis/checkmark-blue.gif");
                  Image b = /*module.*/getImage ("/images/rulevis/box-beige.gif");
                  int imgWidth = b.getWidth (this) > a.getWidth (this) ?
                      b.getWidth (this) : a.getWidth (this);
                  int imgHeight = b.getHeight (this) > a.getHeight(this) ?
                      b.getHeight (this) : a.getHeight (this);
                  //table that holds the confidence and support buttons
                  vhjt = new JTable (rvdm);
                  //this is the table for the confidence/support bars at the top
                  vjt.setModel(vvdm);
                  TableColumnModel tcm = vjt.getColumnModel();
                  ValueCellRenderer vcr = new ValueCellRenderer (
                       imgWidth + vjt.getIntercellSpacing ().height*2);
                  for (int i = 0; i < vvdm.getColumnCount (); i++) {
                    TableColumn col = tcm.getColumn (i);
                    col.setCellRenderer (vcr);
                    col.setMinWidth (30);
                    col.setMaxWidth (30);
                    col.setPreferredWidth (30);
                  }
                  vjt.setRowHeight (RuleVis.BAR_HEIGHT + vjt.getIntercellSpacing ().height*2);
                  vjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

                  //this is the body of the vis table... where the boxes and checks go
                  rjt.setModel(rvdm);
                  rjt.createDefaultColumnsFromModel ();
                  rjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
                  tcm = rjt.getColumnModel();
                  RuleCellRenderer rcr = new RuleCellRenderer (
                     imgHeight + rjt.getIntercellSpacing ().height*2,
                     imgWidth + rjt.getIntercellSpacing ().width*2,
                     a, b);
                  for(int i = 0; i < ruleTable.getNumRulesShowing(); i++){
                    TableColumn col = tcm.getColumn (i);
                    col.setCellRenderer (rcr);
                    col.setMinWidth (30);
                    col.setMaxWidth (30);
                    col.setPreferredWidth (30);
                  }
                  rjt.setRowHeight (imgHeight + rjt.getIntercellSpacing ().height*2);
                  rjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);
                }


		/**	A sorting or tool button was clicked.  Actions are defined here
			@param e the action event.
		*/
                private boolean ALPHA = true;
		public void actionPerformed(ActionEvent e) {

                    if (e.getSource() == conf){
                      this.rvdm.sortConfidenceSupport();
                      sup.setSelected(false);
                      conf.setSelected(true);
                      if(!ALPHA)
                        this.rvdm.rank();
                      this.setAttributes();
                      this.repaint();
                    }
                    else if (e.getSource() == sup){
                      this.rvdm.sortSupportConfidence();
                      conf.setSelected(false);
                      sup.setSelected(true);
                      if(!ALPHA)
                        this.rvdm.rank();
                      this.setAttributes();
                      this.repaint();
                    }
                    else if (e.getSource() == helpButton){
                      help.setVisible(true);
                    }
                    else if (e.getSource() == refreshButton){
                      sup.setSelected(false);
                      conf.setSelected(false);
                      abcButton.setSelected(true);
                      rankButton.setSelected(false);
                      ruleTable.setToOriginal();
                      ruleTable.cleanup();
                      ruleTable.alphaSort();
                      rvdm = new RuleVisDataModel (ruleTable, itemLabels, numExamples);
                      vvdm = new ValueVisDataModel (ruleTable, numExamples);
                      this.setAttributes();
                      this.setBody();
                      this.repaint();
                      ALPHA = true;
                    }
                    else if (e.getSource() == abcButton){
                      abcButton.setSelected(true);
                      rankButton.setSelected(false);
                      ruleTable.alphaSort();
                      this.setAttributes();
                      this.repaint();
                      ALPHA = true;
                    }
                    else if (e.getSource() == rankButton){
                      abcButton.setSelected(false);
                      rankButton.setSelected(true);
                      this.rvdm.rank();
                      this.setAttributes();
                      this.repaint();
                      ALPHA = false;
                    }
                    else if (e.getSource() == filterButton){
                      RuleFilter R = new RuleFilter(this);
                      R.setInput(ruleTable,0);
                    }
                    else if (e.getSource() == print || e.getSource() == printButton) {
                      PrinterJob pj = PrinterJob.getPrinterJob();
                      pj.setPrintable(this);
                      if (pj.printDialog()) {
                        try {
                          pj.print();
                        }
                        catch(Exception exception) {
                          exception.printStackTrace();
                        }
                      }
                    }
                    else if(e.getSource() == pmml) {
                      JFileChooser fileChooser = new JFileChooser();
                      int retVal = fileChooser.showSaveDialog(null);
                      if(retVal == fileChooser.APPROVE_OPTION) {
                        java.io.File file = fileChooser.getSelectedFile();
                        //TODO add back pmml
                        //WriteRuleAssocPMML.writePMML(ruleTable, file.getAbsolutePath());
                      }
                    }
                    vvdm.fireTableDataChanged();
                  }


                /**
                 * This updates the entire view after some rules have been filtered.
                 */
                public void updateView(){
                      vvdm = new ValueVisDataModel (ruleTable);
                      this.setAttributes();
                      this.setBody();
                      this.repaint();
                }



		/**	Set up the labels. We will find the width of the longest label,
			then we will set the width of each of the labels header rows
			to be the width of the widest one. They line up this way.
			@param names the names of the rows.
			@param nameIndices the indexes of the row names in the order displayed.
		*/
		private void setupRowHeaders (java.util.List names) {

			// First compute the max width of the guy.
			int width = 150;

			// Set the widths to the max.
			TableColumnModel tcm = vhjt.getColumnModel();
			TableColumn col = tcm.getColumn (0);
			col.setMinWidth (width);
			col.setMaxWidth (width);
			col.setPreferredWidth (width);

			tcm = rhjt.getColumnModel();
			col = tcm.getColumn (0);
			col.setMinWidth (width);
			col.setMaxWidth (width);
			col.setPreferredWidth (width);

			Dimension dim = new Dimension (width, vhjt.getPreferredSize ().height);
			vhjt.setPreferredScrollableViewportSize (dim);
			dim.height = vhjt.getPreferredSize ().height;
			rhjt.setPreferredScrollableViewportSize (dim);

		}

		/**	This method is called whenever an input arrives, and is responsible
			for modifying the contents of any gui components that should reflect
			the value of the input.

			@param input this is the object that has been input.
			@param index the index of the input that has been received.
		*/

		public void setInput(Object o, int index) {
			if (index == 0)
				ruleTable = (RuleTable)o;
                                // Added call to cleanup() to remove items
                                // from the RuleTable that are not used in any rules
                                ruleTable.cleanup();
                                //initially sort the attribute/values alphabetically
                                ruleTable.alphaSort();
				itemLabels = ruleTable.getNamesList();
				// Do we have all the inputs we need?
				if ((ruleTable != null) /*&& (itemLabels != null)*/){
				this.setLayout (new GridBagLayout ());
				this.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				////////////////////////////////////////////////////////////////////////
				// Create the cell renderers images, then compute the width and height of the
				// cells.
				Image a = /*module.*/getImage ("/images/rulevis/checkmark-blue.gif");
				Image b = /*module.*/getImage ("/images/rulevis/box-beige.gif");

                                /*ButtonGroup group = new ButtonGroup();
                                group.add(conf);
                                group.add(sup);*/
				conf.addActionListener (this);
				sup.addActionListener (this);

				//conf.addActionListener (this);
				//sup.addActionListener (this);

				int imgHeight = b.getHeight (this) > a.getHeight(this) ?
						b.getHeight (this) : a.getHeight (this);
				int imgWidth = b.getWidth (this) > a.getWidth (this) ?
						b.getWidth (this) : a.getWidth (this);

				////////////////////////////////////////////////////////////////////////
				// Create the table that will go on the top, it will show the confidence
				// support and the row headers.
				vvdm = new ValueVisDataModel (ruleTable, numExamples);
				/*final JTable*/ vjt = new JTable (vvdm);
				vjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				vjt.createDefaultColumnsFromModel ();
				JViewport valueRowHeaders = this.getConfSupHeadTable ();

				// set up the scroller for the values, provide it with the row headers.
				JPanel tmp = new JPanel();
				tmp.setOpaque(false);
				tmp.setLayout(new GridBagLayout());
				Constrain.setConstraints(tmp, vjt, 0, 0, 1, 1, GridBagConstraints.NONE,
						GridBagConstraints.WEST, 0.0, 0.0, new Insets(0, 0, 0, 0));
				JPanel filler = new JPanel();
				Dimension fillSize = new Dimension(12, 12);
				filler.setMinimumSize(fillSize);
				filler.setPreferredSize(fillSize);
				filler.setOpaque(false);
				Constrain.setConstraints(tmp, filler, 1, 0, 1, 1, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.CENTER, 1.0, 0.0, new Insets(0, 0, 0, 0));

				JScrollPane valueScroller = new JScrollPane (tmp,
						JScrollPane.VERTICAL_SCROLLBAR_NEVER,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				int unitInc = imgWidth - vjt.getIntercellSpacing ().width*2 - 1;
				valueScroller.setRowHeader (valueRowHeaders);
				valueScroller.getHorizontalScrollBar ().setUnitIncrement (unitInc);
				valueScroller.getHorizontalScrollBar ().setBlockIncrement (unitInc*4);

				///////////////////////////////////////////////////////////////////////
				// next construct the table of the rules themselves, this table
				// will also have row headers in a different table, the scroller
				// understands and deals with the concept of table row headers.
				rvdm = new RuleVisDataModel (ruleTable, itemLabels, numExamples);
				rjt = new JTable (rvdm);
				rjt.createDefaultColumnsFromModel ();
				rjt.setBackground (RuleVis.RULE_VIS_BACKGROUND);

				JViewport rowHeaders = this.getRowHeadTable (imgHeight);

				// Set up the scroller for the rules.
				JScrollPane ruleScroller = new JScrollPane (rjt);
				ruleScroller.setRowHeader (rowHeaders);
				ruleScroller.setBackground (RuleVis.RULE_VIS_BACKGROUND);
				ruleScroller.getViewport().setBackground (RuleVis.RULE_VIS_BACKGROUND);
				ruleScroller.getHorizontalScrollBar ().setUnitIncrement (unitInc);
				ruleScroller.getHorizontalScrollBar ().setBlockIncrement (unitInc*4);
				ruleScroller.setCorner (ScrollPaneConstants.UPPER_LEFT_CORNER, this.getSortPanel ());

				// Set up the viewports and scrollers for the row labels.
                                this.setupRowHeaders (ruleTable.getNamesList());
                                // Set up a table model listener so when rows move in the rules, rows also
				// move appropriately in the confidences.
				rjt.getColumnModel ().addColumnModelListener (new TableColumnModelListener () {
					public void columnAdded(TableColumnModelEvent e) {}
					public void columnRemoved(TableColumnModelEvent e) {}
					public void columnMarginChanged(ChangeEvent e) {}
					public void columnSelectionChanged(ListSelectionEvent e) {}
					public void columnMoved(TableColumnModelEvent e) {
						if (e.getFromIndex () != e.getToIndex ()) {
							vjt.getColumnModel ().moveColumn (e.getFromIndex (), e.getToIndex ());
						}
								}
				});

				///////////////////////////////////////////////////////////////
				// Assign the appropriate cell renderers for all the tables, the
				// labels all share one renderer, the rules have their own as do
				// the values.

				// Set the cell renderer for the rules
				TableColumnModel tcm = rjt.getColumnModel();
				RuleCellRenderer rcr = new RuleCellRenderer (
					imgHeight + rjt.getIntercellSpacing ().height*2,
					imgWidth + rjt.getIntercellSpacing ().width*2,
					a, b);
				for (int i = 0; i < rvdm.getColumnCount (); i++) {
					TableColumn col = tcm.getColumn (i);
					col.setCellRenderer (rcr);
					col.setMinWidth (30);
					col.setMaxWidth (30);
					col.setPreferredWidth (30);
				}
				rjt.setRowHeight (imgHeight + rjt.getIntercellSpacing ().height*2);
				rjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

				// Set the cell renderer for the values
				tcm = vjt.getColumnModel();
				ValueCellRenderer vcr = new ValueCellRenderer (
					imgWidth + vjt.getIntercellSpacing ().height*2);
				for (int i = 0; i < vvdm.getColumnCount (); i++) {
					TableColumn col = tcm.getColumn (i);
					col.setCellRenderer (vcr);
					col.setMinWidth (30);
					col.setMaxWidth (30);
					col.setPreferredWidth (30);
				}
				vjt.setRowHeight (RuleVis.BAR_HEIGHT + vjt.getIntercellSpacing ().height*2);
				vjt.setAutoResizeMode (JTable.AUTO_RESIZE_OFF);

				// Set the cell renderer for the rule labels
                                tcm = rhjt.getColumnModel();
                                LabelCellRenderer lcr = new LabelCellRenderer ();
                                TableColumn col = tcm.getColumn (0);
                                lcr.setBackground (Color.white);
                                col.setCellRenderer (lcr);

				// Same renderer for the conf/support labels
				tcm = vhjt.getColumnModel();
				SortButtonCellRenderer sortButtonRenderer = new SortButtonCellRenderer(conf, sup);
				col = tcm.getColumn (0);
				col.setCellRenderer (sortButtonRenderer);
				SortButtonCellEditor sortButtonEditor = new SortButtonCellEditor(conf, sup);
				col.setCellEditor (sortButtonEditor);

				// Put them into a frame, then place the frame in this view with an inset.
				JPanel jp = new JPanel ();
				jp.setLayout (new GridBagLayout ());
                                this.setConstraints (this, header, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.NORTH, 1, 0, new Insets (0, 0, 0, 0));
                                this.setConstraints (jp, valueScroller, 0, 0, 1, 1, GridBagConstraints.HORIZONTAL,
						GridBagConstraints.WEST, 1, 0, new Insets (0, 0, 0, 0));
				this.setConstraints (jp, ruleScroller, 0, 1, 1, 1, GridBagConstraints.BOTH,
						GridBagConstraints.WEST, 1, 1, new Insets (0, 0, 0, 0));
                                this.setConstraints(this,jp,0,0,1,1,GridBagConstraints.BOTH,
                                    GridBagConstraints.WEST,1,1,new Insets(35,10,10,10));

				// Now fix up the sizes of the scroll panels considering the
				// usage of scroll bars. The value scroller never has a scroll bar
				// yet it's preferred size includes the hieght of one.
				Dimension dim = valueScroller.getMinimumSize ();
				dim.height = vjt.getPreferredSize ().height+2;
				valueScroller.setMinimumSize (dim);
				valueScroller.setPreferredSize (dim);
				valueScroller.setMaximumSize (dim);

				dim = ruleScroller.getPreferredSize ();
				/*dim.height = rjt.getPreferredSize ().height +
					rjt.getTableHeader ().getPreferredSize ().height +
					ruleScroller.getHorizontalScrollBar ().getPreferredSize ().height+
					vjt.getIntercellSpacing ().height*2 +1;
				ruleScroller.setPreferredSize (dim);
				ruleScroller.setMaximumSize (dim);*/
				ruleScroller.setSize (dim);

				// Now link the conf/supp viewport to the rules viewport
				ruleScroller.getViewport ().addChangeListener (new ScrollLinkageChangeListener(
						ruleScroller.getViewport (), valueScroller.getViewport()));


			}
		}

		/**	Link the operation of one scroller to another, in this case, the rule
			scroller is the master, and the value scroll is the slave, and only in
			the horizontal direction.
		*/
		class ScrollLinkageChangeListener implements ChangeListener {
			JViewport master, slave;

			/**	Take the master and slave viewport.
				@param m the master viewport directs scrolling.
				@param s the slave viewport is driven by the scroller.
			*/
			ScrollLinkageChangeListener (JViewport m, JViewport s) {
				master = m;
				slave = s;
			}

			/**	The master viewport has changed, update the slave.
			*/
			public void stateChanged (ChangeEvent ce) {
				Point pos = master.getViewPosition ();
				pos.y = 0;
				slave.setViewPosition (pos);
			}
		}

		/**	Create and set up a grid bag constraint with the values given.
			@param x the x corrdinate in the layout grid.
			@param y the y coordinate.
			@param width the number of rows the object spans.
			@param height the number of cols the object spans.
			@param fill determines how the objects size is computed.
			@param anchor how the object is aligned within the cell.
			@param weightX fraction of the leftover horizontal space the object will use.
			@param weightY fraction of the leftover vertical space the object will use.

		*/
		private GridBagConstraints getConstraints (int x, int y, int width, int height, int fill,
			 int anchor, double weightX, double weightY){
			GridBagConstraints c = new GridBagConstraints ();
			c.gridx 	= x;
			c.gridy 	= y;
			c.gridwidth = width;
			c.gridheight = height;
			c.fill 		= fill;
			c.anchor 	= anchor;
			c.weightx	= weightX;
			c.weighty 	= weightY;
			return c;
		}

		/**	Set up the grid bag constraints for the object, and includes insets other than the
			default.
			@param it the component being added.
			@param x the x corrdinate in the layout grid.
			@param y the y coordinate.
			@param width the number of rows the object spans.
			@param height the number of cols the object spans.
			@param fill determines how the objects size is computed.
			@param anchor how the object is aligned within the cell.
			@param weightX fraction of the leftover horizontal space the object will use.
			@param weightY fraction of the leftover vertical space the object will use.
		*/
		public void setConstraints (Container cont, Component it, int x, int y, int width, int height, int fill,
			 int anchor, double weightX, double weightY, Insets insets)
		{
			GridBagConstraints c = this.getConstraints (x, y, width, height, fill, anchor, weightX, weightY);
			c.insets 	= insets;
			((GridBagLayout) cont.getLayout()).setConstraints (it, c);
			cont.add (it);
		}
		/**	Set up the grid bag constraints for the object.
			@param it the component being added.
			@param x the x corrdinate in the layout grid.
			@param y the y coordinate.
			@param width the number of rows the object spans.
			@param height the number of cols the object spans.
			@param fill determines how the objects size is computed.
			@param anchor how the object is aligned within the cell.
			@param weightX fraction of the leftover horizontal space the object will use.
			@param weightY fraction of the leftover vertical space the object will use.
		*/
		public void setConstraints (Container cont, Component it, int x, int y,
				int width, int height, int fill,
			 int anchor, double weightX, double weightY)
		{
			GridBagConstraints c = this.getConstraints (x, y, width, height, fill, anchor, weightX, weightY);
			c.insets = new Insets (2, 2, 2, 2);
			((GridBagLayout) cont.getLayout()).setConstraints (it, c);
			cont.add (it);
		}

		/**	The data model for rule vis will have an array of bytes representing the number of
			rules along the first dimension, and the number of possible attributes along the
			second dimension. each entry in this matrix will have a 'I' if the associated attribute
			in that rule is an input, and 'O' if it is an objective, anything else means the
			attribute is not represented in the rule.
		*/
		class RuleVisDataModel extends AbstractTableModel {
			int numsets = 0;

			// Save array lengths for performance reasons
			private int numColumns = 0;
			private int numRows = 0;

			/** contains the original rule data. */
			RuleTable rules;

			/** this is set if we are sorting on confidence as the primary. */
			boolean sortOnConfidence = true;

			/**	Init this guy given the rules. Figure out which items are
				represented in the rule set and with are totally absent. When
				we know what items are represented, we create an secondary item
				index so we may exclude those items are are not represented from
				the display.
			*/
			RuleVisDataModel (RuleTable rls, java.util.List itemNames, int numsets) {
				rules = rls;
				numRows = itemNames.size();
				numColumns = rules.getNumRows();
                                this.numsets = numsets;

                                int numRules = rls.getNumRules();
                                order = new int[numRules];
                                //initially set the default order
                                for (int i = 0; i < numRules; i++)
                                  order[i] = i;

			}

                        /**
                         * A constructor that will grab all of it's table information
                         * from the RuleTable 'rls' so the data reflected in this DataModel is
                         * completely current with that in rls.
                         *
                         * @param rls is the RuleTable to visualize
                         */
   			RuleVisDataModel (RuleTable rls) {
				rules = rls;
				numRows = rules.getNamesList().size();
				numColumns = rules.getNumRulesShowing();
			}

			public int getColumnCount() {
				return numColumns;
			}

			public int getRowCount() {
				return numRows;
			}

			/**	return text that indicates if the cell at row, column is
				an antecedent, a result or not.
			*/
			public Object getValueAt (int row, int column) {
				String returnval = "";
				int[] head = rules.getRuleAntecedent(order[column]);
				for (int i=0; i<head.length; i++)
					if (head[i] == row)
                                          returnval = IF;
                                int[] body = rules.getRuleConsequent(order[column]);
                                for (int i=0; i<body.length; i++)
                                  if (body[i] == row)
                                    returnval = THEN;
                                return returnval;
			}

			public String getColumnName (int columnIndex) {
				return Integer.toString (columnIndex);
			}

			/**
				Compare two rules on the basis of a primary key first and then a secondary
				key. Primary and secondary keys can be either of support or confidence.
				@param l the first entry to compare
				@param r the second entry.
			*/
			public int compare (int l) {
				double primary1, secondary1;
				if (sortOnConfidence) {
					primary1 = rules.getConfidence(order[l]);
					secondary1 = rules.getSupport(order[l]);
				} else {
					secondary1 = rules.getConfidence(order[l]);
					primary1 = rules.getSupport(order[l]);
				}

				// First check on the primary keys
				if (primary1 > bestPrimary)
					return 1;
				if (primary1 < bestPrimary)
					return -1;

				// Primarys are equal, check the secondary keys.
				if (secondary1 > bestSecondary)
					return 1;
				if (secondary1 < bestSecondary)
					return -1;
				return 0;
			}

			/**
				Swap two entries int he order array.
				@param l the first entry.
				@param r the second entry.
			*/
			public void swap (int l, int r) {
				int swap = order [l];
				order [l] = order [r];
				order [r] = swap;
			}

			/**
				Set the pivot value for the quicksort.
			*/
			double bestPrimary;
			double bestSecondary;
			private void setPivot (int l) {
				if (sortOnConfidence) {
					this.bestPrimary = rules.getConfidence(order[l]);
					this.bestSecondary = rules.getSupport(order[l]);
				} else {
					this.bestSecondary = rules.getConfidence(order[l]);
					this.bestPrimary = rules.getSupport(order[l]);
				}
			}

			/**
				Perform a quicksort on the data using the Tri-median method to select the pivot.
				@param l the first rule.
				@param r the last rule.
			*/
			private void quickSort(int l, int r) {

				int pivot = (r + l) / 2;
				this.setPivot (pivot);

				// from position i=l+1 start moving to the right, from j=r-2 start moving
				// to the left, and swap when the fitness of i is more than the pivot
				// and j's fitness is less than the pivot
				int i = l;
				int j = r;
				while (i <= j) {
					while ((i < r) && (this.compare (i) > 0))
						i++;
					while ((j > l) && (this.compare (j) < 0))
						j--;
					if (i <= j) {
						this.swap (i, j);
						i++;
						j--;
					}
				}

				// sort the two halves
				if (l < j)
					quickSort(l, j);
				if (i < r)
					quickSort(j+1, r);
			}

			/**
				Bubble sort on confidence as primary key.
			*/
			private void unSort () {
                          int numRules = rules.getNumRules();

                          if (order != null && order.length < numRules)//means we're currently filtered
                           ;
                          else{
                            // Create and init a new order array.
                            order = new int[numRules];
                            for (int i = 0; i < numRules; i++)
                              order[i] = i;
                          }
                        }

                        /**
                         * Collapse the table horizontally to remove the empty rows after
                         * some have been filtered out.
                         */
                        public void condenseRules(boolean[] rules, int numTrue){
                          int[] newOrder = new int[numTrue];
                          int inserted = 0;
                          for(int i = 0; i < rules.length; i++)
                            if(rules[i] == true){
                              newOrder[inserted] = order[i];
                              inserted++;
                            }
                          order = newOrder;
                        }

                        /**
                         * Rank the rows depending on where they lie on in the table, that
                         * is figure out which rows are important based on the Confidence/Support
                         * selection and reorder them accordingly
                         */
                        public void rank(){
                          int[] done = new int[this.getRowCount()];
                          int[] priority = new int[this.getRowCount()];
                          int numInserted = 0;
                          for(int j = 0; j < this.getColumnCount(); j++){
                            for(int i = 0; i < this.getRowCount(); i++){
                              if(!getValueAt(i,j).equals("") && done[i] != 1){
                                priority[numInserted] = i;
                                done[i] = 1;
                                numInserted++;
                              }
                            }
                          }
                           ruleTable.reOrderRows(priority);
                        }


			/**
				Bubble sort on confidence as primary key.
			*/
			public void sortConfidenceSupport () {
				sortOnConfidence = true;
				//int numRules = rules.getNumRules();
				this.unSort();
				this.quickSort (0, order.length-1);

			}

			/**
				Bubble sort on support as primary key.
			*/
			public void sortSupportConfidence () {
				sortOnConfidence = false;
				//int numRules = rules.getNumRules();
				this.unSort();
				this.quickSort (0, order.length-1);

			}

			/**
				The order we originally get the rules in is sorted on the basis
				of the size of the rules.
			*/
			public void sortOnSize () {
				this.unSort();
			}

		}

		/**	The data model for rule vis will have an array of bytes representing the number of
			rules along the first dimension, and the number of possible attributes along the
			second dimension. each entry in this matrix will have a 'I' if the associated attribute
			in that rule is an input, and 'O' if it is an objective, anything else means the
			attribute is not represented in the rule.
		*/
		class ValueVisDataModel extends AbstractTableModel {
                  /** this is the data, we use just the last two entries in each row. */
                  //TableImpl results = null;
                  RuleTable results = null;

                  /** this is the number of documents in the original dataset, to compute percent */
                  int numsets = 0;

                  // Save array lengths for performance reasons
                  private int numColumns = 0;
                  private int numRows = 0;

                  /**	Init this guy given the rules. Figure out which items are
                   represented in the rule set and with are totally absent. When
                   we know what items are represented, we create an secondary item
                   index so we may exclude those items are are not represented from
                   the display.
                   */
                  ValueVisDataModel(RuleTable rls, int numsets) {
                    results = rls;
                    //First 2 columns of rls represent body and head of a rule
                    numRows = rls.getNumColumns() - 2;
                    numColumns = rls.getNumRows();
                    this.numsets = numsets;
                  }

                  /**
                   * Constructor to grab all updated information from the RuleTable rls.
                   * This data affects the actual associations reflected in the vis.
                   *
                   * @param rls is the RuleTable we wish to visualize
                   */
                  ValueVisDataModel(RuleTable rls) {
                    results = rls;
                    //First 2 columns of rls represent body and head of a rule
                    numRows = rls.getNumColumns() - 2;
                    numColumns = rls.getNumRulesShowing();
                  }


                  /**	return the number of columns.
                   @returns the number of columns.
                   */
                  public int getColumnCount() {
                    return numColumns;
                    //return ruleTable.getNumRulesShowing();
                  }

                  /**	return the number of rows.
                   @returns the number of rows.
                   */
                  public int getRowCount() {
                    return numRows;
                  }

                  public String getColumnName(int columnIndex) {
                    return "";
                  }

                  /**
                   Compute the value at row and column
                   @returns the value at row and column.
                   */
                  public Object getValueAt(int row, int column) {
                    float percent = 0;
                    if (row == 0)
                      percent = (float) results.getConfidence(order[column]);
                    else
                      percent = (float) results.getSupport(order[column]);
                    return new Float(percent);
                  }
                }



            private final ImageIcon I = new ImageIcon(getImage("/images/addbutton.gif"));
            /*************************************************************************
             * This is the private class that runs the RuleFilter.  It is based on the
             * FilterConstruction module.
             */
            public class RuleFilter extends JUserPane
                    implements ActionListener, ExpressionListener {
                  private ExpressionGUI gui;
                  private RuleFilterExpression expression;
                  private RuleVisView view;
                  private RuleTable table;
                  private java.util.List names;
                  private LinkedList attributes = new LinkedList();
                  private LinkedList values = new LinkedList();
                  private JButton addColumnButton, addValueButton, addOperationButton,
                      addBooleanButton, abortButton, doneButton, helpButton,
                      addAnteConsButton;
                  private JComboBox attributeBox, valueBox, operationBox, booleanBox,
                      anteConsBox;
                  //private JPanel comboOrFieldPanel;
                  //private CardLayout nominalOrScalarLayout;
                  private ViewModule mod;
                  private String _lastExpression = "";
                  private boolean initialized = false;

                  JFrame F;
                  /**
                   * Constructor to create the JFrame in which the filter will operate
                   * @param view is the RuleVisView upon which the filter will operate
                   */
                  public RuleFilter(RuleVisView view){
                    F = new JFrame("Rule Filter");
                    F.getContentPane().add(this);
                    F.setSize(600,262);
                    F.setVisible(true);
                    this.view = view;
                  }

                  public void initView(ViewModule m) {
                    mod = m;
                  }

                  public void setInput(Object obj, int ind) {
                    if (ind != 0)
                      return;
                    table = (RuleTable)obj;
                    names = table.getNamesList();  //<-the full att/val description
                    parse(names);
                    initialize();
                  }

                  /**
                   *Seperate the attributes from the values (the combinations
                   *given by RuleTable).  This assumes the 'names' list is in the form of
                   *'attribute=value'.
                   */
                  private void parse(java.util.List names){
                    for(int i = 0; i < names.size(); i++){
                      if(names.get(i) == null)
                        ;
                      else{
                        String name = names.get(i).toString();
                        int index = name.indexOf("=");
                        String att = name.substring(0,index);
                        String val = name.substring(index+1);
                        index = attributes.indexOf(att);
                        //indexOf returns a -1 if the member is not in the list
                        if(index == -1){
                          attributes.add(att);
                          //values contains linked lists of values for each attribute
                          values.add(new LinkedList());
                          LinkedList temp = (LinkedList)values.getLast();
                          temp.add(val); //adding the value specified in 'attribute=value'
                        }
                        else{  //the attribute has already been stored
                          LinkedList temp = (LinkedList)values.get(index);
                          temp.add(val);
                        }
                      }
                    }
                  }


                  private void initialize() {  //set up the visuals for the filter
                    expression = new RuleFilterExpression(table, attributes, values, view);
                    gui = new ExpressionGUI(expression, false);
                    gui.addExpressionListener(this);
                    gui.getTextArea().setText(_lastExpression);

                    attributeBox = new JComboBox();
                    attributeBox.addActionListener(this);
                    //load the attribute combobox with those that show up in the RuleVis
                    for(int i = 0; i < attributes.size(); i++){
                      attributeBox.addItem(attributes.get(i));
                    }
                    //initialize valueBox to default
                    valueBox = new JComboBox();
                    LinkedList temp = (LinkedList)values.get(0);
                    for(int j = 0; j < temp.size(); j++)
                      valueBox.addItem(temp.get(j));

                    addColumnButton = new JButton(I);
                    addColumnButton.addActionListener(this);
                    JPanel columnPanel = new JPanel();
                    columnPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(columnPanel, new JLabel(), 0, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
                    Constrain.setConstraints(columnPanel, attributeBox, 1, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
                    Constrain.setConstraints(columnPanel, addColumnButton, 2, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

                    addValueButton = new JButton(I);
                    addValueButton.addActionListener(this);
                    JPanel nominalOrScalarPanel = new JPanel();
                    nominalOrScalarPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(nominalOrScalarPanel, new JLabel(), 0, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
                    Constrain.setConstraints(nominalOrScalarPanel, valueBox, 1, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
                    Constrain.setConstraints(nominalOrScalarPanel, addValueButton, 2, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

                    operationBox = new JComboBox();
                    operationBox.addItem("==");
                    operationBox.addItem("!=");
                    addOperationButton = new JButton(I);
                    addOperationButton.addActionListener(this);
                    JPanel operationPanel = new JPanel();
                    operationPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(operationPanel, new JLabel(), 0, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
                    Constrain.setConstraints(operationPanel, operationBox, 1, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
                    Constrain.setConstraints(operationPanel, addOperationButton, 2, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

                    booleanBox = new JComboBox();
                    booleanBox.addItem("&&");
                    booleanBox.addItem("||");
                    addBooleanButton = new JButton(I);
                    addBooleanButton.addActionListener(this);
                    JPanel booleanPanel = new JPanel();
                    booleanPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(booleanPanel, new JLabel(), 0, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
                    Constrain.setConstraints(booleanPanel, booleanBox, 1, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
                    Constrain.setConstraints(booleanPanel, addBooleanButton, 2, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

                    anteConsBox = new JComboBox();
                    anteConsBox.addItem("antecedent");
                    anteConsBox.addItem("consequent");
                    addAnteConsButton = new JButton(I);
                    addAnteConsButton.addActionListener(this);
                    JPanel anteConsPanel = new JPanel();
                    anteConsPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(anteConsPanel, new JLabel(), 0, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
                    Constrain.setConstraints(anteConsPanel, anteConsBox, 1, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
                    Constrain.setConstraints(anteConsPanel, addAnteConsButton, 2, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);


                    JPanel leftPanel = new JPanel();
                    leftPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(leftPanel, columnPanel, 0, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
                    Constrain.setConstraints(leftPanel, nominalOrScalarPanel, 0, 1, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
                    Constrain.setConstraints(leftPanel, operationPanel, 0, 2, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
                    Constrain.setConstraints(leftPanel, booleanPanel, 0, 3, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
                    Constrain.setConstraints(leftPanel, anteConsPanel, 0, 4, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.NORTH, 1, 0);
                    Constrain.setConstraints(leftPanel, new JLabel(), 0, 5, 1, 1,
                                             GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

                    JPanel topPanel = new JPanel();
                    topPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(topPanel, leftPanel, 0, 0, 1, 1,
                                             GridBagConstraints.VERTICAL, GridBagConstraints.WEST, 0, 1);
                    Constrain.setConstraints(topPanel, gui, 1, 0, 1, 1,
                                             GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);

                    abortButton = new JButton("Abort");
                    abortButton.addActionListener(this);
                    helpButton = new JButton("Help");
                    helpButton.addActionListener(new AbstractAction() {
                      public void actionPerformed(ActionEvent e) {
                        HelpWindow help = new HelpWindow();
                        help.setVisible(true);
                      }
                    });

                    JButton addButton = gui.getAddButton();
                    addButton.setText("Done");
                    JPanel bottomPanel = new JPanel();
                    bottomPanel.setLayout(new GridBagLayout());
                    Constrain.setConstraints(bottomPanel, helpButton, 0, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
                    Constrain.setConstraints(bottomPanel, new JLabel(), 1, 0, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
                    Constrain.setConstraints(bottomPanel, abortButton, 2, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
                    Constrain.setConstraints(bottomPanel, addButton, 3, 0, 1, 1,
                                             GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);

                    this.setLayout(new GridBagLayout());
                    Constrain.setConstraints(this, topPanel, 0, 0, 1, 1,
                                             GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
                    Constrain.setConstraints(this, new JSeparator(), 0, 1, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);
                    Constrain.setConstraints(this, bottomPanel, 0, 2, 1, 1,
                                             GridBagConstraints.HORIZONTAL, GridBagConstraints.SOUTH, 1, 0);

                    initialized = true;
                  }


                  //specifiy actions
                  public void actionPerformed(ActionEvent e) {
                    Object src = e.getSource();

                    if (src == attributeBox && initialized) {
                      //populate the other combobox with the various choices for the selected attribute
                      int index = attributeBox.getSelectedIndex();
                      valueBox.removeAllItems();
                      LinkedList temp = (LinkedList)values.get(index);
                      for(int i = 0; i < temp.size(); i++)
                        valueBox.addItem(temp.get(i));
                    }

                    else if (src == addColumnButton)
                      gui.getTextArea().insert((String)attributeBox.getSelectedItem(),
                                               gui.getTextArea().getCaretPosition());
                    else if (src == addValueButton)
                      gui.getTextArea().insert((String)valueBox.getSelectedItem(),
                                               gui.getTextArea().getCaretPosition());
                    else if (src == addOperationButton)
                      gui.getTextArea().insert(" " + operationBox.getSelectedItem() + " ",
                                               gui.getTextArea().getCaretPosition());
                    else if (src == addBooleanButton)
                      gui.getTextArea().insert(" " + booleanBox.getSelectedItem() + " ",
                                               gui.getTextArea().getCaretPosition());
                    else if(src == addAnteConsButton){
                      gui.getTextArea().insert(" " + anteConsBox.getSelectedItem() + "(  )",
                                               gui.getTextArea().getCaretPosition());
                      gui.getTextArea().moveCaretPosition(gui.getTextArea().getCaretPosition() - 2);
                    }
                    else if (src == abortButton)
                      F.dispose();
                  }

                  public void expressionChanged(Object evaluation) {
                    _lastExpression = gui.getTextArea().getText();
                  }

                  //Loads the RuleFilter help text
                  private class HelpWindow extends JD2KFrame {
                    public HelpWindow() {
                      super ("RuleFilter Help");
                      JEditorPane ep = new JEditorPane("text/html", getHelpString());
                      ep.setCaretPosition(0);
                      ep.setBackground(yellowish);
                      getContentPane().add(new JScrollPane(ep));
                      setSize(510, 390);
                    }
                  }

                  private String getHelpString() {
                    StringBuffer sb = new StringBuffer();
                    sb.append("<html><body><h2>RuleFilter</h2>");
                    sb.append("This module allows a user to filter rules from the RuleVis ");
                    sb.append("by specifying an appropriate filtering expression.");
                    sb.append("<br><br>");
                    sb.append("The user can construct an expression for filtering ");
                    sb.append("using the lists of values and operators on the left. ");
                    sb.append("It is important that this expression be well-formed: that ");
                    sb.append("parentheses match, that attributes and values surround ");
                    sb.append("operators, and so forth.");
                    sb.append("<br><br>");
                    sb.append("Attributes may not contain the following ");
                    sb.append("characters: =, !, |, &.  The words 'antecedent' or ");
                    sb.append("'consequent' may be added to specify that what is desired ");
                    sb.append("is only the antecedent or consequent of the given criteria.  These two options ");
                    sb.append("must evaluate over a simple == or != statement.  For ");
                    sb.append("instance, consequent(attribute1 == value1) is valid while ");
                    sb.append("consequent(attribute1 == value1 && attribute2 == value2) is not.  ");
                    sb.append("However, several attribute/consequent specifiers may be strung ");
                    sb.append("together with the boolean operators && and ||.  For instance, the string ");
                    sb.append("consequent(attribute1 == value1) && antecedent(attribute2 != value2) is valid");
                    sb.append("</body></html>");
                    return sb.toString();
                  }
                }

                /**
                 * This class interprets the expression entered in RuleFilter.  This implementation
                 * is modled after FilterExpression.
                 */
                public class RuleFilterExpression implements Expression {

                  private HashMap attributeToIndex;
                  private HashMap valueToIndex;
                  private Node root;
                  private RuleTable table;
                  private java.util.List attributes;
                  private java.util.List values;
                  private RuleVisView view;

                  public RuleFilterExpression(RuleTable table, java.util.List attributes,
                                                   java.util.List values, RuleVisView view){
                         this.table = table;
                         this.attributes = attributes;
                         this.values = values;
                         this.view = view;

                         //create these HashMaps to reference the members of the lists later for
                         //error checking
                         attributeToIndex = new HashMap();
                         valueToIndex = new HashMap();
                         for (int i = 0; i < attributes.size(); i++)
                           attributeToIndex.put(attributes.get(i), new Integer(i));
                         for (int i = 0; i < values.size(); i++)
                           for (int j = 0; j < ((LinkedList)values.get(i)).size(); j++){
                           valueToIndex.put(((LinkedList)values.get(i)).get(j), new Integer(i));
                         }
                     }


                     /**
                      * This evalutates the expression entered in the RuleFilter for each rule
                      * that exists in the RuleVis visual.  It makes a call to root.evaluate and
                      * from there it recursively evaluates the expression.
                      *
                      * @return a boolean (Object) array that contains an entry for each
                      * rule in this manner:  true if the rule meets the expression criteria,
                      * false if it fails to meet the expression criteria
                      * @throws ExpressionException if there is an error in the entered expression.
                      */
                  public Object evaluate() throws ExpressionException {
                    if(root == null || attributes == null)
                      return null;

                    //boolean isValid = false;
                    int numTrue = 0;
                    boolean[] rulesToShow = new boolean[table.getNumRulesShowing()];
                    for (int i = 0; i < rulesToShow.length; i++){
                      rulesToShow[i] = root.evaluate(i);
                      if(rulesToShow[i] == true)
                        numTrue++;
                    }
                    if(numTrue == 0)
                      throw new ExpressionException("FilterExpression: no rules meet your filter criteria");
                    rvdm = new RuleVisDataModel (ruleTable);
                    rvdm.condenseRules(rulesToShow, numTrue);
                    table.rulesToDisplay(rulesToShow, order);  //make the filter changes in RuleTable
                    view.updateView();   //make the filter changes in the Vis
                    return (Object)rulesToShow;
                  }


                  /**
                   * The expression is set by ExpressionGui.
                   * @param expression is the string entered in the filter
                   * @throws ExpressionException if the filter criteria is invalid.
                   */
                  public void setExpression(String expression) throws ExpressionException {
                    root = parse(expression);
                  }

                  public String toString() {
                    return root.toString();
                  }

                  /******************************************************************************/
                  /* The filter expression string is parsed into a tree in which each node is   */
                  /* either a subexpression or a terminal.                                      */
                  /*                                                                            */
                  /* subexpression:   <terminal> <boolean operator> <terminal>                  */
                  /* terminal:        <element> <comparison operator> <element>                 */
                  /******************************************************************************/

                  private static final int OP_EQ = 100,    // equal to

                      OP_NEQ = 101,   // not equal to
                      BOOL_AND = 106, // boolean AND operator
                      BOOL_OR = 107;  // boolean OR operator

                  private abstract class Node {
                    abstract boolean evaluate(int rowNumber) throws ExpressionException;
                    public abstract String toString();
                  }

                  private class Subexpression extends Node {
                    private int opcode;
                    private Node left, right;

                    Subexpression(int opcode, Node left, Node right) {
                      this.opcode = opcode;
                      this.left = left;
                      this.right = right;
                    }

                    /*
                     * Evaluates expressions in the form terminal op terminal
                     */
                    boolean evaluate(int rowNumber) throws ExpressionException {

                      if(left == null || right == null)
                        throw new ExpressionException("FilterExpression:  && and || must operate on two values");

                      switch(opcode) {
                        case BOOL_AND:
                          return left.evaluate(rowNumber) && right.evaluate(rowNumber);
                        case BOOL_OR:
                          return left.evaluate(rowNumber) || right.evaluate(rowNumber);
                        default:
                          throw new ExpressionException("FilterExpression: illegal opcode: " + opcode);
                      }
                    }

                    public String toString() {

                      StringBuffer buffer = new StringBuffer();
                      buffer.append('(');
                      buffer.append(left.toString());
                      buffer.append(' ');

                      switch (opcode) {
                          case BOOL_AND: buffer.append("&&"); break;
                          case BOOL_OR:  buffer.append("||"); break;
                          default:       buffer.append("??"); break;
                      }
                      buffer.append(' ');
                      buffer.append(right.toString());
                      buffer.append(')');
                      return buffer.toString();
                    }
                  }


                  private class Terminal extends Node {
                    private int opcode;
                    private Element left, right;

                    Terminal(int opcode, Element left, Element right) {
                      this.opcode = opcode;
                      this.left = left;
                      this.right = right;
                    }

                    /*
                     * Evaluates expression in the form element eq element
                     */
                    boolean evaluate(int rowNumber) throws ExpressionException {

                    if (left instanceof AttributeElement) {
                        if (right instanceof AttributeElement) { //attribute op attribute
                            throw new ExpressionException("FilterExpression: invalid operation <attribute> <op> <attribute>");
                          }
                          else { //attribute op nominal
                            int[] rows;

                            switch (opcode) {
                              case OP_EQ:
                                rows = ((AttributeElement)left).evaluateString(order[rowNumber]);
                                for(int i = 0; i < rows.length; i++){
                                  //compare if the criteria in the filter match the att/value combinations
                                  if(table.getNamesList().get(rows[i]).equals(
                                    ((AttributeElement)left).toString() + "=" + ((NominalElement) right).evaluate())){
                                    return true;}
                                }
                                return false;

                              case OP_NEQ:
                                rows = ((AttributeElement)left).evaluateString(order[rowNumber]);
                                for(int i = 0; i < rows.length; i++){
                                  if(table.getNamesList().get(rows[i]).equals(
                                    ((AttributeElement)left).toString() + "=" + ((NominalElement) right).evaluate()))
                                    return false;
                                }
                                return true;

                              default:
                                throw new ExpressionException("FilterExpression: illegal opcode on nominal: " + opcode);
                            }
                          }
                      }

                      else { // left instanceof NominalElement
                        if (right instanceof AttributeElement) { //nominal op attribute
                          int[] rows;
                          switch (opcode) {
                            case OP_EQ:
                              rows = ((AttributeElement)right).evaluateString(order[rowNumber]);
                              for(int i = 0; i < rows.length; i++){
                                if(table.getNamesList().get(rows[i]).equals(
                                  ((AttributeElement)right).toString() + "=" + ((NominalElement)left).evaluate()))
                                  return true;
                              }
                              return false;

                            case OP_NEQ:
                              rows = ((AttributeElement)right).evaluateString(order[rowNumber]);
                              for(int i = 0; i < rows.length; i++){
                                if(table.getNamesList().get(rows[i]).equals(
                                  ((AttributeElement)right).toString() + "=" + ((NominalElement)left).evaluate()))
                                  return false;
                              }
                              return true;

                            default:
                              throw new ExpressionException("FilterExpression: illegal opcode on nominal: " + opcode);
                          }
                        }

                        else { // right instanceof NominalElement
                          throw new ExpressionException("FilterExpression: invalid operation: <nominal> <op> <nominal>");
                        }
                      }
                    }
                  //end Terminal.evaluate

                  public String toString() {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append('(');
                    buffer.append(left.toString());
                    buffer.append(' ');
                    switch (opcode) {
                        case OP_EQ:  buffer.append("=="); break;
                        case OP_NEQ: buffer.append("!="); break;
                        default:     buffer.append("??"); break;
                    }
                    buffer.append(' ');
                    buffer.append(right.toString());
                    buffer.append(')');

                    return buffer.toString();
                  }
                }


                /******************************************************************************/
                /* Elements -- the building blocks of a filter expression string -- can be    */
                /* attributes or values, which are necessary to make up a rule.               */
                /******************************************************************************/

                private abstract class Element {
                  public abstract String toString();
                }

                private class AttributeElement extends Element{
                  private int attributeNumber;
                  private String attributeLabel;
                  private int condition;

                  AttributeElement(String attributeLabel, int condition) throws ExpressionException {
                    Integer I = (Integer)attributeToIndex.get(attributeLabel);
                    if (I == null){
/*                      if (condition == 1)
                        throw new ExpressionException(
                            "FilterExpression: antecedent() must contain <valid attribute> op <valid value>");
                      else if (condition == 2)
                        throw new ExpressionException(
                            "FilterExpression: consequent() must contain <valid attribute> op <valid value>");
                      else*/
                        throw new ExpressionException(
                            "FilterExpression: invalid attribute label: " + attributeLabel);
                    }
                    this.attributeLabel = attributeLabel;
                    this.condition = condition;
                  }

                  public int[] evaluateString(int ruleNumber) {

                    if(this.condition == 1) //the element was in an antecedent()
                      return table.getRuleAntecedent(ruleNumber);
                    if(this.condition == 2){ //the element was in a consequent()
                      return table.getRuleConsequent(ruleNumber);
                    }
                    //we want either consequent or antecedent
                    int[] ante = table.getRuleAntecedent(ruleNumber);
                    int[] both = new int[ante.length + 1];
                    for(int i = 0; i < ante.length; i++){
                      both[i] = ante[i];
                    }
                    //the next line assumes that there can only be one consequent per rule
                    both[ante.length] = table.getRuleConsequent(ruleNumber)[0];
                    return both;
                  }

                  public String toString() {
                    return attributeLabel;
                  }
                }

                //NominalElements are the values
                private class NominalElement extends Element {
                  private String value;

                  NominalElement(String value) throws ExpressionException{
                    Integer I = (Integer)valueToIndex.get(value);
                    if (I == null)
                      throw new ExpressionException("FilterExpression: invalid value label: " + value);
                    this.value = value;
                  }

                  String evaluate() {
                    return value;
                  }

                  public String toString() {
                    StringBuffer buffer = new StringBuffer();
                    buffer.append('\'');
                    buffer.append(value);
                    buffer.append('\'');
                    return buffer.toString();
                  }
                }

                /******************************************************************************/
                /* The expression string is broken down into subexpressions and parsed        */
                /* recursively.                                                               */
                /******************************************************************************/

                private Node parse(String expression) throws ExpressionException {
                  if (expression.length() == 0)
                    return null;
                  char c;
                  int condition = 0;
                  boolean booleanOperatorFound = false;
                  boolean inAntecedent = false;
                  boolean inConsequent = false;
                  int currentDepth = 0, leastDepth = Integer.MAX_VALUE, maximumDepth = 0,
                      leastPrecedenceType = BOOL_AND, leastPrecedencePosition = -1;

                  for (int i = 0; i < expression.length(); i++) {
                    c = expression.charAt(i);
                    switch(c) {

                      case 'a':
                        if((i + 10) < expression.length() && expression.substring(i, i + 10).equals("antecedent")){
                          inAntecedent = true;
                        }
                        break;

                      case 'c':
                        if((i + 10) < expression.length() && expression.substring(i, i + 10).equals("consequent")){
                          inConsequent = true;
                        }
                        break;

                      case '(': currentDepth++; break;
                      case ')': currentDepth--; break;
                      case '&':
                        if (i + 1 == expression.length())
                              throw new ExpressionException(
                                  "FilterExpression:  atecedent( ) and consequent( ) must contain\n              <valid attribute> op <valid value>");
                        else if(expression.charAt(i+1) != '&')
                              throw new ExpressionException(
                                  "FilterExpression: invalid single '&' at position " + i);

                        booleanOperatorFound = true;
                        if (currentDepth < leastDepth ||
                                    currentDepth == leastDepth && BOOL_AND >= leastPrecedenceType) {
                          leastDepth = currentDepth;
                          leastPrecedenceType = BOOL_AND;
                          leastPrecedencePosition = i;
                        }
                        i++;
                        break;

                      case '|':
                        if (i + 1 == expression.length())
                              throw new ExpressionException(
                                  "FilterExpression:  atecedent( ) and consequent( ) must contain\n              <valid attribute> op <valid value>");
                        else if (expression.charAt(i + 1) != '|')
                          throw new ExpressionException("FilterExpression: invalid single '|' at position " + i);
                        booleanOperatorFound = true;
                        if (currentDepth < leastDepth ||
                                    currentDepth == leastDepth && BOOL_OR >= leastPrecedenceType) {
                          leastDepth = currentDepth;
                          leastPrecedenceType = BOOL_OR;
                          leastPrecedencePosition = i;
                        }
                        i++;
                        break;
                    }
                    if (currentDepth > maximumDepth)
                      maximumDepth = currentDepth;
                  }

                  if (leastDepth > maximumDepth) // ...there were no parentheses
                    leastDepth = 0;

                  if (booleanOperatorFound) { // ...we must recurse
                    // remove extraneous parentheses first
                    for (int i = 0; i < leastDepth; i++) {
                      expression = expression.trim();
                      expression = expression.substring(1, expression.length() - 1);
                      leastPrecedencePosition--;
                    }
                    return new Subexpression(
                                leastPrecedenceType,
                                parse(expression.substring(0, leastPrecedencePosition).trim()),
                                parse(expression.substring(leastPrecedencePosition + 2, expression.length()).trim())
                                );
                  }
                  else { // ...this is a terminal
                    // remove extraneous parentheses
                    if(inAntecedent){
                      expression = expression.trim();
                      expression = expression.substring(10);
                      condition = 1;  //set the condition to reflect that an antecedent only is desired
                    }
                    if(inConsequent){
                      expression = expression.trim();
                      expression = expression.substring(10);
                      condition = 2;  //set the condition to reflect that an consequent only is desired
                    }
                    for (int i = 0; i < maximumDepth; i++) {
                      expression = expression.trim();
                      expression = expression.substring(1, expression.length() - 1);
                    }
                    return parseTerminal(expression, condition);
                  }
                }

                //At this point, we're dealing with attribute <op> value or value <op> attribute only
                private Node parseTerminal(String expression, int condition) throws ExpressionException {
                  char c, d;
                  boolean operatorFound = false;

                  for (int i = 0; i < expression.length(); i++) {
                    c = expression.charAt(i);
                    switch(c) {

                      case '=':
                        if (i + 1 == expression.length() || expression.charAt(i + 1) != '=')
                          throw new ExpressionException("FilterExpression: invalid single '=' in expression");

                        return new Terminal(
                                  OP_EQ,
                                  parseElement(expression.substring(0, i).trim(), condition),
                                  parseElement(expression.substring(i + 2, expression.length()).trim(), condition)
                                  );

                      case '!':
                        if (i + 1 == expression.length() || expression.charAt(i + 1) != '=')
                          throw new ExpressionException("FilterExpression: invalid single '!' in expression");

                        return new Terminal(
                                  OP_NEQ,
                                  parseElement(expression.substring(0, i).trim(), condition),
                                  parseElement(expression.substring(i + 2, expression.length()).trim(), condition)
                                  );
                    }
                  }
                  throw new ExpressionException("FilterExpression: apparently malformed expression.");
                }


                private Element parseElement(String expression, int condition) throws ExpressionException {
                  if (expression.length() == 0)
                    throw new ExpressionException("FilterExpression: encountered empty element");

                  Integer I = (Integer)attributeToIndex.get(expression);
                  if(I == null)
                    return new NominalElement(expression);
                  return new AttributeElement(expression, condition);
                }
              }
            } //end RuleVisView class

            /**
             * This small class runs the HelpWindow.
             */
            private final class HelpWindow extends JD2KFrame {
              HelpWindow() {
                super("About Rule Visualization");
                JEditorPane jep = new JEditorPane("text/html", getModuleInfo());
                jep.setBackground(yellowish);
                getContentPane().add(new JScrollPane(jep));
                setSize(400, 400);
              }
            }

       private static final String HEAD = "Head";
       private static final String BODY = "Body";
       private static final String IF = "If";
       private static final String THEN = "Then";

   }

// Start QA Comments
//
// 3/*/03  - Ruth did QA and Tom did updates to the module.
// 3/28/03 - Ready for basic.
//         - WISH:  Group items so attributes show up next to each other (perhaps
//                  done earlier in module sequence prior to building items or rule table.
//         - WISH:  Allow sorting by items that have included in most rules.
//         - WISH:  Show rule consequent items at top of item list.
//         - WISH:  Put back option to save as PMML when that is working.
//         - WISH:  Offer way to print entire matrix, not just viewable area.
// 4/5/03  - Updated Info a bit after Loretta changed so items that aren't in any rules
//           are no longer displayed.  Also, removed that from the Wish List. - ruth
// End QA Comments.


   /**
 * 01-23-04: vered
 * edited about text.
 *
 * 01-26-04:
 * bug 234 - filter button displyas a blank window.
 */