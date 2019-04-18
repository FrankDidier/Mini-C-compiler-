package bitminicc;

import java.util.ArrayList;

/*
 * CMPL_UNIT	: FUNC_LIST
 * FUNC_LIST	: FUNC_DEF FUNC_LIST | e
 * FUNC_DEF		: TYPE_SEPC ID ( ARG_LIST ) CODE_BLOCK
 * TYPE_SPEC	: int | void 
 * ARG_LIST		: ARGUMENT	| ARGUMENT, ARG_LIST | e
 * ARUGMENT		: TYPE_SPEC ID
 * CODE_BLOCK	: { STMT_LIST }
 * STMT_LIST	: STMT STMT_LIST | e
 * STMT			: DECL_STMT | ASSIGN_STMT | RTN_STMT | CALL_STMT
 * CALL_STMT	: ID (RARG_LIST)
 * RARG_LIST	: ID | CONST | RARG_LIST2
 * RARG_LIST2	: , ID RARG_LIST2 | , CONST RARG_LIST2 | e
 * DECL_STMT	: TYPE_SPEC ID ;
 * ASSIGN_STMT	: ID = EXPR ;
 * RTN_STMT		: return EXPR ; 
 * EXPR			: TERM EXPR2
 * EXPR2		: + TERM EXPR2 | - TERM EXPR2 | e
 * TERM			: FACTOR TERM2
 * TERM2		: * FACTOR TERM2 | / FACTOR TERM2 | e
 * FACTOR		: ID | CONST | ( EXPR ) | ID(RARG_LIST)
 */
/*
 * int main(){
 * 	int i;
 *  i = i + 2 * i;
 *  return i;
 * }
 */
		

/*
 * CMPL_UNIT 	=> CMPL_UNIT
 * 				=> FUNC_DEF FUNC_LIST 
 * 				=> TYPE_SEPC ID ( ARG_LIST ) CODE_BLOCK FUNC_LIST
 * 				=> int ID ( ARG_LIST ) CODE_BLOCK FUNC_LIST
 * 				=> int ID ( ) CODE_BLOCK FUNC_LIST  
 * 				=> int ID ( ) { STMT_LIST } FUNC_LIST  
 * 				=> int ID ( ) { STMT STMT_LIST } FUNC_LIST 
 * 				=> int ID ( ) { DECL_STMT STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { TYPE_SPEC ID ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; STMT STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ASSIGN_STMT STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = EXPR ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = TERM EXPR2 ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = FACTOR TERM2 EXPR2 ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = CONST TERM2 EXPR2 ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = CONST ; STMT_LIST } FUNC_LIST
 * 				...
 * 				=> int ID ( ) { int ID ; ID = CONST ; ID = ID + CONST * ID ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = CONST ; ID = ID + CONST * ID ; return EXPR ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = CONST ; ID = ID + CONST * ID ; return ID ; STMT_LIST } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = CONST ; ID = ID + CONST * ID ; return ID ; } FUNC_LIST
 * 				=> int ID ( ) { int ID ; ID = CONST ; ID = ID + CONST * ID ; return ID ; }
 * 
 */
public class Parser {
	private TOKEN_TYPE nextToken;
	private ArrayList<TOKEN_TYPE> tknTypeList;
	private ArrayList<Object> tknValueList;
	
	private int tokenIndex;
	
	public static TreeNode root;
	
	public void run(){
		tknTypeList = Scanner.getTknTypeList();
		tknValueList = Scanner.getTknValueList();
		
		tokenIndex = 0;
		
		System.out.println("Parsing...");
		
		getNextToken();
		TreeNode node = cmpl_unit();
		TreeNode.print(node, "");
		System.out.println("the tree is built!");
		
		this.root = node;
	}
	
