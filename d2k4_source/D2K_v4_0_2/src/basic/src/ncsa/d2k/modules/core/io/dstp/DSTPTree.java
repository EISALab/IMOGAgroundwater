package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import  java.awt.event.*;
import  javax.swing.*;
import  javax.swing.tree.*;


/**
 *
 * <p>Title: DSTPTree</p>
 * <p>Description: This is a support class for ParseDSTPToDBTable</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class DSTPTree extends JTree {
    //==============
    // Data Members
    //==============
    DSTPTreeModel m_ttm = null;

    //================
    // Constructor(s)
    //================
    public DSTPTree (DSTPTreeModel ttm) {
        super(ttm);
        m_ttm = ttm;
    }
    //================
    // Public Methods
    //================
}



