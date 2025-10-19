package objects.utils;

import java.util.Vector;

public class CaltechEval {
   String expression;
   String[] Operators;
   int[] OperatorsArgs;
   boolean[] OperatorsLeftRight;
   int[] OperatorsPrecedence;
   int NOPERATORS;

   public static void main(String[] args) {
      if (args == null || args.length != 1) {
         System.exit(0);
      }

      CaltechEval m = new CaltechEval(args[0]);
      System.out.println(args[0].replace(" ", "") + " = " + m.evaluate());
   }

   public CaltechEval(String s) {
      this.init();
      this.expression = s.replace(" ", "");
   }

   private void init() {
      this.NOPERATORS = 19;
      this.Operators = new String[this.NOPERATORS];
      this.OperatorsArgs = new int[this.NOPERATORS];
      this.OperatorsLeftRight = new boolean[this.NOPERATORS];
      this.OperatorsPrecedence = new int[this.NOPERATORS];
      this.Operators[0] = "+";
      this.OperatorsArgs[0] = 2;
      this.OperatorsLeftRight[0] = true;
      this.OperatorsPrecedence[0] = 2;
      this.Operators[1] = "-";
      this.OperatorsArgs[1] = 2;
      this.OperatorsLeftRight[1] = true;
      this.OperatorsPrecedence[1] = 2;
      this.Operators[2] = "*";
      this.OperatorsArgs[2] = 2;
      this.OperatorsLeftRight[2] = true;
      this.OperatorsPrecedence[2] = 3;
      this.Operators[3] = "/";
      this.OperatorsArgs[3] = 2;
      this.OperatorsLeftRight[3] = true;
      this.OperatorsPrecedence[3] = 3;
      this.Operators[4] = "sin";
      this.OperatorsArgs[4] = 1;
      this.OperatorsLeftRight[4] = false;
      this.OperatorsPrecedence[4] = 10;
      this.Operators[5] = "cos";
      this.OperatorsArgs[5] = 1;
      this.OperatorsLeftRight[5] = false;
      this.OperatorsPrecedence[5] = 10;
      this.Operators[6] = "tan";
      this.OperatorsArgs[6] = 1;
      this.OperatorsLeftRight[6] = false;
      this.OperatorsPrecedence[6] = 10;
      this.Operators[7] = "exp";
      this.OperatorsArgs[7] = 1;
      this.OperatorsLeftRight[7] = false;
      this.OperatorsPrecedence[7] = 10;
      this.Operators[8] = "sqrt";
      this.OperatorsArgs[8] = 1;
      this.OperatorsLeftRight[8] = false;
      this.OperatorsPrecedence[8] = 10;
      this.Operators[9] = "asin";
      this.OperatorsArgs[9] = 1;
      this.OperatorsLeftRight[9] = false;
      this.OperatorsPrecedence[9] = 10;
      this.Operators[10] = "acos";
      this.OperatorsArgs[10] = 1;
      this.OperatorsLeftRight[10] = false;
      this.OperatorsPrecedence[10] = 10;
      this.Operators[11] = "atan";
      this.OperatorsArgs[11] = 1;
      this.OperatorsLeftRight[11] = false;
      this.OperatorsPrecedence[11] = 10;
      this.Operators[12] = "atan2";
      this.OperatorsArgs[12] = 2;
      this.OperatorsLeftRight[12] = false;
      this.OperatorsPrecedence[12] = 10;
      this.Operators[13] = "max";
      this.OperatorsArgs[13] = 2;
      this.OperatorsLeftRight[13] = false;
      this.OperatorsPrecedence[13] = 10;
      this.Operators[14] = "min";
      this.OperatorsArgs[14] = 2;
      this.OperatorsLeftRight[14] = false;
      this.OperatorsPrecedence[14] = 10;
      this.Operators[15] = ",";
      this.OperatorsArgs[15] = 2;
      this.OperatorsLeftRight[15] = true;
      this.OperatorsPrecedence[15] = 1;
      this.Operators[16] = "u";
      this.OperatorsArgs[16] = 1;
      this.OperatorsLeftRight[16] = false;
      this.OperatorsPrecedence[16] = 4;
      this.Operators[17] = "d";
      this.OperatorsArgs[17] = 1;
      this.OperatorsLeftRight[17] = false;
      this.OperatorsPrecedence[17] = 4;
      this.Operators[18] = "n";
      this.OperatorsArgs[18] = 1;
      this.OperatorsLeftRight[18] = false;
      this.OperatorsPrecedence[18] = 11;
   }