	public void getNextToken(){
		nextToken = tknTypeList.get(tokenIndex);
	}
	public TreeNode cmpl_unit(){
		TreeNode fl = func_list();
		
		TreeNode node = new TreeNode();
		node.setType(TreeNodeType.TNT_CMPL_UNIT);
		if(fl != null)
			node.getChildren().add(fl);
		
		return node;
	}
	// FUNC_LIST	: FUNC_DEF FUNC_LIST | e
	public TreeNode func_list(){
		if(tokenIndex >= tknTypeList.size())
			return null;
		if(nextToken == TOKEN_TYPE.TKN_KW_INT || nextToken == TOKEN_TYPE.TKN_KW_VOID){
			tokenIndex++;
			TreeNode fd = func_def();
			TreeNode fl = func_list();
			
			TreeNode node = new TreeNode();
			node.setType(TreeNodeType.TNT_FUNC_LIST);
			node.getChildren().add(fd);
			if(fl != null){
				node.getChildren().add(fl);
			}
			
			return node;
		}else{
			return null;
		}
	}
	//FUNC_DEF		: TYPE_SEPC ID ( ARG_LIST ) CODE_BLOCK
	public TreeNode func_def(){
		TreeNode ts = type_spec();
		
		match_token(TOKEN_TYPE.TKN_ID);
		TreeNode id = new TreeNode();
		id.setType(TreeNodeType.TNT_ID);
		id.setTknIndex(tokenIndex);
		tokenIndex++;
		
		
		match_token(TOKEN_TYPE.TKN_SP_OPENING);
		tokenIndex++;
		TreeNode kbo = new TreeNode();
		kbo.setType(TreeNodeType.TNT_SP_KB_OPENING);
		
		TreeNode al = arg_list();
		
		
		match_token(TOKEN_TYPE.TKN_SP_CLOSING);
		tokenIndex++;
		TreeNode kbc = new TreeNode();
		kbc.setType(TreeNodeType.TNT_SP_KB_CLOSING);

		
		TreeNode cb = code_block();
		
		TreeNode node = new TreeNode();
		node.setType(TreeNodeType.TNT_FUNC_DEF);
		
		node.getChildren().add(ts);
		node.getChildren().add(id);
		node.getChildren().add(kbo);
		node.getChildren().add(al);
		node.getChildren().add(kbc);
		node.getChildren().add(cb);
		
		return node;
	}
	
	public TreeNode code_block(){
		match_token(TOKEN_TYPE.TKN_SP_KB_OPENING);
		tokenIndex++;
		TreeNode sl = stmt_list();
		match_token(TOKEN_TYPE.TKN_SP_KB_CLOSING);
		tokenIndex++;
		
		TreeNode node = new TreeNode();
		node.setType(TreeNodeType.TNT_CODE_BLOCK);
		
		if(sl != null)
			node.getChildren().add(sl);
		
		return node;
	}
	
