package bitminicc;

import java.util.ArrayList;


enum TOKEN_TYPE{
	TKN_ID,
	TKN_CNST_INT,
	TKN_CNST_FLOAT,
	
	TKN_OP_PLUS,
	TKN_OP_MINUS,
	TKN_OP_MUL,
	TKN_OP_DIV,
	
	TKN_SP_COMMA,	//,
	TKN_SP_KB_OPENING,	// {
	TKN_SP_KB_CLOSING,	// }
	TKN_SP_OPENING,		// (
	TKN_SP_CLOSING,		// )
	TKN_SP_SC,			// ;
	TKN_OP_EQUAL,
	
	TKN_KW_INT,
	TKN_KW_VOID,
	TKN_KW_RETURN
	
}

public class Scanner {
	private static ArrayList<TOKEN_TYPE> tknTypeList;
	private static ArrayList<Object> tknValueList;
	
	public Scanner(){
		setTknTypeList(new ArrayList<TOKEN_TYPE>());
		setTknValueList(new ArrayList<Object>());
	}
	private boolean isLetter(char c){
		if(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z'){
			return true;
		}else{
			return false;
		}
	}
	private boolean isDigit(char c){
		if(c >= '0' && c <='9')
			return true;
		else
			return false;
	}
	public void run(String code){
		System.out.println("Scanning...");
		int state = 0;
		String tknValue = "";
		
		for(int i = 0; i < code.length(); i++){
			char c = code.charAt(i);
			if(state == 0){
				if(c == '='){
					getTknTypeList().add(TOKEN_TYPE.TKN_OP_EQUAL);
					getTknValueList().add(null);
				}else if(c == ';'){
					System.out.println(c);
					getTknTypeList().add(TOKEN_TYPE.TKN_SP_SC);
					getTknValueList().add(null);
				}else if(c == '='){
					System.out.println(c);
					//tknTypeList.add(TOKEN_TYPE.);
					//tknValueList.add(null);
				}else if(c == '('){
					tknTypeList.add(TOKEN_TYPE.TKN_SP_OPENING);
					getTknValueList().add("(");
				}else if(c == ')'){
					tknTypeList.add(TOKEN_TYPE.TKN_SP_CLOSING);
					getTknValueList().add(")");
				}else if(c == '{'){
					tknTypeList.add(TOKEN_TYPE.TKN_SP_KB_OPENING);
					getTknValueList().add("{");
				}else if(c == '}'){
					tknTypeList.add(TOKEN_TYPE.TKN_SP_KB_CLOSING);
					getTknValueList().add("}");
					//System.out.println(c);
				}else if(c == '+'){
					tknTypeList.add(TOKEN_TYPE.TKN_OP_PLUS);
					getTknValueList().add("+");
					//System.out.println(c);
				}else if(isLetter(c)){//identifier
					state = 2;
					System.out.print(c);
					tknValue += c;
				}else if(isDigit(c)){//integer constant
					state = 3;
					System.out.print(c);
					tknValue += c;
				}
			}else if(state == 1){
				
			}else if(state == 2){
				if(isLetter(c) || isDigit(c)){
					System.out.print(c);
					tknValue += c;
				}else{
					if(tknValue.equals("int")){
						getTknTypeList().add(TOKEN_TYPE.TKN_KW_INT);
					}else if(tknValue.equals("void")){
						getTknTypeList().add(TOKEN_TYPE.TKN_KW_VOID);
					}else if(tknValue.equals("return")){
						getTknTypeList().add(TOKEN_TYPE.TKN_KW_RETURN);
					}else{
						getTknTypeList().add(TOKEN_TYPE.TKN_ID);
					}
					getTknValueList().add(tknValue);
					tknValue = "";
					state = 5;
					i--;
				}
			}else if(state == 3){
				if(isDigit(c)){
					System.out.print(c);
					tknValue += c;
				}else if(c == '.'){
					tknValue += c;
					state = 7;
				}else{
					getTknTypeList().add(TOKEN_TYPE.TKN_CNST_INT);
					//Integer value = Integer.parseInt(tknValue);
					getTknValueList().add(tknValue);
					tknValue = "";
					state = 6;
					i--;
				}
			}else if(state ==4){
				
			}else if(state == 5 || state == 6){
				i--;
				state = 0;
				System.out.println();
			}else if(state == 7){
				if(isDigit(c)){
					System.out.print(c);
					tknValue += c;
				}else{
					getTknTypeList().add(TOKEN_TYPE.TKN_CNST_FLOAT);
					getTknValueList().add(tknValue);
					tknValue = "";
					state = 6;
					i--;
				}
			}
			
		}

		System.out.print("");
		
	}
	public static ArrayList<TOKEN_TYPE> getTknTypeList() {
		return tknTypeList;
	}
	public static void setTknTypeList(ArrayList<TOKEN_TYPE> list) {
		tknTypeList = list;
	}
	public static ArrayList<Object> getTknValueList() {
		return tknValueList;
	}
	public static void setTknValueList(ArrayList<Object>list) {
		tknValueList = list;
	}
	
}
