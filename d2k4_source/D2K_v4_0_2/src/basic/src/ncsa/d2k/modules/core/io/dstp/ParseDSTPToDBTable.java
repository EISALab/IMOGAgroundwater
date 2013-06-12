package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;

//==============
// Java Imports
//==============
import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;

//JDOM
import org.jdom.*;
import org.jdom.input.*;
//DSTP Client
import backend.*;
//import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.db.*;
import ncsa.d2k.modules.core.datatype.table.db.dstp.*;
import ncsa.d2k.userviews.swing.*;


import ncsa.d2k.modules.core.transform.StaticMethods;

/**
 *
 * <p>Title: ParseDSTPToDBTable</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class ParseDSTPToDBTable
    extends HeadlessUIModule {
  //==============
  // Data Members
  //==============
  private DSTPView m_view = null;

  //============
  // Properties
  //============

  public String m_servername = "sol.ncsa.uiuc.edu";
  public String getServerName() {
    return m_servername;
  }

  public void setServerName(String name) {
    m_servername = name;
  }

  //================
  // Constructor(s)
  //================
  public ParseDSTPToDBTable() {
  }

  //================
  // Static Methods
  //================
  public static void main(String[] args) {
    ParseDSTPToDBTable DSTPSelect1 = new ParseDSTPToDBTable();
  }

  //================
  // Public Methods
  //================

  //========================
  // D2K Abstract Overrides

  /**
     Return the name of this module.
     @return The name of this module.
   */
  public String getModuleName() {
    return "Select DSTP Dataset";
  }

  /**
   * Return array of property descriptors for this module.
   * @return array
   */
  public PropertyDescription[] getPropertiesDescriptions() {
    PropertyDescription[] descriptions = new PropertyDescription[4];
    descriptions[0] = super.supressDescription;
    descriptions[1] = new PropertyDescription("serverName",
                                              "DSTP Server DNS or IP",
        "This is the address of the DSTP server.");
    descriptions[2] = new PropertyDescription("category", "Category", "Category of the datafile to be loaded (this property is used when 'Supress User Interface Display' is set to true)");
    descriptions[3] = new PropertyDescription("datafileName", "Datafile Name", "The data file to be loaded (this property is used when 'Supress User Interface Display' is set to true)");
    //descriptions[4] = new PropertyDescription("datafileSource", "Datafile Source", "The data file to be loaded");

    return descriptions;
  }


  /**
     Code to execute before doit.
   */
  public void beginExecution() {
    if (m_view != null) {
      m_view.reset(this.getServerName());
    }
  }

  /**
     Code to execute at end of itinerary run.
   */
  public void endExecution() {
    super.endExecution();
  }

  public void quitModule() {
    executionManager.moduleDone(this);
  }

  /**
     Return the description of a specific output.
     @param i The index of the output.
     @return The description of the output.
   */
  public String getOutputInfo(int parm1) {
    if (parm1 == 0) {
      return
          "DBTable containing the data that was selected form the DSTP server.";
    } else {
      return "";
    }
  }

  /**
     Return the name of a specific output.
     @param i The index of the output.
     @return The name of the output
   */
  public String getOutputName(int parm1) {
    if (parm1 == 0) {
      return "DBTable";
    } else {
      return "";
    }
  }

  /**
     Return a String array containing the datatypes of the outputs of this
     module.
     @return The datatypes of the outputs.
   */
  public String[] getOutputTypes() {
    String[] out = {
        "ncsa.d2k.modules.core.datatype.table.Table"};
    return out;
  }

  /**
    Return information about the module.
    @return A detailed description of the module.
   */
  public String getModuleInfo() {
    String s = "<p>Overview: ";
    s += "This module provides a GUI that is a metadata viewer for a DSTP ";
    s += "server.  The user can select a data source to be loaded into a DBTable ";
    s += "for use in D2K itineraries.";
    s += "</p>";

    s += "<p>";
    s += "See: http://www.dataspaceweb.net/index.htm.  DataSpaces is a research ";
    s += "product of The National Center for Data Mining (NCDM) at the University ";
    s += "of Illinois at Chicago (UIC).";
    s += "</p>";

    s += "<p>Detailed Description: ";
    s += "This GUI provides a tree view of metadata for DSTP data sources.  Any ";
    s += "of these sources can be selected and loaded into a DBTable for use in ";
    s += "a D2K itinerary.  To select a source simply double click on the tree ";
    s += "node that bears the source name.  The attributes for the source will ";
    s += "be displayed in the ";
    s += "window on the right.  Initially, all attributes will appear selected. ";
    s += "The user can select and deselect attributes as desired.  When the ";
    s += "<i>Done</i> button is pressed the DBTable is built and output and ";
    s += "the GUI will be dismissed.  If the server on which the data source ";
    s += "resides (not necessarily the same as the server initially contacted) ";
    s += "is not reachable, then an error message is displayed and the GUI remains. ";
    s += "The <i>Reset</i> button causes the metadata to be rebuilt.  The ";
    s += "<i>Abort</i> button aborts the itinerary.  This version of the GUI ";
    s += "does not support sampling.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The DBTable that is created uses a primitive implementation of a DBDataSource ";
    s += "that loads the entire dataset into memory.  The DBTable is serializable ";
    s += "but the data is transient.  The data is reacquired from the DSTP server ";
    s += "when the object is deserialized. The DBTable is not mutable.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The DBTable created currently has the same memory limitations as an in ";
    s += "memory table.  This will eventually be corrected with an appropriate ";
    s += "caching scheme.";
    s += "</p>";
    return s;
  }

  /**
     Return a String array containing the datatypes the inputs to this
     module.
     @return The datatypes of the inputs.
   */
  public String[] getInputTypes() {
    String[] in = null;
    return in;
  }

  /**
     Return a description of a specific input.
     @param i The index of the input
     @return The description of the input
   */
  public String getInputInfo(int parm1) {
    if (parm1 == 0) {
      return "";
    } else {
      return "";
    }
  }

  public void abort(Exception ex) {
    ex.printStackTrace();
    System.out.println(ex.getMessage());
    System.out.println("Aborting: ParseDSTPToDBTable");
    viewAbort();
  }

  public void abort() {
    System.out.println("Aborting: ParseDSTPToDBTable");
    viewAbort();
  }

  public void push(Object o, int i) {
    pushOutput(o, i);
  }

  //===================
  // Protected Methods
  //===================
  public UserView createUserView() {
    m_view = new DSTPView(this);
    return m_view;
  }

  protected String[] getFieldNameMapping() {
    String[] out = null;
    return out;
  }

