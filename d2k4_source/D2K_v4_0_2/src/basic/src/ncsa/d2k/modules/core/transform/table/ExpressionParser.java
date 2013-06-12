package ncsa.d2k.modules.core.transform.table;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author vered
 * @version 1.0
 *
 * parses expressions with relations '==' or '=' (for sql) '!=' '<=' '>=' '<' '>',
 * with operators '||' '&&' or 'or' and 'and' (sql)
 * values are to be inside quotes: 'val'
 * allows empty parentheses '()'
 * allows no space before or after parentheses.
 */

public class ExpressionParser {
  public ExpressionParser() {
  }


   public class Expected {
    static final int OPEN_CLOSE_LEFT = 0; //after '(' expecting '(' or ')' or a left hand operand.
    static final int AFTER_OPEN = 0;

    static final int CLOSE_OPERATOR_NOTHING = 1; //after a right hand operand or a closing parentheses
    //expecting an operator (&&, ||) or ')' or nothing.
    static final int AFTER_CLOSE = 1;
    static final int AFTER_RIGHT = 1;
    static final int AFTER_MAL_EXPRESSION = 1;

    static final int RELATION = 2; //after a right hand operand expecting a relation (<, > etc.)
    static final int AFTER_LEFT = 2;

    static final int OPEN_LEFT = 3; //at the beginning of the parsing expecting open parentheses or right hand operand.
    static final int BEGINNING = 3;
    static final int AFTER_OPERATOR = 3;

    static final int RIGHT = 4;   //after a relation expecting a left hand operand.
    static final int AFTER_RELATION = 4;


   }

