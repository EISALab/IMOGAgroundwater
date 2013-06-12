package ncsa.d2k.modules.core.io.dstp;

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



     public class attribute
        implements java.io.Serializable {
      attribute() {}

      attribute(Element att) {
        _attname = att.getAttributeValue("NAME");
        _attnumber = att.getAttributeValue("NUMBER");
        _atttype = att.getAttributeValue("DATA-TYPE");
        _attunit = att.getAttributeValue("UNIT");
        _attnote = att.getAttributeValue("NOTE");
        _attuckname = att.getAttributeValue("UCKNAME");
        _attuckid = att.getAttributeValue("UCKID");
      }

      private String _attname = null;
      public String getAttName() {
        return _attname;
      }

      public void setAttName(String name) {
        _attname = name;
      }

      private String _attnumber = null;
      public String getAttNumber() {
        return _attnumber;
      }

      public void setAttNumber(String number) {
        _attnumber = number;
      }

      private String _atttype = null;
      public String getAttType() {
        return _atttype;
      }

      public void setAttType(String type) {
        _atttype = type;
      }

      private String _attunit = null;
      public String getAttUnit() {
        return _attunit;
      }

      public void setAttUnit(String unit) {
        _attunit = unit;
      }

      private String _attnote = null;
      public String getAttNote() {
        return _attnote;
      }

      public void setAttNote(String note) {
        _attnote = note;
      }

      private String _attuckname = null;
      public String getAttUCKName() {
        return _attuckname;
      }

      public void setAttUCKName(String uckname) {
        _attuckname = uckname;
      }

      private String _attuckid = null;
      public String getAttUCKID() {
        return _attuckid;
      }

      public void setAttUCKID(String uckid) {
        _attuckid = uckid;
      }
    }
