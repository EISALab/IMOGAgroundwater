package ncsa.d2k.modules.core.discovery.cluster.gui.properties;

/**
 * <p>Title: KMeansParams_Props </p>
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
import ncsa.d2k.modules.core.discovery.cluster.hac.*;
import ncsa.d2k.modules.core.discovery.cluster.sample.*;

public class KMeansParams_Props extends JPanel
    implements CustomModuleEditor, ActionListener, ClusterParameterDefns {

  //==============
  // Data Members
  //==============

  private GridBagLayout m_gbl = new GridBagLayout();
  private GridBagConstraints m_gbc = new GridBagConstraints();

  //components


  private JLabel m_numClustLbl = null;
  private JTextField m_numClust = null;

  private JLabel m_seedLbl = null;
  private JTextField m_seed = null;

  private JLabel m_cmLbl = null;
  private JLabel m_dmLbl = null;
  private javax.swing.JComboBox m_methods = null;
  private javax.swing.JComboBox m_distMetrics = null;

  private JCheckBox m_useFirst = null;

  private JLabel m_maxLbl = null;
  private JTextField m_max = null;


  private KMeansParams _src = null;



  //================
  // Constructor(s)
  //================


  public KMeansParams_Props(KMeansParams src){
    _src = src;
    init();
  }

  //================
  // Public Methods
  //================

  public boolean updateModule() throws Exception {

    int num = -1;
    if (this.m_numClust.isEnabled()){
      try{
        num = Integer.parseInt(m_numClust.getText());
      } catch (Exception e){
        throw new PropertyVetoException("Error in " + NUM_CLUSTERS + " field: " + e.getMessage(), null);
      }
      if (num < 2){
        throw new PropertyVetoException(NUM_CLUSTERS + " must be > 1.", null);
      }
    }

    int seed = -1;
    try {
      seed = Integer.parseInt(m_seed.getText());
    }
    catch (Exception e) {
      throw new PropertyVetoException("Error in " + SEED + " field: " + e.getMessage(), null);
    }
    if (seed < 0) {
      throw new PropertyVetoException(SEED +" must be >= 0.", null);
    }

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
      _src.setUseFirst(m_useFirst.isSelected());
      _src.setNumClusters(num);
      _src.setSeed(seed);
      _src.setMaxIterations(maxit);
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
    m_gbc.fill = GridBagConstraints.VERTICAL;
    m_gbc.insets = new Insets(2,2,2,2);

    m_gbc.gridwidth = 1;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_cmLbl = new JLabel();
    m_cmLbl.setText(CLUSTER_METHOD + ": ");
    m_cmLbl.setToolTipText("Select method to use in determining similarity between two clusters.");
    m_gbl.setConstraints(m_cmLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;

    m_methods = new JComboBox(HAC.s_ClusterMethodLabels);
    m_methods.setEditable(false);
    m_methods.setToolTipText(HAC.s_ClusterMethodDesc[_src.getClusterMethod()]);
    m_methods.setSelectedIndex(_src.getClusterMethod());
    m_methods.addActionListener(this);
    m_gbl.setConstraints(m_methods, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.insets = new Insets(2,2,2,2);
    m_gbc.anchor = GridBagConstraints.EAST;
    m_seedLbl = new JLabel();
    m_seedLbl.setText(SEED + ": ");
    m_seedLbl.setToolTipText("Enter integer value >= 0 specifying random seed.");
    m_gbl.setConstraints(m_seedLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_seed = new JTextField(Integer.toString((_src.getSeed() < 0)?0:_src.getSeed()), 5);
    m_seed.setFont(new Font("Arial", Font.BOLD,12));
    m_seed.setToolTipText("Enter integer value >= 0 specifying random seed.");
    m_gbl.setConstraints(m_seed, m_gbc);

    m_gbc.gridwidth = 2;

    m_gbc.gridx = 0;
    m_gbc.gridy++;
    m_gbc.insets = new Insets(2,2,2,2);
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_useFirst = new JCheckBox(USE_FIRST, _src.getUseFirst());
    m_useFirst.setToolTipText("If checked, use first rows in table as sample set.");
    m_gbl.setConstraints(m_useFirst, m_gbc);


    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.gridwidth = 1;
    m_gbc.insets = new Insets(2,2,2,2);
    m_gbc.anchor = GridBagConstraints.EAST;
    m_numClustLbl = new JLabel();
    m_numClustLbl.setText( NUM_CLUSTERS + ": " );
    m_numClustLbl.setToolTipText("Enter integer value > 1 specifying number of clusters desired.");
    m_gbl.setConstraints(m_numClustLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_numClust = new JTextField(Integer.toString((_src.getNumClusters() < 2)?5:_src.getNumClusters()), 5);
    m_numClust.setFont(new Font("Arial", Font.BOLD,12));
    m_numClust.setToolTipText("Enter integer value > 1 specifying number of clusters desired.");
    m_gbl.setConstraints(m_numClust, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_gbc.insets = new Insets(2,2,2,2);
    m_gbc.gridwidth = 1;
    m_dmLbl = new JLabel();
    m_dmLbl.setText( DISTANCE_METRIC + ": ");
    m_dmLbl.setToolTipText("Select method to use in determining distance between two examples.");
    m_gbl.setConstraints(m_dmLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    this.m_distMetrics = new JComboBox(HAC.s_DistanceMetricLabels);
    m_distMetrics.setEditable(false);
    m_distMetrics.setToolTipText(HAC.s_DistanceMetricDesc[_src.getDistanceMetric()]);
    m_distMetrics.setSelectedIndex(_src.getDistanceMetric());
    m_distMetrics.addActionListener(this);
    m_gbl.setConstraints(m_distMetrics, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.insets = new Insets(2,2,2,2);
    m_gbc.anchor = GridBagConstraints.EAST;
    m_maxLbl = new JLabel();
    m_maxLbl.setText( MAX_ITERATIONS + ": ");
    //m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_maxLbl.setToolTipText("Enter integer value > 0 specifying maximum number of " +
                             "cluster assignment/refinement iterations." );
    m_gbl.setConstraints(m_maxLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_max = new JTextField(Integer.toString((_src.getMaxIterations() < 1)?5:_src.getMaxIterations()), 5);
    m_max.setFont(new Font("Arial", Font.BOLD,12));
    m_max.setToolTipText("Enter integer value > 0 specifying maximum number of " +
                         "cluster assignment/refinement iterations." );
    m_gbl.setConstraints(m_max, m_gbc);

    add(m_cmLbl);
    add(m_methods);
    add(m_numClustLbl);
    add(m_numClust);
    add(m_dmLbl);
    add(m_distMetrics);
    add(m_useFirst);
    add(m_seed);
    add(m_seedLbl);
    add(m_maxLbl);
    add(m_max);


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
// 4/6/03 - Ruth starts QA
//          Made minor changes for consistency - some tests/msgs didn't agree.
// 4/7/03 - Used KMeansParameterDefns so dialog & property descriptions are consistent.
//        - Ready for Basic
// 4/8/03 - Changed to use ClusterParameterDefns
// End QA Comments
