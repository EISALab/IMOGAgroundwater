package ncsa.d2k.modules.core.discovery.cluster.gui.properties;

/**
 * <p>Title: ClusterAssignment_Props </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */


//==============
// Java Imports
//==============

import java.util.ArrayList;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.util.Enumeration;
import java.beans.*;

//===============
// Other Imports
//===============

import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.discovery.cluster.sample.*;
import ncsa.d2k.modules.core.discovery.cluster.hac.*;

public class ClusterAssignment_Props extends JPanel 
	implements CustomModuleEditor, ActionListener, ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  private GridBagLayout m_gbl = new GridBagLayout();
  private GridBagConstraints m_gbc = new GridBagConstraints();

  private boolean _showVerbose = false;
  private boolean _showMVCheck = false;

  //components

  private JLabel m_maxLbl = null;
  private JTextField m_max = null;

  private JLabel m_cmLbl = null;
  private JLabel m_dmLbl = null;
  private javax.swing.JComboBox m_methods = null;
  private javax.swing.JComboBox m_distMetrics = null;

  private JCheckBox m_verbose = null;
  private JCheckBox m_checkMV = null;

  private ClusterAssignment _src = null;



  //================
  // Constructor(s)
  //================


  public ClusterAssignment_Props(ClusterAssignment src, boolean ver, boolean mvc){
    this._showVerbose = ver;
    this._showMVCheck = mvc;
    _src = src;
    init();
  }

  //================
  // Public Methods
  //================

  public boolean updateModule() throws Exception {

    int maxit = -1;
    try {
      maxit = Integer.parseInt(m_max.getText());
    }
    catch (Exception e) {
      throw new PropertyVetoException("Error in " + MAX_ITERATIONS + " field: " +
                                      e.getMessage(), null);
    }
    if (maxit < 1) {
      throw new PropertyVetoException(MAX_ITERATIONS + " must be > 0.", null);
    }

    if (_src != null){
      if (this._showMVCheck){
        _src.setCheckMissingValues(this.m_checkMV.isSelected());
      }
      if  (this._showVerbose){
        _src.setVerbose(m_verbose.isSelected());
      }
      _src.setNumAssignments(maxit);
      _src.setClusterMethod(this.m_methods.getSelectedIndex());
      _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
    }

    return true;
  }


  //=================
  // Private Methods
  //=================

  private void init(){
    setLayout(m_gbl);

    m_gbc.gridx = 0;
    m_gbc.gridy = 0;
    m_gbc.gridwidth = 1;
    //m_gbc.weightx = 1;
    //m_gbc.weighty = 1;
    m_gbc.fill = GridBagConstraints.VERTICAL;
    m_gbc.insets = new Insets(2,2,2,2);

    m_gbc.gridwidth = 1;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_cmLbl = new JLabel();
    m_cmLbl.setText(CLUSTER_METHOD + ": ");
    m_cmLbl.setToolTipText("Select method to use in determining similarity between two clusters."); 
    //m_cmLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_gbl.setConstraints(m_cmLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;

    m_methods = new JComboBox(HAC.s_ClusterMethodLabels);
    m_methods.setEditable(false);
    //m_methods.setFont(new Font("Arial", Font.BOLD,10));
    m_methods.setToolTipText(HAC.s_ClusterMethodDesc[_src.getClusterMethod()]);
    m_methods.setSelectedIndex(_src.getClusterMethod());
    m_methods.addActionListener(this);
    m_gbl.setConstraints(m_methods, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.insets = new Insets(4,2,2,2);
    m_gbc.anchor = GridBagConstraints.EAST;
    m_maxLbl = new JLabel();
    m_maxLbl.setText(MAX_ITERATIONS + ": ");
    //m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_maxLbl.setToolTipText("Enter integer value > 0 specifying maximum number of " +
                             "cluster assignment/refinement iterations." );
    m_gbl.setConstraints(m_maxLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_max = new JTextField(Integer.toString((_src.getNumAssignments() < 1)?5:_src.getNumAssignments()), 5);
    m_max.setFont(new Font("Arial", Font.BOLD,12));
    m_maxLbl.setToolTipText("Enter integer value > 0 specifying maximum number of " +
                             "cluster assignment/refinement iterations." );
    m_gbl.setConstraints(m_max, m_gbc);


    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_gbc.insets = new Insets(10,2,2,2);
    m_gbc.gridwidth = 1;
    m_dmLbl = new JLabel();
    m_dmLbl.setText( DISTANCE_METRIC + ": ");
    m_dmLbl.setToolTipText("Select method to use in determining distance between two examples.");       
    //m_dmLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_gbl.setConstraints(m_dmLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    this.m_distMetrics = new JComboBox(HAC.s_DistanceMetricLabels);
    m_distMetrics.setEditable(false);
    //m_distMetrics.setFont(new Font("Arial", Font.BOLD,10));
    m_distMetrics.setToolTipText(HAC.s_DistanceMetricDesc[_src.getDistanceMetric()]);
    m_distMetrics.setSelectedIndex(_src.getDistanceMetric());
    m_distMetrics.addActionListener(this);
    m_gbl.setConstraints(m_distMetrics, m_gbc);

    m_gbc.gridwidth = 2;

    m_gbc.gridx = 0;
    m_gbc.gridy++;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.insets = new Insets(4,2,2,2);
    this.m_checkMV = new JCheckBox(CHECK_MV,_src.getCheckMissingValues());
    //m_checkMV.setFont(new Font("Arial", Font.BOLD,10));
    m_checkMV.setToolTipText("Check for missing values in the table of examples.");
    m_gbl.setConstraints(m_checkMV, m_gbc);
    m_checkMV.setVisible(this._showMVCheck);

    m_gbc.gridy++;
    m_gbc.insets = new Insets(4,2,2,2);
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_verbose = new JCheckBox(VERBOSE, _src.getVerbose());
    //m_verbose.setFont(new Font("Arial", Font.BOLD,10));
    m_verbose.setToolTipText("Write verbose status information to console.");
    m_gbl.setConstraints(m_verbose, m_gbc);
    m_verbose.setVisible(_showVerbose);

    add(m_cmLbl);
    add(m_methods);
    add(m_maxLbl);
    add(m_max);
    add(m_dmLbl);
    add(m_distMetrics);
    add(m_checkMV);
    add(m_verbose);

    this.setMinimumSize(this.getPreferredSize());
    this.validateTree();

  }

  //==========================================
  // Interface Implementation: ActionListener
  //==========================================

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == this.m_methods){
      JComboBox cb = (JComboBox)src;
      m_methods.setToolTipText(HAC.s_ClusterMethodDesc[cb.getSelectedIndex()]);
    }
    if (src == this.m_distMetrics){
      JComboBox cb = (JComboBox)src;
      m_distMetrics.setToolTipText(HAC.s_DistanceMetricDesc[cb.getSelectedIndex()]);
    }
  }


}
// Start QA Comments
// 4/8/03 - Ruth starts QA;  Updates for consistenct w/ other modules.
//          added ClusterParameterDefns; Fixed one bounds check;
//          Ready for Basic
// End QA Comments