//headless conversion support



//[when gui is supressed]
  private String category;       //category of chosen node

  private String datafileName;   //datafile name
  private String[] attNames;     //selected attribute names in datafileName
  private String[] attTypes;     //selected attribute types in datafileName

  //setter and getter methods
  public String getCategory(){return category;}

  public String getDatafileName(){return datafileName;}

  public void setCategory(String cat){category = cat;}

  public void setDatafileName(String name){datafileName = name;}

  public Object[] getAttNames(){return attNames;}
  public Object[] getAttTypes(){return attTypes;}

    public void setAttNames(Object[] names){
    attNames  = new String[names.length];
    for(int i=0; i<names.length; i++)
      attNames[i] = (String)names[i];
  }
    public void setAttTypes(Object[] types){
      attTypes  = new String[types.length];
       for(int i=0; i<types.length; i++)
         attTypes[i] = (String)types[i];
  }

//end setter and getter.



  public void doit() throws Exception{

    //validating that properties were set:
    if(m_servername == null || attNames == null || attTypes == null || category == null || datafileName == null)
      throw new Exception (this.getAlias()+" has not been configured. Before running headless, run with the gui and configure the parameters.");


  //creating a tree panel to hold the data in the dataspace.
  DSTPTreePanel tree = new DSTPTreePanel(m_servername);
  buildTree(tree);

//retrieving the root
  DefaultMutableTreeNode root = tree.getRoot();

//retrieving the root's children - categories.
  Enumeration e_category = root.children();


//if selected category is an element in the enumeration - categoryNode will point to its node.
  DefaultMutableTreeNode categoryNode = null;
  //going over the categories and looking for the selected category.
  while(e_category.hasMoreElements()){
    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e_category.nextElement();
    String currCat = (String)node.getUserObject();
    //categoryMap.put(currCat, new Integer(counter));
    //counter++;
    if(currCat.equalsIgnoreCase(category))
      categoryNode = node;
  }//while e_category

//validating that the chosen category was found in the dataspace

  if(categoryNode == null)
    throw new Exception (this.getAlias()+": The configured category " + category + " does not exist in the chosen server " + m_servername + " dataspace." +
                         " Please re configure the module (using the properties editor or via a GUI run) " +
                         "so it can run Headless.");

//everything is ok...
  //retrieving the category's children - datafiles.
  Enumeration e_datafile = categoryNode.children();

  //if the selected datafile name is in the enumeration fileNode will point to its meta node.
 MetaNode fileNode = null;
 //going over the tree node (datafiles) looking for the selected data file name.
   while(e_datafile.hasMoreElements()){
     DefaultMutableTreeNode node = (DefaultMutableTreeNode) e_datafile.nextElement();
     DSTPTreeNodeData currFile = (DSTPTreeNodeData) node.getUserObject();
     MetaNode currFileNode = currFile.getNode();
     if(currFileNode.getDatafileName().equalsIgnoreCase(datafileName))
       fileNode = currFileNode;

   }//while fileMap

//validating that the chosen datafile is in the chosen category

  if(fileNode == null)
    throw new Exception (this.getAlias()+ ": The configured datafile name " + datafileName + " does not exist in the chosen category " + category +
                         ". Please reconfigure the module (using the properties editor or via a GUI run) " +
                         "so it can run Headless.");

//everything is fine...

//getting the attributes of this file, building lookup hash maps
  Iterator attIt = fileNode.getAttributes();
  int counter = 0;
  //maps name <-> id
   HashMap attsNameMap = new HashMap();
   //maps id <-> attribute object
   HashMap attsIdMap = new HashMap();

  while(attIt.hasNext()){
    attribute currAtt = (attribute) attIt.next();
    attsNameMap.put(currAtt.getAttName().toUpperCase(), new Integer(counter));
    attsIdMap.put( new Integer(counter), currAtt);
    counter++;
  }//while fileMap

  //building a map name <-> id of the selected attribute names (by the user).
  HashMap selectedAtts = new HashMap();
  for(int i=0; i<attNames.length; i++)
    selectedAtts.put(attNames[i].toUpperCase(), new Integer(i));

  //verifying that the chosen attributes match the maps

  //getting the target attribute names. only attribute names that were selected
  //and also in attNamesMap will be in targetAttNames.
  String[] targetAttNames = StaticMethods.getIntersection(attNames, attsNameMap);

  if(targetAttNames.length < attNames.length)
    throw new Exception(this.getAlias()+ ": Some of the configured attributes were not found "
                       + " in the datafile " + datafileName +
                       ". Please re configure the module " +
                         "so it can run Headless.");

//verifying that each type matches.

  //validAtt will hold attribute objects with names from targetAttNames, that their selected
  //types (by the user) match their types in the dataspace.
  ArrayList validAtt = new ArrayList();

//so - for each targetAttNames[i]
  for (int i=0; i<targetAttNames.length; i++){
    //finding its id in the dataspace map
    Integer id = (Integer ) attsNameMap.get(targetAttNames[i].toUpperCase());
    //finding its attribute object in the id map (again matching the dataspace.
    attribute currAtt = (attribute) attsIdMap.get(id);
    //getting its type (according to dataspace)
    String currType = currAtt.getAttType();
    //finding the type that was selected by the user prior to this run.
    String selectedType = attTypes[((Integer)selectedAtts.get(targetAttNames[i].toUpperCase())).intValue()];

    //validating matching between the types.
    if(! currType.equals(selectedType))
      throw new Exception(this.getAlias()+ ": The configured type for attribute name " + targetAttNames[i] +
    " does not match the type is the data space." +
    " Please reconfigure this module via a GUI run, so it can run Headless");

      //if everything is ok, adding to the array list.
    validAtt.add(i, currAtt);

  }//for target att names


  //setting the meta node.
  fileNode.setSelectedAttributes(validAtt);
  //creating the data source that will hold all of the data of this datafile
  //(pushOutput is called by run method of DSTPDataSource...
  DSTPDataSource dsource = new DSTPDataSource(fileNode, this);
  dsource.join();

  }//doit

  //methods copied from DSTPView.

  private void buildTree(DSTPTreePanel tree){
    try {
         Hashtable _cats = new Hashtable();
         //_treepan.getTree().removeAll();
         DSTPConnection conn = new DSTPConnection(m_servername);
         parseMetaData(conn, _cats);
         DSTPTreeModel model = tree.getModel();
         DefaultMutableTreeNode root = tree.getRoot();

         model.setRoot(root);
         //rebuild the tree
         Enumeration cats = _cats.keys();
         while (cats.hasMoreElements()) {
           Element cat = (Element) cats.nextElement();
           DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(cat.
               getAttributeValue("NAME"));

           model.insertNodeInto(newnode, root, 0);
           Iterator mnodes = ( (ArrayList) _cats.get(cat)).iterator();
           while (mnodes.hasNext()) {
             MetaNode mnode = (MetaNode) mnodes.next();
             mnode.buildSubTree(model, newnode);
           }
         }
       }
       catch (Exception e) {
         /*JOptionPane.showMessageDialog(this, e.getMessage());
         System.out.println("EXCEPTION: " + e.getMessage());*/
         e.printStackTrace();
         return;
       }



  }//buildTree


  private void parseMetaData(DSTPConnection conn, Hashtable _cats)
 throws Exception {

    Vector data = null;
    data = conn.getServerDataVector("METADATA EXPAND", 0);
    String concat = "<DSML>";
    for (int j = 0, k = data.size(); j < k; j++) {
      String datum = (String) data.get(j);
      if (j > 0) {
        concat += datum;
      }
    }
    concat += "</DSML>";

    SAXBuilder sb = new SAXBuilder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    dos.writeBytes(concat);
    dos.flush();
    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    Document doc = sb.build(bais);

    System.out.println("Metadata parsed and document built ...");

    Iterator cats = doc.getRootElement().getChildren("CATEGORY").iterator();

    while (cats.hasNext()) {
      Element cat = (Element) cats.next();
      ArrayList nodes = new ArrayList();
      _cats.put(cat, nodes);
      buildMetaNodes(nodes, cat);
    }//while





}//parseMetaData


  private void buildMetaNodes(ArrayList nodes, Element cat) {





 Hashtable ht = new Hashtable();

 Iterator ucks = cat.getChildren("UCK").iterator();
 while (ucks.hasNext()) {
   Element uck = (Element) ucks.next();
   String uckname = uck.getAttributeValue("NAME");
   String uckid = uck.getAttributeValue("ID");
   Iterator servers = uck.getChildren("SERVER").iterator();
   while (servers.hasNext()) {
     Element server = (Element) servers.next();
     String servername = server.getAttributeValue("NAME");
     String serverlocation = server.getAttributeValue("Location");
     Iterator datafiles = server.getChildren("DATAFILE").iterator();
     while (datafiles.hasNext()) {
       Element datafile = (Element) datafiles.next();
       String datafilename = datafile.getAttributeValue("NAME");
       String datafiledate = datafile.getAttributeValue("DATE");
       String datafiledescription = datafile.getAttributeValue("DESCRIPTION");
       String datafilenumrecords = datafile.getAttributeValue("NUMRECORDS");
       String datafilesource = datafile.getAttributeValue("SOURCE");
       //type
       //delimiter
       //dsfilename
       //address
       String key = datafilename + "::" + servername;
    MetaNode node = (MetaNode) ht.get(key);
       if (node == null) {
         //put a new metanode
         node = new MetaNode(cat.getAttributeValue("NAME"),
                             servername,
                             serverlocation,
                             datafilename,
                             datafiledate,
                             datafiledescription,
                             datafilenumrecords,
                             datafilesource);
         node.addUCK(new uck(uckname, uckid));
         ht.put(key, node);
         nodes.add(node);
         Iterator atts = datafile.getChildren("ATTRIBUTE-DESCRIPTOR").
             iterator();
         while (atts.hasNext()) {
           Element att = (Element) atts.next();
           //add att to metanode
           node.addAttribute(new attribute(att));
         }
       } else {
         //update existing metanode with a new uck
         node.addUCK(new uck(uckname, uckid));
       }
     }
   }
 }


}//buildMetaNode



  //headless conversion support



}

/**
 * basic 4 qa comments
 *
 * 01-23-04:
 * vered started qa process.
 * fixed typo in module info.
 * updated properties descriptions.
 * module is ready for basic 4.
 */
