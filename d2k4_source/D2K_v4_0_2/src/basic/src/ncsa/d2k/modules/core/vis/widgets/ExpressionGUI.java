package ncsa.d2k.modules.core.vis.widgets;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ncsa.gui.*;
import ncsa.d2k.modules.core.datatype.*;

/**
 * <code>ExpressionGUI</code> encapsulates some functionality common to a
 * typical user interface involving an <code>Expression</code>.
 * <p>
 * An <code>ExpressionGUI</code> object is a <code>JPanel</code> that
 * contains the following elements, arranged in a reasonable manner:
 *
 * <ul>
 *    <li>A text area for typing the expression string.</li>
 *    <li>A button for inserting parentheses around a location or
 *        group of characters (if some of the string is highlighted).</li>
 *    <li>A button for checking syntax. The supplied
 *        <code>Expression</code> object must behave properly in order
 *        for this functionality to work (i.e., throw an
 *        <code>ExpressionException</code> if an attempt to evaluate
 *        the string fails).</li>
 *    <li>A button for clearing the string.</li>
 *    <li>A button for "setting" the current expression string. This
 *        button triggers actual evaluation of the
 *        <code>Expression</code> and passes the evaluation object
 *        to all registered <code>ExpressionListener</code>s.</li>
 * </ul>
 * <p>
 * A user interface that includes an <code>ExpressionGUI</code> object must
 * first register itself as an <code>ExpressionListener</code> with that
 * object. The interface will then receive the <code>Expression</code>'s
 * evaluation via its <code>expressionChanged</code> method.
 *
 * @author gpape
 */
public class ExpressionGUI extends JPanel implements ActionListener {

   private ArrayList listeners;
   private Expression expression;

   private JButton parenButton, syntaxButton, clearButton, addButton;
   private JTextArea textArea;

   /**
    * Constructor.
    *
    * @param expression      the <code>Expression</code> this element should
    *                        use to parse and evaluate expression strings
    * @param showAddButton   whether the "add" button should be displayed.
    *                        Some applications may prefer to obtain it via
    *                        <code>getAddButton()</code> and display it
    *                        elsewhere.
    */
   public ExpressionGUI(Expression expression, boolean showAddButton) {

      listeners = new ArrayList();
      this.expression = expression;

      parenButton = new JButton("Add Parens");
      parenButton.addActionListener(this);
      syntaxButton = new JButton("Check Syntax");
      syntaxButton.addActionListener(this);
      clearButton = new JButton("Clear");
      clearButton.addActionListener(this);
      addButton = new JButton("Add");
      addButton.addActionListener(this);

      textArea = new JTextArea();
      textArea.setEditable(true);
      textArea.getCaret().setVisible(true);
      textArea.setCaretColor(Color.red);
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);

      JPanel bottomPanel = new JPanel();
      bottomPanel.setLayout(new GridBagLayout());
      Constrain.setConstraints(bottomPanel, parenButton, 0, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
      Constrain.setConstraints(bottomPanel, syntaxButton, 1, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.EAST, 0, 0);
      Constrain.setConstraints(bottomPanel, new JLabel(), 2, 0, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);
      Constrain.setConstraints(bottomPanel, clearButton, 3, 0, 1, 1,
         GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
      if (showAddButton) {
         Constrain.setConstraints(bottomPanel, addButton, 4, 0, 1, 1,
            GridBagConstraints.NONE, GridBagConstraints.WEST, 0, 0);
      }

      this.setLayout(new GridBagLayout());
      Constrain.setConstraints(this, new JScrollPane(textArea), 0, 0, 1, 1,
         GridBagConstraints.BOTH, GridBagConstraints.CENTER, 1, 1);
      Constrain.setConstraints(this, bottomPanel, 0, 1, 1, 1,
         GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER, 1, 0);

   }

   /**
    * Adds an <code>ExpressionListener</code> to this element. This listener
    * will receive evaluation objects from this element.
    *
    * @param e               the <code>ExpressionListener</code>
    */
   public void addExpressionListener(ExpressionListener e) {
      listeners.add(e);
   }

   /**
    * Returns this element's text area.
    * @return
    */
   public JTextArea getTextArea() {
      return textArea;
   }

   /**
    * Returns this element's "add" button.
    * @return
    */
   public JButton getAddButton() {
      return addButton;
   }

   /**
    * Sets this element's underlying <code>Expression</code> to the new
    * object.
    *
    * @param e
    */
   public void setExpression(Expression e) {
      this.expression = e;
   }

   public void actionPerformed(ActionEvent e) {

      Object src = e.getSource();

      if (src == parenButton) {

         int start = textArea.getSelectionStart(),
             end = textArea.getSelectionEnd();

         textArea.insert(")", end);
         textArea.insert("(", start);

      }

      else if (src == syntaxButton) {

         try {
            expression.setExpression(textArea.getText());
         }
         catch(ExpressionException ex) {
            JOptionPane.showMessageDialog(this,
               "This expression appears to be invalid:\n\n" + ex.getMessage(),
               "Invalid expression", JOptionPane.ERROR_MESSAGE);
            return;
         }

         JOptionPane.showMessageDialog(this,
            "This expression appears to be valid.",
            "Valid expression", JOptionPane.INFORMATION_MESSAGE);

      }

      else if (src == clearButton) {

         textArea.setText(null);

      }

      else if (src == addButton) {

         Object eval = null;
         try {
            expression.setExpression(textArea.getText());
            eval = expression.evaluate();
         }
         catch(ExpressionException ex) {
            JOptionPane.showMessageDialog(this,
               "This expression appears to be invalid:\n\n" + ex.getMessage(),
               "Invalid expression", JOptionPane.ERROR_MESSAGE);
            return;
         }

         for (int i = 0; i < listeners.size(); i++)
            ((ExpressionListener)listeners.get(i)).expressionChanged(eval);

      }

   }

}
