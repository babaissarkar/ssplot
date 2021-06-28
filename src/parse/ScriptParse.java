package parse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.script.*;


public class ScriptParse {
	
	public ScriptParse()
	{
	}
	
	public void parse() {
		ScriptEngineManager m = new ScriptEngineManager();
		
		List<ScriptEngineFactory> l = m.getEngineFactories();
		for (ScriptEngineFactory scriptEngineFactory : l) {
			System.out.println(scriptEngineFactory.getEngineName());
		}
		
		ScriptEngine engine = m.getEngineByName("jython");
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			String s = r.readLine();
			engine.eval(s);
			/*int a = (int) engine.get("x");
			System.out.println("a = " + a);*/
		} catch (ScriptException | IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new ScriptParse().parse();
	}

}
