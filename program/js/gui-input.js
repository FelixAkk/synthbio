/**
 * Project Zelula
 *
 * Contextproject TI2800
 * TU Delft - University of Technology
 *
 * Authors:
 *  Felix Akkermans, Niels Doekemeijer, Thomas van Helden
 *  Albert ten Napel, Jan Pieter Waagmeester
 *
 * https://github.com/FelixAkk/synthbio
 *
 * @author Jan-Pieter Waagmeester
 *
 * GUI JavaScript Document, concerns input values definition.
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * Create the input editor.
 */
synthbio.gui.inputEditor = function(){
	console.log('entered inputEditor');
	
	//clear input signals container.
	$('#input-signals').html('');

	var inputs=synthbio.model.getSimulationInputs();

	//fill advanced settings form fields
	$('#simulate-length').val(inputs.getLength());
	$('#simulate-low-level').val(inputs.getLowLevel());
	$('#simulate-high-level').val(inputs.getHighLevel());
	$('#simulate-tick-width').val(inputs.getTickWidth());
	
	//iterate over signals and create signal input editors.
	$.each(
		inputs.getValues(),
		function(name, ticks){
			var signalEditor=$('<div class="signal" id="signal'+name+'">'+name+': <i class="toggle-highlow low icon-resize-vertical" title="Set signal always on, always off or costum"></i> </div>');
			var levels='<div class="levels">';
			var currentLevel="L";
			var i;
			for(i=0; i<inputs.getLength(); i++){
				if(i < ticks.length){
					currentLevel=ticks.charAt(i);
				}
				levels+='<div class="tick '+(currentLevel==="H" ? 'high': 'low')+'"></div>';
			}
			levels+='</div>';
					
			signalEditor.append(levels);
			$('#input-signals').append(signalEditor);
		}
	);
	
	//attach click listener to the high/low button.
	$('.toggle-highlow').on('click', function() {
		var self=$(this);
		self.toggleClass('high').toggleClass('low');
		self.parent().find('.levels div').toggleClass('high').toggleClass('low');
	});
	
	//click listener for each .levels div containing ticks.
	$('.levels').on('click', function(event) {
		if($(event.target).hasClass('tick')){
			$(event.target).toggleClass('low').toggleClass('high');
			$(this).find('toggle-highlow').removeClass('low').removeClass('high');
		}
	});

	//attach changed listener to simulation length
	$('#simulate-length').on('change keyup', synthbio.gui.updateInputEditor);
};

/**
 * Save inputs back to circuit.
 * 
 * @param circuit the circuit to save to, defaults to syntbio.model
 */
synthbio.gui.saveInputs = function(circuit) {
	circuit = circuit || synthbio.model;

	var simulationInput=circuit.getSimulationInputs();
	
	//copy the options from the form to the object.
	synthbio.util.form2object(
		simulationInput,
		[
			{ selector: '#simulate-tick-width', setter: 'setTickWidth' },
			{ selector: '#simulate-length', setter: 'setLength' },
			{ selector: '#simulate-low-level', setter: 'setLowLevel' },
			{ selector: '#simulate-high-level', setter: 'setHighLevel' }
		]
	);

	//copy the values for each input signal 
	$('.signal').each(function(index, elem) {
		var signal = '';
		$(this).find('.tick').each(function(index, tick) {
			if($(tick).hasClass('high')) {
				signal += 'H';
			} else {
				signal += 'L';
			}
		});
		
		var protein=$(this).attr('id').substr(-1);
		simulationInput.setValue(protein, signal);
	});
};

/**
 * Update the editor according to the new settings for simulation length.
 */
synthbio.gui.updateInputEditor = function() {
	synthbio.gui.saveInputs();
	synthbio.gui.inputEditor();
};

$(document).ready(function() {
	/**
	 *  Build the input editor.
	 */
	$('#define-inputs').on('show', function() {
		// build the editor
		synthbio.gui.inputEditor();

		// attach action to save button.
		$('#save-inputs').on('click', function(){
			synthbio.gui.saveInputs();
		});
		$('#save-inputs-and-simulate').on('click', function() {
			synthbio.gui.saveInputs();
			//trigger click on simulation button to start simuation
			$('#simulate').click();
		});
		
		
		//attach 'advanced' toggle.
		$('#simulation-advanced-toggle').on('click', function(){
			$('#simulation-advanced').toggle(500);
		});
	});
});
