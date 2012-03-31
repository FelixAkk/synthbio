/**
 * Synthetic Biology project (Biobrick Modeller/Simulator)
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.json.test;


import org.junit.*;
import org.junit.Test;
import static org.junit.Assert.*;

import synthbio.json.JSONResponse;
import synthbio.models.CDS;


public class TestJSONResponse{
		
	
	@Test
	public void testDataEmpty(){
		JSONResponse response=new JSONResponse();
		response.success=true;
		response.message="";

		String expected="{\"success\":true,\"message\":\"\",\"data\":null}";

		assertEquals(expected, response.toJSONString());
	}
	@Test
	public void testDataString(){
		JSONResponse response=new JSONResponse();
		response.success=true;
		response.message="";
		response.data="Test123";

		String expected="{\"success\":true,\"message\":\"\",\"data\":\""+response.data+"\"}";

		assertEquals(expected, response.toJSONString());
	}

	/**
	 * Pass an array of Strings and check json ouput
	 */
	@Test
	public void testDataArrayOfStrings(){
		JSONResponse response=new JSONResponse();
		response.success=true;
		response.message="";

		String[] foobar={"foo", "bar"};
		response.data=foobar;

		String expected="{\"success\":true,\"message\":\"\",\"data\":[\"foo\",\"bar\"]}";

		assertEquals(expected, response.toJSONString());
	}

	/**
	 * put a CDS in the data field, and check json output.
	 */
	@Test
	public void testDataCDS(){
		JSONResponse response=new JSONResponse();
		response.success=true;
		response.message="";

		response.data=new CDS("A", 1, 2, 3);

		String expected=
			"{\"success\":true,\"message\":\"\","+
			"\"data\":{\"name\":\"A\",\"k2\":1,\"d1\":2,\"d2\":3}}";

		assertEquals(expected, response.toJSONString());
	}
	@Test
	public void testDataArrayOfCDS(){
		JSONResponse response=new JSONResponse();
		response.success=true;
		response.message="";

		CDS[] data={
			new CDS("A", 1, 2, 3),
			new CDS("B", 4, 5, 6)
		};
		response.data=data;

		String expected=
			"{\"success\":true,\"message\":\"\","+
			"\"data\":["+
				"{\"name\":\"A\",\"k2\":1,\"d1\":2,\"d2\":3},"+
				"{\"name\":\"B\",\"k2\":4,\"d1\":5,\"d2\":6}"+
			"]}";

		assertEquals(expected, response.toJSONString());
	}
}