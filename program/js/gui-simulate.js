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
 * @author Felix Akkermans & Jan-Pieter Waagmeester & Niels Doekemeijer
 *
 * GUI JavaScript Document, concerns all simulation GUI matters (validation & plotting).
 */

/*jslint devel: true, browser: true, forin: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb, Highcharts */

/**
 * syntbio package.
 */
var synthbio = synthbio || {};
synthbio.gui = synthbio.gui || {};

/**
 * The big plot object (Highcharts.StockChart)
 */
synthbio.gui.plot = {};

/**
 * Precision of plotting data
 */
synthbio.gui.plotPrecision = 2;

/**
 * Update the synthbio.gui.plot sum series (after adding/hiding a series)
 * @param val Array of values
 * @param hidden True for hiding values, false for showing 
 */
synthbio.gui.updateSumSeries = function(val, hidden) {
	var sumSeries = synthbio.gui.plot.get("navseries");

	if (sumSeries && sumSeries.yData) {
		var newData = sumSeries.yData;

		if (hidden) {
			$.each(val, function(idx, v) {
				newData[idx] -= v;
			});
		} else {
			$.each(val, function(idx, v) {
				newData[idx] += v;
			});	
		}

		newData = synthbio.util.roundSeries(newData);
		sumSeries.setData(newData);
	}
};

/**
 * Plot an array of series in the group chart
 * @param series Array of output points ([{name: "name", data: [1, 2, 3, ..]}, ..])
 * @param timestep Timestep in seconds (defaults to 1).
 */
synthbio.gui.plotSeries = function(series, timestep) {
	timestep = timestep || 1;

	if (!series || series.length < 1) {
		series = [{name: "Empty", data: [0, 0, 0, 0, 0], visible: false}];
	}

	//Extend the default options with the series
	var options = $.extend(true, {}, synthbio.chartOptions, {
		series: series,
		navigator: {
			series: { data: synthbio.util.calculateSumSeries(series) }
		}
	});

	//Destroy old chart if present
	if (synthbio.gui.plot && synthbio.gui.plot.destroy) {
		synthbio.gui.plot.destroy();
	}

	//Plot!
	synthbio.gui.plot = new Highcharts.StockChart(options);
};

/**
 * Plot an array of series separately
 * @param series Array of output points ([{name: "name", data: [1, 2, 3, ..]}, ..])
 */
synthbio.gui.plotSeriesSeparate = (function() {
	//Keep track of old charts to destroy before overriding
	var charts = [];

	return function(series) {
		//Adjust the default options
		var chartOptions = $.extend(true, {}, synthbio.chartOptions, {
			chart: {
				zoomType: 'x',
				height: 100,
				width: 510,
				marginLeft: 25,
				marginRight: 0,
				marginBottom: 15
			},
			title: {text: null},

			legend:        { enabled: false },
			navigator:     { enabled: false },
			rangeSelector: { enabled: false },
			scrollbar:     { enabled: false },
			exporting:     { enabled: false }
		});

		//Delete previous charts
		$.each(charts, function(idx, chart) {
			chart.destroy();
		});
		charts = [];

		//Get container and clear
		var container = $("#chart-separate");
		container.empty();

		//Plot new series
		$.each(series, function(idx, data) {
			//Create div for chart and append to container
			var el = $('<div class="chart">'+data.name+': </div>');
			container.append(el);

			//Add series to options
			data.visible = true;
			var options = $.extend(true, {}, chartOptions, {
				chart: {renderTo: el[0]},
				yAxis: {title: {text: data.name}},
				series: [data]
			});

			//Plot chart and add to chart array
			var chart = new Highcharts.StockChart(options);
			charts.push(chart);
		});

		//Default text if there are no results
		if (charts.length < 1) {
			container.html("Please simulate first.");
		}
	};
}());

/**
 * Plot simulation output
 * @param response Data object from synthbio.requests.simulate
 */
synthbio.gui.plotOutput = function(response) {
	var timestep = response.step || 1;
	var proteins = synthbio.getProteins() || {};

	//Map the series from the synthbio.requests.simulate output to Highcharts.Stockchart data
	var series = $.map(response.data, function(val, name) {
		//Check if this is a protein (otherwise it could be mRNA) and if it should be visible
		var valid = proteins[name] !== undefined;
		var show = valid &&
			(proteins[name] !== false) &&
			(proteins[name].isInput() || proteins[name].isOutput());

		//Determine color
		var color = (valid)
			? synthbio.gui.proteinColor(name, true)
			: synthbio.gui.proteinColor(name.substring(1), true);

		return {
			type: 'spline',
			name: name,
			color: color,
			dashStyle: (valid ? 'solid' : 'shortdash'),
			lineWidth: (valid ? 1.25 : 1),
			pointInterval: timestep,
			data: synthbio.util.roundSeries(val),
			visible: show
		};
	});

	synthbio.gui.plotSeries(series, timestep);
	synthbio.gui.plotSeriesSeparate(series);
};

