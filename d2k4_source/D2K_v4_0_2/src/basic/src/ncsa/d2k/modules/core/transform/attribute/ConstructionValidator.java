package ncsa.d2k.modules.core.transform.attribute;
import ncsa.d2k.modules.core.datatype.table.Table;
import ncsa.d2k.modules.core.datatype.table.ColumnTypes;
import java.util.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

class ConstructionValidator {
  private String[]  _expressions;
  private Table _table;


//what can we expect when parsing
  public static final int BEGIN = 0; //left, open, nothing
  public static final int LEFT_OPEN_NOTHING = 0;

  public static final int AFTER_OPEN = 1; //left, open, close
  public static final int LEFT_OPEN_CLOSE = 1;

  public static final int AFTER_CLOSE = 2; // operator, close, nothing
  public static final int AFTER_RIGHT = 2; // operator, close, nothing
  public static final int OPERATOR_CLOSE_NOTHING = 2;

  public static final int AFTER_LEFT = 3; // operator
  public static final int OPERATOR = 3;

  public static final int AFTER_OPERATOR = 4; //right
  public static final int RIGHT = 4;





  private HashMap columns;

  public ConstructionValidator(String[] expressionss, Table table) {
    _expressions = expressionss;
    _table = table;
}

  public boolean validate() throws Exception{

//building a column lookup map.
    columns = new HashMap();
    for (int i = 0; i < _table.getNumColumns(); i++)
      if (_table.isColumnNumeric(i) ||
          (_table.getColumnType(i) == ColumnTypes.BOOLEAN))
        columns.put(_table.getColumnLabel(i).toUpperCase(), new Integer(i));

//for each expression
    for (int i = 0; i < _expressions.length; i++) {

     boolean end = true; //at beginning, after right hand operand and  after closing parenthese (if parenthese are balanced)
     int paren_balance = 0;

//initializing a tokenizer.
      StringTokenizer tok = new StringTokenizer(_expressions[i], " \t()*-%/+|&\\", true);

      String curTok;         //will hold current token
      String small_exp = ""; //will hold parsed expression so far
      int expected = this.BEGIN;  //what tokens are expected now.
      String right = "";          //will hold current right hand operand
      String left = "";           //will hold current left hand operand


      //start parsing
      while (tok.hasMoreTokens()) {
        curTok = tok.nextToken();
        //inspecting the first char of the token
        switch (curTok.charAt(0)) {
          //cases of a delimiter first:

          //if it is a white space - skip
          case ' ':
          case '\t': break;

          //if it is a calculus operator
          case '+':
          case '-':
          case '*':
          case '%':
          case '/':
//verifying that an operator is expected.
            if (expected == this.OPERATOR ||
                expected == this.OPERATOR_CLOSE_NOTHING) {
//if this comes right after a left hand operand
              if(expected == this.AFTER_LEFT){
                //the operand needs to be validated
                validate(left, small_exp);
                //and added to the parsed expression so far
                small_exp += " " + left;
              }//if after left

              //if this comes right after a right hand operand
              if(expected == this.AFTER_RIGHT){
                //the operand needs to be validated
                validate(right, small_exp);
                //and added to the parsed expression so far
                small_exp += " " + right;
              }//if after left


//now we are expecting for something after an operator
              expected = this.AFTER_OPERATOR;
              end = false;
//updating parsed expression
              small_exp += " " + curTok;
            }
            else{


              throw new Exception("Discovered operator '" + curTok + "'" + getMsg(small_exp));
            }
            break;

//if it is a boolean operator - the validating is similar
//except for an additional validation of second half of the operator
          case '&':
          case '|':


            if (expected == this.OPERATOR ||
                expected == this.OPERATOR_CLOSE_NOTHING) {

              if(expected == this.AFTER_LEFT){
                validate(left, small_exp);
                small_exp += " " + left;
              }
              //getting the second half of the operator
              String endOp = tok.nextToken();
              //if the second half is equal to the first one - all's well
              if (endOp.equals(curTok)) {
                expected = this.AFTER_OPERATOR;
                end = false;
                small_exp += " " + curTok + endOp;
              } //if
              else{


                throw new Exception("Discovered operator '" + curTok + "'" + getMsg(small_exp) +
                                    " and a '" + endOp +
                                    "' after that.");
              }
            }
            else{


              throw new Exception("Discovered operator '" + curTok + "'" + getMsg(small_exp));
            }
            break;


  //parentheses:
      //openning
          case '(':


            if (expected == this.LEFT_OPEN_CLOSE ||
                expected == this.LEFT_OPEN_NOTHING) {
              expected = this.AFTER_OPEN;
              small_exp += " " + curTok;
              paren_balance ++;
              end = false;
            } //if
            else
              throw new Exception("Discovered opening parentheses " +
                                  getMsg(small_exp));
            break;
//if this is a closing one
          case ')':


            if (expected == this.LEFT_OPEN_CLOSE ||
                expected == this.OPERATOR_CLOSE_NOTHING) {
//if the last token was a right hand operand - it needs to be validted and added
              if(expected == this.AFTER_RIGHT){
              validate(right, small_exp);
              small_exp += " " + right;
            }

              expected = this.AFTER_CLOSE;
              paren_balance--;

              if(paren_balance < 0)
                throw new Exception ("Unbalanced parentheses!! Please reconfigure module via a GUI run.");

              if(paren_balance == 0) end = true;
              small_exp += " " + curTok;
            } //if
            else
              throw new Exception("Discovered closing parentheses " +
                                  getMsg(small_exp));
            break;


//special case of back slash. this one comes always before a hyphen.
          case '\\':
            //checking if last token was right hand or left hand. if not raising an exception.
            if(!(expected == this.AFTER_RIGHT || expected == this.AFTER_LEFT)){
              throw new Exception ("Discovered '" + curTok + getMsg(small_exp));
            }
//all's well - keeping on validating.
            //getting the last operand
                String prevTok;
                if(expected == this.AFTER_RIGHT)
                  prevTok = right;
                else prevTok = left;

           //getting the hyphen

                curTok = tok.nextToken();

                //validating that it is indeed a hyphen
                if(!curTok.equals("-")){
                  throw new Exception ("Discovered '" + curTok + "' after: " +
                                        small_exp + " " + prevTok);
                }
                //everything is ok - parsing the second half
                curTok = tok.nextToken();

                //adjusting right/left
                if(expected == this.AFTER_RIGHT)
                 right += "-" + curTok;
               else left += "-" + curTok;

               break;


          default: //this is a left hand or a right hand.
            if (expected == this.LEFT_OPEN_CLOSE ||
                expected == this.LEFT_OPEN_NOTHING){
              expected = this.AFTER_LEFT;
              left = curTok;
            }

            else if (expected == this.RIGHT){
              expected = this.AFTER_RIGHT;
              right = curTok;
            }

            else
              throw new Exception("Discovered '" + curTok + "'" + getMsg(small_exp));



            break;

        } //switch char at 0

      } //while has more tokens

      //this is the end of the expression. if the last token was a right hand
      //operand it is needed to be validated
      if(expected == AFTER_RIGHT){
        validate(right, small_exp);
        end = true;
      }

      if(!(paren_balance == 0))
        throw new Exception ("Unbalanced parentheses!\nPlease reconfigure the module via a GUI run.");

      if(!end)
        throw new Exception ("The expression '" + small_exp + "' is invalid!\n" +
      "Please reconfigure the moduel via g GUI run.");


    }//for i



    return true;
}//validate


  private void validate(String str, String exp)throws Exception{
    try {
             Double.parseDouble(str);
           } //try
           catch (Exception e) {
             if (!columns.containsKey(str.toUpperCase()))
               throw new Exception("Discovered label '" + str +
                                   "' .\nThe label could not be found in the input table. " +
                   "\nPlease reconfigure the module by running it with GUI.");

           } //catch

  }

  private String getMsg(String small_exp){

     if(small_exp.length() == 0)
       return " at the beginning of expression.";
     else return " after '" + small_exp + "'.";

  }

}//ConstructionValidator