   public double evaluate() {
      return this.eval(this.expression);
   }

   private int getOperator(String op) {
      for (int i = 0; i < this.NOPERATORS; i++) {
         if (op.equals(this.Operators[i]) && op.length() == this.Operators[i].length()) {
            return i;
         }
      }

      return -1;
   }

   private double doOp(String op, double arg1, double arg2) {
      if (op.equals("+")) {
         return arg1 + arg2;
      } else if (op.equals("-")) {
         return arg1 - arg2;
      } else if (op.equals("*")) {
         return arg1 * arg2;
      } else if (op.equals("/")) {
         return arg1 / arg2;
      } else if (op.equals("sin")) {
         return Math.sin(arg1);
      } else if (op.equals("cos")) {
         return Math.cos(arg1);
      } else if (op.equals("tan")) {
         return Math.tan(arg1);
      } else if (op.equals("exp")) {
         return Math.exp(arg1);
      } else if (op.equals("sqrt")) {
         return Math.sqrt(arg1);
      } else if (op.equals("asin")) {
         return Math.asin(arg1);
      } else if (op.equals("acos")) {
         return Math.acos(arg1);
      } else if (op.equals("atan")) {
         return Math.atan(arg1);
      } else if (op.equals("atan2")) {
         return Math.atan2(arg1, arg2);
      } else if (op.equals("max")) {
         return Math.max(arg1, arg2);
      } else if (op.equals("min")) {
         return Math.min(arg1, arg2);
      } else if (op.equals("u")) {
         return Math.ceil(arg1);
      } else if (op.equals("d")) {
         return Math.floor(arg1);
      } else if (op.equals("n")) {
         return -arg1;
      } else {
         throw new RuntimeException("Invalid operator: " + op);
      }
   }

   private boolean inOperand(char c) {
      return c >= '0' && c <= '9' || c == '.';
   }

   private Vector toTokens(String s) {
      Vector tokenStrings = new Vector(1);
      String operand = "";
      String operator = "";
      int nd = 0;
      int nt = 0;
      int i = 0;

      while (i < s.length()) {
         char c = s.charAt(i);
         char cnext = ' ';
         if (i < s.length() - 1) {
            cnext = s.charAt(i + 1);
         }

         if (nt > 0 && operator.equals("atan") && c == '2') {
            tokenStrings.addElement("atan2");
            operator = "";
            nt = 0;
            i++;
         } else if (this.inOperand(c)) {
            operand = operand + s.charAt(i);
            nd++;
            if (nt > 0) {
               tokenStrings.addElement(operator);
            }

            nt = 0;
            operator = "";
            i++;
         } else if (c != 'e' || nd <= 0 || i >= s.length() - 2 || cnext != '+' && cnext != '-' && !this.inOperand(cnext)) {
            if (c != '(' && c != ')') {
               if (nd > 0) {
                  tokenStrings.addElement(operand);
               }

               nd = 0;
               operand = "";
               operator = operator + c;
               if (!operator.equals("+") && !operator.equals("-") && !operator.equals("*") && !operator.equals("/") && !operator.equals(",")) {
                  nt++;
               } else {
                  tokenStrings.addElement(operator);
                  nt = 0;
                  operator = "";
               }

               i++;
            } else {
               if (nd > 0) {
                  tokenStrings.addElement(operand);
               }

               nd = 0;
               operand = "";
               if (nt > 0) {
                  tokenStrings.addElement(operator);
               }

               operator = c + "";
               tokenStrings.addElement(operator);
               nt = 0;
               operator = "";
               i++;
            }
         } else {
            operand = operand + c;
            operand = operand + cnext;
            nd += 2;
            nt = 0;
            i += 2;
         }
      }

      if (nd > 0) {
         tokenStrings.addElement(operand);
         nt = 0;
      }

      if (nt > 0) {
         tokenStrings.addElement(operator);
      }

      return tokenStrings;
   }

