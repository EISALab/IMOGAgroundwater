package ncsa.d2k.modules.core.io.dstp;

//==============
// Java Imports
//==============
import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;

/**
 *
 * <p>Title: ProgressViewer</p>
 * <p>Description: This is a support class for ParseDSTPToDBTable</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: NCSA Automated Learning Group</p>
 * @version 1.0
 */
public class ProgressViewer extends JFrame {
    //==============
    // Data Members
    //==============
    public final static int TIME_INTERVAL = 500;
    private JProgressBar progressBar = null;
    private Timer timer = null;
    private ProgressQueryable m_pq = null;
    private int m_start = 0;
    private int m_stop = 0;
    private boolean m_quitting = false;
    private JLabel m_label = null;
    private JFrame m_me = null;
    private Color m_bcolor = new Color(237, 235, 223);          //light tan

    //================
    // Constructor(s)
    //================
    public ProgressViewer (String fstring, String title, int start, int stop,
            ProgressQueryable pq) {
        super(fstring);
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        m_me = this;
        m_start = start;
        m_stop = stop;
        m_pq = pq;
        m_label = new JLabel(title);
        progressBar = new JProgressBar(start, stop);
        progressBar.setValue(start);
        progressBar.setStringPainted(true);
        //progressBar.setOpaque(false);
        JPanel panel = new JPanel();
        panel.add(progressBar);
        panel.setBackground(m_bcolor);
        JPanel contentPane = new JPanel();
        contentPane.setBackground(m_bcolor);
        contentPane.setLayout(new BorderLayout());
        contentPane.add(panel, BorderLayout.CENTER);
        contentPane.add(m_label, BorderLayout.NORTH);
        contentPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        //Create a timer.
        timer = new Timer(TIME_INTERVAL, new ActionListener() {

            /**
             * put your documentation comment here
             * @param evt
             */
            public void actionPerformed (ActionEvent evt) {
                if (m_quitting == true) {
                    m_me.hide();
                    timer.stop();
                    m_me.dispose();
                }
                progressBar.setValue(m_pq.getProgress());
                if (m_pq.getProgress() >= m_stop) {
                    m_quitting = true;
                }
            }
        });
        timer.start();
        addWindowListener(new WindowAdapter() {

            public void windowClosing (WindowEvent e) {
                if (timer != null) {
                    timer.stop();
                }
                setVisible(false);
            }
        });
        Rectangle r = gc.getBounds();
        int width = (int)r.getWidth();
        int height = (int)r.getHeight();
        Dimension d = this.getPreferredSize();
        int fwidth = (int)d.getHeight();
        int fheight = (int)d.getWidth();
        int x = (int)Math.round((width - fwidth)/2);
        int y = (int)Math.round((height - fheight)/2);
        this.setLocation(x, y);
        pack();
        setVisible(true);
    }

    /**
     * put your documentation comment here
     */
    public void quit () {
        m_me.hide();
        timer.stop();
        m_me.dispose();
    }
}



