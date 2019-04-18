import bitminicc.CodeGenerator;
import bitminicc.Parser;
import bitminicc.Preprocessor;
import bitminicc.Scanner;
import bitminicc.SemanticChecker;


public class MiniCCompiler {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World!");
		if(args.length < 1){
			System.out.println("Usage: test.c");
			return;
		}else{
			System.out.println(args[0]);
		}
		
		Preprocessor pp = new Preprocessor();
		Scanner scanner = new Scanner();
		Parser parser = new Parser();
		SemanticChecker sc = new SemanticChecker();
		CodeGenerator cg = new CodeGenerator();
		
		String code = pp.run(args[0]);
		scanner.run(code);
		parser.run();
		sc.run();
		cg.run();
		
	}

}