/**
 * General event handler for when the validate action is called.
 */
synthbio.gui.validateHandler = function(){
	console.log('Started validating circuit...');
	synthbio.requests.validate(
		synthbio.model,
		function(response){
			synthbio.gui.displayValidation(
				response.message || "No response..", 
				response.success
			);
		}
	);
};

/**
 * General event handler for when the simulate action is called.
 */
synthbio.gui.simulateHandler = function() {
	console.log('Simulate initiated.');
	synthbio.gui.plot.showLoading();

	synthbio.requests.simulate(
		synthbio.model,
		function(response){
			console.log("synthbio.request.simulate called response callback");
			if(response.message !== '') {
				synthbio.gui.displayValidation(response.message, false);
			}else{
				synthbio.gui.displayValidation("Circuit validates!", true, true);
				$('#simulation-tab a[href="#tab-chart"]').tab("show");
				synthbio.gui.plotOutput(response.data);
			}
		}
	);
};

/**
 * Setup options for Highcharts.StockChart 
 */
synthbio.chartOptions = {
	chart:   {renderTo: 'chart-group'},
	credits: {enabled: false},
	title:   {text: 'Simulation output'},
	loading: {style: { backgroundColor: 'silver' }},
	series:  [{name: "Empty", data: [0, 0, 0, 0, 0]}],
	yAxis:   {min: 0, showFirstLabel: false}
};

//x-axis: Display the x value and add an "s" (data always starts at 0)
synthbio.chartOptions.xAxis = {
	minRange: 3,
	labels: {
		formatter: function() { return this.value + "s"; }
	}
};

//tooltip: Format the output for the tooltip (show time in seconds and all values)
synthbio.chartOptions.tooltip = {
	formatter: function() {
		//Current time
		var multipleLines = this.points.length > 1;
		var s = '<b>'+ this.x +' seconds</b>';

		//Protein values
		$.each(this.points, function(i, point) {
			var hover = multipleLines && point.series.state === "hover";
			var weight = (hover) ? "font-weight:bold;" : "";

			s += "<br/>";
			s += '<span style="'+weight+'color: ' + point.series.color + '";>' + point.series.name + "</span>:";
			s += (hover) ? "<strong>" : " ";
			s += this.point.y.roundTo(synthbio.gui.plotPrecision);
			s += (hover) ? "</strong>" : "";
		});
		return s;
	}
};

//navigator: Make sure the id is "navseries"
synthbio.chartOptions.navigator = {
	series: { id: "navseries", data: [0, 0, 0, 0, 0] },
	xAxis: synthbio.chartOptions.xAxis,
	top: 200
};

//legend: Show legend at the right
synthbio.chartOptions.legend = {
	enabled: true,
	borderWidth: 0,

	align: 'right',
	layout: 'vertical',
	verticalAlign: 'top',
	y: 75,
	shadow: true
};

//Update the sumseries in the navigator when a series is hidden or shown
synthbio.chartOptions.plotOptions = {
	series: {
		events: {
			show: function() {
				synthbio.gui.updateSumSeries(this.yData, false);
			},
			hide: function() {
				synthbio.gui.updateSumSeries(this.yData, true);
			}
		}
	}
};

//Setup the range selector (5s, 10s, 20s, all)
synthbio.chartOptions.rangeSelector = {
	buttons: [{
		type: 'millisecond',
		count: 5,
		text: '5s'
	}, {
		type: 'millisecond',
		count: 10,
		text: '10s'
	}, {
		type: 'millisecond',
		count: 20,
		text: '20s'
	}, {
		type: 'all',
		text: 'All'
	}],

	selected : 3,
	inputEnabled: false                                                
};

$(document).ready(function() {
	//Create an initial chart (in a loading state) as a stub
	var options = $.extend(true, {}, synthbio.chartOptions);
	synthbio.gui.plot = new Highcharts.StockChart(options);
	synthbio.gui.plot.showLoading();

	// Validate
	$('#validate').on('click', synthbio.gui.validateHandler);

	/**
	 * Run simulation
	 */
	$('#simulate').on('click', synthbio.gui.simulateHandler);
});
