package A2;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author javiergs
 */
public class Parser {

  private static int codeLine = 1;
  private static DefaultMutableTreeNode root;
  private static Vector<Token> tokens;
  private static int currentToken;
  public static Gui gui;
  private static int labelW = 0;
  private static int labelI = 0;

  public static DefaultMutableTreeNode run(Vector<Token> t, Gui gui) {
	Parser.gui = gui;
    tokens = t;
    currentToken = 0;
    root = new DefaultMutableTreeNode("program");
    CodeGenerator.clear(gui);
    //
    rule_program(root);
	CodeGenerator.addInstruction("OPR", "0", "0");
	codeLine++;
    gui.writeSymbolTable(SemanticAnalyzer.getSymbolTable());
    CodeGenerator.writeCode(gui);
    //
    return root;
  }
  
  //program
  private static void rule_program(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = null;
	  if (tokens.get(currentToken).getWord().equals("{")) {
		  node = new DefaultMutableTreeNode("{");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  node = new DefaultMutableTreeNode("body");
		  parent.add(node);
	  }
	  rule_body(node);
	  if (tokens.get(currentToken).getWord().equals("}")) {
		  node = new DefaultMutableTreeNode("}");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
  }
  
  //body
  private static void rule_body(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = null;
	  while (!tokens.get(currentToken).getWord().equals("}") && !(currentToken >= tokens.size()-1)) {
		  String name = tokens.get(currentToken).getWord();
		  //call print
		  if (tokens.get(currentToken).getWord().equals("print")) {
			  node = new DefaultMutableTreeNode("print");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_print(node);
			  if (tokens.get(currentToken).getWord().equals(";")) {
				  node = new DefaultMutableTreeNode(";");
				  parent.add(node);
				  if (currentToken < tokens.size()-1)
					  currentToken++;
			  }
		  }
		  //call assignment
		  else if (tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
			  String id = tokens.get(currentToken).getWord();
			  int line = tokens.get(currentToken).getLine();
			  String type = SemanticAnalyzer.doesExist(id);
			  if (type.equals("int"))
				  type = "INTEGER";
			  else if (type.equals("float"))
				  type = "FLOAT";
			  else if (type.equals("string"))
				  type = "STRING";
			  else if (type.equals("char"))
				  type = "CHARACTER";
			  else if (type.equals("octal"))
				  type = "OCTAL";
			  else if (type.equals("binary"))
				  type = "BINARY";
			  else if (type.equals("hexadecimal"))
				  type = "HEXADECIMAL";
			  else if (type.equals("void"))
				  type = "VOID";
			  else if (type.equals("boolean"))
				  type = "BOOLEAN";
			  else {
				  SemanticAnalyzer.error(gui, 0, line, id);
				  //SemanticAnalyzer.error(gui, 2, line, "");
			  }
			  SemanticAnalyzer.pushStack(type);
			  node = new DefaultMutableTreeNode("assignment");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_assignment(node);
			  if (tokens.get(currentToken).getWord().equals(";")) {
				  node = new DefaultMutableTreeNode(";");
				  parent.add(node);
				  if (currentToken < tokens.size()-1)
					  currentToken++;
			  }

			  if (tokens.get(currentToken).getWord().equals(")"))
				  currentToken++;
		  }
		  //call variable
		  else if (tokens.get(currentToken).getWord().equals("int")
				  | tokens.get(currentToken).getWord().equals("float")
				  | tokens.get(currentToken).getWord().equals("boolean")
				  | tokens.get(currentToken).getWord().equals("char")
				  | tokens.get(currentToken).getWord().equals("string")
				  | tokens.get(currentToken).getWord().equals("void")) {
			  node = new DefaultMutableTreeNode("variable");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_variable(node);
			  if (tokens.get(currentToken).getWord().equals(";")) {
				  node = new DefaultMutableTreeNode(";");
				  parent.add(node);
				  if (currentToken < tokens.size()-1)
					  currentToken++;
			  }
		  }
		  //call while
		  else if (tokens.get(currentToken).getWord().equals("while")) {
			  node = new DefaultMutableTreeNode("while");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_while(node);
		  }
		  //call if
		  else if (tokens.get(currentToken).getWord().equals("if")) {
			  node = new DefaultMutableTreeNode("if");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_if(node);
		  }
		  //call switch
		  else if (tokens.get(currentToken).getWord().equals("switch")) {
			  node = new DefaultMutableTreeNode("switch");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_switch(node);
		  }
		  //call return
		  else if (tokens.get(currentToken).getWord().equals("return")) {
			  node = new DefaultMutableTreeNode("return");
			  parent.add(node);
			  DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("return");
			  node.add(node1);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  if (tokens.get(currentToken).getWord().equals(";")) {
				  node = new DefaultMutableTreeNode(";");
				  parent.add(node);
				  if (currentToken < tokens.size()-1)
					  currentToken++;
			  }
			  CodeGenerator.addInstruction("OPR", "1", "0");
			  codeLine++;
		  }
	  }
  }
  
  //print
  private static void rule_print(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("print");
	  parent.add(node);
	  if (tokens.get(currentToken).getWord().equals("(")) {
		  node = new DefaultMutableTreeNode("(");
		  parent.add(node);
	  }
	  node = new DefaultMutableTreeNode("expression");
	  parent.add(node);
	  if (currentToken < tokens.size()-1)
		  currentToken++;
	  rule_expression(node);
	  if (tokens.get(currentToken).getWord().equals(")")) {
		  node = new DefaultMutableTreeNode(")");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
				  currentToken++;
	  }
	  CodeGenerator.addInstruction("OPR", "21", "0");
	  codeLine++;
  }
  
  //assignment
  private static void rule_assignment(DefaultMutableTreeNode parent) {
	  String id = tokens.get(currentToken-1).getWord();
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("identifier(" + id + ")");
	  parent.add(node);
	  if (tokens.get(currentToken).getWord().equals("=")) {
		  node = new DefaultMutableTreeNode("=");
		  parent.add(node);
	  }
	  node = new DefaultMutableTreeNode("expression");
	  parent.add(node);
	  int line = tokens.get(currentToken).getLine();
	  if (currentToken < tokens.size()-1 && !tokens.get(currentToken).getWord().equals(")"))
		  currentToken++;
	  rule_expression(node);
	  CodeGenerator.addInstruction("STO", id, "0");
	  codeLine++;
	  String x = SemanticAnalyzer.popStack();
	  String y = SemanticAnalyzer.popStack();
	  String result = SemanticAnalyzer.calculateCube(x, y, "=");
	  if (!result.equals("ok"))
		  SemanticAnalyzer.error(gui, 2, line, "");
  }
  
  //variable
  private static void rule_variable(DefaultMutableTreeNode parent) {
	  String type = tokens.get(currentToken-1).getWord();
	  String id = tokens.get(currentToken).getWord();
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode(type);
	  parent.add(node);
	  if (tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
		  SemanticAnalyzer.checkVariable(tokens.get(currentToken-1).getWord(), tokens.get(currentToken).getWord(), tokens.get(currentToken).getLine(), gui);
		  node = new DefaultMutableTreeNode("identifier(" + id + ")");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  CodeGenerator.addVariable(type, id);
	  }
  }
  private static int labelNum = 1;
  //while
  private static void rule_while(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("while");
	  parent.add(node);
	  String start = "whileS"+labelNum;
	  final int num = labelNum;
	  String end = "whileE"+labelNum;
	  labelNum++;
	  CodeGenerator.addLabel(start, codeLine);
	  if (tokens.get(currentToken).getWord().equals("(")) {
		  node = new DefaultMutableTreeNode("(");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  node = new DefaultMutableTreeNode("expression");
	  parent.add(node);
	  rule_expression(node);
	  CodeGenerator.addInstruction("JMC", "#"+end, "false");
	  codeLine++;
	  int n = tokens.get(currentToken).getLine();
	  String x = SemanticAnalyzer.popStack();
	  if(!x.equals("BOOLEAN"))
		  SemanticAnalyzer.error(gui, 3, n, "");
	  if (tokens.get(currentToken).getWord().equals(")")) {
		  node = new DefaultMutableTreeNode(")"); 
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  node = new DefaultMutableTreeNode("program");
	  parent.add(node);
	  rule_program(node);
	  CodeGenerator.addInstruction("JMP", "#"+start, "0");
	  codeLine++;
	  CodeGenerator.addLabel("whileE"+num, codeLine);
	  
  }
  private static int ifNum = 1;
  //if
  private static void rule_if(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("if");
	  parent.add(node);
	  String end = "ifE"+ifNum;
	  int num = ifNum;
	  ifNum++;
	  if (tokens.get(currentToken).getWord().equals("(")) {
		  node = new DefaultMutableTreeNode("(");
		  parent.add(node);
		  node = new DefaultMutableTreeNode("expression");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_expression(node);
		  CodeGenerator.addInstruction("JMC", "#ifE"+ifNum, "false");
		  codeLine++;
		  int n = tokens.get(currentToken).getLine();
		  String x = SemanticAnalyzer.popStack();
		  if (!x.equals("BOOLEAN"))
			  SemanticAnalyzer.error(gui, 3, n, "");
		  if (tokens.get(currentToken).getWord().equals(")")) {
			  node = new DefaultMutableTreeNode(")");
			  parent.add(node);
			  node = new DefaultMutableTreeNode("program");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
			  rule_program(node);
			  CodeGenerator.addInstruction("JMP", "#ifE"+num, "0");
			  codeLine++;
			  CodeGenerator.addLabel("ifE"+ifNum, codeLine);
			  if (tokens.get(currentToken).getWord().equals("else")) {
				  node = new DefaultMutableTreeNode("else");
				  parent.add(node);
				  node = new DefaultMutableTreeNode("program");
				  parent.add(node);
				  if (currentToken < tokens.size()-1)
					  currentToken++;
				  rule_program(node);
			  }
			  CodeGenerator.addLabel("ifE"+num, codeLine);
		  }
	  }
  }
  //switch
  private static void rule_switch(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = null;
	  DefaultMutableTreeNode parentS = parent;
	  node = new DefaultMutableTreeNode("switch");
	  parent.add(node);
	  String id = "";
	  if (tokens.get(currentToken).getWord().equals("(")) {
		  node = new DefaultMutableTreeNode("(");
		  parent.add(node);
		  currentToken++;
	  }
	  if (tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
		  node = new DefaultMutableTreeNode("identifier(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  int line = tokens.get(currentToken).getLine();
		  id = tokens.get(currentToken).getWord();
		  String type = SemanticAnalyzer.doesExist(id);
		  String value = SemanticAnalyzer.getValue(id);
		  if (type.equals("error"))
			  SemanticAnalyzer.error(gui, 0, line, id);
		  currentToken++;
	  }
	  if (tokens.get(currentToken).getWord().equals(")")) {
		  node = new DefaultMutableTreeNode(")");
		  parent.add(node);
		  currentToken++;
	  }
	  if (tokens.get(currentToken).getWord().equals("{")) {
		  node = new DefaultMutableTreeNode("{");
		  parent.add(node);
		  currentToken++;
	  }
	  if (tokens.get(currentToken).getWord().equals("case")) {
		  node = new DefaultMutableTreeNode("case");
		  parentS.add(node);
			  currentToken++;
	  }
	  rule_cases(parentS, node, id);
	  if (tokens.get(currentToken).getWord().equals("default")) {
		  node = new DefaultMutableTreeNode("default");
		  parent.add(node);
		  currentToken++;
		  CodeGenerator.addInstruction("JMP", "#labelD", "0");
		  codeLine++;
		  rule_default(node);
	  } 
	  if (tokens.get(currentToken).getWord().equals("}")) {
		  node = new DefaultMutableTreeNode("}");
		  parent.add(node);
		  currentToken++;
	  }
	  //codeLine++;
	  //codeLine++;
	  CodeGenerator.addLabel("labelES", codeLine);
  }
  private static int lNum = 1;
  //cases
  private static void rule_cases(DefaultMutableTreeNode parent, DefaultMutableTreeNode caseParent, String id) {
	  DefaultMutableTreeNode node = null;
	  DefaultMutableTreeNode nodeP = caseParent;
	  node = new DefaultMutableTreeNode("case");
	  caseParent.add(node);
	  //int labelNum = 2;
	  //int num = lNum;
	  do {
		  if (tokens.get(currentToken).getWord().equals("case")) {
			  nodeP = new DefaultMutableTreeNode("case");
			  parent.add(nodeP);
			  currentToken++;
			  node = new DefaultMutableTreeNode("case");
			  nodeP.add(node);
		  }	
		  String name = tokens.get(currentToken).getWord();
		  String type = tokens.get(currentToken).getToken();
		  String value = SemanticAnalyzer.getValue(name);
		  if (type.equals("INTEGER")) {
			  node = new DefaultMutableTreeNode("integer(" + name + ")");
			  nodeP.add(node);
			  currentToken++;
			  CodeGenerator.addInstruction("LOD", id, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("LIT", name, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("OPR", "15", "0");
			  codeLine++;
			  CodeGenerator.addInstruction("JMC", "#label"+lNum, "false");
			  codeLine++;
		  }
		  else if (type.equals("OCTAL")) {
			  node = new DefaultMutableTreeNode("octal(" + name + ")");
			  nodeP.add(node);
			  currentToken++;
			  CodeGenerator.addInstruction("LOD", id, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("LIT", name, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("OPR", "15", "0");
			  codeLine++;
			  CodeGenerator.addInstruction("JMC", "#label"+lNum, "false");
			  codeLine++;
		  }
		  else if (type.equals("HEXIDECIMAL")) {
			  node = new DefaultMutableTreeNode("hexidecimal(" + name + ")");
			  nodeP.add(node);
			  currentToken++;
			  CodeGenerator.addInstruction("LOD", id, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("LIT", name, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("OPR", "15", "0");
			  codeLine++;
			  CodeGenerator.addInstruction("JMC", "#label"+lNum, "false");
			  codeLine++;
		  }
		  else if (type.equals("BINARY")) {
			  node = new DefaultMutableTreeNode("binary(" + name + ")");
			  nodeP.add(node);
			  currentToken++;
			  CodeGenerator.addInstruction("LOD", id, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("LIT", name, "0");
			  codeLine++;
			  CodeGenerator.addInstruction("OPR", "15", "0");
			  codeLine++;
			  CodeGenerator.addInstruction("JMC", "#label"+lNum, "false");
			  codeLine++;
		  }
		  //else error
		  if (tokens.get(currentToken).getWord().equals(":")) {
			  node = new DefaultMutableTreeNode(":");
			  nodeP.add(node);
			  currentToken++;
		  }
		  rule_program(nodeP);
		  CodeGenerator.addInstruction("JMP", "#labelES", "0");
		  codeLine++;
		  CodeGenerator.addLabel("label"+lNum, codeLine);
		  lNum++;
	  }
	  while (tokens.get(currentToken).getWord().equals("case"));
  }
  //default
  private static void rule_default(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = null;
	  node = new DefaultMutableTreeNode("default");
	  parent.add(node);
	  if (tokens.get(currentToken).getWord().equals(":")) {
		  node = new DefaultMutableTreeNode(":");
		  parent.add(node);
		  currentToken++;
	  }
	  CodeGenerator.addLabel("labelD", codeLine);
	  rule_program(parent);
  }
  //expression
  private static void rule_expression(DefaultMutableTreeNode parent) {
	  int counter = 0;
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("X");
	  parent.add(node);
	  rule_x(node);
	  counter++;
	  while (tokens.get(currentToken).getWord().equals("|")) {
		  node = new DefaultMutableTreeNode("|");
		  parent.add(node);
		  node = new DefaultMutableTreeNode("X");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_x(node);
		  counter++;
	  }
	  if (counter == 2) {
		  CodeGenerator.addInstruction("OPR", "8", "0");
		  codeLine++;
		  String x = SemanticAnalyzer.popStack();
		  String y = SemanticAnalyzer.popStack();
		  String result = SemanticAnalyzer.calculateCube(x, y, "|");
		  SemanticAnalyzer.pushStack(result);
		  counter--;
	  }
  }

  //and
  private static void rule_x(DefaultMutableTreeNode parent) {
	  int counter = 0;
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("Y");
	  parent.add(node);
	  rule_y(node);
	  counter++;
	  while (tokens.get(currentToken).getWord().equals("&")) {
		  node = new DefaultMutableTreeNode("&");
		  parent.add(node);
		  node = new DefaultMutableTreeNode("Y");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_y(node);
		  counter++;
		  //if visited twice
		  if (counter == 2) {
			  CodeGenerator.addInstruction("OPR", "9", "0");
			  codeLine++;
			  String x = SemanticAnalyzer.popStack();
			  String y = SemanticAnalyzer.popStack();
			  String result = SemanticAnalyzer.calculateCube(x, y, "&");
			  SemanticAnalyzer.pushStack(result);
			  counter--;
		  }
	  }
  }
  //not
  private static void rule_y(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("R");
	  boolean b = false;
	  if (tokens.get(currentToken).getWord().equals("!")) {
		  node = new DefaultMutableTreeNode("!");
		  parent.add(node);
		  b = true;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  node = new DefaultMutableTreeNode("R");
	  parent.add(node);
	  rule_r(node);
	  if (b) {
		  CodeGenerator.addInstruction("OPR", "10", "0");
		  codeLine++;
		  String x = SemanticAnalyzer.popStack();
		  String result = SemanticAnalyzer.calculateCube(x, "!");
		  SemanticAnalyzer.pushStack(result);
	  }
  }
//relational
  private static void rule_r(DefaultMutableTreeNode parent) {
	  int counter = 0;
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("E");
	  parent.add(node);
	  rule_e(node);
	  counter++;
	  String op = "";
	  while (tokens.get(currentToken).getWord().equals("<")
			  | tokens.get(currentToken).getWord().equals(">")
			  | tokens.get(currentToken).getWord().equals("==")
			  | tokens.get(currentToken).getWord().equals("!=")) {
		  op = tokens.get(currentToken).getWord();
		  String getOpNum = "";
		  //get operator number
		  if (op.equals("<"))
			  getOpNum = "12";
		  else if (op.equals(">"))
			  getOpNum = "11";
		  else if (op.equals("=="))
			  getOpNum = "15";
		  else if (op.equals("!="))
			  getOpNum = "16";
		  node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
		  parent.add(node);
		  node = new DefaultMutableTreeNode("E");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_e(node);
		  counter++;
		  //if visited twice
		  if (counter == 2) {
			  CodeGenerator.addInstruction("OPR", getOpNum, "0");
			  codeLine++;
			  String x = SemanticAnalyzer.popStack();
			  String y = SemanticAnalyzer.popStack();
			  String result = SemanticAnalyzer.calculateCube(x, y, op);
			  SemanticAnalyzer.pushStack(result);
			  counter--;
		  }
	  }
  }
  //plus and minus
  private static void rule_e(DefaultMutableTreeNode parent) {
	  int counter = 0;
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("A");
	  parent.add(node);
	  rule_a(node);
	  counter++;
	  String op = "";
	  while (tokens.get(currentToken).getWord().equals("-")
			  | tokens.get(currentToken).getWord().equals("+")) {
		  op = tokens.get(currentToken).getWord();
		  //get operator number
		  String getOpNum = "";
		  if (op.equals("-"))
			  getOpNum = "3";
		  else if (op.equals("+"))
			  getOpNum = "2";
		  node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
		  parent.add(node);
		  node = new DefaultMutableTreeNode("A");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_a(node);
		  counter++;
		  //if visited twice
		  if (counter == 2) {
			  CodeGenerator.addInstruction("OPR", getOpNum, "0");
			  codeLine++;
			  String x = SemanticAnalyzer.popStack();
			  String y = SemanticAnalyzer.popStack();
			  String result = SemanticAnalyzer.calculateCube(x, y, op);
			  SemanticAnalyzer.pushStack(result);
			  counter--;
		  }
	  }
  }
  //div and mult
  private static void rule_a(DefaultMutableTreeNode parent) {
	  int counter = 0;
	  DefaultMutableTreeNode node = new DefaultMutableTreeNode("B");
	  parent.add(node);
	  rule_b(node);
	  counter++;
	  String op = "";
	  while (tokens.get(currentToken).getWord().equals("/")
			  || tokens.get(currentToken).getWord().equals("*")) {
		  op = tokens.get(currentToken).getWord();
		  //get operator number
		  String getOpNum = "";
		  if (op.equals("/"))
			  getOpNum = "5";
		  else if (op.equals("*"))
			  getOpNum = "4";
		  node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
		  parent.add(node);
		  node = new DefaultMutableTreeNode("B");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_b(node);
		  counter++;
		  if (counter == 2) {
			  CodeGenerator.addInstruction("OPR", getOpNum, "0");
			  codeLine++;
			  String x = SemanticAnalyzer.popStack();
			  String y = SemanticAnalyzer.popStack();
			  String result = SemanticAnalyzer.calculateCube(x, y, op);
			  SemanticAnalyzer.pushStack(result);
			  counter--;
		  }
	  }
  }
  //negative
  private static void rule_b(DefaultMutableTreeNode parent) {
	  DefaultMutableTreeNode node = null;
	  boolean b = false;
	  if (tokens.get(currentToken).getWord().equals("-")) {
		  b = true;
		  node = new DefaultMutableTreeNode(tokens.get(currentToken).getWord());
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  CodeGenerator.addInstruction("LIT", "0", "0");
		  codeLine++;
	  } 
	  node = new DefaultMutableTreeNode("C");
	  parent.add(node);
	  if (b) {
		  String x = SemanticAnalyzer.popStack();
		  String result = SemanticAnalyzer.calculateCube(x, "-");
		  SemanticAnalyzer.pushStack(result);
	  }
	  rule_c(node);
	  if (b) {
		  CodeGenerator.addInstruction("OPR", "3", "0");
		  codeLine++;
	  }
  }
  
  private static void rule_c(DefaultMutableTreeNode parent) {
	  String id = tokens.get(currentToken).getWord();
	  String type = SemanticAnalyzer.doesExist(id);
	  int n = tokens.get(currentToken).getLine();
	  DefaultMutableTreeNode node = null;
	  if (tokens.get(currentToken).getToken().equals("INTEGER")) {
		  node = new DefaultMutableTreeNode("integer(" + id + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack(tokens.get(currentToken).getToken());
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  } 
	  else if (tokens.get(currentToken).getToken().equals("OCTAL")) {
		  node = new DefaultMutableTreeNode("octal(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("INTEGER");
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  else if (tokens.get(currentToken).getToken().equals("HEXADECIMAL")) {
		  node = new DefaultMutableTreeNode("hexadecimal(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("INTEGER");
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
  	  else if (tokens.get(currentToken).getToken().equals("BINARY")) {
  		  node = new DefaultMutableTreeNode("binary(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("INTEGER");
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
      }
	  else if (tokens.get(currentToken).getWord().equals("true")) {
		  node = new DefaultMutableTreeNode("true");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("BOOLEAN");
		  CodeGenerator.addInstruction("LIT", "true", "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  else if (tokens.get(currentToken).getWord().equals("false")) {
		  node = new DefaultMutableTreeNode("false");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("BOOLEAN");
		  CodeGenerator.addInstruction("LIT", "false", "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  else if (tokens.get(currentToken).getToken().equals("STRING")) {
		  node = new DefaultMutableTreeNode("string(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("STRING");
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  else if (tokens.get(currentToken).getToken().equals("CHARACTER")) {
		  node = new DefaultMutableTreeNode("char(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("CHARACTER");
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  else if (tokens.get(currentToken).getToken().equals("FLOAT")) {
		  node = new DefaultMutableTreeNode("float(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  SemanticAnalyzer.pushStack("FLOAT");
		  CodeGenerator.addInstruction("LIT", id, "0");
		  codeLine++;
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  }
	  else if (tokens.get(currentToken).getToken().equals("IDENTIFIER")) {
		  node = new DefaultMutableTreeNode("identifier(" + tokens.get(currentToken).getWord() + ")");
		  parent.add(node);
		  CodeGenerator.addInstruction("LOD", id, "0");
		  codeLine++;
		  if (type.equals("void"))
			  SemanticAnalyzer.pushStack("VOID");
		  else if (type.equals("float"))
			  SemanticAnalyzer.pushStack("FLOAT");
		  else if (type.equals("boolean"))
			  SemanticAnalyzer.pushStack("BOOLEAN");
		  else if (type.equals("int"))
			  SemanticAnalyzer.pushStack("INTEGER");
		  else if (type.equals("char"))
			  SemanticAnalyzer.pushStack("CHARACTER");
		  else if (type.equals("string"))
			  SemanticAnalyzer.pushStack("STRING");
		  else {
			  SemanticAnalyzer.pushStack("other");
			  SemanticAnalyzer.error(gui, 0, n, id);
		  }
		  if (currentToken < tokens.size()-1)
			  currentToken++;
	  } 
	  else if (tokens.get(currentToken).getWord().equals("(")) {
		  node = new DefaultMutableTreeNode("(");
		  parent.add(node);
		  node = new DefaultMutableTreeNode("expression");
		  parent.add(node);
		  if (currentToken < tokens.size()-1)
			  currentToken++;
		  rule_expression(node);
		  if (tokens.get(currentToken).getWord().equals(")")) {
			  node = new DefaultMutableTreeNode(")");
			  parent.add(node);
			  if (currentToken < tokens.size()-1)
				  currentToken++;
		  }
	  }
  }
}