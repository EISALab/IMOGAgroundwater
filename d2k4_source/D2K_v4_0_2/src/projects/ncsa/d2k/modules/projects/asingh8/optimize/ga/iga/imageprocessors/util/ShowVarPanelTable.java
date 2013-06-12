package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.imageprocessors.util;


import ncsa.d2k.core.modules.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.datatype.table.transformations.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;
import ncsa.d2k.modules.projects.pgroves.vis.*;
import ncsa.d2k.modules.projects.pgroves.bp.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.*;
/**
	Displays a passed in JPanel with OK and ABORT buttons added
	below. This code is basically TableViewer with the TableMatrix
	stripped out and replaced by the passed in JPanel.


	@author pgroves
	@date 01/21/04
	*/

public class ShowVarPanelTable extends UIModule
	implements java.io.Serializable {

	//////////////////////
	//d2k Props
	////////////////////

	boolean debug=false;
	/////////////////////////
	/// other fields
	////////////////////////

        /** Image Max and Min intensities that should be painted */
        public double userImgMin, userImgMax;

        //inputs
        private FalseColorPanelArray falColPanArr;
        /** the current population */
        private IGANsgaPopulation currentPop;
        // IDs of archived individuals
        private int [] archivedInds;
        /** array storing ranks of individuals gathered from various ranking panels */
        protected double [] rankIndivs ;
        /** Output Table that stores has the archived Individual IDs and their human ranks. */
        protected MutableTableImpl archiveUpdatedRanksTable;
        /** flags to detect input arrival*/
        boolean fcArrayArrivedFlag = false;
        boolean popArrivedFlag = false;
        boolean archivedIndsArrivedFlag = false;
	//////////////////////////
	///d2k control methods
	///////////////////////

	public boolean isReady(){
		return super.isReady();
	}
	public void endExecution(){
		wipeFields();
		super.endExecution();
	}
	public void beginExecution(){
		wipeFields();
		super.beginExecution();
	}
	public void wipeFields(){
	}

	//////////////////////////
	//special ui module methods
	//////////////////////////
         public double getUserImageMin (){
            return userImgMin;
         }
         public double getUserImageMax (){
            return userImgMax;
         }
         public void setUserImageMin (double d){
            userImgMin = d;
         }
         public void setUserImageMax (double d){
            userImgMax = d;
         }
	/**
	* Not used by this module.
	*/
	public String[] getFieldNameMapping() {
		return null;
	}

	/**
       @return The UserView part of this module.
    */
    public UserView createUserView() {
		return new PanelView();
    }


  /**
  		Holds the buttons and the past in JPanel
    */
    public class PanelView extends JUserPane
        implements java.io.Serializable, ActionListener{

      /**a reference to the module that we belong to*/
      ShowVarPanelTable parent = null;

      /** ok button*/
      protected JButton ok;
      /** cancel button*/
      protected JButton cancel;
      // temporary variable for picture ID
      int picId;
     /**
         Initialize the view.  Insert all components into the view.
         @param mod The ShowPanelTable module that owns us
         */
      public void initView(ViewModule mod) {
         parent = (ShowVarPanelTable)mod;
      }
	     /**
         Called whenever inputs arrive to the module. Redraws everything.
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {


            if(idx == 0) {
              falColPanArr = (FalseColorPanelArray) input;
              fcArrayArrivedFlag = true ;
            }
            else if (idx == 1) {
              currentPop = (IGANsgaPopulation) input;
              popArrivedFlag = true ;
            }
            else if (idx == 2) {
              archivedInds = (int[]) input;
              archivedIndsArrivedFlag = true ;
              System.out.println("Size of archivedInds in ShowBPPanelTable : " + archivedInds.length);
            }
            

            synchronized (this) {
                if ((fcArrayArrivedFlag == true) && (popArrivedFlag == true)&& (archivedIndsArrivedFlag == true)){
                    this.removeAll();
                    fcArrayArrivedFlag = false ;
                    popArrivedFlag = false ;
                    archivedIndsArrivedFlag = false ;
                    // This stores all the FalseColor visual images related to an individual
                    JPanel[][] panelIn = (JPanel[][]) falColPanArr.falColPan;
                    // This stores the names of the visual images
                    String [][] panelInTitle = (String[][]) falColPanArr.panelTitles ;
                    int numInds = 0;
                    int numIndPics = 0;
                    if(panelIn != null) {
                    numInds= panelIn.length;
                    numIndPics = panelIn[0].length;
                    }

                    // a panel to put the buttons on
                    JPanel buttonPanel = new JPanel();
                    ok = new JButton("Done (when session over)");
                    ok.addActionListener(this);
                    cancel = new JButton("Abort");
                    cancel.addActionListener(this);
                    buttonPanel.add(cancel);
                    buttonPanel.add(ok);



                    // add pictures to a grid panel
                  /*  JPanel gridPanel = new JPanel ();
                    gridPanel.setLayout(new GridLayout(panelIn.length,panelIn[0].length));
                    gridPanel.setMinimumSize(new Dimension(450,150));
                    for (int i=0; i < numInds; i++){
                      for (int j=0; j < numIndPics; j++){
                        // obtain pictures from false color panel
                        FalseColorPanel fc = (FalseColorPanel)(panelIn[i][j]);
                        fc.setUserImageMax(userImgMax);
                        fc.setUserImageMin(userImgMin);
                        // create a radio button group

                        // add pictures and button group to a panel

                        // add panel to gridPanel
                        gridPanel.add(fc);
                      }
                    }*/
    // *************************************************************************
                // rankPicture stores the picture and button panels
                JPanel [] rankPicture = new JPanel[numInds];
                // pictureScrollPane stores the pictures
                JScrollPane [] pictureScrollPane = new JScrollPane[numInds];
                // rankButtonPanel stores all the radio buttons
                JPanel [] rankButtonPanel = new JPanel[numInds];
                // radio buttons for every picture
                JRadioButton [] radioButton = new JRadioButton [numInds];
                // radio button groups
                ButtonGroup [] rbg = new ButtonGroup [numInds];

                rankIndivs = new double [numInds];

                for (int i =0; i<numInds; i++){

                    rankPicture [i] = new JPanel();
                    // ADD IMAGES HERE
                    // add pictures to a grid panel

                      float hc,sc,bc;
                      hc = 0.219F;
                      sc = 0.141F;
                      bc = 0.823F;
                      JPanel picgridTitlePanel = new JPanel();
                      picgridTitlePanel.setLayout(new GridLayout(4,numIndPics));
                      picgridTitlePanel.setBackground(Color.getHSBColor(hc,sc,bc));
                     // picgridTitlePanel.setPreferredSize(new Dimension(450,20));

                     String [] ObjTitles = new String [currentPop.getNumObjectives()];
                     Double [] ObjValues = new Double [currentPop.getNumObjectives()];
                     IGANsgaSolution picInd;
                     String ObjecTitle = new String ();
                     // obtaining information of Objectives for this individual

                     System.out.println(" i : " + i);
                     if (archivedInds[i] != -1) {
                        picInd = (IGANsgaSolution) (currentPop.getIndInHumanRankedPopulationArchive(archivedInds[i]));

                        for ( int n = 0; n < currentPop.getNumObjectives(); n++) {
                          ObjTitles [n] = currentPop.getObjectiveConstraints()[n].getName();
                          ObjValues [n] = new Double(picInd.getObjective(n));
                          ObjecTitle = ObjecTitle + ObjTitles [n] + " : " + ObjValues [n].toString() + " , ";
                        }


                        // initializing rankIndivs with previous human ranks
                        for (int m=0; m < currentPop.getNumObjectives(); m++) {
                            if (currentPop.getIgaQualObj()[m] == true) {
                              rankIndivs [i] = ObjValues [m].doubleValue();
                            }
                        }
                     }
                     else{
                              rankIndivs [i] = 1;
                     }
                     ////////////////////////////////////////////////////////////////////
                      for (int j=0; j< numIndPics; j++){
                        if (archivedInds[i] != -1) {
                          picgridTitlePanel.add(new JLabel(panelInTitle[i][j]));
                          picgridTitlePanel.add(new JLabel(ObjecTitle));
                          //picgridTitlePanel.add(new JLabel(" o : Wells Installed,  + : Wells Not Installed"));
                        } else {
                          //picgridTitlePanel.add(new JLabel("ALL WELLS SOLUTION"));
                          //picgridTitlePanel.add(new JLabel(" o : Wells Installed,  + : Wells Not Installed"));
                        }
                      }

                      JPanel picgridPanel = new JPanel();
                      //JScrollPane picgridPanel = new JScrollPane();
                      picgridPanel.setLayout(new GridLayout(1,numIndPics));
                      picgridPanel.setPreferredSize(new Dimension(450,150));
                      for (int j=0; j< numIndPics; j++){
                          FalseColorPanel fc = (FalseColorPanel)(panelIn[i][j]);
                          fc.setUserImageMax(userImgMax);
                          fc.setUserImageMin(userImgMin);
                          picgridPanel.add(fc);
                      }
                      JPanel picAndTitleGridPanel = new JPanel();
                      picAndTitleGridPanel.setLayout(new BoxLayout(picAndTitleGridPanel, BoxLayout.Y_AXIS));
                      picgridTitlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                      picAndTitleGridPanel.add(picgridTitlePanel);
                      picgridPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
                      picAndTitleGridPanel.add(picgridPanel);

                      pictureScrollPane[i] = new JScrollPane(picAndTitleGridPanel);
                      //picture[k][i].setSize(new Dimension(400,200));
                      pictureScrollPane[i].setMinimumSize (new Dimension (400, 250));
                      pictureScrollPane[i].setPreferredSize (new Dimension (400, 250));
                      pictureScrollPane[i].setAutoscrolls(true);

                      //////////////////////////////////////////////////////////////

                      rankButtonPanel[i] = new JPanel();
                      rankButtonPanel[i].setLayout(new GridLayout(1,5));
                      Integer buttonPanelID;
                      rbg[i] = new ButtonGroup();
                      picId = i ;
                      //////////////////////////////////////////////////////////////

                      radioButton[i] = new JRadioButton("1 (Best)");
                      buttonPanelID =  new Integer(i);
                      radioButton[i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[i].add(radioButton[i]);
                      rbg[i].add (radioButton[i]);
                      radioButton[i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                            //  System.out.println("activePicId is :::::::::: "+ activePicId);
                              picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 1;
                             // System.out.println("picId is :::::::::: "+ picId);
                             // System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      if(rankIndivs[picId] == 1) {
                        radioButton[i].setSelected(true);
                      } else {
                        radioButton[i].setSelected(false);
                      }
                      //////////////////////////////////////////////////////////////

                      radioButton[i] = new JRadioButton("2");
                      buttonPanelID =  new Integer(i);
                      radioButton[i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[i].add(radioButton[i]);
                      rbg[i].add (radioButton[i]);
                      radioButton[i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                             // System.out.println("activePicId is :::::::::: "+ activePicId);
                              picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 2;
                             // System.out.println("picId is :::::::::: "+ picId);
                            //  System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      if(rankIndivs[picId] == 2) {
                        radioButton[i].setSelected(true);
                      } else {
                        radioButton[i].setSelected(false);
                      }
                      //////////////////////////////////////////////////////////////

                      radioButton[i] = new JRadioButton("3");
                      buttonPanelID =  new Integer(i);
                      radioButton[i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[i].add(radioButton[i]);
                      rbg[i].add (radioButton[i]);
                      radioButton[i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                              //System.out.println("activePicId is :::::::::: "+ activePicId);
                              picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 3;
                             // System.out.println("picId is :::::::::: "+ picId);
                             // System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      if(rankIndivs[picId] == 3) {
                        radioButton[i].setSelected(true);
                      } else {
                        radioButton[i].setSelected(false);
                      }
                      //////////////////////////////////////////////////////////////

                      radioButton[i] = new JRadioButton("4");
                      buttonPanelID =  new Integer(i);
                      radioButton[i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[i].add(radioButton[i]);
                      rbg[i].add (radioButton[i]);
                      radioButton[i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                             // System.out.println("activePicId is :::::::::: "+ activePicId);
                              picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 4;
                             // System.out.println("picId is :::::::::: "+ picId);
                             // System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      if(rankIndivs[picId] == 4) {
                        radioButton[i].setSelected(true);
                      } else {
                        radioButton[i].setSelected(false);
                      }
                      //////////////////////////////////////////////////////////////

                      radioButton[i] = new JRadioButton("5 (Worst)");
                      buttonPanelID =  new Integer(i);
                      radioButton[i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[i].add(radioButton[i]);
                      rbg[i].add (radioButton[i]);
                      radioButton[i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                             // System.out.println("activePicId is :::::::::: "+ activePicId);
                              picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 5;
                            //  System.out.println("picId is :::::::::: "+ picId);
                            //  System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      if(rankIndivs[picId] == 5) {
                        radioButton[i].setSelected(true);
                      } else {
                        radioButton[i].setSelected(false);
                      }
                      //////////////////////////////////////////////////////////////


                      // 'Details' button to see further detail information on that individual
                      //DetailButton[k][i] = new JButton(" Details >> ");
                      //DetailButton[k][i].setBackground(Color.getHSBColor(h,s,b));

                      rankButtonPanel[i].setBorder(BorderFactory.createLineBorder(Color.white,2));

                      rankPicture[i].setLayout(new BoxLayout(rankPicture[i], BoxLayout.Y_AXIS));
                      //DetailButton[k][i].setAlignmentX(Component.CENTER_ALIGNMENT);
                      //rankPicture[k][i].add(DetailButton[k][i]);
                      pictureScrollPane[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                      rankPicture[i].add(pictureScrollPane[i]);
                      rankButtonPanel[i].setAlignmentX(Component.CENTER_ALIGNMENT);
                      rankPicture[i].add(rankButtonPanel[i]);

                      float hcc,scc,bcc;
                      hcc = 0.168F;
                      scc = 0.208F;
                      bcc = 0.979F;
                      rankPicture[i].setMinimumSize (new Dimension (450, 320));
                      rankPicture[i].setPreferredSize (new Dimension (450, 320));
                      rankPicture[i].setBorder(BorderFactory.createLineBorder(Color.getHSBColor(hcc,scc,bcc),15));


                }
          // *************************************************************************
                // add pictures to a grid panel
                JPanel gridAllPanel = new JPanel ();
                gridAllPanel.setLayout(new GridLayout(numInds,1));
                gridAllPanel.setMinimumSize(new Dimension(450,150));
                for (int i=0; i < numInds; i++){
                    gridAllPanel.add(rankPicture[i]);
                }

                // create a scroll pane
                JScrollPane scrollPane = new JScrollPane(gridAllPanel);
                scrollPane.setMinimumSize(new Dimension(450, 150));
                // add everything to this
                add(scrollPane, BorderLayout.CENTER);
                add(buttonPanel, BorderLayout.SOUTH);
                }
          }//end synchronize
            
      }

      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
         viewDone("Done");
      }

      /**
         This is the ActionListener for the ok and cancel buttons.  The
         finishUp() method is called if ok is pressed.  The viewCancel()
         method of the parent module is called if cancel is
         pressed.
         @param e the ActionEvent
         */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();
         if(src == ok){
                ///////////////////////////////////////////////////
                // UPDATE FITNESSES HERE IN THE POPULATION DATA STRUCTURE BEFORE PUSHING POPULATION
                archiveUpdatedRanksTable = new MutableTableImpl(2);
                archiveUpdatedRanksTable.setColumn(new IntColumn(archivedInds), 0);
                archiveUpdatedRanksTable.setColumn(new DoubleColumn(rankIndivs), 1);

                ///////////////////////////////////////////////////

                pushOutput(archiveUpdatedRanksTable, 0);
                finishUp();
         }
         else if(src == cancel){
            parent.viewCancel();
         }
      }



	}
	/////////////////////
	//work methods
	////////////////////
	/**
		does not get called
	*/
	public void doit() throws Exception{
		if(debug){
			System.out.println(getAlias()+":Firing");
		}
	}


	////////////////////////
	/// D2K Info Methods
	/////////////////////


	public String getModuleInfo(){
		return
			""+
			""+
			""+
			"";
	}

   	public String getModuleName() {
		return "";
	}
	public String[] getInputTypes(){
		String[] types = {"ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.FalseColorPanelArray",
                "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation", "[I"};
		return types;
	}

	public String getInputInfo(int index){
		switch (index) {
			case 0:
				return "False Color Panel Array";
			case 1:
				return "IGA Nsga Population";
			case 2:
				return "Archived individual IDs array";
			default: return "No such input";
		}
	}

	public String getInputName(int index) {
		switch(index) {
			case 0:
				return "JPanel Array";
			case 1:
				return "IGA Nsga Population";
			case 2:
				return "Archived individual IDs array";
			default: return "No such input";
		}
	}
	public String[] getOutputTypes(){
		String[] types = {"ncsa.d2k.modules.core.datatype.table.Table"};
		return types;
	}

	public String getOutputInfo(int index){
		switch (index) {
			case 0:
				return "Archived Individuals Rank Table";
			default: return "No such output";
		}
	}
	public String getOutputName(int index) {
		switch(index) {
			case 0:
				return "Archived Individuals Rank Table";
			default: return "No such output";
		}
	}
	////////////////////////////////
	//D2K Property get/set methods
	///////////////////////////////
	public void setDebug(boolean b){
		debug=b;
	}
	public boolean getDebug(){
		return debug;
	}
	/*
	public boolean get(){
		return ;
	}
	public void set(boolean b){
		=b;
	}
	public double  get(){
		return ;
	}
	public void set(double d){
		=d;
	}
	public int get(){
		return ;
	}
	public void set(int i){
		=i;
	}

	public String get(){
		return ;
	}
	public void set(String s){
		=s;
	}
	*/
}












