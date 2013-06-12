package ncsa.d2k.modules.projects.asingh8.optimize.ga.iga.imageprocessors.vis;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.gui.*;
import ncsa.d2k.gui.*;

import java.awt.Component;
import java.awt.Container;
import javax.swing.BoxLayout;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.ImageIcon;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JToolBar;
import javax.swing.JButton;
import java.net.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.awt.image.BufferedImage ;

import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;

import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.projects.pgroves.vis.falsecolor.*;
import ncsa.d2k.modules.projects.pgroves.vis.*;
import ncsa.d2k.modules.projects.pgroves.bp.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.*;
//import ncsa.d2k.modules.projects.clutter.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
/**
 * This module displays the selected individuals for comparative rating.
 * Images created for this are through the FalseColorPanel that shows
 * visual distribution of attributes in a 2 dimensional field
 * @author Meghna Babbar
 */
public class IGASolutionRankingWindowFCJPanelVar_dummy extends UIModule {

    ////////////////////////////
    // module fields
    ////////////////////////////

    /** Image Max and Min intensities that should be painted */
    public double userImgMin1,userImgMin2, userImgMax1,userImgMax2;

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////

   public UserView createUserView() {
      return new IGARankingView();
   }

   /////////////////////////////////
   // get set methods for fields
   /////////////////////////////////

   public double getUserImageMin1 (){
      return userImgMin1;
   }
   public double getUserImageMin2 (){
      return userImgMin2;
   }
   public double getUserImageMax1 (){
      return userImgMax1;
   }
   
   public double getUserImageMax2 (){
      return userImgMax2;
   }
   
   public void setUserImageMin1 (double d){
      userImgMin1 = d;
  }
      public void setUserImageMin2 (double d){
      userImgMin2 = d;
  }
   public void setUserImageMax1 (double d){
      userImgMax1 = d;
  }
      public void setUserImageMax2 (double d){
      userImgMax2 = d;
  }
   /**
    * Not used by this module.
    */
   public String[] getFieldNameMapping() {
      return null;
   }

   public String getInputInfo(int i) {
      if (i == 0)
         return "The <i>IGA NSGA Population</i>.";
      else if (i == 1)
         return "The <i>Table</i> that contains individuals selected from tradeoff plots";
      else if (i == 2)
         return "The <i>FalseColorPanel Array</i> that contains JPanel visual pictures for selected individuals";
      return "No such output";
   }

   public String getInputName(int i) {
      if (i == 0)
         return "Multiobjective IGA NSGA Population";
      else if (i == 1)
         return "Table";
      else if (i == 2)
         return "FalseColorPanel Array";
      return "No such output";
   }

