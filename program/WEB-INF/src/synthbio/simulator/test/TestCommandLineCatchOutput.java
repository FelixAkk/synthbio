/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author Albert ten Napel
 */

package synthbio.simulator.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.junit.Test;

public class TestCommandLineCatchOutput {

	/**
	 * Runs a command line program and catches the output.
	 * 
	 */
	@Test
	public void testCommandline() {
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("java -jar \"src/synthbio/simulator/test/Hello.jar\"");
			
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			
			String res = "";
			String line = null;
			while((line = input.readLine()) != null) {
				res += line + "\n";
			}
			
			int exitVal = pr.waitFor();
			
			assertEquals("Hello\n", res);
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}