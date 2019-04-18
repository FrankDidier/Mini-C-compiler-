package bitminicc;

import java.util.ArrayList;

enum TreeNodeType{
	TNT_CMPL_UNIT,
	TNT_FUNC_LIST,
	TNT_FUNC_DEF,
	
	TNT_ID,
	TNT_SP_KB_OPENING,
	
	TNT_UNKNOWN, TNT_SP_KB_CLOSING, TNT_TYPE_SPEC, TNT_ARG_LIST, TNT_CODE_BLOCK, TNT_STMT, TNT_STMT_DECL, TNT_STMT_RTN, TNT_FUNC_CALL, TNT_RARG_LIST, TNT_FACTOR, TNT_CNST_FLOAT, TNT_CNST_INT, TNT_FUNC_ARGS, TNT_STMT_CALL, TNT_EXPR2, TNT_EXPR, TNT_OP_PLUS;
}

public class TreeNode {
	private TreeNodeType type;
	private ArrayList<TreeNode> children;
	private SymbolTable st;
	private int tknIndex;
	private int reg;
	
	public TreeNode(){
		this.setChildren(new ArrayList<TreeNode>());
		this.setType(TreeNodeType.TNT_UNKNOWN);
	}

	public static void print(TreeNode root, String indent){
		String str = (String) Scanner.getTknValueList().get(root.getTknIndex());
		System.out.println(indent + root.getType() + ":" + str + ":" + root.getReg());
		for(TreeNode node : root.getChildren()){
			print(node, indent + "    ");
		}
	}
	
	public TreeNodeType getType() {
		return type;
	}

	public void setType(TreeNodeType type) {
		this.type = type;
	}

	public ArrayList<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}
	
	public TreeNode getChildByIndex(int index){
		if(index >= this.children.size()){
			System.out.println("out of bound!");
			System.exit(1);
		}
		
		return this.children.get(index);
	}

	public void setSymbolTable(SymbolTable st) {
		this.st = st;
	}
	
	public SymbolTable getSymbolTable(){
		return this.st;
	}

	public int getTknIndex() {
		return tknIndex;
	}

	public void setTknIndex(int tknIndex) {
		this.tknIndex = tknIndex;
	}

	public int getReg() {
		return reg;
	}

	public void setReg(int reg) {
		this.reg = reg;
	}
}
