package bitminicc;

import java.util.HashMap;

public class SemanticChecker {

	public void run(){
		System.out.println("semantic checking...");
		
		TreeNode root = Parser.root;
		
		//buildSymbolTable(root, null);
		
		//printSymbolTable(root);
		
		System.out.println("-------------parsing to ast------------------------");
		parsing2AST(root);
		root.print(root, "");
		
		
		checkRedef(root);
	}
	
	public void buildSymbolTable(TreeNode root, SymbolTable st){
		if(root.getType() == TreeNodeType.TNT_CMPL_UNIT){
			st = new SymbolTable();
			root.setSymbolTable(st);
		}else if(root.getType() == TreeNodeType.TNT_FUNC_DEF){
			SymbolEntry se = new SymbolEntry();
			TreeNode id = root.getChildByIndex(1);
			int tknIndex = id.getTknIndex();
			String name = (String) Scanner.getTknValueList().get(tknIndex);
			se.name = name;
			se.kind = SymbolKind.SYM_FUNC;
			st.insertSymbol(se);
			
			st = new SymbolTable();
			root.setSymbolTable(st);
		}else if(root.getType() == TreeNodeType.TNT_STMT_DECL){
			SymbolEntry se = new SymbolEntry();
			TreeNode id = root.getChildByIndex(1);
			int tknIndex = id.getTknIndex();
			String name = (String) Scanner.getTknValueList().get(tknIndex);
			se.name = name;
			se.kind = SymbolKind.SYM_VAR;
			st.insertSymbol(se);
		}
		
		for(TreeNode n: root.getChildren()){
			buildSymbolTable(n, st);
		}
	}
	
	public void printSymbolTable(TreeNode root){
		if(root.getType() == TreeNodeType.TNT_CMPL_UNIT ||
				root.getType() == TreeNodeType.TNT_FUNC_DEF){
			SymbolTable t = root.getSymbolTable();
			if(t != null){
				t.print();
			}
		}
		
		for(TreeNode n: root.getChildren()){
			printSymbolTable(n);
		}
	}
	
	public void checkRedef(TreeNode root){
		if(root.getType() == TreeNodeType.TNT_FUNC_DEF){
			SymbolTable t = root.getSymbolTable();
			if(t != null){
				HashMap<String, Integer> symHash = new HashMap<String, Integer>();
				for(SymbolEntry se: t.getSymbolList()){
					if(symHash.keySet().contains(se.name)){
						System.out.println("Symbol " + se.name + " is redefined!");
					}else{
						symHash.put(se.name, 1);
					}
				}
				
			}
		}
		
		for(TreeNode n: root.getChildren()){
			checkRedef(n);
		}
	}
	
	/*public void checkUnInit(TreeNode root, SymbolTable st){
		if(root.getType() == TreeNodeType.TNT_FUNC_DEF){
			st = root.getSymbolTable();
		}else if(root.getType() == TreeNodeType.TNT_STMT_ASGN){
			TreeNode id = root.getChildByIndex(0);
			int tknIndex = id.getTknIndex();
			String name = (String) Scanner.getTknValueList().get(tknIndex);
			
			SymbolEntry se = st.findByName(name);
			if(se == null){
				System.out.println("undefined!");
			}else{
				se.initialized = true;
			}
		}else if(root.getType() == TreeNodeType.TNT_ID){
			int tknIndex = root.getTknIndex();
			String name = (String) Scanner.getTknValueList().get(tknIndex);
			SymbolEntry se = st.findByName(name);
			if(se == null){
				System.out.println("undefined!");
			}else if(!se.initialized){
				System.out.println("uninitialized!");
			}
		}
		
		int index = 0;
		for(TreeNode n: root.getChildren()){
			if(root.getType() == TreeNodeType.TNT_STMT_DECL){
				return;
			}else if(root.getType() == TreeNodeType.TNT_STMT_ASGN){
				if(index == 0){
					continue;
				}else{
					checkUnInit(n, st);
				}
			}else{
				checkUnInit(n, st);
			}
			index++;
		}
	}*/
	
	
	public void parsing2AST(TreeNode root){
		if(root.getType() == TreeNodeType.TNT_CMPL_UNIT){
			if(root.getChildren().size() == 1){
				TreeNode funcList = root.getChildByIndex(0);
				TreeNode funcDef = funcList.getChildByIndex(0);
				
				root.getChildren().add(funcDef);
				
				while(funcList.getChildren().size() >= 2){
					funcList = funcList.getChildByIndex(1);
					
					funcDef = funcList.getChildByIndex(0);
					root.getChildren().add(funcDef);
				}
				root.getChildren().remove(0);
			}
			
		}else if(root.getType() == TreeNodeType.TNT_CODE_BLOCK){
			if(root.getChildren().size() == 1){
				TreeNode smtList = root.getChildByIndex(0);
				TreeNode stmt = smtList.getChildByIndex(0);
				
				root.getChildren().add(stmt);
				
				while(smtList.getChildren().size() >= 2){
					smtList = smtList.getChildByIndex(1);
					
					stmt = smtList.getChildByIndex(0);
					root.getChildren().add(stmt);
				}
				root.getChildren().remove(0);
			}
			
		}else if(root.getType() == TreeNodeType.TNT_EXPR){
			if(root.getChildren().size() >= 2){
				TreeNode p1 = root.getChildByIndex(0);
				TreeNode expr2 = root.getChildByIndex(1);
				TreeNode op = expr2.getChildByIndex(0);
				TreeNode p2 = expr2.getChildByIndex(1);
				
				root.getChildren().clear();
				root.getChildren().add(op);
				op.getChildren().add(p1);
				op.getChildren().add(p2);
				
				if(expr2.getChildren().size() >= 3){
					root.getChildren().add(expr2.getChildByIndex(2));
					parsing2AST(root);
				}
			}
		}
		
		for(TreeNode n: root.getChildren()){
			parsing2AST(n);
		}
	}
}
