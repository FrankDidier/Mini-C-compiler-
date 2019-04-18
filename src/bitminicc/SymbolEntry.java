package bitminicc;

enum SymbolKind{
	SYM_FUNC,
	SYM_ARG,
	SYM_VAR
}
public class SymbolEntry {
	public String name;
	public int type;
	public SymbolKind kind;
	public int reg;
	public int offset;
	
	public boolean initialized;
}
