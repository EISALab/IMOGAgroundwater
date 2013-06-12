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

public class uck
     implements java.io.Serializable {
   uck() {}

   uck(String name, String id) {
     _uckname = name;
     _uckid = id;
   }

   private String _uckname = null;
   public String getUCKName() {
     return _uckname;
   }

   public void setUCKName(String name) {
     _uckname = name;
   }

   private String _uckid = null;
   public String getUCKID() {
     return _uckid;
   }

   public void setUCKID(String id) {
     _uckid = id;
   }
 }

