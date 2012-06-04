/**
 * Project Zelula
 *
 * Contextproject TI2800 
 * TU Delft - University of Technology
 *  
 * Authors: 
 * 	Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 * 	Albert ten Napel, Jan Pieter Waagmeester
 * 
 * https://github.com/FelixAkk/synthbio
 */

package synthbio.servlets;
 
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import synthbio.files.BioBrickRepository;
import synthbio.files.SynRepository;
import synthbio.models.Circuit;
import synthbio.models.CircuitException;
import synthbio.models.CircuitFactory;
import synthbio.json.JSONResponse;
import synthbio.simulator.Solver;

import synthbio.Util;

/**
 * CircuitServlet is the super class for all servlets concerning Circuits.
 * 
 * API functions documented at:
 * https://github.com/FelixAkk/synthbio/wiki/Zelula-HTTP-API
 * 
 * @author jieter 
 */
@SuppressWarnings("serial")
public class CircuitServlet extends SynthbioServlet {

	/**
	 * The repository of .syn files.
	 */
	protected SynRepository synRepository;

	/**
	 * The BioBrick repository
	 */
	protected BioBrickRepository biobrickRepository;

	protected CircuitFactory circuitFactory;
	
}
