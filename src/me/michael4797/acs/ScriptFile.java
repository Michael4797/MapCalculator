package me.michael4797.acs;

import me.michael4797.parse.ByteStream;
import me.michael4797.parse.Grammar;
import me.michael4797.parse.Lexer;
import me.michael4797.parse.Parser;
import me.michael4797.parse.ParserException;
import me.michael4797.parse.ParserToken;
import me.michael4797.wad.WadFile;

public class ScriptFile {
	
	private static final Grammar acsGrammar = new Grammar();
	
	static {
		
		//The massive, sprawling, likely over-complicated ACS Grammar
		acsGrammar.addLexerIgnoreRule("WhiteSpace", "[\t\r\n \\x00]+");
		acsGrammar.addLexerIgnoreRule("SingleLineComment", "//[^\n]*\n");
		acsGrammar.addLexerIgnoreRule("MultiLineComment", "/\\*[^\\*]*(\\*[^/][^\\*]*)*\\*/");

		acsGrammar.addParserRule("GlobalList", "Global GlobalList | /-");
		acsGrammar.addParserRule("Global", "Declaration | Script | Function");
		acsGrammar.addParserRule("Declaration", "Scope Type IndexedVariableList Semicolon | Static Type VariableList Semicolon | Type VariableList Semicolon");
		acsGrammar.addParserRule("IndexedVariableList", "IndexedVariableComma IndexedVariableList | IndexedVariable");
		acsGrammar.addParserRule("IndexedVariable", "Integer Colon LeftHandOperand Equals Value | Integer Colon LeftHandOperand");
		acsGrammar.addParserRule("VariableList", "Variable Comma VariableList | Variable");
		acsGrammar.addParserRule("Variable", "LeftHandOperand Equals Value | LeftHandOperand");		
		acsGrammar.addParserRule("Value", "OpenBrace ValueList CloseBrace | RightHandExpression");
		acsGrammar.addParserRule("SingleValue", "Character | Float | Integer | QuotedString");
		acsGrammar.addParserRule("ValueList", "Value Comma ValueList | Value | /-");
		acsGrammar.addParserRule("Script", "ScriptDeclaration Block");
		acsGrammar.addParserRule("ScriptDeclaration", "ScriptHeader ScriptName ParameterList ScriptType NetType | ScriptHeader ScriptName ParameterList NetType | ScriptHeader ScriptName ScriptType NetType");
		acsGrammar.addParserRule("NetType", "Net ClientSide | Net | ClientSide | /-");
		acsGrammar.addParserRule("ScriptName", "Integer | QuotedString");
		acsGrammar.addParserRule("Function", "FunctionDeclaration Block");
		acsGrammar.addParserRule("FunctionDeclaration", "FunctionHeader FunctionType Identifier ParameterList");
		acsGrammar.addParserRule("FunctionType", "Type | Void");
		acsGrammar.addParserRule("ParameterList", "OpenParenthesis ParameterList2 CloseParenthesis | OpenParenthesis Void CloseParenthesis");
		acsGrammar.addParserRule("ParameterList2", "Type Identifier Comma ParameterList2 | Type Identifier");
		acsGrammar.addParserRule("Body", "Block | Statement");
		acsGrammar.addParserRule("Block", "OpenBrace StatementList CloseBrace");
		acsGrammar.addParserRule("StatementList", "Body StatementList | /-");
		acsGrammar.addParserRule("Statement", "ControlStatement | Declaration | Expression Semicolon | Semicolon");
		acsGrammar.addParserRule("ControlStatement", "IfStatement | SwitchStatement | ForStatement | WhileStatement | UntilStatement | DoWhileStatement | DoUntilStatement | ReturnStatement | Keyword Semicolon");
		acsGrammar.addParserRule("Keyword", "Continue | Break | Restart | Terminate | Suspend");
		acsGrammar.addParserRule("ReturnStatement", "Return RightHandExpression Semicolon | Return Semicolon");
		acsGrammar.addParserRule("IfStatement", "If OpenParenthesis RightHandExpression CloseParenthesis Body ElseIfStatement | If OpenParenthesis RightHandExpression CloseParenthesis Body Else Body | If OpenParenthesis RightHandExpression CloseParenthesis Body");
		acsGrammar.addParserRule("ElseIfStatement", "Else IfStatement");
		acsGrammar.addParserRule("WhileStatement", "While OpenParenthesis RightHandExpression CloseParenthesis Body");
		acsGrammar.addParserRule("UntilStatement", "Until OpenParenthesis RightHandExpression CloseParenthesis Body");
		acsGrammar.addParserRule("DoWhileStatement", "Do Body While OpenParenthesis RightHandExpression CloseParenthesis");
		acsGrammar.addParserRule("DoUntilStatement", "Do Body Until OpenParenthesis RightHandExpression CloseParenthesis");
		acsGrammar.addParserRule("ForStatement", "For OpenParenthesis ForInitial Semicolon RightHandExpression Semicolon AssignmentList CloseParenthesis Body");
		acsGrammar.addParserRule("ForInitial", "Type VariableList | AssignmentList");
		acsGrammar.addParserRule("AssignmentList", "Assignment Comma AssignmentList | Assignment");
		acsGrammar.addParserRule("SwitchStatement", "Switch OpenParenthesis RightHandExpression CloseParenthesis OpenBrace CaseList CloseBrace");
		acsGrammar.addParserRule("CaseList", "Case SingleValue Colon StatementList CaseList | /-");
		acsGrammar.addParserRule("Expression", "Assignment | FunctionCall");
		acsGrammar.addParserRule("Assignment", "BinaryAssignment | UnaryAssignment");
		acsGrammar.addParserRule("BinaryAssignment", "BinaryEqualsAssignment | BinaryOrAssignment | BinaryXorAssignment | BinaryAndAssignment | BinaryLeftShiftAssignment | BinaryRightShiftAssignment | BinaryPlusAssignment | BinaryMinusAssignment | BinaryMultiplyAssignment | BinaryDivideAssignment | BinaryRemainderAssignment");
		acsGrammar.addParserRule("BinaryEqualsAssignment", "LeftHandOperand Equals RightHandExpression");
		acsGrammar.addParserRule("BinaryOrAssignment", "LeftHandOperand OrEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryXorAssignment", "LeftHandOperand XorEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryAndAssignment", "LeftHandOperand AndEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryLeftShiftAssignment", "LeftHandOperand LeftShiftEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryRightShiftAssignment", "LeftHandOperand RightShiftEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryPlusAssignment", "LeftHandOperand PlusEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryMinusAssignment", "LeftHandOperand MinusEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryMultiplyAssignment", "LeftHandOperand MultiplyEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryDivideAssignment", "LeftHandOperand DivideEquals RightHandExpression");
		acsGrammar.addParserRule("BinaryRemainderAssignment", "LeftHandOperand RemainderEquals RightHandExpression");
		acsGrammar.addParserRule("RightHandExpression", "BinaryAssignment | TernaryExpression");
		acsGrammar.addParserRule("TernaryExpression", "TernaryOperation | LogicalOrExpression");
		acsGrammar.addParserRule("TernaryOperation", "LogicalOrExpression Ternary TernaryExpression Colon TernaryExpression");
		acsGrammar.addParserRule("LogicalOrExpression", "LogicalAndExpression LogicalOr LogicalOrExpression | LogicalAndExpression");
		acsGrammar.addParserRule("LogicalAndExpression", "BinaryOrExpression LogicalAnd LogicalAndExpression | BinaryOrExpression");
		acsGrammar.addParserRule("BinaryOrExpression", "BinaryXorExpression BinaryOr BinaryOrExpression | BinaryXorExpression");
		acsGrammar.addParserRule("BinaryXorExpression", "BinaryAndExpression BinaryXor BinaryXorExpression | BinaryAndExpression");
		acsGrammar.addParserRule("BinaryAndExpression", "EqualityExpression BinaryAnd BinaryAndExpression | EqualityExpression");
		acsGrammar.addParserRule("EqualityExpression", "RelationalExpression EqualityOperator EqualityExpression | RelationalExpression");
		acsGrammar.addParserRule("EqualityOperator", "LogicalEquals | NotEquals");
		acsGrammar.addParserRule("RelationalExpression", "ShiftExpression RelationalOperator RelationalExpression | ShiftExpression");
		acsGrammar.addParserRule("RelationalOperator", "GreaterThan | GreaterThanEquals | LessThan | LessThanEquals");
		acsGrammar.addParserRule("ShiftExpression", "AdditiveExpression ShiftOperator ShiftExpression | AdditiveExpression");
		acsGrammar.addParserRule("ShiftOperator", "LeftShift | RightShift");
		acsGrammar.addParserRule("AdditiveExpression", "MultiplicativeExpression AdditiveOperator AdditiveExpression | MultiplicativeExpression");
		acsGrammar.addParserRule("AdditiveOperator", "Plus | Minus");
		acsGrammar.addParserRule("MultiplicativeExpression", "UnaryExpression MultiplicativeOperator MultiplicativeExpression | UnaryExpression");
		acsGrammar.addParserRule("MultiplicativeOperator", "Multiply | Divide | Remainder");
		acsGrammar.addParserRule("UnaryExpression", "ParentheticalExpression | UnaryAssignment | UnaryLogicalNot | UnaryBinaryNot | UnaryPlus | UnaryMinus | FunctionCall | SingleValue | LeftHandOperand");
		acsGrammar.addParserRule("ParentheticalExpression", "OpenParenthesis RightHandExpression CloseParenthesis");
		acsGrammar.addParserRule("CastExpression", "CastCharacter RightHandExpression");
		acsGrammar.addParserRule("UnaryLogicalNot", "LogicalNot UnaryExpression");
		acsGrammar.addParserRule("UnaryBinaryNot", "BinaryNot UnaryExpression");
		acsGrammar.addParserRule("UnaryPlus", "Plus UnaryExpression");
		acsGrammar.addParserRule("UnaryMinus", "Minus UnaryExpression");
		acsGrammar.addParserRule("UnaryAssignment", "PostIncrement | PostDecrement | PreIncrement | PreDecrement");
		acsGrammar.addParserRule("PostIncrement", "LeftHandOperand Increment");
		acsGrammar.addParserRule("PostDecrement", "LeftHandOperand Decrement");
		acsGrammar.addParserRule("PreIncrement", "Increment LeftHandOperand");
		acsGrammar.addParserRule("PreDecrement", "Decrement LeftHandOperand");		
		acsGrammar.addParserRule("FunctionCall", "Identifier OpenParenthesis EmptyArgumentList CloseParenthesis");
		acsGrammar.addParserRule("EmptyArgumentList", "PrintArgumentList | /-");
		acsGrammar.addParserRule("PrintArgumentList", "PrintList Semicolon ArgumentList | PrintList | ArgumentList");
		acsGrammar.addParserRule("PrintList", "CastExpression Comma PrintList | CastExpression");
		acsGrammar.addParserRule("ArgumentList", "Argument Comma ArgumentList | Argument");
		acsGrammar.addParserRule("Argument", "Translation | RightHandExpression");
		acsGrammar.addParserRule("Translation", "Integer Colon Integer Equals Color Colon Color");
		acsGrammar.addParserRule("Color", "Desaturated | Blended | Tinted | Direct | Integer");
		acsGrammar.addParserRule("Desaturated", "Remainder Direct");
		acsGrammar.addParserRule("Blended", "Blend Direct");
		acsGrammar.addParserRule("Tinted", "Tint Direct");
		acsGrammar.addParserRule("Direct", "OpenBracket Integer Comma Integer Comma Integer CloseBracket");
		acsGrammar.addParserRule("LeftHandOperand", "Identifier ArrayAccess | Identifier");
		acsGrammar.addParserRule("ArrayAccess", "OpenBracket RightHandExpression CloseBracket ArrayAccess | OpenBracket RightHandExpression CloseBracket");

		acsGrammar.addLexerRule("Blend", "#");
		acsGrammar.addLexerRule("Tint", "@");
		acsGrammar.addLexerRule("OpenBracket", "\\[");
		acsGrammar.addLexerRule("CloseBracket", "\\]");
		acsGrammar.addLexerRule("OpenBrace", "\\{");
		acsGrammar.addLexerRule("CloseBrace", "\\}");
		acsGrammar.addLexerRule("OpenParenthesis", "\\(");
		acsGrammar.addLexerRule("CloseParenthesis", "\\)");
		acsGrammar.addLexerRule("Equals", "=");
		acsGrammar.addLexerRule("Semicolon", ";");
		acsGrammar.addLexerRule("Colon", ":");
		acsGrammar.addLexerRule("Comma", ",");
		acsGrammar.addLexerRule("Plus", "\\+");
		acsGrammar.addLexerRule("Minus", "-");
		acsGrammar.addLexerRule("Multiply", "\\*");
		acsGrammar.addLexerRule("Divide", "/");
		acsGrammar.addLexerRule("Remainder", "%");
		acsGrammar.addLexerRule("LeftShift", "<<");
		acsGrammar.addLexerRule("RightShift", ">>");
		acsGrammar.addLexerRule("Ternary", "\\?");
		acsGrammar.addLexerRule("BinaryAnd", "\\&");
		acsGrammar.addLexerRule("BinaryOr", "\\|");
		acsGrammar.addLexerRule("BinaryXor", "\\^");
		acsGrammar.addLexerRule("PlusEquals", "\\+=");
		acsGrammar.addLexerRule("MinusEquals", "-=");
		acsGrammar.addLexerRule("MultiplyEquals", "\\*=");
		acsGrammar.addLexerRule("DivideEquals", "/=");
		acsGrammar.addLexerRule("RemainderEquals", "%=");
		acsGrammar.addLexerRule("LeftShiftEquals", "<<=");
		acsGrammar.addLexerRule("RightShiftEquals", ">>=");
		acsGrammar.addLexerRule("AndEquals", "\\&=");
		acsGrammar.addLexerRule("OrEquals", "\\|=");
		acsGrammar.addLexerRule("XorEquals", "\\^=");
		acsGrammar.addLexerRule("LogicalAnd", "\\&\\&");
		acsGrammar.addLexerRule("LogicalOr", "\\|\\|");
		acsGrammar.addLexerRule("LogicalEquals", "==");
		acsGrammar.addLexerRule("NotEquals", "!=");
		acsGrammar.addLexerRule("GreaterThan", ">");
		acsGrammar.addLexerRule("GreaterThanEquals", ">=");
		acsGrammar.addLexerRule("LessThan", "<");
		acsGrammar.addLexerRule("LessThanEquals", "<=");
		acsGrammar.addLexerRule("LogicalNot", "!");
		acsGrammar.addLexerRule("BinaryNot", "~");
		acsGrammar.addLexerRule("Increment", "\\+\\+");
		acsGrammar.addLexerRule("Decrement", "--");
		acsGrammar.addLexerRule("CastCharacter", "[A-Za-z]:");
		acsGrammar.addLexerRule("Define", "#[Dd][Ee][Ff][Ii][Nn][Ee][\t\r\n \\x00]+[^\t\r\n \\x00]+[\t\r\n \\x00]+(\\([^)]*\\)|[^\t\r\n \\x00]+)");
		acsGrammar.addLexerRule("Include", "#[Ii][Nn][Cc][Ll][Uu][Dd][Ee]");
		acsGrammar.addLexerRule("Library", "#[Ll][Ii][Bb][Rr][Aa][Rr][Yy]");
		acsGrammar.addLexerRule("If", "[Ii][Ff]");
		acsGrammar.addLexerRule("Else", "[Ee][Ll][Ss][Ee]");
		acsGrammar.addLexerRule("For", "[Ff][Oo][Rr]");
		acsGrammar.addLexerRule("While", "[Ww][Hh][Ii][Ll][Ee]");
		acsGrammar.addLexerRule("Do", "[Dd][Oo]");
		acsGrammar.addLexerRule("Until", "[Uu][Nn][Tt][Ii][Ll]");
		acsGrammar.addLexerRule("Switch", "[Ss][Ww][Ii][Tt][Cc][Hh]");
		acsGrammar.addLexerRule("Case", "[Cc][Aa][Ss][Ee]");
		acsGrammar.addLexerRule("Void", "[Vv][Oo][Ii][Dd]");
		acsGrammar.addLexerRule("Return", "[Rr][Ee][Tt][Uu][Rr][Nn]");
		acsGrammar.addLexerRule("Continue", "[Cc][Oo][Nn][Tt][Ii][Nn][Uu][Ee]");
		acsGrammar.addLexerRule("Break", "[Bb][Rr][Ee][Aa][Kk]");
		acsGrammar.addLexerRule("Restart", "[Rr][Ee][Ss][Tt][Aa][Rr][Tt]");
		acsGrammar.addLexerRule("Terminate", "[Tt][Ee][Rr][Mm][Ii][Nn][Aa][Tt][Ee]");
		acsGrammar.addLexerRule("Suspend", "[Ss][Uu][Ss][Pp][Ee][Nn][Dd]");
		acsGrammar.addLexerRule("FunctionHeader", "[Ff][Uu][Nn][Cc][Tt][Ii][Oo][Nn]");
		acsGrammar.addLexerRule("ScriptHeader", "[Ss][Cc][Rr][Ii][Pp][Tt]");
		acsGrammar.addLexerRule("ScriptType", "OPEN|ENTER|RETURN|RESPAWN|DEATH|LIGHTNING|UNLOADING|DISCONNECT|KILL|REOPEN|PICKUP|BLUERETURN|REDRETURN|WHITERETURN");
		acsGrammar.addLexerRule("Net", "[Nn][Ee][Tt]");
		acsGrammar.addLexerRule("ClientSide", "[Cc][Ll][Ii][Ee][Nn][Tt][Ss][Ii][Dd][Ee]");
		acsGrammar.addLexerRule("Static", "static");
		acsGrammar.addLexerRule("Scope", "world|global");
		acsGrammar.addLexerRule("Type", "[Ii][Nn][Tt]|[Ss][Tt][Rr]|[Bb][Oo][Oo][Ll]");
		acsGrammar.addLexerRule("Character", "\'(\\\\0|\\\\\\\\|\\\\n|\\\\t|.)\'");
		acsGrammar.addLexerRule("Float", "[0-9]+\\.[0-9]+");
		acsGrammar.addLexerRule("Integer", "([1-9]+[0-9]*)|[0]");
		acsGrammar.addLexerRule("QuotedString", "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"");
		acsGrammar.addLexerRule("Identifier", "[A-Za-z_]+[A-Za-z0-9_]*");
	}
	
	
	private ParserToken ast;

	/**
	 * Loads ACS Script from a WAD and parses them into an AST with the ACS Grammar.
	 * @param wad The WAD file to parse.
	 */
	public ScriptFile(WadFile wad) {

		byte[] scriptData = wad.getLump("scripts");
		if(scriptData == null)
			throw new IllegalArgumentException("The specified wad does not contain ACS source code");

		Lexer lexer = acsGrammar.compileLexer(new ByteStream(scriptData)); //Compile the Lexer separately since we have a custom TokenStream for scripts
		Parser parser = acsGrammar.compileParser(new ScriptTokenStream(lexer));
		
		try {
					
			ast = parser.parse("GlobalList"); //Parse the scripts
			if(ast == null)
				throw new RuntimeException("Error parsing ACS");
		} catch (ParserException e) {
			
			throw new RuntimeException("Error parsing ACS", e);
		}
	}
	
	/**
	 * Retrieves the AST for the Scripts file.
	 * @return The parsed AST.
	 */
	public ParserToken getAbstractSyntaxTree() {
		
		return ast;
	}
}
