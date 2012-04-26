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
	
	/**
	 * Takes a sbml file, a time series file (csv), a sbml file (for output of optimized sbml) and a result file (csv) and solves it.
	 * Returns commandline output
	 */
	public String solve(String sbmlIn, String timeIn, String sbmlOut, String results) {
		String res = "ERROR";
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec(
				"java -jar \""+SBMLSIMULATOR_PATH+
				"\" --sbml-input-file \""+sbmlIn+
				"\" --time-series-file \""+timeIn+
				"\" --sbml-output-file \""+sbmlOut+
				"\" --simulation-output-file \""+results+"\""
			);
			
			
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			
			res = "";
			String line = null;
			while((line = input.readLine()) != null) {
				res += line + "\n";
			}
			
			int exitVal = pr.waitFor();
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return res;
	}
} 