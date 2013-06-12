package ncsa.d2k.modules.core.discovery.cluster.gui.properties;

/**
 * <p>Title: CoverageSampler_Props </p>
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

public class CoverageSampler_Props extends JPanel implements CustomModuleEditor, ChangeListener, ActionListener {

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

  private JLabel m_distLbl = null;
  private JSlider m_distSlide = null;

  private JLabel m_dmLbl = null;
  private javax.swing.JComboBox m_distMetrics = null;

  private JCheckBox m_verbose = null;
  private JCheckBox m_checkMV = null;

  private CoverageSampler _src = null;



  //================
  // Constructor(s)
  //================


  public CoverageSampler_Props(CoverageSampler src, boolean ver, boolean mvc){
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
      throw new PropertyVetoException("Error in max number of samples field: " +
                                      e.getMessage(), null);
    }
    if (num < 1) {
      throw new PropertyVetoException("Max number of samples must be > 0", null);
    }

    if (_src != null){
      if (this._showMVCheck){
        _src.setCheckMissingValues(this.m_checkMV.isSelected());
      }
      if  (this._showVerbose){
        _src.setVerbose(m_verbose.isSelected());
      }
      _src.setCutOff((m_distSlide.getValue() == 0) ? 1 : m_distSlide.getValue());
      _src.setMaxNumCenters(num);
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

    m_gbc.anchor = GridBagConstraints.EAST;
    m_numClustLbl = new JLabel();
    m_numClustLbl.setText("Max samples: ");
    m_numClustLbl.setToolTipText("Enter integer value > 0 specifying max number of samples desired.");
    m_gbl.setConstraints(m_numClustLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_numClust = new JTextField(Integer.toString((_src.getMaxNumCenters() < 1)?500:_src.getMaxNumCenters()), 7);
    m_numClust.setFont(new Font("Arial", Font.BOLD,12));
    m_numClust.setToolTipText("Enter integer value > 0 specifying max number of samples desired.");
    m_gbl.setConstraints(m_numClust, m_gbc);


    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.gridwidth = 2;
    m_gbc.insets = new Insets(10,2,2,2);
    m_distLbl = new JLabel();
    m_distLbl.setText("Distance Cutoff (% of Max):  "+ Integer.toString((_src.getCutOff() == 0)?1:_src.getCutOff()));
    //m_distLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_gbl.setConstraints(m_distLbl, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridwidth = 2;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.insets = new Insets(2,2,10,2);
    m_distSlide = new JSlider(JSlider.HORIZONTAL, 0, 100, (_src.getCutOff() == 0)?1:_src.getCutOff());
    m_distSlide.setMinorTickSpacing(10);
    m_distSlide.setMajorTickSpacing(25);
    m_distSlide.setPaintLabels(true);
//    for (Enumeration enum = m_distSlide.getLabelTable().elements(); enum.hasMoreElements();){
//      ((JLabel)enum.nextElement()).setFont(new Font("Arial", Font.BOLD,10));
//    }
    m_distSlide.addChangeListener(this);
    m_gbl.setConstraints(m_distSlide, m_gbc);



    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_gbc.insets = new Insets(10,2,2,2);
    m_gbc.gridwidth = 1;
    m_dmLbl = new JLabel();
    m_dmLbl.setText("Distance Metric: ");
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
    m_gbc.insets = new Insets(4,2,2,2);
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_verbose = new JCheckBox("Verbose", _src.getVerbose());
    //m_verbose.setFont(new Font("Arial", Font.BOLD,10));
    m_verbose.setToolTipText("Print verbose messages to console.");
    m_gbl.setConstraints(m_verbose, m_gbc);
    m_verbose.setVisible(_showVerbose);

    m_gbc.gridy++;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.insets = new Insets(4,2,2,2);
    this.m_checkMV = new JCheckBox("Check Missing for Values",_src.getCheckMissingValues());
    //m_checkMV.setFont(new Font("Arial", Font.BOLD,10));
    m_checkMV.setToolTipText("Screen for missing values in the input table.");
    m_gbl.setConstraints(m_checkMV, m_gbc);
    m_checkMV.setVisible(this._showMVCheck);

    add(m_numClustLbl);
    add(m_numClust);
    add(m_distLbl);
    add(m_distSlide);
    add(m_dmLbl);
    add(m_distMetrics);
    add(m_verbose);
    add(m_checkMV);

    this.setMinimumSize(this.getPreferredSize());
    this.validateTree();

  }

  //==========================================
  // Interface Implementation: ActionListener
  //==========================================

  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == this.m_distMetrics){
      JComboBox cb = (JComboBox)src;
      m_distMetrics.setToolTipText(HAC.s_DistanceMetricDesc[cb.getSelectedIndex()]);
    }
  }


  //==========================================
  // Interface Implementation: ChangeListener
  //==========================================

  public void stateChanged(ChangeEvent e) {
    if (e.getSource() == m_distSlide){
      String disp = Integer.toString((m_distSlide.getValue() == 0) ? 1 : m_distSlide.getValue());
      m_distSlide.setToolTipText(disp);
      m_distLbl.setText("Distance Cutoff (% of Max):  "+ disp);
    }
  }

}