   /**
    * parses <code>expression</code> into sub expression, to verify
    * that all of them are legal and relevant to the attributes in <code>map</code>.
    * allows empty parentheses. checks for validity of parentheses as well.
    * this method is static becuase it supports also sql filter construction module's doit.
    * @param expression - an expression to parse. operators as && or ||
    * @param map - attributes map. each left hand operand in the returned value
    *              must be in the map. (maps attribute name <-> id
    * @return -    <code>expression</code> - pruned if needed (if sub expressions
    *              have attributes that are not in <code>map</code>
    * @throws Exception - if finds an unexpected token or illegal prentheses.
    */
   public static String parseExpression(String expression, HashMap map, boolean sql) throws Exception{

     final String equal, and, or;
     if(sql){
       equal = "=";
       and = "and";
       or = "or" ;
     }
     else{
      equal = "==";
      and = "&&";
      or = "||";
      }


     String goodCondition = ""; //the returned value

     StringTokenizer tok = new StringTokenizer(expression, " \n\t\f\r()<>=!&|", true);


       int numOpen = 0;  //number of opening parenthese without matching closing ones.
       int expected = Expected.BEGINNING;  //what type of token we are expecting to parse.
       int lastGoodExpected = expected;

      // String lastGoodToken = ""; //in cased we've parsed a non relative sub expression
       //this will let us know if before this expression was parentheses or an operator.
       //or nothing (the beginning)

       //whenparsing a sub expression - it will be added at once after the left hand
       //operand was parsed and the attribute was verifies.
       String rightHand = "";
       String leftHand = "";
       String relation = "";
       String operator = ""; //the operator will be added before the sub expression.


       //parsing the condition, each sub condition that holds a valid
          //attribute name will be copied into goodCondition
          //assuming the expression could be malformed.

          String secondChar = null;

       while(tok.hasMoreTokens()){

         //parsing a token
         String currTok = tok.nextToken();

         //handling special case of sql operators.
         if(sql){
           if(currTok.equals(and) || currTok.equals(or)){
             //are we expecting an operator now?
              if(!(expected == Expected.CLOSE_OPERATOR_NOTHING))
                throw new Exception ("Did not expect to find operator " + currTok + " after " + goodCondition + " " + leftHand + " " + relation + " " + rightHand);

              operator = currTok; //we are not adding the operator yet.
              //if the following sub expression is valid it will be added.
              //or if a '(' comes right after this currTok.
              expected = Expected.AFTER_OPERATOR;
              continue;
           }//if sql operator

         }//if sql

         //according to the first character of the token identification is made.
        switch(currTok.charAt(0)){
          case ' ':
          case '\t':
          case '\n':
          case '\r':
          case '\f': break;

          //open parentheses
          case '(':
            //a space is required after '('
   //         if(currTok.length() > 1) throw new Exception("there must be a space before and after each parentheses!");
            //are we expecting this token?
            if(!(expected == Expected.OPEN_CLOSE_LEFT || expected == Expected.OPEN_LEFT))
              throw new Exception("Did not expect to find '(' after " + goodCondition +
                                  " " + leftHand + " " + relation + " " + rightHand);

            //the token is legal at this point
            numOpen++;

            //checking if the last good token was an operator - if so, adding it.
            //actually no need to check, it will be empty if it isn't
            //if(!operator.equals("")){
              goodCondition += " " + operator;
              operator = ""; //initializing the operator...
            //}

            goodCondition += " " + currTok;
            expected = Expected.AFTER_OPEN;
            lastGoodExpected = expected;
           // lastGoodToken = currTok;
            break;

            //close parentheses
          case ')':
            //requires space after it.
 //           if(currTok.length() > 1) throw new Exception("there must be a space before and after each parentheses!");
             //are we expecting this token?
             if(!(expected == Expected.CLOSE_OPERATOR_NOTHING || expected == Expected.OPEN_CLOSE_LEFT))
               throw new Exception("Did not expect to find ')' after " + goodCondition +
                                  " " + leftHand + " " + relation + " " + rightHand);
             //so far it is legal
             numOpen--;
             //do we have too many closing parentheses?
             if(numOpen < 0) throw new Exception ("Illegal expression, paretheses order is invalid!");

             goodCondition += " " + currTok;
             expected = Expected.AFTER_CLOSE;
             lastGoodExpected = expected;
          //   lastGoodToken = currTok;
             break;


             //this is an operator
          case '&':
         case '|':
           //are we expecting an operator now?
               if(!(expected == Expected.CLOSE_OPERATOR_NOTHING))
                 throw new Exception ("Did not expect to find operator " + currTok + " after " + goodCondition+
                              " " + leftHand + " " + relation + " " + rightHand);

     //parsing the next token, expecting to find a delimiter '&' or '|'
         secondChar = tok.nextToken();
        if(!currTok.equals(secondChar))
            //is it a legal operator?
            //if(!(currTok.equals(and) || currTok.equals(or)))
               throw new Exception ("Found illegal operator: " + currTok + secondChar.charAt(0));

             if(goodCondition.length() != 0 && goodCondition.charAt(goodCondition.length() -1) != '(')
                   operator = currTok + secondChar.charAt(0); //we are not adding the operator yet.
                   //if the following sub expression is valid it will be added.
                   //or if a '(' comes right after this currTok.

                   expected = Expected.AFTER_OPERATOR;
                 //  lastGoodToken = currTok;
                   break;
         case '=':
         case '!':
         case '<':
         case '>':
                  //are we expecting a relation?
                  if(!(expected == Expected.RELATION))
                    throw new Exception ("Did not expect to find relation " + currTok + " after " + goodCondition +
                                 " " + leftHand + " " + relation + " " + rightHand);

          //parsing the rest of the relation. if there is:
           secondChar = tok.nextToken();

           if(secondChar.length() != 1 )
             throw new Exception ("Found illegal relation: " + currTok );

           char c = secondChar.charAt(0);

          if(c == '=')
            currTok += secondChar;
           //if it is not a white space - it is illegal.
           else if (c != ' ' || c != '\n' || c != '\t' ||  c != '\f' || c != '\r')
             throw new Exception ("Found illegal relation: " );

                   //verifying validity of the relation
                   if (!( currTok.equals(equal) || currTok.equals("!=") || currTok.equals("<") ||
                       currTok.equals(">") || currTok.equals("<=")  || currTok.equals(">=") ))
                     throw new Exception ("Found illegal relation: " + currTok);

                   //the relation will be added if the sub expression is relevant and valid.
                   relation = currTok;
                   expected = Expected.AFTER_RELATION;
                   break;




         default: //meaning this is either left or right hand operands

                  //are we expecting an operand?
                  if(!(expected == Expected.RIGHT || expected == Expected.OPEN_CLOSE_LEFT || expected == Expected.OPEN_LEFT))
                    throw new Exception ("Did not expect to find operand " + currTok + " after " + goodCondition +
                                  " " + leftHand + " " + relation + " " + rightHand);

                  if(expected == Expected.RIGHT){ //it is a right hand operand

                    //if it is in the map of attributes
                    rightHand = currTok;
                    if(map.containsKey(currTok)){
                      //switching between right to left operands
                      rightHand = leftHand;
                      leftHand = currTok;
                    }//if contains currTok

                    //testing that the right hadn operand is really a value.
                    //trying first to parse a number...
                    try{
                      double d = Double.parseDouble(rightHand);
                    }
                    catch(Exception e){
                      //if it did not work looking for ' at the beginning and end.
                      if(rightHand.charAt(0) != '\'' || rightHand.charAt(rightHand.length() -1) != '\'')
                        throw new Exception ("Found illegal value - " + rightHand + ". Nominal values should be surounded with single quotes!");

                    }


                    //now verifying that the left hand is indeed an attribute
                    if(map.containsKey(leftHand)){

                      //testing that value is between ' ' was done before
                      /*char first = rightHand.charAt(0);
                      char last = rightHand.charAt(rightHand.length()-1);

                      if(!(first == '\'' && last == '\'' ))
                        throw new Exception ("\nValues have to be surounded by single quotes (\')\n");
*/
                      //adding to good condition operator right hand relation and left hand
                      goodCondition += " " + operator + " " + leftHand + " " + relation + " " + rightHand;
                      operator = "";
                      leftHand = "";
                      rightHand = "";
                      relation = "";

                      expected = Expected.AFTER_RIGHT;
                      lastGoodExpected = expected;
                   //   lastGoodToken = currTok;
                    }//if contains leftHand
                    else { //the sub expression is not relevant

                      System.out.println("The attribute " + rightHand + "nor " + leftHand +
                      " in the sub expression: " +  leftHand + " " +
                                         relation + " " + rightHand +
                                         "could not be found in the attribute map.\n" +
                                         "The expression is being removed");

                      //restoring the last good expected
                        expected = Expected.AFTER_MAL_EXPRESSION;
                        lastGoodExpected = expected;
                        operator = "";
                     leftHand = "";
                     rightHand = "";
                     relation = "";


                    }//else contains leftHand

                  }//if (right hand operand)

                  else{ //this is a left hand operand

                    leftHand = currTok;
                    expected = Expected.AFTER_LEFT;
                  }//else right


                  break;
        }//switch
    }//while

    if(numOpen > 0 )
      throw new Exception("\n\nThe expression " + goodCondition + " has too many opening parentheses\n");

    return goodCondition;

   }//parseExpression




}//ExpressionParser