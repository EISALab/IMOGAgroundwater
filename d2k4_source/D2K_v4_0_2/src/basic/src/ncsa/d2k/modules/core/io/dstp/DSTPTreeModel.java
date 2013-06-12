package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import  java.awt.event.*;
import  java.awt.*;
import  javax.swing.*;
import  javax.swing.tree.*;


/**
 *
 * <p>Title: DSTPTreeModel</p>
 * <p>Description: This is a support class for ParseDSTPToDBTable</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class DSTPTreeModel extends DefaultTreeModel {
    //==============
    // Data Members
    //==============

    //================
    // Constructor(s)
    //================
    public DSTPTreeModel (DefaultMutableTreeNode root) {
        super(root);
    }
}



