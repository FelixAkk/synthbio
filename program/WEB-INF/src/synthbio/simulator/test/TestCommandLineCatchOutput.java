/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 * @author Albert ten Napel
 */

package synthbio.simulator.test;

import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.*;

public class TestCommandLineCatchOutput {

	/**
	 * Runs a command line program and catches the output.
	 * 
	 */
	@Test @Ignore
	public void testCommandline() {
		try {
			Runtime rt = Runtime.getRuntime();
			Process pr = rt.exec("java -jar Hello.jar");
			System.out.println("test");
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = input.readLine();
			System.out.println(line);
			System.out.println(input.readLine());
			int exitVal = pr.waitFor();
			
			assertEquals("Hello", line);
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}