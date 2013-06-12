package ncsa.d2k.modules.core.discovery.cluster.gui.properties;

/**
 * <p>Title: FractionationSampler_Props </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */


//==============
// Java Imports
//==============

import java.beans.*;

import java.awt.*;
import javax.swing.*;

//===============
// Other Imports
//===============
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.core.discovery.cluster.sample.*;

public class FractionationSampler_Props extends JPanel implements CustomModuleEditor {

  //==============
  // Data Members
  //==============

  private GridBagLayout m_gbl = new GridBagLayout();
  private GridBagConstraints m_gbc = new GridBagConstraints();

  private boolean _showVerbose = false;
  private boolean _showMVCheck = false;

  //components

  private JLabel m_numClustLbl = null;
  private JTextField m_numClust = null;

  private JLabel m_maxPartLbl = null;
  private JTextField m_maxPart = null;

  private JLabel m_nthSortLbl = null;
  private JTextField m_nthSort = null;


  private JCheckBox m_verbose = null;
  private JCheckBox m_checkMV = null;


  private FractionationSampler _src = null;



  //================
  // Constructor(s)
  //================


  public FractionationSampler_Props(FractionationSampler src, boolean ver, boolean mvc){
    this._showVerbose = ver;
    this._showMVCheck = mvc;
    _src = src;
    init();
  }

  //================
  // Public Methods
  //================

  public boolean updateModule() throws Exception {

    int num = -1;
    try {
      num = Integer.parseInt(m_numClust.getText());
    }
    catch (Exception e) {
      throw new PropertyVetoException("Error in number of clusters field: " +
                                      e.getMessage(), null);
    }
    if (num < 2) {
      throw new PropertyVetoException("Number of clusters must be two or more.", null);
    }

    int max = -1;
    try {
      max = Integer.parseInt(m_maxPart.getText());
    }
    catch (Exception e) {
      throw new PropertyVetoException("Error in max partition size field: " +
                                      e.getMessage(), null);
    }
    if (max < 1) {
      throw new PropertyVetoException("Max partition size must be > 0.", null);
    }

    int nth = -1;
    try {
      nth = Integer.parseInt(m_nthSort.getText());
    }
    catch (Exception e) {
      throw new PropertyVetoException("Error in nth sort field: " +
                                      e.getMessage(), null);
    }
    if (nth < 0) {
      throw new PropertyVetoException("Nth sort index must be >= 0.", null);
    }

    if (_src != null){
      if (this._showMVCheck){
        _src.setCheckMissingValues(this.m_checkMV.isSelected());
      }
      _src.setNumberOfClusters(num);
      _src.setMaxPartitionSize(max);
      _src.setNthSortTerm(nth);
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

    m_gbc.anchor = GridBagConstraints.EAST;
    m_numClustLbl = new JLabel();
    m_numClustLbl.setText("Number of clusters: ");
    //m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_numClustLbl.setToolTipText("Enter integer value > 2 specifying number of clusters desired.");
    m_gbl.setConstraints(m_numClustLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_numClust = new JTextField(Integer.toString((_src.getNumberOfClusters() < 2)?5:_src.getNumberOfClusters()), 5);
    m_numClust.setFont(new Font("Arial", Font.BOLD,12));
    m_numClust.setToolTipText("Enter integer value > 2 specifying number of clusters desired.");
    m_gbl.setConstraints(m_numClust, m_gbc);



    m_gbc.gridx = 0;
    m_gbc.gridy++;
    m_gbc.gridwidth = 1;
    m_gbc.fill = GridBagConstraints.VERTICAL;
    m_gbc.insets = new Insets(2,2,2,2);

    m_gbc.anchor = GridBagConstraints.EAST;
    m_maxPartLbl = new JLabel();
    m_maxPartLbl.setText("Max partition size: ");
    m_maxPartLbl.setToolTipText("Enter integer value > 0 specifying max partition size.");
    m_gbl.setConstraints(m_maxPartLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_maxPart = new JTextField(Integer.toString((_src.getmaxPartitionSize() < 1)?250:_src.getmaxPartitionSize()), 7);
    m_maxPart.setFont(new Font("Arial", Font.BOLD,12));
    m_maxPart.setToolTipText("Enter integer value > 0 specifying max partition size.");
    m_gbl.setConstraints(m_maxPart, m_gbc);



    m_gbc.gridx = 0;
    m_gbc.gridy++;
    m_gbc.gridwidth = 1;
    m_gbc.fill = GridBagConstraints.VERTICAL;
    m_gbc.insets = new Insets(2,2,2,2);

    m_gbc.anchor = GridBagConstraints.EAST;
    m_nthSortLbl = new JLabel();
    m_nthSortLbl.setText("Sort attribute: ");
    m_nthSortLbl.setToolTipText("Enter integer value >= 0 specifying the index of attribute to sort on.");
    m_gbl.setConstraints(m_nthSortLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_nthSort = new JTextField(Integer.toString((_src.getNthSortTerm() < 0)?0:_src.getNthSortTerm()), 5);
    m_nthSort.setFont(new Font("Arial", Font.BOLD,12));
    m_nthSort.setToolTipText("Enter integer value >= 0 specifying the index of attribute to sort on.");
    m_gbl.setConstraints(m_nthSort, m_gbc);

    m_gbc.gridwidth = 2;

    m_gbc.gridx = 0;
    m_gbc.gridy++;
    m_gbc.insets = new Insets(4,2,2,2);
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_verbose = new JCheckBox("Verbose", _src.getVerbose());
    m_verbose.setToolTipText("Print verbose messages to console.");
    m_gbl.setConstraints(m_verbose, m_gbc);
    m_verbose.setVisible(_showVerbose);

    m_gbc.gridy++;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.insets = new Insets(4,2,2,2);
    this.m_checkMV = new JCheckBox("Check Missing for Values",_src.getCheckMissingValues());
    m_checkMV.setToolTipText("Screen for missing values in the input table.");
    m_gbl.setConstraints(m_checkMV, m_gbc);
    m_checkMV.setVisible(this._showMVCheck);

    add(m_numClustLbl);
    add(m_numClust);
    add(m_maxPartLbl);
    add(m_maxPart);
    add(m_nthSortLbl);
    add(m_nthSort);
    add(m_verbose);
    add(m_checkMV);

    this.setMinimumSize(this.getPreferredSize());
    this.validateTree();

  }


}
