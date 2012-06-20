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
synthbio.gui.updateInputEditor = function(){
	
	//clear input signals container.
	$('#input-signals').html('');

	var inputs = synthbio.model.getSimulationSetting();

	//fill advanced settings form fields
	$('#simulate-tick-width').val(inputs.getTickWidth());
	$('#simulate-length').val(inputs.getLength());
	$('#simulate-low-level').val(inputs.getLowLevel());
	$('#simulate-high-level').val(inputs.getHighLevel());

	//iterate over signals and create signal input editors.
	$.each(
		inputs.getValues(),
		function(name, ticks) {
			if(name === "") return;
			var signalEditor = $('<tr class="signal" id="signal' + name + '">' +
				'<td class="pName">' + name + '</td>' +
				'<td><a class="btn toggle-highlow low" href="#" title="Inverts the defined signal (all values are flipped).">' +
				'<i class="icon-resize-vertical"></i> Flip</a></td></tr>');
			var levels='<td class="levels">';
			var currentLevel="L";
			var i;
			var cssClass;
			for(i=0; i<inputs.getLength(); i++) {
				if(i < ticks.length) {
					currentLevel = ticks.charAt(i);
				}
				cssClass = 'tick ';
				cssClass += (currentLevel === "H" ? 'high': 'low')+' ';
				
				//add colored markers on each quarter to see where we are
				if(i !== 0 && i !== inputs.getLength() && (i % Math.floor(inputs.getLength()/4)) === 0) {
					cssClass += 'marker ';
				}
				
				levels += '<div class="' + cssClass + '" id="tick' + name + '_' + i +  '"></div>';
			}
			levels += '</td>';
					
			signalEditor.append(levels);
			$('#input-signals').append(signalEditor);
		}
	);
	
	//attach click listener to the high/low button.
	$('.toggle-highlow').on('click', function() {
		var self = $(this);
		self.toggleClass('high').toggleClass('low');
		self.closest("tr").find('.levels div').toggleClass('high').toggleClass('low');
		//do a sort of auto save by saving now already instead of requiring the user to explicitly click save
		synthbio.gui.saveProteinInputLevels(inputs);
	});
	
	//click listener for each .levels div containing ticks.
	var selectionStart = {};
	
	//utility functions to filter out protein and tickid.
	var getProtein = function(tick) {
		return tick.attr('id').split('_')[0].substring(4,5);
	};
	var getTickId = function(tick) {
		return parseInt(tick.attr('id').split('_')[1], 10);
	};
	
	//define event on levels to support tick modification...
	$('.levels').on({
		/* At mousedown, save the tick it occured on.
		 */
		'mousedown': function(event) {
			var tick=$(event.target);
			if(tick.hasClass('tick')){
				selectionStart[getProtein(tick)]=getTickId(tick);
			}
			
			//return false to prevent dragging shizzle.
			return false;
		},
		
		/* The mousemove event takes care of highlighting the selected
		 * range of ticks before the mouse button is released.
		 */ 
		'mousemove': function(event){
			var tick=$(event.target);
			if(!tick.hasClass('tick')){
				return;
			}

			//check if a mousedown for this protein occured.
			var protein = getProtein(tick);
			var start = selectionStart[protein];
			if(start === undefined){
				return;
			}

			//retrieve the range of ticks from the previously recorded start
			//to the current tick.
			var end = getTickId(tick);
			var ticks=$(this).parent().find('.tick')
				.slice(Math.min(start, end), Math.max(end, start) + 1);
				
			//and highlight the selected part.
			$('.tick').removeClass('changing');
			ticks.addClass('changing');
			
		},
		
		/* When the mouse button is released above a tick, toggle the range.
		 */
		'mouseup': function(event){
			var tick=$(event.target);

			//remove changing classes to clear 
			$('.tick').removeClass('changing');
			
			if(!tick.hasClass('tick')){
				selectionStart={};
				return;
			}

			var protein = getProtein(tick);
			var selectionEnd = getTickId(tick);

			var ticks;
			var start = selectionStart[protein];
			if(start !== undefined && start !== selectionEnd){
				//toggle a range of ticks according to the value of the first one.
				var newState, oldState;
				if($('#tick'+protein + '_' + start).hasClass('high')){
					newState='low';
					oldState='high';
				}else{
					newState='high';
					oldState='low';
				}

				//slice wants a range from a smaller to a bigger number.
				ticks = $(this).parent().find('.tick');
				if(selectionEnd > start){
					ticks = ticks.slice(start, selectionEnd + 1);
				}else{
					ticks = ticks.slice(selectionEnd, start + 1);
				}
				
				//the actual toggling.
				ticks.addClass(newState).removeClass(oldState);
			}else{
				//just toggle one tick.
				ticks=tick;
				ticks.toggleClass('low').toggleClass('high');
			}

			//highlight the range with a color for some time.
			if(ticks){
				ticks.addClass('changed', 300).delay(600).removeClass('changed', 400);
			}

			//clear saved selection start.
			selectionStart={};

			//do a sort of auto save by saving now already instead of requiring the user to explicitly click save
			synthbio.gui.saveProteinInputLevels(inputs);
		}
	});
};

