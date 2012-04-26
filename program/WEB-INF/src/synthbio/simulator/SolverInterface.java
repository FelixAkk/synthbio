/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author Albert ten Napel
 */
 
 package synthbio.simulator;
 
 import java.io.*;
 
 /**
  * A class that solves a SBML file by executing an external SBML solver.
	* The SBML solver used is SBMLsimulator: http://sourceforge.net/projects/sbml-simulator/
  */
public class SolverInterface {
	private final String SBMLSIMULATOR_PATH = "lib/SBMLsimulator.jar";
	private String result;
	
	public SolverInterface solve(String file) {
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("java -jar \""+SBMLSIMULATOR_PATH+"\" --sbml-input-file \""+file+"\"");
			
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			
			String res = "";
			String line = null;
			while((line = input.readLine()) != null) {
				res += line + "\n";
			}

			int exitVal = pr.waitFor();
		
			result = res;
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return this;
	}
	
	public String getStringResult() {
		return result;
	}
} 