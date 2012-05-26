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
 */

/*jslint devel: true, browser: true, vars: true, plusplus: true, sloppy: true, white: true, maxerr: 50, indent: 4 */
/*global $, synthbio, jsPlumb */

/**
 * Rendering the gates.
 * 
 * @author Felix Akkermans & Niels Doekemeijer
 */

$(document).ready(function() {
	
	jsPlumb.ready(function() {
		
		jsPlumb.setRenderMode(jsPlumb.SVG);
		jsPlumb.Defaults.Container = $("#grid-container");

		jsPlumb.importDefaults({
			DragOptions : { cursor: 'pointer', zIndex:2000 },

			//Overlays for wires
			ConnectionOverlays : [
				//Arrow overlay
				[ "Arrow", { location:0.9 } ],

				//Text overlay
				[ "Label", { 
					location:0.5,
					id:"label",
					cssClass:"arrowLabel"
				}]
			]
		});			

		// This is the paint style for the connecting lines..
		var pointHoverStyle = {
			lineWidth:3,
			strokeStyle:"purple"
		},
		connectorPaintStyle = {
			lineWidth:3,
			strokeStyle:"#deea18",
			joinstyle:"round"
		},
		// .. and this is the hover style. 
		connectorHoverStyle = {
			lineWidth:5,
			strokeStyle:"#2e2aF8"
		};

		var inputCounter = 0;
		// The definition of input endpoints
		synthbio.gui.inputEndpoint = {
			endpoint:"Rectangle",					
			paintStyle:{ fillStyle:"#558822",width:11,height:11 },
			hoverPaintStyle:pointHoverStyle,
			isTarget:true,
			dropOptions: {
				activeClass:'dragActive',
				hoverClass:'dragHover'
			},
			beforeDrop: function(opt) {
				if (!opt.connection || 
					opt.connection.sourceId !== "gate-input" || 
					opt.connection.endpoints[0].getUuid())
				{
					return true;
				}

				// Get a free endpoint for "gate-input" and reconnect
				var src = synthbio.gui.getFreeEndpoint(opt.connection.sourceId, false);
				jsPlumb.connect({
					source: src,
					target: opt.target
				});

				// Disallow the old connection
				return false;
			}
		};

		// The definition of target endpoints
		synthbio.gui.outputEndpoint = {
			endpoint: "Dot",
			paintStyle:{ fillStyle:"#225588",radius:7 },
			connector: ["Bezier", { curviness:50 } ],
			connectorStyle: connectorPaintStyle,
			hoverPaintStyle: pointHoverStyle,
			connectorHoverStyle: connectorHoverStyle,
			isSource: true,
			maxConnections: -1
		};

		var connCount = 0;
		// Listen for new connections; initialise them the same way we initialise the connections at startup.
		jsPlumb.bind("jsPlumbConnection", function(connInfo, originalEvent) {
			var signal = connInfo.connection.getParameters().signal;
			signal =  signal || synthbio.gui.displayConnection(connInfo.connection).signal;

			signal.fromEndpoint = synthbio.gui.getEndpointIndex(connInfo.connection.endpoints[0]);
			signal.toEndpoint = synthbio.gui.getEndpointIndex(connInfo.connection.endpoints[1]);
			
			connCount++;
			// Instantiate the connection count variable because `connCount` will be incremented later
			var wireID = connCount;
			var connectionOverlay = connInfo.connection.getOverlay("label");
			var label = '<a id="conn' + connCount + '" href=#>';
			label += signal.getProtein() || "Choose protein";
			label += '</a>';
			connectionOverlay.setLabel(label);
			
			var currentProtein = "";
			var wire = $('#conn' + connCount, 0);
			
			//If it's one char it succesfully selected a protein from the start.
			if(signal.getProtein().length === 1) {
				synthbio.proteins[signal.getProtein()] = true;
				currentProtein = signal.getProtein();
			}
			
			wire.on("click", function(event) {
				synthbio.clickWire(wire, wireID);
				//Set proper menu location
				connectionOverlay.setLocation(connectionOverlay.getLocation());
			});
			
			wire.on("change", function(event) {
				currentProtein = synthbio.changeWire(wire, wireID, currentProtein, signal);
				//Set proper label location
				connectionOverlay.setLocation(connectionOverlay.getLocation());
			});
		});

		// Listen for disposal of connections; delete endpoints if necessary
		jsPlumb.bind("jsPlumbConnectionDetached", function(connInfo, originalEvent) {
			if (connInfo.sourceId === "gate-input" && !connInfo.sourceEndpoint.connections.length) {
				jsPlumb.deleteEndpoint(connInfo.sourceEndpoint);
			}
			if (connInfo.targetId === "gate-output" && !connInfo.targetEndpoint.connections.length) {
				jsPlumb.deleteEndpoint(connInfo.targetEndpoint);
			}

			synthbio.gui.removeDisplaySignal(connInfo.connection.id);
		});

		// Remove connection on double click	
		jsPlumb.bind("dblclick", function(conn) {
			if (confirm("Delete this connection?")) {
				synthbio.gui.removeDisplaySignal(conn.id);
			}
		});

		jsPlumb.draggable("gate-input", {handle: "h4"});
		jsPlumb.draggable("gate-output", {handle: "h4", start: function() { $(".output").css("right", "auto");}});

		var oep = $.extend(true, {
			anchor:"Continuous",
			deleteEndpointsOnDetach: false
		}, synthbio.gui.outputEndpoint);
		var iep = $.extend(true, {
			anchor:"Continuous",
			deleteEndpointsOnDetach: false
		}, synthbio.gui.inputEndpoint);

		jsPlumb.makeSource("gate-input", oep);
		jsPlumb.makeTarget("gate-output", iep);

		synthbio.gui.reset();
	});
});