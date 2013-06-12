package ncsa.d2k.modules.core.discovery.cluster.gui.properties;

/**
 * <p>Title: FractionationParams_Props </p>
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

public class FractionationParams_Props extends JPanel implements CustomModuleEditor, MouseInputListener, ChangeListener, ActionListener {

  //==============
  // Data Members
  //==============

  private GridBagLayout m_gbl = new GridBagLayout();
  private GridBagConstraints m_gbc = new GridBagConstraints();

  //components

  private boolean m_auto_sel = false;

  private JLabel m_numClustLbl = null;
  private JTextField m_numClust = null;

  private JCheckBox m_auto = null;

  private JLabel m_distLbl = null;
  private JSlider m_distSlide = null;

  private JLabel m_cmLbl = null;
  private JLabel m_dmLbl = null;
  private javax.swing.JComboBox m_methods = null;
  private javax.swing.JComboBox m_distMetrics = null;

  private JLabel m_maxLbl = null;
  private JTextField m_max = null;

  private boolean _initAuto = false;

  private JLabel m_maxPartLbl = null;
  private JTextField m_maxPart = null;

  private JLabel m_nthSortLbl = null;
  private JTextField m_nthSort = null;

  private FractionationParams _src = null;



  //================
  // Constructor(s)
  //================


  public FractionationParams_Props(FractionationParams src){
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
        throw new PropertyVetoException("Error in number of clusters field: " + e.getMessage(), null);
      }
      if (num < 2){
        throw new PropertyVetoException("Number of clusters must be two or more.", null);
      }
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

    int maxit = -1;
    try {
      maxit = Integer.parseInt(m_max.getText());
    }
    catch (Exception e) {
      throw new PropertyVetoException("Error in number of assignments field: " +
                                      e.getMessage(), null);
    }
    if (maxit < 1) {
      throw new PropertyVetoException("Number of assignments must be > 1.", null);
    }

    if (_src != null){
      if (m_auto.isSelected()){
        _src.setHacDistanceThreshold((m_distSlide.getValue() == 0) ? 1 : m_distSlide.getValue());
      } else {
        _src.setHacDistanceThreshold(0);
      }
      _src.setNumClusters(num);
      _src.setRefinementMaxIterations(maxit);
      _src.setClusterMethod(this.m_methods.getSelectedIndex());
      _src.setDistanceMetric(this.m_distMetrics.getSelectedIndex());
      _src.setPartitionSize(max);
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
    m_gbc.fill = GridBagConstraints.VERTICAL;
    m_gbc.insets = new Insets(2,2,2,2);

    m_gbc.gridwidth = 1;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_cmLbl = new JLabel();
    m_cmLbl.setText("Cluster Method: ");
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
    m_gbc.gridwidth = 1;
    m_gbc.insets = new Insets(2,2,2,2);
    m_gbc.anchor = GridBagConstraints.EAST;
    m_numClustLbl = new JLabel();
    m_numClustLbl.setText("Number of clusters: ");
    m_numClustLbl.setToolTipText("Enter integer value > 2 specifying number of clusters desired.");
    m_gbl.setConstraints(m_numClustLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_numClust = new JTextField(Integer.toString((_src.getNumClusters() < 2)?5:_src.getNumClusters()), 5);
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
    m_maxPart = new JTextField(Integer.toString((_src.getPartitionSize() < 1)?250:_src.getPartitionSize()), 7);
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
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.insets = new Insets(10,2,2,2);

    m_auto = new JCheckBox("Auto Cluster",true);
    m_auto.addMouseListener(this);
    _initAuto = (_src.getHacDistanceThreshold() > 0);
    m_auto_sel = _initAuto;
    m_auto.setSelected(m_auto_sel);
    m_auto.setToolTipText("If selected you must specify a distance threshold at which point clustering will stop.");
    m_gbl.setConstraints(m_auto, m_gbc);

    m_gbc.gridy++;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.gridwidth = 2;
    m_gbc.insets = new Insets(2,2,2,2);
    m_distLbl = new JLabel();
    m_distLbl.setText("Distance Cutoff (% of Max):  "+ Integer.toString((_src.getHacDistanceThreshold() == 0)?1:_src.getHacDistanceThreshold()));
    m_gbl.setConstraints(m_distLbl, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridwidth = 2;
    m_gbc.anchor = GridBagConstraints.CENTER;
    m_gbc.insets = new Insets(2,2,2,2);
    m_distSlide = new JSlider(JSlider.HORIZONTAL, 0, 100, (_src.getHacDistanceThreshold() == 0)?1:_src.getHacDistanceThreshold());
    m_distSlide.setMinorTickSpacing(10);
    m_distSlide.setMajorTickSpacing(25);
    m_distSlide.setPaintLabels(true);
    m_distSlide.addChangeListener(this);
    m_distSlide.setEnabled(m_auto_sel);
    m_gbl.setConstraints(m_distSlide, m_gbc);

    m_gbc.gridy++;
    m_gbc.gridx = 0;
    m_gbc.anchor = GridBagConstraints.EAST;
    m_gbc.insets = new Insets(10,2,2,2);
    m_gbc.gridwidth = 1;
    m_dmLbl = new JLabel();
    m_dmLbl.setText("Distance Metric: ");
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
    m_maxLbl.setText("Num assignments: ");
    //m_numClustLbl.setFont(new Font("Arial", Font.BOLD,10));
    m_maxLbl.setToolTipText("Enter integer value > 0 specifying number of assignment passes to perform.");
    m_gbl.setConstraints(m_maxLbl, m_gbc);

    m_gbc.gridx = 1;
    m_gbc.anchor = GridBagConstraints.WEST;
    m_max = new JTextField(Integer.toString((_src.getRefinementMaxIterations() < 1)?5:_src.getRefinementMaxIterations()), 5);
    m_max.setFont(new Font("Arial", Font.BOLD,12));
    m_max.setToolTipText("Enter integer value > 0 specifying number of assignment passes to perform.");
    m_gbl.setConstraints(m_max, m_gbc);

    add(m_cmLbl);
    add(m_methods);
    add(m_numClustLbl);
    add(m_numClust);
    add(m_auto);
    add(m_distLbl);
    add(m_distSlide);
    add(m_dmLbl);
    add(m_distMetrics);
    add(m_maxLbl);
    add(m_max);
    add(this.m_maxPart);
    add(this.m_maxPartLbl);
    add(this.m_nthSort);
    add(this.m_nthSortLbl);

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

  //=========================================
  // Interface Implementation: MouseListener
  //=========================================

  public void mouseClicked(MouseEvent evt){
    //System.out.println("MOUSE CLICKED");
  }

  public void mouseEntered(MouseEvent evt){
  }

  public void mouseExited(MouseEvent evt){
  }

  public void mousePressed(MouseEvent evt){
    if (evt.getSource() == m_auto){
      m_auto_sel = !m_auto_sel;
      m_distSlide.setEnabled(m_auto_sel);
      m_numClust.setEnabled(!m_auto_sel);
      repaint();
    }
  }

  public void mouseReleased(MouseEvent evt){
    if (evt.getSource() == m_auto){
      if (m_auto.isSelected() != m_auto_sel){
        m_auto.setSelected(m_auto_sel);
      }
      repaint();
    }
  }

  public void mouseMoved(MouseEvent evt){
    //System.out.println("MOUSE MOVED");
  }

  public void mouseDragged(MouseEvent evt){
    //System.out.println("MOUSE DRAGGED");
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
