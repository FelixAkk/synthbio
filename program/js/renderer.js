// GUI JavaScript Document
/**
 * 		Project Zelula:
 * Synthetic biology modeller/simulator
 * https://github.com/FelixAkk/synthbio
 * by Group E, TU Delft
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

		// this is the paint style for the connecting lines..
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

		// the definition of input endpoints
		synthbio.gui.inputEndpoint = {
			endpoint:"Rectangle",					
			paintStyle:{ fillStyle:"#558822",width:11,height:11 },
			hoverPaintStyle:pointHoverStyle,
			isTarget:true,
			dropOptions: {
				activeClass:'dragActive',
				hoverClass:'dragHover'
			}
		};

		// the definition of target endpoints
		synthbio.gui.outputEndpoint = {
			endpoint:"Dot",
			paintStyle:{ fillStyle:"#225588",radius:7 },
			connector:[ "Flowchart", { stub:40 } ],
			connectorStyle:connectorPaintStyle,
			hoverPaintStyle:pointHoverStyle,
			connectorHoverStyle:connectorHoverStyle,
			isSource:true,
			maxConnections: -1
		};

		// listen for new connections; initialise them the same way we initialise the connections at startup.
		jsPlumb.bind("jsPlumbConnection", function(connInfo, originalEvent) { 
			connInfo.connection.getOverlay("label").setLabel("I am a signal");
		});

		//jsPlumb.draggable("gate-input");
		jsPlumb.draggable("gate-output");

		jsPlumb.makeSource("gate-input", synthbio.gui.outputEndpoint);
		jsPlumb.makeTarget("gate-output", synthbio.gui.inputEndpoint);
	});
});