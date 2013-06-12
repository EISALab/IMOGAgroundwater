package ncsa.d2k.modules.projects.mbabbar.optimize.ga.util;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;


import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.userviews.swing.*;
import ncsa.d2k.modules.core.optimize.ga.emo.*;
import ncsa.d2k.modules.core.optimize.ga.nsga.*;
import ncsa.d2k.modules.projects.mbabbar.optimize.ga.iga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;
import ncsa.d2k.modules.core.io.file.output.*;
import ncsa.d2k.modules.core.vis.widgets.*;



/**
 * This module adds more generations to population, if there is a need to continue with optimization..
 *
 * @author Meghna Babbar
 */
public class AddMoreGenerToPop extends ncsa.d2k.core.modules.DataPrepModule {

////////////////////////////////////////////////////////////////////////////////
// Module methods                                                             //
////////////////////////////////////////////////////////////////////////////////


   public String getInputInfo(int i) {
      if (i == 0)
         return "The <i>Population</i>.";
      return "No such input";
   }

   public String getInputName(int i) {
      if (i == 0)
         return "Population";
      return "No such input";
   }

   public String[] getInputTypes() {
      return new String[] {
         "ncsa.d2k.modules.core.optimize.ga.Population"
      };
   }

   public String getModuleInfo() {
      StringBuffer sb = new StringBuffer("<p>Overview: ");
      sb.append("This module adds more generations to the IGA NsgaPopulation, so that it can continue");
      return sb.toString();
   }

   public String getModuleName() {
      return "AddMoreGenerToPop";
   }

   public String getOutputInfo(int i) {
      if (i == 0)
         return "The <i>Population</i>.";
      return "No such output";
   }

   public String getOutputName(int i) {
      if (i == 0)
         return "Population";
      return "No such output";
   }

   public String[] getOutputTypes() {
      return new String[] { "ncsa.d2k.modules.core.optimize.ga.Population"};
    // return null;
   }

   //////////////////////
   // get set methods
   //////////////////////
   int generationIncrement;

   public void setGenerationIncrement (int increment){
      generationIncrement = increment;
   }
   public int getGenerationIncrement (){
      return generationIncrement;
   }


   /////////////////////
   //work methods
   ////////////////////

   Population pop;

   /**
      Evaluate the individuals in this class.
   */
   public void doit () throws Exception {

            pop =  (Population) this.pullInput(0);

            pop.setMaxGenerations(pop.getCurrentGeneration()+ generationIncrement);


            this.pushOutput (pop, 0);
    }


}//AddMoreGenerToPop
