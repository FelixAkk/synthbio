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
			Process pr = rt.exec("helloworld.exe");
			
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			
			assertEquals("Hello world!", input.readLine());
		} catch(Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}