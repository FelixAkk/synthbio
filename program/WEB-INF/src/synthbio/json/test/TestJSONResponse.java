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

/**
 * Testing the JSONResponse
 *
 * @author jieter
 */
public class TestJSONResponse{
		
	/**
	 * Insert no data and expect "data":null
	 */
	@Test
	public void testDataEmpty(){
		JSONResponse response=new JSONResponse(true, "");

		String expected="{\"data\":null,\"message\":\"\",\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}

	/**
	 * Insert a String and expect it back
	 */
	@Test
	public void testDataString(){
		JSONResponse response=new JSONResponse(true, "");
		response.data="Test123";

		String expected="{\"data\":\""+response.data+"\",\"message\":\"\",\"success\":true}";

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

		String expected="{\"data\":[\"foo\",\"bar\"],\"message\":\"\",\"success\":true}";

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
			"{\"data\":{\"d1\":2,\"d2\":3,\"k2\":1,\"name\":\"A\"},"+
			"\"message\":\"\","+
			"\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}

	@Test
	public void testDataAndPromotor(){
		JSONResponse response=new JSONResponse(true, "");

		response.data=new AndPromotor("A", "B", 1, 2, 3);

		String expected=
			"{\"data\":{\"k1\":1,\"km\":2,\"n\":3,\"tf1\":\"A\",\"tf2\":\"B\"},"+
			"\"message\":\"\","+
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
			"{\"data\":["+
				"{\"d1\":2,\"d2\":3,\"k2\":1,\"name\":\"A\"},"+
				"{\"d1\":5,\"d2\":6,\"k2\":4,\"name\":\"B\"}],"+
			"\"message\":\"\","+
			"\"success\":true}";

		assertEquals(expected, response.toJSONString());
	}

}