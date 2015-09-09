package A2;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class SemanticAnalyzer {
  
  private static final Hashtable<String, Vector<SymbolTableItem>> symbolTable = new Hashtable<String, Vector<SymbolTableItem>>();
  private static final Stack stack = new Stack();
  
  // create here a data structure for the cube of types
  
  public static Hashtable<String, Vector<SymbolTableItem>> getSymbolTable() {
    return symbolTable;
  }
  
  public static String doesExist(String id) {
	  Enumeration item = symbolTable.keys();
	  String name = "";
	  int i = 0;
	  String t = "";
	  while(item.hasMoreElements()) {
		  name = (String)item.nextElement();
	      if (name.equals(id)) {
	    	  t = ((SymbolTableItem)(symbolTable.get(name).get(0))).getType();
	    	  return t;
	      }
	      i++;
	  }
	  return "error";
  }
  
  public static String getValue(String id) {
	  Enumeration item = symbolTable.keys();
	  String name = "";
	  int i = 0;
	  String t = "";
	  while(item.hasMoreElements()) {
		  name = (String)item.nextElement();
	      if (name.equals(id)) {
	    	  t = ((SymbolTableItem)(symbolTable.get(name)).get(0)).getValue();
	    	  return t;
	      }
	      i++;
	  }
	  return "error";
  }
  
  public static void checkVariable(String type, String id, int line, Gui gui) {
	  Parser.gui = gui;
	  Enumeration items = symbolTable.keys();
	  Boolean b = false;
	  String name = "";
	  
    // A. search the id in the symbol table
	  while(items.hasMoreElements()) {
		  name = (String)items.nextElement();
	      if (name.equals(id)) 
	    	  b = true;
	    }

    // B. if !exist then insert: type, scope=global, value={0, false, "", '')
	  if (b == false) {
		  Vector v = new Vector();
		  v.add(new SymbolTableItem(type, "global", ""));
		  symbolTable.put(id, v);
	  }

    // C. else error: "variable id is already defined"
	  else 
		  error(gui, 1, line, id);
  }

  public static void pushStack(String type) {
  
    // push type in the stack
	  stack.push(type);
  }
  
  public static String popStack() {
    String result="";
    // pop a value from the stack
    result = (String) stack.pop();
    return result;
  }
  
  
  public static String calculateCube(String type, String operator) {
    String result="";
    // unary operator - 
    if (operator.equals("-")) {
    	if (type.equals("INTEGER"))
    		result = "INTEGER";
    	else if (type.equals("FLOAT"))
    		result = "FLOAT";
    	else
    		result = "error";
    }
    // unary operator !
    if (operator.equals("!")) {
    	if (type.equals("BOOLEAN"))
    		result = "BOOLEAN";
    	else
    		result = "error";
    }
    
    return result;
  }

  public static String calculateCube(String type1, String type2, String operator) {
    String result="";
    // binary op -, *, /
    if (operator.equals("-") || operator.equals("*") || operator.equals("/")) {
    	if (type1.equals("INTEGER") && type2.equals("INTEGER"))
    		result = "INTEGER";
    	else if (type1.equals("FLOAT") && type2.equals("INTEGER") ||
    			type1.equals("INTEGER") && type2.equals("FLOAT") ||
    			type1.equals("FLOAT") && type2.equals("FLOAT"))
    		result = "FLOAT";
    	else
    		result = "error";
    }
    // binary op +
    if (operator.equals("+")) {
    	if (type1.equals("INTEGER") && type2.equals("INTEGER"))
    		result = "INTEGER";
    	else if (type1.equals("FLOAT") && type2.equals("INTEGER") ||
    			type1.equals("FLOAT") && type2.equals("FLOAT") ||
    			type1.equals("INTEGER") && type2.equals("FLOAT"))
    		result = "FLOAT";
    	else if (type1.equals("STRING") && (type2.equals("INTEGER") || type2.equals("FLOAT") ||
    			type2.equals("CHARACTER") || type2.equals("STRING") || type2.equals("BOOLEAN") ||
    			type2.equals("BINARY")))
    		result = "STRING";
    	else if (type2.equals("STRING") && (type1.equals("INTEGER") || type1.equals("FLOAT") ||
    			type1.equals("CHARACTER") || type1.equals("STRING") || type1.equals("BOOLEAN") ||
    			type1.equals("BINARY")))
    		result = "STRING";
    	else
    		result = "error";		
    }
    // binary op <, >
    if (operator.equals("<") || operator.equals(">")) {
    	if ((type1.equals("INTEGER") || type1.equals("FLOAT")) && (type2.equals("INTEGER") || type2.equals("FLOAT")))
    		result = "BOOLEAN";
    	else
    		result = "error";
    }
    // binary op !=, ==
    if (operator.equals("!=") || operator.equals("==")) {
    	if ((type1.equals("INTEGER") || type1.equals("FLOAT")) && (type2.equals("INTEGER") || type2.equals("FLOAT")))
    		result = "BOOLEAN";
    	else if ((type1.equals("CHARACTER") && type2.equals("CHARACTER")) || (type1.equals("STRING") && type2.equals("STRING")) ||
    		(type1.equals("BOOLEAN") && type2.equals("BOOLEAN")))
    		result = "BOOLEAN";
    	else
    		result = "error";
    }
    // binary op &, |
    if (operator.equals("&") || operator.equals("|")) {
    	if (type1.equals("BOOLEAN") && type2.equals("BOOLEAN"))
    		result = "BOOLEAN";
    	else
    		result = "error";
    }
    // binary op =
    if (operator.equals("=")) {
    	if ((type1.equals("INTEGER") && type2.equals("INTEGER")) ||
    			type1.equals("INTEGER") && type2.equals("FLOAT") ||
    			type1.equals("FLOAT") && type2.equals("FLOAT") ||
    			type1.equals("CHARACTER") && type2.equals("CHARACTER") ||
    			type1.equals("STRING") && type2.equals("STRING") ||
    			type1.equals("BOOLEAN") && type2.equals("BOOLEAN") ||
    			type1.equals("VOID") && type2.equals("VOID"))
    		result = "ok";
    	else
    		result = "error";
    }
    return result;
  }
  
  public static void error(Gui gui, int err, int n, String info) {
    switch (err) {
    	case 0:
    		gui.writeConsole("Line " + n + ": variable <" + info + "> not found");
    		break;
    	case 1: 
    		gui.writeConsole("Line " + n + ": variable <" + info + "> is already defined"); 
    		break;
    	case 2: 
    		gui.writeConsole("Line " + n + ": incompatible types: type mismatch"); 
    		break;
    	case 3: 
    		gui.writeConsole("Line " + n + ": incompatible types: expected boolean"); 
    		break;

    }
  }
  
}
