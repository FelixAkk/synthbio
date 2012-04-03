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
import synthbio.models.AndPromotor;

public class TestJSONResponse{
		
	/**
	 * Insert no data and expect "data":null
	 */
	@Test
	public void testDataEmpty(){
		JSONResponse response=new JSONResponse(true, "");

		String expected="{\"message\":\"\",\"data\":null,\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}

	/**
	 * Insert a String and expect it back
	 */
	@Test
	public void testDataString(){
		JSONResponse response=new JSONResponse(true, "");
		response.data="Test123";

		String expected="{\"message\":\"\",\"data\":\""+response.data+"\",\"success\":true}";

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

		String expected="{\"message\":\"\",\"data\":[\"foo\",\"bar\"],\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}

	/**
	 * put a CDS in the data field, and check json output.
	 */
	@Ignore
	@Test
	public void testDataCDS(){
		JSONResponse response=new JSONResponse(true, "");

		response.data=new CDS("A", 1, 2, 3);

		String expected=
			"{"+
			"\"success\":true,"+
			"\"message\":\"\","+
			"\"data\":{\"d1\":2,\"d2\":3,\"name\":\"A\",\"k2\":1}}";

		assertEquals(expected, response.toJSONString());
	}

	@Test
	public void testDataAndPromotor(){
		JSONResponse response=new JSONResponse(true, "");

		response.data=new AndPromotor("A", "B", 1, 2, 3);

		String expected=
			"{\"message\":\"\","+
			"\"data\":{\"tf1\":\"A\",\"n\":3,\"tf2\":\"B\",\"k1\":1,\"km\":2},"+
			"\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}
	/**
	 * Array of CDSs.
	 */
	@Test
	public void testDataArrayOfCDS(){
		JSONResponse response=new JSONResponse(true, "");
		
		CDS[] data={
			new CDS("A", 1, 2, 3),
			new CDS("B", 4, 5, 6)
		};
		response.data=data;

		String expected=
			"{\"message\":\"\","+
			"\"data\":["+
				"{\"d1\":2,\"d2\":3,\"name\":\"A\",\"k2\":1},"+
				"{\"d1\":5,\"d2\":6,\"name\":\"B\",\"k2\":4}"+
			"],"+
			"\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}

}