	/*
	 *  * STMT_LIST	: STMT STMT_LIST | e
	 * 
	 */
	public TreeNode stmt_list(){
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_SP_KB_CLOSING){
			return null;
		}else{
			TreeNode n = new TreeNode();
			n.setType(TreeNodeType.TNT_STMT);
			
			TreeNode s = stmt();
			TreeNode sl = stmt_list();
			
			n.getChildren().add(s);
			if(sl != null){
				n.getChildren().add(sl);
			}
			return n;
		}
	}
	
	/*
	 *  
	 * STMT			: DECL_STMT | ASSIGN_STMT | RTN_STMT
	 */
	public TreeNode stmt(){
		TreeNode n = null;
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_KW_RETURN){
			n = rtn_stmt();
		}else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_KW_INT ||
				tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_KW_VOID){
			n = decl_stmt();
		}else{// asignment statement
			if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_ID 
					&& tokenIndex + 1 < tknTypeList.size()
					&& tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_OP_EQUAL
					){
				n = asgn_stmt();
			}else{
				n = call_stmt();
			}
		}
		
		return n;
	}
	
	/*
	 * CALL_STMT	: ID (RARG_LIST)
	 * RARG_LIST	: ID | CONST | RARG_LIST2
	 * RARG_LIST2	: , ID RARG_LIST2 | , CONST RARG_LIST2 | e
	 */
	
	public TreeNode call_args(){
		TreeNode args = new TreeNode();
		args.setType(TreeNodeType.TNT_RARG_LIST);
		
		match_token(TOKEN_TYPE.TKN_SP_OPENING);
		tokenIndex++;
		match_token(TOKEN_TYPE.TKN_SP_CLOSING);
		tokenIndex++;
		match_token(TOKEN_TYPE.TKN_SP_SC);
		tokenIndex++;
		
		return args;
	}
	/*
	 * DECL_STMT	: TYPE_SPEC ID ;
	 */
	public TreeNode decl_stmt(){
		TreeNode n = new TreeNode();
		n.setType(TreeNodeType.TNT_STMT_DECL);
		
		TreeNode t = new TreeNode();
		t.setType(TreeNodeType.TNT_TYPE_SPEC);
		t.setTknIndex(tokenIndex);
		type_spec();
		
		TreeNode id = new TreeNode();
		id.setType(TreeNodeType.TNT_ID);
		id.setTknIndex(tokenIndex);
		
		match_token(TOKEN_TYPE.TKN_ID);
		tokenIndex++;
		match_token(TOKEN_TYPE.TKN_SP_SC);
		tokenIndex++;
		
		n.getChildren().add(t);
		n.getChildren().add(id);
		
		//System.out.println("declaration matched!");
		
		return n;
	}
	
	/*
	 * RTN_STMT		: return EXPR ; 
	 */
	public TreeNode rtn_stmt(){
		TreeNode n = new TreeNode();
		n.setType(TreeNodeType.TNT_STMT_RTN);
		
		match_token(TOKEN_TYPE.TKN_KW_RETURN);
		tokenIndex++;

		TreeNode expr = expr();
		
		n.getChildren().add(expr);
		
		match_token(TOKEN_TYPE.TKN_SP_SC);
		tokenIndex++;
		System.out.println("return matched!");

		
		return n;
	}
	
	//ASGN_STMT
	public TreeNode asgn_stmt(){
		TreeNode asgn = new TreeNode();
		
		TreeNode id = new TreeNode();
		id.setType(TreeNodeType.TNT_ID);
		id.setTknIndex(tokenIndex);
		tokenIndex++;
		
		match_token(TOKEN_TYPE.TKN_OP_EQUAL);
		tokenIndex++;
		
		TreeNode expr = expr();
		
		match_token(TOKEN_TYPE.TKN_SP_SC);
		tokenIndex++;
		
		
		asgn.getChildren().add(id);
		asgn.getChildren().add(expr);
		
		return asgn;
	}
	
	public TreeNode call_stmt(){
		TreeNode call = new TreeNode();
		call.setType(TreeNodeType.TNT_STMT_CALL);
		
		call.getChildren().add(func_call());
		
		match_token(TOKEN_TYPE.TKN_SP_SC);
		tokenIndex++;
		
		return call;
	}
	
	/*
	 *  * EXPR			: TERM EXPR2
 * EXPR2		: + TERM EXPR2 | - TERM EXPR2 | e
	 */
	
	public TreeNode expr(){
		TreeNode t = term();
		TreeNode e2 = expr2();
		
		TreeNode ex = new TreeNode();
		ex.setType(TreeNodeType.TNT_EXPR);
		ex.getChildren().add(t);
		if(e2 != null){
			ex.getChildren().add(e2);
		}
		
		return ex;
	}
	
	public TreeNode expr2(){
		
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_OP_PLUS){
			
			TreeNode op = new TreeNode();
			op.setType(TreeNodeType.TNT_OP_PLUS);
			match_token(TOKEN_TYPE.TKN_OP_PLUS);
			tokenIndex++;
			
			TreeNode t = term();
			
			TreeNode ex = new TreeNode();
			ex.setType(TreeNodeType.TNT_EXPR2);
			
			ex.getChildren().add(op);
			ex.getChildren().add(t);
			
			if(tknTypeList.get(tokenIndex) != TOKEN_TYPE.TKN_SP_SC){
				TreeNode e2 = expr2();
				if(e2 != null){
					ex.getChildren().add(e2);
				}
			}
			return ex;
		}else{
			return null;
		}
	}
	
	public TreeNode term(){
		TreeNode f = factor();
		
		return f;
	}
	
	public TreeNode factor(){
		TreeNode f = new TreeNode();
		f.setType(TreeNodeType.TNT_FACTOR);
		
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_CNST_FLOAT){
			TreeNode cnst = new TreeNode();
			cnst.setType(TreeNodeType.TNT_CNST_FLOAT);
			cnst.setTknIndex(tokenIndex);
			tokenIndex++;
			f.getChildren().add(cnst);
		}else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_CNST_INT){
			TreeNode cnst = new TreeNode();
			cnst.setType(TreeNodeType.TNT_CNST_INT);
			cnst.setTknIndex(tokenIndex);
			tokenIndex++;
			f.getChildren().add(cnst);
		}else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_ID){
			TreeNode id = new TreeNode();
			id.setType(TreeNodeType.TNT_ID);
			id.setTknIndex(tokenIndex);
			tokenIndex++;
			
			if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_SP_OPENING){
				tokenIndex--;
				TreeNode call = func_call();
				f.getChildren().add(call);
			}else{
				f.getChildren().add(id);
			}
		}
		
		return f;
	}
	
	public TreeNode func_call(){
		TreeNode call = new TreeNode();
		call.setType(TreeNodeType.TNT_FUNC_CALL);
		
		TreeNode id = new TreeNode();
		id.setType(TreeNodeType.TNT_ID);
		id.setTknIndex(tokenIndex);
		tokenIndex++;
		
		call.getChildren().add(id);
		
		match_token(TOKEN_TYPE.TKN_SP_OPENING);
		tokenIndex++;
		
		TreeNode args = real_args();
		if(args != null){
			call.getChildren().add(args);
		}
		
		match_token(TOKEN_TYPE.TKN_SP_CLOSING);
		tokenIndex++;
		
		return call;
	}
	
	public TreeNode real_args(){
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_SP_CLOSING){
			return null;
		}else{
			TreeNode arg = new TreeNode();
			 if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_ID){
				 arg.setType(TreeNodeType.TNT_ID);
			 }else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_CNST_FLOAT){
				 arg.setType(TreeNodeType.TNT_CNST_FLOAT);
			 }else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_CNST_INT){
				 arg.setType(TreeNodeType.TNT_CNST_INT);
			 }
			 arg.setTknIndex(tokenIndex);
			 tokenIndex++;
			 
			 TreeNode args = new TreeNode();
			 args.setType(TreeNodeType.TNT_FUNC_ARGS);
			 args.getChildren().add(arg);
			 
			 if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_SP_COMMA){
				 TreeNode list = real_args2();
				 args.getChildren().add(list);
			 }
			 
			 return args;
		}
		
	}
	
	public TreeNode real_args2(){
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_SP_CLOSING){
			return null;
		}else{
			match_token(TOKEN_TYPE.TKN_SP_COMMA);
			tokenIndex++;
			
			TreeNode arg = new TreeNode();
			 if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_ID){
				 arg.setType(TreeNodeType.TNT_ID);
			 }else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_CNST_FLOAT){
				 arg.setType(TreeNodeType.TNT_CNST_FLOAT);
			 }else if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_CNST_INT){
				 arg.setType(TreeNodeType.TNT_CNST_INT);
			 }
			 arg.setTknIndex(tokenIndex);
			 tokenIndex++;
			 
			 TreeNode args = new TreeNode();
			 args.setType(TreeNodeType.TNT_FUNC_ARGS);
			 args.getChildren().add(arg);
			 
			 if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_SP_COMMA){
				 TreeNode list = real_args2();
				 args.getChildren().add(list);
			 }
			 
			 return args;
		}
	}
	
	public TreeNode type_spec(){
		if(tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_KW_INT || 
				tknTypeList.get(tokenIndex) == TOKEN_TYPE.TKN_KW_INT){
			getNextToken();
			tokenIndex++;		
		}else{
			System.out.println("error in type_spec");
		}
		
		TreeNode node = new TreeNode();
		node.setType(TreeNodeType.TNT_TYPE_SPEC);
		return node;
	}
	//ARG_LIST		: ARGUMENT	| ARGUMENT, ARG_LIST | e
	public TreeNode arg_list(){
		TreeNode node = new TreeNode();
		node.setType(TreeNodeType.TNT_ARG_LIST);
		return node;
	}
	
	public void match_token(TOKEN_TYPE token){
		if(tknTypeList.get(tokenIndex) == token){
			
		}else{
			System.out.println("match token error");
		}
	}
}
