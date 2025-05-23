/*
 * SSMathParser.java
 * 
 * Copyright 2024-2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

/* SSMathParser.java */
/* Generated By:JavaCC: Do not edit this line. SSMathParser.java */
package parse;
import parse.tree.*;
import java.io.StringReader;

public class SSMathParser implements SSMathParserConstants {

        public static TreeNode parseString(String s) throws Exception {
                StringReader r = new StringReader(s);
                SSMathParser parser = new SSMathParser(r);
                TreeNode node = parser.getNode();
                return node;
        }

        public static void main(String[] args) {
                System.out.println("Init Parser...");

                try {
                        System.out.println(SSMathParser.parseString("x").toString());
                } catch(Exception e) {
                        // nothing
                }
        }

  final public void parse() throws ParseException {Token s;
        TreeNode root;
    label_1:
    while (true) {
      if (jj_2_1(4)) {
        ;
      } else {
        break label_1;
      }
      if (jj_2_4(4)) {
        root = mathStmt();
System.out.println(root.toString()); System.out.println("Value : " + root.getValue());
      } else if (jj_2_5(4)) {
        jj_consume_token(EOL);
      } else if (jj_2_6(4)) {
        if (jj_2_2(4)) {
          jj_consume_token(0);
        } else if (jj_2_3(4)) {
          jj_consume_token(EXIT);
        } else {
          jj_consume_token(-1);
          throw new ParseException();
        }
{if ("" != null) return;}
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
}

  final public TreeNode getNode() throws ParseException {TreeNode root;
    root = mathStmt();
{if ("" != null) return root;}
    throw new Error("Missing return statement in function");
}

  final public TreeNode mathStmt() throws ParseException {Token s;
        TreeNode root;
    root = plusStmt();
{if ("" != null) return root;}
    throw new Error("Missing return statement in function");
}

  final public TreeNode bktStmt() throws ParseException {Token s;
        TreeNode a, b;
    jj_consume_token(BL);
    a = mathStmt();
    jj_consume_token(BR);
{if ("" != null) return a;}
    throw new Error("Missing return statement in function");
}

  final public TreeNode plusStmt() throws ParseException {Token s;
        Double d = 0.0, d2 = 0.0;
        TreeNode a;
        TreeNode b = null;
    a = multStmt();
TreeNode sumNode = new TreeNode(TreeOperator.forName("+"));
                sumNode.addChild(a);
    label_2:
    while (true) {
      if (jj_2_7(4)) {
        ;
      } else {
        break label_2;
      }
      b = multStmt();
sumNode.addChild(b);
    }
if (b != null) {
                        {if ("" != null) return sumNode;}
                } else {
                        {if ("" != null) return a;}
                }
    throw new Error("Missing return statement in function");
}

  final public TreeNode multStmt() throws ParseException {Token s;
        Double d = 0.0, d2 = 0.0;
        TreeNode a;
        TreeNode b = null;
    if (jj_2_8(4)) {
      a = divStmt();
    } else if (jj_2_9(4)) {
      a = unaryStmt();
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
TreeNode multNode = new TreeNode(TreeOperator.forName("*"));
                multNode.addChild(a);
    label_3:
    while (true) {
      if (jj_2_10(4)) {
        ;
      } else {
        break label_3;
      }
      jj_consume_token(TIMES);
      if (jj_2_11(4)) {
        b = divStmt();
      } else if (jj_2_12(4)) {
        b = unaryStmt();
      } else {
        jj_consume_token(-1);
        throw new ParseException();
      }
multNode.addChild(b);
    }
if (b != null) {
                        {if ("" != null) return multNode;}
                } else {
                        {if ("" != null) return a;}
                }
    throw new Error("Missing return statement in function");
}

  final public TreeNode divStmt() throws ParseException {Token s;
        Double d = 0.0, d2 = 0.0;
        TreeNode a;
        TreeNode b = null;
    a = powStmt();
    if (jj_2_13(4)) {
      jj_consume_token(DIV);
      b = powStmt();
    } else {
      ;
    }
if (b != null) {
                        TreeNode divNode = new TreeNode(TreeOperator.forName("/"));
                        divNode.addChild(a);
                        divNode.addChild(b);
                        {if ("" != null) return divNode;}
                } else {
                        {if ("" != null) return a;}
                }
    throw new Error("Missing return statement in function");
}

  final public TreeNode powStmt() throws ParseException {Token s;
        Double d = 0.0, d2 = 0.0;
        TreeNode a;
        TreeNode b = null;
    a = unaryStmt();
    if (jj_2_14(4)) {
      jj_consume_token(POW);
      b = unaryStmt();
    } else {
      ;
    }
if (b != null) {
                        TreeNode powNode = new TreeNode(TreeOperator.forName("^"));
                        powNode.addChild(a);
                        powNode.addChild(b);
                        {if ("" != null) return powNode;}
                } else {
                        {if ("" != null) return a;}
                }
    throw new Error("Missing return statement in function");
}

// TODO : function statements
  final public 
TreeNode unaryStmt() throws ParseException {TreeNode a;
        int minusCount = 0;
    if (jj_2_17(4)) {
      label_4:
      while (true) {
        if (jj_2_15(4)) {
          ;
        } else {
          break label_4;
        }
        jj_consume_token(PLUS);
      }
      a = doubleStmt();
{if ("" != null) return a;}
    } else if (jj_2_18(4)) {
      label_5:
      while (true) {
        jj_consume_token(MINUS);
minusCount++;
        if (jj_2_16(4)) {
          ;
        } else {
          break label_5;
        }
      }
      a = doubleStmt();
if (minusCount % 2 != 0) {
                        TreeNode negNode = new TreeNode(TreeOperator.forName("-"));
                        negNode.addChild(a);
                        {if ("" != null) return negNode;}
                } else {
                        {if ("" != null) return a;}
                }
                minusCount = 1;
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  final public TreeNode fnStmt() throws ParseException {Token s;
        TreeNode arg;
    s = jj_consume_token(FN);
    arg = bktStmt();
TreeNode node = new TreeNode(FnOperator.forName(s.toString()));
                node.addChild(arg);
                {if ("" != null) return node;}
    throw new Error("Missing return statement in function");
}

  final public TreeNode doubleStmt() throws ParseException {Token s;
        Constant d = new Constant(0.0);
        Variable x;
        TreeNode node = new TreeNode(d);
    if (jj_2_19(4)) {
      s = jj_consume_token(DIGIT);
try {
                                d = new Constant(Double.parseDouble(s.toString()));
                                node = new TreeNode(d);
                        } catch (Exception e) {}
                        {if ("" != null) return node;}
    } else if (jj_2_20(4)) {
      s = jj_consume_token(NUMBER);
try {
                                d = new Constant(Double.parseDouble(s.toString()));
                                node = new TreeNode(d);
                        } catch (Exception e) {}
                        {if ("" != null) return node;}
    } else if (jj_2_21(4)) {
      s = jj_consume_token(LETTER);
// Variable
                        try {
                                x = new Variable(s.toString());
                                node = new TreeNode(x);
                        } catch (Exception e) {}
                        {if ("" != null) return node;}
    } else if (jj_2_22(4)) {
      node = fnStmt();
{if ("" != null) return node;}
    } else if (jj_2_23(4)) {
      node = bktStmt();
{if ("" != null) return node;}
    } else {
      jj_consume_token(-1);
      throw new ParseException();
    }
    throw new Error("Missing return statement in function");
}

  private boolean jj_2_1(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_1()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  private boolean jj_2_2(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_2()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  private boolean jj_2_3(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_3()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  private boolean jj_2_4(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_4()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  private boolean jj_2_5(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_5()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  private boolean jj_2_6(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_6()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  private boolean jj_2_7(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_7()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  private boolean jj_2_8(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_8()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  private boolean jj_2_9(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_9()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  private boolean jj_2_10(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_10()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  private boolean jj_2_11(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_11()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(10, xla); }
  }

  private boolean jj_2_12(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_12()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(11, xla); }
  }

  private boolean jj_2_13(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_13()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(12, xla); }
  }

  private boolean jj_2_14(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_14()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(13, xla); }
  }

  private boolean jj_2_15(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_15()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(14, xla); }
  }

  private boolean jj_2_16(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_16()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(15, xla); }
  }

  private boolean jj_2_17(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_17()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(16, xla); }
  }

  private boolean jj_2_18(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_18()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(17, xla); }
  }

  private boolean jj_2_19(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_19()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(18, xla); }
  }

  private boolean jj_2_20(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_20()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(19, xla); }
  }

  private boolean jj_2_21(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_21()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(20, xla); }
  }

  private boolean jj_2_22(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_22()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(21, xla); }
  }

  private boolean jj_2_23(int xla)
 {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return (!jj_3_23()); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(22, xla); }
  }

  private boolean jj_3_3()
 {
    if (jj_scan_token(EXIT)) return true;
    return false;
  }

  private boolean jj_3_8()
 {
    if (jj_3R_divStmt_150_9_8()) return true;
    return false;
  }

  private boolean jj_3R_multStmt_126_9_7()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_8()) {
    jj_scanpos = xsp;
    if (jj_3_9()) return true;
    }
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_10()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3R_mathStmt_79_9_6()
 {
    if (jj_3R_plusStmt_102_9_14()) return true;
    return false;
  }

  private boolean jj_3R_powStmt_172_9_10()
 {
    if (jj_3R_unaryStmt_193_9_9()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_14()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_23()
 {
    if (jj_3R_bktStmt_89_9_13()) return true;
    return false;
  }

  private boolean jj_3_2()
 {
    if (jj_scan_token(0)) return true;
    return false;
  }

  private boolean jj_3R_fnStmt_218_9_12()
 {
    if (jj_scan_token(FN)) return true;
    if (jj_3R_bktStmt_89_9_13()) return true;
    return false;
  }

  private boolean jj_3_22()
 {
    if (jj_3R_fnStmt_218_9_12()) return true;
    return false;
  }

  private boolean jj_3_6()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_2()) {
    jj_scanpos = xsp;
    if (jj_3_3()) return true;
    }
    return false;
  }

  private boolean jj_3_5()
 {
    if (jj_scan_token(EOL)) return true;
    return false;
  }

  private boolean jj_3_21()
 {
    if (jj_scan_token(LETTER)) return true;
    return false;
  }

  private boolean jj_3_13()
 {
    if (jj_scan_token(DIV)) return true;
    if (jj_3R_powStmt_172_9_10()) return true;
    return false;
  }

  private boolean jj_3_7()
 {
    if (jj_3R_multStmt_126_9_7()) return true;
    return false;
  }

  private boolean jj_3_4()
 {
    if (jj_3R_mathStmt_79_9_6()) return true;
    return false;
  }

  private boolean jj_3_1()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_4()) {
    jj_scanpos = xsp;
    if (jj_3_5()) {
    jj_scanpos = xsp;
    if (jj_3_6()) return true;
    }
    }
    return false;
  }

  private boolean jj_3_12()
 {
    if (jj_3R_unaryStmt_193_9_9()) return true;
    return false;
  }

  private boolean jj_3_16()
 {
    if (jj_scan_token(MINUS)) return true;
    return false;
  }

  private boolean jj_3_20()
 {
    if (jj_scan_token(NUMBER)) return true;
    return false;
  }

  private boolean jj_3R_divStmt_150_9_8()
 {
    if (jj_3R_powStmt_172_9_10()) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_13()) jj_scanpos = xsp;
    return false;
  }

  private boolean jj_3_18()
 {
    Token xsp;
    if (jj_3_16()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_16()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_doubleStmt_233_9_11()) return true;
    return false;
  }

  private boolean jj_3R_plusStmt_102_9_14()
 {
    if (jj_3R_multStmt_126_9_7()) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_7()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  private boolean jj_3_19()
 {
    if (jj_scan_token(DIGIT)) return true;
    return false;
  }

  private boolean jj_3_11()
 {
    if (jj_3R_divStmt_150_9_8()) return true;
    return false;
  }

  private boolean jj_3_15()
 {
    if (jj_scan_token(PLUS)) return true;
    return false;
  }

  private boolean jj_3R_unaryStmt_193_9_9()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_17()) {
    jj_scanpos = xsp;
    if (jj_3_18()) return true;
    }
    return false;
  }

  private boolean jj_3_17()
 {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_15()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_doubleStmt_233_9_11()) return true;
    return false;
  }

  private boolean jj_3_14()
 {
    if (jj_scan_token(POW)) return true;
    if (jj_3R_unaryStmt_193_9_9()) return true;
    return false;
  }

  private boolean jj_3R_doubleStmt_233_9_11()
 {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_19()) {
    jj_scanpos = xsp;
    if (jj_3_20()) {
    jj_scanpos = xsp;
    if (jj_3_21()) {
    jj_scanpos = xsp;
    if (jj_3_22()) {
    jj_scanpos = xsp;
    if (jj_3_23()) return true;
    }
    }
    }
    }
    return false;
  }

  private boolean jj_3_9()
 {
    if (jj_3R_unaryStmt_193_9_9()) return true;
    return false;
  }

  private boolean jj_3_10()
 {
    if (jj_scan_token(TIMES)) return true;
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_11()) {
    jj_scanpos = xsp;
    if (jj_3_12()) return true;
    }
    return false;
  }

  private boolean jj_3R_bktStmt_89_9_13()
 {
    if (jj_scan_token(BL)) return true;
    if (jj_3R_mathStmt_79_9_6()) return true;
    if (jj_scan_token(BR)) return true;
    return false;
  }

  /** Generated Token Manager. */
  public SSMathParserTokenManager token_source;
  SimpleCharStream jj_input_stream;
  /** Current token. */
  public Token token;
  /** Next token. */
  public Token jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  private int jj_gen;
  final private int[] jj_la1 = new int[0];
  static private int[] jj_la1_0;
  static {
	   jj_la1_init_0();
	}
	private static void jj_la1_init_0() {
	   jj_la1_0 = new int[] {};
	}
  final private JJCalls[] jj_2_rtns = new JJCalls[23];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  /** Constructor with InputStream. */
  public SSMathParser(java.io.InputStream stream) {
	  this(stream, null);
  }
  /** Constructor with InputStream and supplied encoding */
  public SSMathParser(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream = new SimpleCharStream(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source = new SSMathParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream) {
	  ReInit(stream, null);
  }
  /** Reinitialise. */
  public void ReInit(java.io.InputStream stream, String encoding) {
	 try { jj_input_stream.ReInit(stream, encoding, 1, 1); } catch(java.io.UnsupportedEncodingException e) { throw new RuntimeException(e); }
	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < 0; i++) jj_la1[i] = -1;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor. */
  public SSMathParser(java.io.Reader stream) {
	 jj_input_stream = new SimpleCharStream(stream, 1, 1);
	 token_source = new SSMathParserTokenManager(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(java.io.Reader stream) {
	if (jj_input_stream == null) {
	   jj_input_stream = new SimpleCharStream(stream, 1, 1);
	} else {
	   jj_input_stream.ReInit(stream, 1, 1);
	}
	if (token_source == null) {
 token_source = new SSMathParserTokenManager(jj_input_stream);
	}

	 token_source.ReInit(jj_input_stream);
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Constructor with generated Token Manager. */
  public SSMathParser(SSMathParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  /** Reinitialise. */
  public void ReInit(SSMathParserTokenManager tm) {
	 token_source = tm;
	 token = new Token();
	 jj_ntk = -1;
	 jj_gen = 0;
	 for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  private Token jj_consume_token(int kind) throws ParseException {
	 Token oldToken;
	 if ((oldToken = token).next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 if (token.kind == kind) {
	   jj_gen++;
	   if (++jj_gc > 100) {
		 jj_gc = 0;
		 for (int i = 0; i < jj_2_rtns.length; i++) {
		   JJCalls c = jj_2_rtns[i];
		   while (c != null) {
			 if (c.gen < jj_gen) c.first = null;
			 c = c.next;
		   }
		 }
	   }
	   return token;
	 }
	 token = oldToken;
	 jj_kind = kind;
	 throw generateParseException();
  }

  @SuppressWarnings("serial")
  static private final class LookaheadSuccess extends java.lang.Error {
    @Override
    public Throwable fillInStackTrace() {
      return this;
    }
  }
  static private final LookaheadSuccess jj_ls = new LookaheadSuccess();
  private boolean jj_scan_token(int kind) {
	 if (jj_scanpos == jj_lastpos) {
	   jj_la--;
	   if (jj_scanpos.next == null) {
		 jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
	   } else {
		 jj_lastpos = jj_scanpos = jj_scanpos.next;
	   }
	 } else {
	   jj_scanpos = jj_scanpos.next;
	 }
	 if (jj_rescan) {
	   int i = 0; Token tok = token;
	   while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
	   if (tok != null) jj_add_error_token(kind, i);
	 }
	 if (jj_scanpos.kind != kind) return true;
	 if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
	 return false;
  }


/** Get the next Token. */
  final public Token getNextToken() {
	 if (token.next != null) token = token.next;
	 else token = token.next = token_source.getNextToken();
	 jj_ntk = -1;
	 jj_gen++;
	 return token;
  }

/** Get the specific Token. */
  final public Token getToken(int index) {
	 Token t = token;
	 for (int i = 0; i < index; i++) {
	   if (t.next != null) t = t.next;
	   else t = t.next = token_source.getNextToken();
	 }
	 return t;
  }

  private int jj_ntk_f() {
	 if ((jj_nt=token.next) == null)
	   return (jj_ntk = (token.next=token_source.getNextToken()).kind);
	 else
	   return (jj_ntk = jj_nt.kind);
  }

  private java.util.List<int[]> jj_expentries = new java.util.ArrayList<int[]>();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
	 if (pos >= 100) {
		return;
	 }

	 if (pos == jj_endpos + 1) {
	   jj_lasttokens[jj_endpos++] = kind;
	 } else if (jj_endpos != 0) {
	   jj_expentry = new int[jj_endpos];

	   for (int i = 0; i < jj_endpos; i++) {
		 jj_expentry[i] = jj_lasttokens[i];
	   }

	   for (int[] oldentry : jj_expentries) {
		 if (oldentry.length == jj_expentry.length) {
		   boolean isMatched = true;

		   for (int i = 0; i < jj_expentry.length; i++) {
			 if (oldentry[i] != jj_expentry[i]) {
			   isMatched = false;
			   break;
			 }

		   }
		   if (isMatched) {
			 jj_expentries.add(jj_expentry);
			 break;
		   }
		 }
	   }

	   if (pos != 0) {
		 jj_lasttokens[(jj_endpos = pos) - 1] = kind;
	   }
	 }
  }

  /** Generate ParseException. */
  public ParseException generateParseException() {
	 jj_expentries.clear();
	 boolean[] la1tokens = new boolean[17];
	 if (jj_kind >= 0) {
	   la1tokens[jj_kind] = true;
	   jj_kind = -1;
	 }
	 for (int i = 0; i < 0; i++) {
	   if (jj_la1[i] == jj_gen) {
		 for (int j = 0; j < 32; j++) {
		   if ((jj_la1_0[i] & (1<<j)) != 0) {
			 la1tokens[j] = true;
		   }
		 }
	   }
	 }
	 for (int i = 0; i < 17; i++) {
	   if (la1tokens[i]) {
		 jj_expentry = new int[1];
		 jj_expentry[0] = i;
		 jj_expentries.add(jj_expentry);
	   }
	 }
	 jj_endpos = 0;
	 jj_rescan_token();
	 jj_add_error_token(0, 0);
	 int[][] exptokseq = new int[jj_expentries.size()][];
	 for (int i = 0; i < jj_expentries.size(); i++) {
	   exptokseq[i] = jj_expentries.get(i);
	 }
	 return new ParseException(token, exptokseq, tokenImage);
  }

  private boolean trace_enabled;

/** Trace enabled. */
  final public boolean trace_enabled() {
	 return trace_enabled;
  }

  /** Enable tracing. */
  final public void enable_tracing() {
  }

  /** Disable tracing. */
  final public void disable_tracing() {
  }

  private void jj_rescan_token() {
	 jj_rescan = true;
	 for (int i = 0; i < 23; i++) {
	   try {
		 JJCalls p = jj_2_rtns[i];

		 do {
		   if (p.gen > jj_gen) {
			 jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
			 switch (i) {
			   case 0: jj_3_1(); break;
			   case 1: jj_3_2(); break;
			   case 2: jj_3_3(); break;
			   case 3: jj_3_4(); break;
			   case 4: jj_3_5(); break;
			   case 5: jj_3_6(); break;
			   case 6: jj_3_7(); break;
			   case 7: jj_3_8(); break;
			   case 8: jj_3_9(); break;
			   case 9: jj_3_10(); break;
			   case 10: jj_3_11(); break;
			   case 11: jj_3_12(); break;
			   case 12: jj_3_13(); break;
			   case 13: jj_3_14(); break;
			   case 14: jj_3_15(); break;
			   case 15: jj_3_16(); break;
			   case 16: jj_3_17(); break;
			   case 17: jj_3_18(); break;
			   case 18: jj_3_19(); break;
			   case 19: jj_3_20(); break;
			   case 20: jj_3_21(); break;
			   case 21: jj_3_22(); break;
			   case 22: jj_3_23(); break;
			 }
		   }
		   p = p.next;
		 } while (p != null);

		 } catch(LookaheadSuccess ls) { }
	 }
	 jj_rescan = false;
  }

  private void jj_save(int index, int xla) {
	 JJCalls p = jj_2_rtns[index];
	 while (p.gen > jj_gen) {
	   if (p.next == null) { p = p.next = new JJCalls(); break; }
	   p = p.next;
	 }

	 p.gen = jj_gen + xla - jj_la; 
	 p.first = token;
	 p.arg = xla;
  }

  static final class JJCalls {
	 int gen;
	 Token first;
	 int arg;
	 JJCalls next;
  }

}