synthbio.gui.saveProteinInputLevels = function(simulationSetting) {
//copy the values for each input signal
	$('.signal').each(function (index, elem) {
		var signal = '';
		$(this).find('.tick').each(function (index, tick) {
			if ($(tick).hasClass('high')) {
				signal += 'H';
			} else {
				signal += 'L';
			}
		});

		var protein = $(this).attr('id').substr(-1);
		simulationSetting.setValue(protein, signal);
	});
}
/**
 * Save inputs back to circuit.
 * 
 * @param circuit the circuit to save to, defaults to syntbio.model
 */
synthbio.gui.saveInputs = function(circuit) {
	circuit = circuit || synthbio.model;
	
	var simulationSetting = circuit.getSimulationSetting();
	
	//copy the options from the form to the object.
	synthbio.util.form2object(
		simulationSetting,
		[
			{ selector: '#simulate-tick-width', setter: 'setTickWidth' },
			{ selector: '#simulate-length', setter: 'setLength' },
			{ selector: '#simulate-low-level', setter: 'setLowLevel' },
			{ selector: '#simulate-high-level', setter: 'setHighLevel' }
		]
	);

	if($('#input-csv-textarea').val() !== "") {
		simulationSetting.setCSV($('#input-csv-textarea').val());
	} else {
		synthbio.gui.saveProteinInputLevels(simulationSetting);
	}
};

$(document).ready(function() {
	/**
	 * Rebuild the editor on every show of inputs definition tab.
	 */
	$('#simulation-tabs li.dropdown a[href="#input-editor"]').tab('show', function(event) {
		var simulationSetting = synthbio.model.getSimulationSetting();
		//if csv is not empty, bring up that tab.
		if(simulationSetting.getCSV() !== "") {
			$('#input-csv').addClass('active');

		} else {
			// build the editor
			synthbio.gui.updateInputEditor();
		}
		
		$('#simulate-low-level').val(simulationSetting.getLowLevel());
		$('#simulate-high-level').val(simulationSetting.getHighLevel());
		$('#input-csv-textarea').val(simulationSetting.getCSV());
	});

	/**
	 * When the input-editor tab is clicked, check if csv is active,
	 * and warn about loosing data if proceeding.
	 */
	$('#simulation-tabs li.dropdown a[href="#input-editor"]').on('click', function(event) {
		var simulationSetting = synthbio.model.getSimulationSetting();
		
		if(simulationSetting.getCSV() !== "") {
			if(confirm("When using the input editor, CSV input will be lost!")){
				//emtpy csv and build the editor.
				simulationSetting.setCSV({});
				if(simulationSetting.getLength() == '') {
					simulationSetting.setLength(40);
				}
				if(simulationSetting.getTickWidth() == '') {
					simulationSetting.setTickWidth(10);
				}
			}else{
				//If the confirm is answered negative, don't go to the tab.
				event.preventDefault();
				return false;
			}
		}
	});

	/**
	 * When the input-csv tab is clicked, check if the editor is active,
	 * and warn about loosing data if proceeding...
	 */
	$('#simulation-tabs li.dropdown a[href="#input-csv"]').on('click', function(event) {
		var simulationSetting = synthbio.model.getSimulationSetting();
		
		if(simulationSetting.getCSV() == "") {
			if(confirm("When using CSV input, the values in the input editor will be lost!")){
				//Empty the editor, set CSV textarea to
				simulationSetting.setCSV("t,");
				$('#input-csv-textarea').val('t,');
			}else{
				//If the confirm is answered negative, don't go to the tab.
				event.preventDefault();
				return false;
			}
		}
	});
	
	// attach action to save button.
	$('#save-inputs').on('click', function() {
		synthbio.gui.saveInputs();
		synthbio.gui.updateInputEditor();
	});

	$('#save-inputs-and-simulate').on('click', function() {
		$('#save-inputs').click();
		//trigger click on simulation button to start simuation
		$('#simulate').click();
	});

	// Reset both protein concentration level definitions and all the other integer settings
	$('#inputs-reset').on('click', function() {
		synthbio.model.setSimulationInput(new synthbio.SimulationSetting());
		synthbio.gui.updateInputEditor();
	});
});
