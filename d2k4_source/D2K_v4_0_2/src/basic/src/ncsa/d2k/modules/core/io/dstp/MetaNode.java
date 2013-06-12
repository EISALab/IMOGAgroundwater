package ncsa.d2k.modules.core.io.dstp;

import java.io.Serializable;

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
import ncsa.d2k.core.modules.*;
import ncsa.d2k.gui.*;
import ncsa.d2k.modules.core.datatype.table.db.*;
import ncsa.d2k.modules.core.datatype.table.db.dstp.*;
import ncsa.d2k.userviews.swing.*;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class MetaNode implements Serializable {


    //==============
    // Data Members
    //==============
    private ArrayList _ucks = new ArrayList();
    private ArrayList _atts = new ArrayList();
    private ArrayList _selatts = null;

    //============
    // Properties
    //============
    private String _category = null;
    public String getCategory() {
      return _category;
    }

    public void setCategory(String cat) {
      _category = cat;
    }

    private String _servername = null;
    public String getServerName() {
      return _servername;
    }

    public void setServerName(String servername) {
      _servername = servername;
    }

    private String _serverlocation = null;
    public String getServerLocation() {
      return _serverlocation;
    }

    public void setServerLocation(String serverlocation) {
      _serverlocation = serverlocation;
    }

    private String _datafilename = null;
    public String getDatafileName() {
      return _datafilename;
    }

    public void setDatafileName(String datafilename) {
      _datafilename = datafilename;
    }

    private String _datafiledate = null;
    public String getDatafileDate() {
      return _datafiledate;
    }

    public void setDatafileDate(String datafiledate) {
      _datafiledate = datafiledate;
    }

    private String _datafiledescription = null;
    public String getDatafileDescription() {
      return _datafiledescription;
    }

    public void setDatafileDescription(String datafiledescription) {
      _datafiledescription = datafiledescription;
    }

    private String _datafilenumrecords = null;
    public String getDatafileNumRecords() {
      return _datafilenumrecords;
    }

    public void setDatafileNumRecords(String datafilenumrecords) {
      _datafilenumrecords = datafilenumrecords;
    }

    private String _datafilesource = null;
    public String getDatafileSource() {
      return _datafilesource;
    }

    public void setDatafileSource(String datafilesource) {
      _datafilesource = datafilesource;
    }

    //===============
    // Constructor(s)
    //===============
    public MetaNode() {
    }

    public MetaNode(String cat,
                    String servername,
                    String serverlocation,
                    String datafilename,
                    String datafiledate,
                    String datafiledescription,
                    String datafilenumrecords,
                    String datafilesource) {
      _category = cat;
      _servername = servername;
      _serverlocation = serverlocation;
      _datafilename = datafilename;
      _datafiledate = datafiledate;
      _datafiledescription = datafiledescription;
      _datafilenumrecords = datafilenumrecords;
      _datafilesource = datafilesource;
    }

    //================
    // Static Methods
    //================

    //================
    // Public Methods
    //================

    public void buildSubTree(DSTPTreeModel model, DefaultMutableTreeNode root) {





      DefaultMutableTreeNode datafilenode = new DefaultMutableTreeNode(new
          DSTPTreeNodeData(this, getDatafileName()));
      model.insertNodeInto(datafilenode, root, 0);

      DefaultMutableTreeNode servernamenode = new DefaultMutableTreeNode(
          "Server: " + this.getServerName(), false);
      model.insertNodeInto(servernamenode, datafilenode, 0);

      DefaultMutableTreeNode serverlocnode = new DefaultMutableTreeNode(
          "Server Location: " + this.getServerLocation(), false);
      model.insertNodeInto(serverlocnode, datafilenode, 0);

      DefaultMutableTreeNode datenode = new DefaultMutableTreeNode("Date: " +
          this.getDatafileDate(), false);
      model.insertNodeInto(datenode, datafilenode, 0);

      DefaultMutableTreeNode descnode = new DefaultMutableTreeNode(
          "Description: " + this.getDatafileDescription(), false);
      model.insertNodeInto(descnode, datafilenode, 0);

      DefaultMutableTreeNode numnode = new DefaultMutableTreeNode(
          "Number of Records: " + this.getDatafileNumRecords(), false);
      model.insertNodeInto(numnode, datafilenode, 0);

      DefaultMutableTreeNode srcnode = new DefaultMutableTreeNode("Source: " +
          this.getDatafileSource(), false);
      model.insertNodeInto(srcnode, datafilenode, 0);

      Iterator ucks = getUCK();
      String ucklbl = "UCK's ";
      while (ucks.hasNext()) {
        ucklbl += ( (uck) ucks.next()).getUCKName();
        if (ucks.hasNext()) {
          ucklbl += ", ";
        }
      }
      DefaultMutableTreeNode ucknode = new DefaultMutableTreeNode(ucklbl);
      model.insertNodeInto(ucknode, datafilenode, 0);
      ucks = getUCK();
      while (ucks.hasNext()) {
        uck ukey = (uck) ucks.next();
        DefaultMutableTreeNode ucknodedetail = new DefaultMutableTreeNode(ukey.
            getUCKName() + " " + ukey.getUCKID(), false);
        model.insertNodeInto(ucknodedetail, ucknode,
                             model.getChildCount(ucknode));
      }

      Iterator atts = this.getAttributes();
      DefaultMutableTreeNode attsnode = new DefaultMutableTreeNode("Attributes");
      model.insertNodeInto(attsnode, datafilenode, 0);
      while (atts.hasNext()) {
        attribute att = (attribute) atts.next();
        DefaultMutableTreeNode attnamenode = new DefaultMutableTreeNode(att.
            getAttName());
        model.insertNodeInto(attnamenode, attsnode,
                             model.getChildCount(attsnode));

        DefaultMutableTreeNode atttypenode = new DefaultMutableTreeNode(
            "Type: " + att.getAttType(), false);
        model.insertNodeInto(atttypenode, attnamenode,
                             model.getChildCount(attnamenode));
        DefaultMutableTreeNode attunitnode = new DefaultMutableTreeNode(
            "Unit: " + att.getAttUnit(), false);
        model.insertNodeInto(attunitnode, attnamenode,
                             model.getChildCount(attnamenode));
        DefaultMutableTreeNode attnumnode = new DefaultMutableTreeNode(
            "Number: " + att.getAttNumber(), false);
        model.insertNodeInto(attnumnode, attnamenode,
                             model.getChildCount(attnamenode));
        DefaultMutableTreeNode attnotenode = new DefaultMutableTreeNode(
            "Note: " + att.getAttNote(), false);
        model.insertNodeInto(attnotenode, attnamenode,
                             model.getChildCount(attnamenode));
      }




    }

    public void addAttribute(attribute att) {
      this._atts.add(att);
    }

    public void removeAttribute(attribute att) {
      _atts.remove(att);
    }

    public Iterator getAttributes() {
      return _atts.iterator();
    }

    public void setSelectedAttributes(ArrayList alist) {
      _selatts = alist;
    }

    public Iterator getSelectedAttributes() {
      if (_selatts != null) {
        return _selatts.iterator();
      }
      return null;
    }

    public void addUCK(uck u) {
      this._ucks.add(u);
    }

    public Iterator getUCK() {
      return _ucks.iterator();
    }

    public String toString() {
      return _servername;
    }

    public String getKey() {
      return getDatafileName() + "::" + getServerName();
    }
  }