   private double eval(String s) {
      Double D = new Double(0.0);
      Vector tokens = this.toTokens(s);

      while (tokens.size() > 1) {
         tokens = this.reduceTokens(tokens);
      }

      return Double.parseDouble((String)tokens.elementAt(0));
   }

   public Vector reduceTokens(Vector tokens) {
      Double D = new Double(0.0);
      double leftValue = 0.0;

      while (tokens.indexOf("(") != -1) {
         int ib = tokens.indexOf("(");
         Vector bracketTokens = new Vector();
         int brackets = 1;

         for (int it = ib + 1; it < tokens.size(); it++) {
            String st = (String)tokens.elementAt(it);
            if (st.equals(")")) {
               brackets--;
            } else if (st.equals("(")) {
               brackets++;
            }

            if (brackets == 0) {
               for (int i3 = ib + 1; i3 < it; i3++) {
                  bracketTokens.addElement(tokens.elementAt(i3));
               }

               int startsize = bracketTokens.size();
               bracketTokens = this.reduceTokens(bracketTokens);
               int endsize = bracketTokens.size();
               int ip = ib;

               for (int i3 = 0; i3 < bracketTokens.size(); i3++) {
                  tokens.setElementAt(bracketTokens.elementAt(i3), ip++);
               }

               for (int i = 0; i < startsize - endsize + 2; i++) {
                  tokens.removeElementAt(ip);
               }
               break;
            }
         }
      }

      while (tokens.size() > 1) {
         int maxprec = 0;
         int ipos = -1;

         for (int it = 0; it < tokens.size(); it++) {
            String stx = (String)tokens.elementAt(it);
            int iop = this.getOperator(stx);
            if (iop != -1 && this.OperatorsPrecedence[iop] >= maxprec) {
               maxprec = this.OperatorsPrecedence[iop];
               ipos = it;
            }
         }

         if (ipos == -1) {
            return tokens;
         }

         String stx = (String)tokens.elementAt(ipos);
         int iop = this.getOperator(stx);
         if (this.OperatorsLeftRight[iop]) {
            if (!stx.equals(",")) {
               int ipt = ipos - 1;
               if (ipos > 0) {
                  String stleft = (String)tokens.elementAt(ipos - 1);
                  if (this.getOperator(stleft) != -1) {
                     ipt = ipos;
                     leftValue = 0.0;
                  } else {
                     leftValue = Double.parseDouble(stleft);
                  }
               } else {
                  ipt = 0;
                  leftValue = 0.0;
               }

               String stright = (String)tokens.elementAt(ipos + 1);
               double rightValue1 = Double.parseDouble(stright);
               double value = this.doOp(stx, leftValue, rightValue1);
               stright = value + "";
               tokens.setElementAt(stright, ipt);
               tokens.removeElementAt(ipt + 1);
               if (ipos > 0 && ipt != ipos) {
                  tokens.removeElementAt(ipt + 1);
               }
            } else {
               tokens.removeElementAt(ipos);
            }
         } else {
            int nargs = this.OperatorsArgs[iop];
            String stright = (String)tokens.elementAt(ipos + 1);
            double rightValue1 = Double.parseDouble(stright);
            double rightValue2 = 0.0;
            if (nargs > 1) {
               stright = (String)tokens.elementAt(ipos + 2);
               rightValue2 = Double.parseDouble(stright);
            }

            double value = this.doOp(stx, rightValue1, rightValue2);
            stright = value + "";
            tokens.setElementAt(stright, ipos);
            tokens.removeElementAt(ipos + 1);
            if (nargs > 1) {
               tokens.removeElementAt(ipos + 1);
            }
         }
      }

      return tokens;
   }
}
