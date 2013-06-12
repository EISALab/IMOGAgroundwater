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
 *
 * <p>Title: DSTPView</p>
 * <p>Description: This is a support class for ParseDSTPToDBTable.  It
 * implements the UserView for the UI module.  It reads the metadata from the
 * DSTP server to populate the UI.  If the user selects a data source to load,
 * it creates a DSTPDatSource object (that is a component to a DBTable) and
 * passes to it the DSTPView.MetaNode for the data file being retrieved.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @author D. Searsmith
 * @version 1.0
 */
public class DSTPView
    extends JUserPane
    implements ActionListener, ComponentListener, KeyListener {

  //==============
  // Data Members
  //==============
  static private final int s_MIN_WIDTH = 700;
  static private final int s_MIN_HEIGHT = 500;
  private boolean _debug = true;
  private ParseDSTPToDBTable _select = null;

  private MetaNode _chosenNode = null;

  private Hashtable _cats = new Hashtable();

  //Components
  private JButton _reset = null;
  private JButton _abort = null;
  private JButton _done = null;

  private JMenuBar _menu = null;
  private JMenuItem _helpItem = null;
  private JMenu _help = null;

  private DSTPTreePanel _treepan = null;
  static private DSTPView s_instance = null;
  private JLabel _serverlbl = null;
  private JTextField _servertext = null;
  private JList _list = null;
  private DefaultListModel _listmodel = new DefaultListModel();

  private HelpWindow helpWindow;

  //================
  // Constructor(s)
  //================

  public DSTPView() {
    s_instance = this;
    setupMenu();
    helpWindow = new HelpWindow();
  }

  public DSTPView(ParseDSTPToDBTable select) {
    _select = select;
    s_instance = this;
    setupMenu();
    helpWindow = new HelpWindow();
  }

  //================
  // Static Methods
  //================
  static public DSTPView getInstance() {
    return s_instance;
  }

  static public void main(String[] args) {
    DSTPView view = new DSTPView();
    JFrame frame = new JFrame("TEST");
    frame.getContentPane().add(view);
    view.init();
    view.addServerInfoToTree("ncdm121.lac.uic.edu");
    frame.pack();
    frame.show();
  }

  //================
  // Public Methods
  //================

  //callback method
  public void pushOut(DSTPDataSource ds) {
    getMain().push(new DBTable(ds), 0);
    enableAll();
    getMain().viewDone("Done");
  }

  public void disableAll() {
    _list.setEnabled(false);
    _treepan.setEnabled(false);
    _abort.setEnabled(false);
    _done.setEnabled(false);
    _reset.setEnabled(false);
  }

  public void enableAll() {
    _list.setEnabled(true);
    _treepan.setEnabled(true);
    _done.setEnabled(true);
    _reset.setEnabled(true);
    _abort.setEnabled(true);
  }

  public void reset() {
    _listmodel.removeAllElements();
    _select.setServerName(_servertext.getText());
    addServerInfoToTree(_servertext.getText());
  }

  public void reset(String srvr) {
    _listmodel.removeAllElements();
    _servertext.setText(srvr);
    addServerInfoToTree(srvr);
  }

  public ParseDSTPToDBTable getMain() {
    return _select;
  }

  public Dimension getPreferredSize() {
    return new Dimension(700, 500);
  }

  public void setChosenNode(MetaNode node) {
    _chosenNode = node;
    _listmodel.removeAllElements();
    Iterator atts = node.getAttributes();
    while (atts.hasNext()) {
      attribute att = (attribute) atts.next();
      _listmodel.addElement(att.getAttName() + " " + att.getAttType() + " " +
                            att.getAttNote());
    }
    _list.setSelectionInterval(0, _listmodel.getSize() - 1);
  }

  //=================
  // Private Methods
  //=================
  private void setupMenu() {
    _menu = new JMenuBar();
    _menu.setBackground(new Color(83, 110, 117));
    _help = new JMenu("Help");
    _help.setBackground(new Color(83, 110, 117));
    _help.setForeground(Color.white);
    _help.setFont(new Font("Arial", Font.BOLD, 14));
    _menu.add(_help);
    this._helpItem = new JMenuItem("About ParseDSTPToDBTable");
    _helpItem.setFont(new Font("Arial", Font.BOLD, 14));
    _helpItem.addActionListener(this);
    _help.add(_helpItem);
  }

  private void init() {
    this.setBackground(new Color(83, 110, 117));
    this.setLayout(new BorderLayout());
    _treepan = new DSTPTreePanel(this);

    _list = new JList(_listmodel);
    JScrollPane sp1 = new JScrollPane(_list);

    JSplitPane split = new JSplitPane();
    split.add(_treepan, JSplitPane.LEFT);
    split.add(sp1, JSplitPane.RIGHT);

    add(split, BorderLayout.CENTER);

    JPanel tpan = new JPanel();
    _serverlbl = new JLabel("Server: ");
    _servertext = new JTextField();
    _servertext.setColumns(35);
    _servertext.addKeyListener(this);
    tpan.add(_serverlbl);
    tpan.add(_servertext);
    add(tpan, BorderLayout.NORTH);

    JPanel buttpan = new JPanel();
    _done = new JButton("Done");
    _done.addActionListener(this);
    buttpan.add(_done);
    _reset = new JButton("Reset");
    _reset.addActionListener(this);
    buttpan.add(_reset);
    _abort = new JButton("Abort");
    _abort.addActionListener(this);
    buttpan.add(_abort);

    add(buttpan, BorderLayout.SOUTH);

    if ( (_select != null) && (_select.getServerName() != null)) {
      _servertext.setText(_select.getServerName());
      addServerInfoToTree(_select.getServerName());
    }
  }

  private void parseMetaData(DSTPConnection conn) {
    try {
      Vector data = null;
      data = conn.getServerDataVector("METADATA EXPAND", 0);
      String concat = "<DSML>";
      for (int j = 0, k = data.size(); j < k; j++) {
        String datum = (String) data.get(j);
        if (j > 0) {
          concat += datum;
        }
      }
      concat += "</DSML>";

      SAXBuilder sb = new SAXBuilder();
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream dos = new DataOutputStream(baos);
      dos.writeBytes(concat);
      dos.flush();
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      Document doc = sb.build(bais);

      System.out.println("Metadata parsed and document built ...");

      Iterator cats = doc.getRootElement().getChildren("CATEGORY").iterator();

      while (cats.hasNext()) {
        Element cat = (Element) cats.next();
        ArrayList nodes = new ArrayList();
        _cats.put(cat, nodes);
        buildMetaNodes(nodes, cat);
      }
    }
    catch (Exception e) {
    }
  }

  private void buildMetaNodes(ArrayList nodes, Element cat) {

    Hashtable ht = new Hashtable();

    Iterator ucks = cat.getChildren("UCK").iterator();
    while (ucks.hasNext()) {
      Element uck = (Element) ucks.next();
      String uckname = uck.getAttributeValue("NAME");
      String uckid = uck.getAttributeValue("ID");
      Iterator servers = uck.getChildren("SERVER").iterator();
      while (servers.hasNext()) {
        Element server = (Element) servers.next();
        String servername = server.getAttributeValue("NAME");
        String serverlocation = server.getAttributeValue("Location");
        Iterator datafiles = server.getChildren("DATAFILE").iterator();
        while (datafiles.hasNext()) {
          Element datafile = (Element) datafiles.next();
          String datafilename = datafile.getAttributeValue("NAME");
          String datafiledate = datafile.getAttributeValue("DATE");
          String datafiledescription = datafile.getAttributeValue("DESCRIPTION");
          String datafilenumrecords = datafile.getAttributeValue("NUMRECORDS");
          String datafilesource = datafile.getAttributeValue("SOURCE");
          //type
          //delimiter
          //dsfilename
          //address
          String key = datafilename + "::" + servername;
          MetaNode node = (MetaNode) ht.get(key);
          if (node == null) {
            //put a new metanode
            node = new MetaNode(cat.getAttributeValue("NAME"),
                                servername,
                                serverlocation,
                                datafilename,
                                datafiledate,
                                datafiledescription,
                                datafilenumrecords,
                                datafilesource);
            node.addUCK(new uck(uckname, uckid));
            ht.put(key, node);
            nodes.add(node);
            Iterator atts = datafile.getChildren("ATTRIBUTE-DESCRIPTOR").
                iterator();
            while (atts.hasNext()) {
              Element att = (Element) atts.next();
              //add att to metanode
              node.addAttribute(new attribute(att));
            }
          } else {
            //update existing metanode with a new uck
            node.addUCK(new uck(uckname, uckid));
          }
        }
      }
    }
  }

  private void addServerInfoToTree(String server) {
    try {
      _cats = new Hashtable();
      //_treepan.getTree().removeAll();
      DSTPConnection conn = new DSTPConnection(server);
      parseMetaData(conn);
      DSTPTreeModel model = _treepan.getModel();
      DefaultMutableTreeNode root = _treepan.getNewRoot(server);
      model.setRoot(root);
      //rebuild the tree
      Enumeration cats = _cats.keys();
      while (cats.hasMoreElements()) {
        Element cat = (Element) cats.nextElement();
        DefaultMutableTreeNode newnode = new DefaultMutableTreeNode(cat.
            getAttributeValue("NAME"));
        model.insertNodeInto(newnode, root, 0);
        Iterator mnodes = ( (ArrayList) _cats.get(cat)).iterator();
        while (mnodes.hasNext()) {
          MetaNode mnode = (MetaNode) mnodes.next();
          mnode.buildSubTree(model, newnode);
        }
      }
    }
    catch (Exception e) {
      JOptionPane.showMessageDialog(this, e.getMessage());
      System.out.println("EXCEPTION: " + e.getMessage());
      //e.printStackTrace();
      return;
    }
  }

  //====================================
  // Interface Implementation: UserView
  //====================================
  public void setInput(Object o, int i) {}

  public void initView(ViewModule vm) {
    init();
  }

  public Object getMenu() {
    return this._menu;
  }

  //This must be the King of all hacks!!! UGLY
  boolean first = true;

  public void paint(Graphics g) {
    super.paint(g);
    if (first) {
      ( (JFrame)this.getParent().getParent().getParent().getParent()).
          addComponentListener(this);
      ( (JFrame)this.getParent().getParent().getParent().getParent()).
          addWindowListener(
          new WindowAdapter() {

        /**
         * put your documentation comment here
         * @param e
         */
        public void windowClosing(WindowEvent e) {
          getMain().viewAbort();
        }
      });
      first = false;
    }
  }

  //==========================================
  // Interface Implementation: KeyListener
  //==========================================

  public void keyPressed(KeyEvent e) {
    //Object src = e.getSource();
    Object src = e.getSource();
    if (src == _servertext) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER) {
        reset();
      }
    }
  }

  public void keyReleased(KeyEvent e) {
    //Object src = e.getSource();
  }

  public void keyTyped(KeyEvent e) {
  }

  //==========================================
  // Interface Implementation: ActionListener
  //==========================================
  public void actionPerformed(ActionEvent e) {
    Object src = e.getSource();
    if (src == _reset) {
      reset();
    } else if (src == _abort) {
      _select.viewAbort();
    } else if (src == _done) {
      Object[] vals = _list.getSelectedValues();
      if (vals.length == 0) {
        JOptionPane.showMessageDialog(this, "No attributes were selected.");
        return;
      }
      ArrayList alist = new ArrayList();
      for (int i = 0, n = vals.length; i < n; i++) {

        StringTokenizer toker = new StringTokenizer( (String) vals[i]);
        alist.add(toker.nextToken());
      }
      Iterator atts = _chosenNode.getAttributes();
      ArrayList sels = new ArrayList();
      while (atts.hasNext()) {

        attribute att = (attribute) atts.next();



        if (alist.contains(att.getAttName())) {


          sels.add(att);
        }
      }
      _chosenNode.setSelectedAttributes(sels);
      disableAll();

//headless conversion support
      String[] attNames = new String [sels.size()];
      String[] attTypes = new String[sels.size()];
      for (int i=0; i<attNames.length; i++){
        attribute att = (attribute) sels.get(i);
        attNames[i] = att.getAttName();
        attTypes[i] = att.getAttType();
      }
      _select.setAttNames(attNames);
      _select.setAttTypes(attTypes);



      _select.setCategory(_chosenNode.getCategory());

      _select.setDatafileName(_chosenNode.getDatafileName());
//headless conversion support

      new DSTPDataSource(this, _chosenNode);
    } else if (src == _helpItem) {
      helpWindow.setVisible(true);
    }

  }

  //=============================================
  // Interface Implementation: ComponentListener
  //=============================================
  public void componentResized(ComponentEvent e) {
    int width = ( (JFrame)this.getParent().getParent().getParent().getParent()).
        getWidth();
    int height = ( (JFrame)this.getParent().getParent().getParent().getParent()).
        getHeight();
    //we check if either the width
    //or the height are below minimum
    boolean resize = false;
    if (width < s_MIN_WIDTH) {
      resize = true;
      width = s_MIN_WIDTH;
    }
    if (height < s_MIN_HEIGHT) {
      resize = true;
      height = s_MIN_HEIGHT;
    }
    if (resize) {
      ( (JFrame)this.getParent().getParent().getParent().getParent()).setSize(
          width,
          height);
    }
  }

  /**
   * put your documentation comment here
   * @param e
   */
  public void componentMoved(ComponentEvent e) {}

  /**
   * put your documentation comment here
   * @param e
   */
  public void componentShown(ComponentEvent e) {}

  /**
   * put your documentation comment here
   * @param e
   */
  public void componentHidden(ComponentEvent e) {}


  private final class HelpWindow
      extends JD2KFrame {
    HelpWindow() {
      super("About Dendogram Vis");
      JEditorPane jep = new JEditorPane("text/html", getHelpString());
      jep.setBackground(new Color(255, 255, 240));
      getContentPane().add(new JScrollPane(jep));
      setSize(400, 400);
    }
  }

  private static final String getHelpString() {
    String s = "<html><h2>ParseDSTPToDBTable</h2><p>Overview: ";
    s += "This module provides a GUI that is a metadata viewer for a DSTP ";
    s += "server.  The user can select a data source to be loaded into a DBTable ";
    s += "for use in D2K itineraries.";
    s += "</p>";


    s += "<p>";
    s +=
        "See: http://www.dataspaceweb.net/index.htm.  DataSpaces is a research ";
    s +=
        "product of The National Center for Data Mining (NCDM) at the University ";
    s += "of Illinois at Chicago (UIC).";
    s += "</p>";


    s += "<p>Detailed Description: ";
    s += "This GUI provides a tree view of metadata for DSTP data sources.  Any";
    s += "of these sources can be selected and loaded into a DBTable for use in ";
    s += "a D2K itinerary.  To select a source simply double click on the tree ";
    s += "node that bears the source name.  The attributes for the source will ";
    s += "be displayed in the ";
    s += "window on the right.  Initially, all attributes will appear selected. ";
    s += "The user can select and deselect attributes as desired.  When the ";
    s += "<i>Done</i> button is pressed the DBTable is built and output and ";
    s += "the GUI will be dismissed.  If the server on which the data source ";
    s += "resides (not necessarily the same as the server initially contacted) ";
    s += "is not reachable, then an error message is displayed and the GUI remains. ";
    s += "The <i>Reset</i> button causes the metadata to be rebuilt.  The ";
    s += "<i>Abort</i> button aborts the itinerary.  This version of the GUI ";
    s += "does not support sampling.";
    s += "</p>";

    s += "<p>Data Handling: ";
    s += "The DBTable that is created uses a primitive implementation of a DBDataSource ";
    s += "that lads the entire dataset into memory.  The DBTable is serializable ";
    s += "but the data is transient.  The data is reacquired from the DSTP server ";
    s += "when the object is deserialized. The DBTable is not mutable.";
    s += "</p>";

    s += "<p>Scalability: ";
    s += "The DBTable created currently has the same memory limitations as an in ";
    s += "memory table.  This will eventually be corrected with an appropriate ";
    s += "caching scheme.";
    s += "</p></html>";
    return s;
  }

}


//=============
// Inner Class
//=============

/*  public class MetaNode
    implements java.io.Serializable {

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
*/
/*  public class uck
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
*/