   public String[] getInputTypes() {
      return new String[] { "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation",
         "ncsa.d2k.modules.core.datatype.table.Table",
         "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.imageprocessors.util.FalseColorPanelArray"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module displays the figures and pictures related to individuals in a population for ranking.");
      return sb.toString();
   }

   public String getModuleName() {
      return "IGA: Solutions Ranking Section (JPanel format)";
   }

   public String getOutputInfo(int i) {
      if (i == 0)
         return "The <i>IGA NSGA Population</i> after updating its ranks and qualitative information.";
      return "No such input";
   }

   public String getOutputName(int i) {
      if (i == 0)
         return "Multiobjective IGA NSGA Population";
      return "No such input";
   }

   public String[] getOutputTypes() {
      return new String[] {
        "ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.IGANsgaPopulation"
      };
   }
////////////////////////////////////////////////////////////////////////////////
// user view                                                                  //
////////////////////////////////////////////////////////////////////////////////

   /**
    * This class uses a <code>TableMatrix</code> to display the
    * <code>Table</code>.
    */
   public class IGARankingView extends JUserPane implements ActionListener   {

      TableMatrix matrix;
      /** the table with data */
     // protected Table table = null;
      /** a reference to our parent module */
      protected IGASolutionRankingWindowFCJPanelVar_dummy parent;
      /** Back button */
      protected JButton back; //ok;
      /** Next button */
      protected JButton next1, next2; // cancel;
      /** Quit button */
      protected JButton quit; //;
      /** JButton Panel for back, quit and next buttons */
      protected JPanel buttonPanel; // = new JPanel(new FlowLayout(FlowLayout.CENTER));
      /** panels to display cardlayout of pictures */
      protected CardLayout pictureCardLayout; // = new CardLayout();
      protected JPanel pictureCardPanel; // = new JPanel();
      /** integer to traverse the cards in a sequence and not in a circle (for cardlayout) */
      protected int cardSequence =0;

      /** multiple panels to hold groupd of pictures for ranking */
      protected JPanel [] picturePanel;
      /** number of panels that will hold all groups of four of all the selected individuals */
      protected int numPanels = 0;
      /** array storing ids of individuals that need to be ranked */
      protected int [] picIndivIds;
      /** array storing ranks of individuals gathered from various ranking panels */
      protected int [] rankIndivs ;
      /** total number of pictures being evaluated */
      int totalPics;

       /** the maximum number of generations of the current population */
      private int maxGen = 0;

      /** the current generation of the current population */
      private int currentGen = 0;

      /** the current population */
      private IGANsgaPopulation currentPop;
      private MutableTable currentPopTable;

      /** FalseColor JPanel Images array **/
      FalseColorPanelArray falColPanArr;

      // which objectives out of all the objectives are qualitative.
      // This array has size 'number of objectives', and if objective #1 is qualitative
      // then qualObjs[1] = true.
      boolean [] qualObjs;

      boolean tableArrivedFlag = false;
      boolean popArrivedFlag = false;
      boolean fcJpanelArrivedFlag = false;

      /**
         Initialize the view.  Insert all components into the view.
         @param mod The IGASolutionRankingWindowFCJPanel module that owns us
         */
      public void initView(ViewModule mod) {
        removeAll();
        currentPop = (IGANsgaPopulation)pullInput(0);
        System.out.println("initview: NSGA POP INPUT ACCEPTED");
        currentPopTable = (MutableTable)pullInput(1);
        System.out.println("initview: MutableTable INPUT ACCEPTED");
        falColPanArr = (FalseColorPanelArray)pullInput(2);
        System.out.println("initview: FalseColorPanelArray INPUT ACCEPTED");
        
        while (currentPop == null){
            currentPop = (IGANsgaPopulation)pullInput(0);
        }
        while (currentPopTable == null){
            currentPopTable = (MutableTable)pullInput(1);
            
        }
        while (falColPanArr ==  null){
            falColPanArr = (FalseColorPanelArray)pullInput(2);
            
        }

        qualObjs = (boolean[]) (currentPop.getIgaQualObj());

        parent = (IGASolutionRankingWindowFCJPanelVar_dummy)mod;
        createPicturesPanel();

      }

      public Object getMenu() {
         return null;
      }

      /**
         Called whenever inputs arrive to the module.
         @param input the Object that is the input
         @param idx the index of the input
         */
      public void setInput(Object input, int idx) {


            if(idx == 0) {
              currentPop = (IGANsgaPopulation) input;
              popArrivedFlag = true ;
              System.out.println("setinput : NSGA POP INPUT ACCEPTED");
            }
            else if (idx == 1) {
              currentPopTable = (MutableTable)input;
              tableArrivedFlag = true ;
              System.out.println("setinput : MutableTable INPUT ACCEPTED");
            }
            else if (idx == 2) {
              falColPanArr = (FalseColorPanelArray)input;
              fcJpanelArrivedFlag = true ;
              System.out.println("setinput : FalseColorPanelArray INPUT ACCEPTED");
            }

            synchronized (this) {
                if ((popArrivedFlag == true) && (tableArrivedFlag == true) && (fcJpanelArrivedFlag == true)){
                  removeAll();
                  createPicturesPanel();
                  popArrivedFlag = false ;
                  tableArrivedFlag = false ;
                  fcJpanelArrivedFlag = false ;
                }
            }

      }

      protected void createPicturesPanel( ) {
               // obtaining number of panels required for holding all pictures in groups of four.
         totalPics = 0;
         for (int i =0; i < currentPopTable.getNumRows(); i++){
            if (currentPopTable.getBoolean(i,currentPopTable.getNumColumns()-2) == true)
                totalPics ++;
         }
         System.out.println("TOTAL NUMBER OF PICS : " + totalPics);
         picIndivIds = new int [totalPics];
         rankIndivs = new int [totalPics];
         numPanels = totalPics / 4;
         if (numPanels*4 < totalPics){
            numPanels++ ;
         }

         // obtaining ids of individuals that have been tagged for ranking in the selection column
         int j = 0;
         for (int i =0; i < currentPopTable.getNumRows(); i++){
            if (currentPopTable.getBoolean(i,currentPopTable.getNumColumns()-2) == true){
                picIndivIds [j] = i;
                rankIndivs [j] = 3;
                j++;
            }
         }


         //Create picture panels
         buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
         pictureCardLayout = new CardLayout();
         pictureCardPanel = new JPanel();
         //createPictures();

         // Create Button Panel
         float h,s,b;
         h = 0.219F;
         s = 0.141F;
         b = 0.823F;
         buttonPanel.setBackground(Color.getHSBColor(h,s,b));
         buttonPanel.setBorder(BorderFactory.createLineBorder(Color.white,2));
         back = new JButton("Back");
         back.setEnabled(false);
         back.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              next2.setEnabled(true);
              ((CardLayout)pictureCardPanel.getLayout()).previous(pictureCardPanel);
            //  System.out.println("Back pressed, cardSequence : "+ cardSequence);
              cardSequence--;
              if (cardSequence <= 0)
                  back.setEnabled(false);
            }
         });
         buttonPanel.add(back);

         quit = new JButton("Quit");
         quit.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
              viewCancel();
            }
         });
         buttonPanel.add(quit);


         next1 = new JButton("End Ranking Session");
         next1.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {

              ///////////////////////////////////////////////////
              // UPDATE FITNESSES HERE IN THE POPULATION DATA STRUCTURE BEFORE PUSHING POPULATION
              for(int i = 0; i < currentPop.size(); i++ ){
                IGANsgaSolution nis = (IGANsgaSolution) (currentPop.getMember(i));
                nis.setRankedIndivFlag(false);
              }
              
              for (int m = 0; m < totalPics ; m++){
                  System.out.println("Individual Id : "+ picIndivIds[m] + ", Human Rank : "+ rankIndivs[m]);
                  IGANsgaSolution nis = (IGANsgaSolution) (currentPop.getMember(picIndivIds[m]));

                  for ( int n = 0; n < currentPop.getNumObjectives(); n++) {
                      if ( qualObjs[n] == true ){
                          //nis.setObjective(n , (double) rankIndivs[m]);
                      }
                  }
                  

                  // add the human ranked IGANsgaSolution to the ARCHIVE of human ranked individuals
                  //add half the individuals only
                  //this is to test the prediction model remove once testing is over
                  //if(m < totalPics/2){
                      nis.setRankedIndivFlag(true);
                  
              if (nis instanceof MONumericIndividual) {
                      currentPop.addHumanRankedIndivToArchive ((MONumericIndividual)nis);
                  }
                  else {
                      currentPop.addHumanRankedIndivToArchive ((MOBinaryIndividual)nis);
                  }
                 // }
              }

              ///////////////////////////////////////////////////
              System.out.println("Getting out of Ranking window");
               //for(int i = 0; i < currentPop.size();i++){
               //System.out.println(i+"Ranked Flag "+((IGANsgaSolution)currentPop.getMember(i)).getRankedIndivFlag());
               //}
                pushOutput(currentPop, 0);
                viewDone("Done");
            }
        });
        buttonPanel.add(next1);

        next2 = new JButton("Continue Ranking");
        if (numPanels == 1) {
          next2.setEnabled(false);
        }
        next2.addActionListener(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                 back.setEnabled(true);
               //  System.out.println("Continue Ranking pressed, cardSequence : "+ cardSequence);
                 ((CardLayout)pictureCardPanel.getLayout()).next(pictureCardPanel);
                 cardSequence ++;
                 if (cardSequence >= numPanels-1)
                    next2.setEnabled(false);
            }
        });
        buttonPanel.add(next2);

        // add button Panel and Picture Panels to the parent
        add(buttonPanel, BorderLayout.SOUTH);
        add(pictureCardPanel, BorderLayout.CENTER);
        revalidate();
        repaint();

      }
      
      protected void createPictures( ) {

        int counter = 0;

        // instantiating number of picture panels that will hold the groups of pictures.
        picturePanel = new JPanel[numPanels];
        String [] picPanelStrings = new String [numPanels];
        pictureCardPanel.setLayout(pictureCardLayout);

        // This stores all the FalseColor visual images related to an individual
        JPanel[][] panelIn = (JPanel[][]) falColPanArr.falColPan ;
        // This stores the names of the visual images
        String [][] panelInTitle = (String[][]) falColPanArr.panelTitles ;

        JPanel [][] rankPicture = new JPanel[numPanels][4];
        JLabel [][] picture = new JLabel[numPanels][4] ;
        JScrollPane [][] pictureScrollPane = new JScrollPane[numPanels][4];
        JPanel [][] rankButtonPanel = new JPanel[numPanels][4];
        JRadioButton[][] radioButton = new JRadioButton [numPanels][4];
        ButtonGroup [][] rbg = new ButtonGroup [numPanels][4];
        // 'Details' button to see further detail information on that individual
        JButton [][] DetailButton = new JButton [numPanels][4] ;

        for (int k =0; k < numPanels; k++) {
                float h,s,b;
                h = 0.609F;
                s = 0.130F;
                b = 1.000F;

                picturePanel[k] = new JPanel();
                picPanelStrings[k] = new String("Picture" + k);
                picturePanel[k].setLayout(new GridLayout(2,2));
    // *************************************************************************
             /*   JPanel [][] rankPicture = new JPanel[numPanels][4];
                JLabel [][] picture = new JLabel[numPanels][4] ;
                JScrollPane [][] pictureScrollPane = new JScrollPane[numPanels][4];
                JPanel [][] rankButtonPanel = new JPanel[numPanels][4];
                JRadioButton[][] radioButton = new JRadioButton [numPanels][4];
                ButtonGroup [][] rbg = new ButtonGroup [numPanels][4];
                 // 'Details' button to see further detail information on that individual
                JButton [][] DetailButton = new JButton [numPanels][4] ;
              */

                for (int i =0; i<4; i++){

                    rankPicture[k][i] = new JPanel();
                    // ADD IMAGES HERE
                    // add pictures to a grid panel

                      float hc,sc,bc;
                      hc = 0.219F;
                      sc = 0.141F;
                      bc = 0.823F;
                      JPanel picgridTitlePanel = new JPanel();
                      picgridTitlePanel.setLayout(new GridLayout(4,panelIn[0].length));
                      picgridTitlePanel.setBackground(Color.getHSBColor(hc,sc,bc));
                      Dimension dp1 = new Dimension (picgridTitlePanel.getPreferredSize());
                      System.out.println ("dp1 Height : " + dp1.getHeight() + " dp1 Width : " + dp1.getWidth());
                      picgridTitlePanel.setPreferredSize(new Dimension(300,60));
                      
                     // obtaining information of Numerical Objectives for this individual
                     IGANsgaSolution picInd = (IGANsgaSolution) (currentPop.getMember(picIndivIds[counter]));
                     String [] ObjTitles = new String [currentPop.getNumObjectives()];
                     Double [] ObjValues = new Double [currentPop.getNumObjectives()];

                     String ObjecTitle1 = new String ();
                     String ObjecTitle2 = new String ();
                     for ( int n = 0; n< currentPop.getNumObjectives(); n++){
                          ObjTitles [n] = currentPop.getObjectiveConstraints()[n].getName();
                          ObjValues [n] = new Double(picInd.getObjective(n));
                          ObjTitles[n] = ObjTitles[n]+" : "+ObjValues [n].toString()+"  ";
                          //ObjecTitle1 = ObjecTitle1 + ObjTitles [n] + " : " + ObjValues [n].toString() + "   ";
                     }
                     


                     ////////////////////////////////////////////////////////////////////
                      for (int j=0; j< panelInTitle[0].length; j++){
                          picgridTitlePanel.add(new JLabel(panelInTitle[currentPopTable.getInt(picIndivIds[counter],currentPopTable.getNumColumns()-1)][j]));
                           //picgridTitlePanel.add(new JLabel(" "));
                          }
                       for (int j = 0;j<currentPop.getNumObjectives();j++){
                          picgridTitlePanel.add(new JLabel(ObjTitles[j]));
                          //picgridTitlePanel.add(new JLabel(" "));
                          }
                          picgridTitlePanel.add(new JLabel("Ranked Before : " + picInd.getRankedIndivFlag()));
                          //picgridTitlePanel.add(new JLabel("+ : Observation Points"));
                      

                      JPanel picgridPanel = new JPanel();
                      //JScrollPane picgridPanel = new JScrollPane();
                      picgridPanel.setLayout(new GridLayout(1,panelIn[0].length));
                      //picgridPanel.LEFT_ALIGNMENT;
                      /// Change made by Meghna & Abhishek to change background size ... begin
                      Dimension dp = new Dimension (panelIn[0][0].getPreferredSize());
                      for (int j=1; j< panelIn[0].length; j++){
                          Dimension dpj = ((FalseColorPanel) panelIn[0][j]).getPreferredSize();
                          dp.height = Math.max(dp.height,dpj.height);
                          dp.width = dp.width + dpj.width;
                      }
                      System.out.println ("Height : " + dp.getHeight() + " Width : " + dp.getWidth());
                      picgridPanel.setMaximumSize(dp);
                      /// change... end
                      
                      
                      for (int j=0; j< panelIn[0].length; j++){
                          FalseColorPanel fc = (FalseColorPanel)(panelIn[currentPopTable.getInt(picIndivIds[counter],currentPopTable.getNumColumns()-1)][j]);
                          if(j == 0) {
                          fc.setUserImageMax(userImgMax1);
                          fc.setUserImageMin(userImgMin1);
                          }
                          if(j == 1){
                          fc.setUserImageMax(userImgMax2);
                          fc.setUserImageMin(userImgMin2);
                          }
                              
                          picgridPanel.add(fc);
                      }
                      JPanel picAndTitleGridPanel = new JPanel();
                      picAndTitleGridPanel.setLayout(new BoxLayout(picAndTitleGridPanel, BoxLayout.Y_AXIS));
                      picgridTitlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                      picAndTitleGridPanel.add(picgridTitlePanel);
                      picgridPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                      picAndTitleGridPanel.add(picgridPanel);
                      //picAndTitleGridPanel.add(picgridTitlePanel, BorderLayout.PAGE_START);
                      //picAndTitleGridPanel.add(picgridPanel, BorderLayout.PAGE_END);

                      pictureScrollPane[k][i] = new JScrollPane(picAndTitleGridPanel);
                      //picture[k][i].setSize(new Dimension(400,200));
                      pictureScrollPane[k][i].setMinimumSize (new Dimension (200, 250));
                      pictureScrollPane[k][i].setPreferredSize (new Dimension (200, 250));
                      pictureScrollPane[k][i].setAutoscrolls(true);

                      ////////////////////////////////////////

                      rankButtonPanel[k][i] = new JPanel();
                      rankButtonPanel[k][i].setLayout(new GridLayout(1,5));
                      Integer buttonPanelID;

                      rbg[k][i] = new ButtonGroup();
                      radioButton[k][i] = new JRadioButton("1 (Best)");
                      buttonPanelID =  new Integer(counter);
                      radioButton[k][i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[k][i].add(radioButton[k][i]);
                      rbg[k][i].add (radioButton[k][i]);
                      radioButton[k][i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                            //  System.out.println("activePicId is :::::::::: "+ activePicId);
                              int picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 1;
                             // System.out.println("picId is :::::::::: "+ picId);
                             // System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      

                      //////////////////////////////////////////////////////////////

                      radioButton[k][i] = new JRadioButton("2");
                      buttonPanelID =  new Integer(counter);
                      radioButton[k][i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[k][i].add(radioButton[k][i]);
                      rbg[k][i].add (radioButton[k][i]);
                      radioButton[k][i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                             // System.out.println("activePicId is :::::::::: "+ activePicId);
                              int picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 2;
                             // System.out.println("picId is :::::::::: "+ picId);
                            //  System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });

                      //////////////////////////////////////////////////////////////

                      radioButton[k][i] = new JRadioButton("3");
                      buttonPanelID =  new Integer(counter);
                      radioButton[k][i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[k][i].add(radioButton[k][i]);
                      rbg[k][i].add (radioButton[k][i]);
                      radioButton[k][i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                              //System.out.println("activePicId is :::::::::: "+ activePicId);
                              int picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 3;
                             // System.out.println("picId is :::::::::: "+ picId);
                             // System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });
                      radioButton[k][i].setSelected(true);

                      //////////////////////////////////////////////////////////////

                      radioButton[k][i] = new JRadioButton("4");
                      buttonPanelID =  new Integer(counter);
                      radioButton[k][i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[k][i].add(radioButton[k][i]);
                      rbg[k][i].add (radioButton[k][i]);
                      radioButton[k][i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                             // System.out.println("activePicId is :::::::::: "+ activePicId);
                              int picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 4;
                             // System.out.println("picId is :::::::::: "+ picId);
                             // System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });

                      //////////////////////////////////////////////////////////////

                      radioButton[k][i] = new JRadioButton("5 (Worst)");
                      buttonPanelID =  new Integer(counter);
                      radioButton[k][i].setActionCommand(buttonPanelID.toString());
                      rankButtonPanel[k][i].add(radioButton[k][i]);
                      rbg[k][i].add (radioButton[k][i]);
                      radioButton[k][i].addActionListener(new AbstractAction() {
                            public void actionPerformed(ActionEvent e) {
                              String activePicId = new String(e.getActionCommand());
                             // System.out.println("activePicId is :::::::::: "+ activePicId);
                              int picId = Integer.parseInt(activePicId);
                              rankIndivs[picId] = 5;
                            //  System.out.println("picId is :::::::::: "+ picId);
                            //  System.out.println("rankIndivs[picId] is :::::::::: "+ rankIndivs[picId]);
                            }
                      });

                      //////////////////////////////////////////////////////////////


                      // 'Details' button to see further detail information on that individual
                      DetailButton[k][i] = new JButton(" Details >> ");
                      DetailButton[k][i].setBackground(Color.getHSBColor(h,s,b));

                      rankButtonPanel[k][i].setBorder(BorderFactory.createLineBorder(Color.white,2));

                      rankPicture[k][i].setLayout(new BoxLayout(rankPicture[k][i], BoxLayout.Y_AXIS));
                      DetailButton[k][i].setAlignmentX(Component.CENTER_ALIGNMENT);
                      rankPicture[k][i].add(DetailButton[k][i]);
                      pictureScrollPane[k][i].setAlignmentX(Component.CENTER_ALIGNMENT);
                      rankPicture[k][i].add(pictureScrollPane[k][i]);
                      rankButtonPanel[k][i].setAlignmentX(Component.CENTER_ALIGNMENT);
                      rankPicture[k][i].add(rankButtonPanel[k][i]);

                      float hcc,scc,bcc;
                      hcc = 0.168F;
                      scc = 0.208F;
                      bcc = 0.979F;
                      rankPicture[k][i].setMinimumSize (new Dimension (450, 320));
                      rankPicture[k][i].setPreferredSize (new Dimension (450, 320));
                      rankPicture[k][i].setBorder(BorderFactory.createLineBorder(Color.getHSBColor(hcc,scc,bcc),15));

                      counter ++ ;
                }
          // *************************************************************************
                h = 0.168F;
                s = 0.208F;
                b = 0.979F;
                for (int i=0; i<4; i++){
                  rankPicture[k][i].setBackground(Color.getHSBColor(h,s,b));
                  picturePanel[k].add(rankPicture[k][i]);
                }

                // adding picture panel to Jpanel with cardlayout.
                pictureCardPanel.add(picPanelStrings[k],picturePanel[k]);
               // System.out.println("picture panel added : "+ picPanelStrings[k]);

        } // k loop for numPanels
      }
      /**
         Perform any clean up to the table and call the finish() method
         on the VerticalTableViewer module.  Since all cells are
         uneditable in this implementation, we simply call the finish()
         method.  A subclass may want to juggle the contents of the table,
         however.
         */
      protected void finishUp() {
       //  pushOutput(table, 0);
         viewDone("Done");
      }

      /**
         This is the ActionListener for the ok and cancel buttons.  The
         finishUp() method is called if ok is pressed.  The viewCancel()
         method of the VerticalTableViewer module is called if cancel is
         pressed.
         @param e the ActionEvent
         */
      public void actionPerformed(ActionEvent e) {
         Object src = e.getSource();
    /*     if(src == back)
            finishUp();
         else if(src == quit)
            parent.viewCancel();
         else if(src == next)
            finishUp();
            */
      }


   } //  IGARankingView
} // IGASolutionRankingWindowFCJPanel
