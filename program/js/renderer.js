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

		var inputCounter = 0;
		// the definition of input endpoints
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
					opt.connection.sourceId != "gate-input" || 
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

		// the definition of target endpoints
		synthbio.gui.outputEndpoint = {
			endpoint:"Dot",
			paintStyle:{ fillStyle:"#225588",radius:7 },
			//connector:[ "Flowchart", { stub:40 } ],
			connector: ["Bezier", { curviness:50 } ],
			connectorStyle:connectorPaintStyle,
			hoverPaintStyle:pointHoverStyle,
			connectorHoverStyle:connectorHoverStyle,
			isSource:true,
			maxConnections:-1
		};

		var connCount = 0;
		// listen for new connections; initialise them the same way we initialise the connections at startup.
		jsPlumb.bind("jsPlumbConnection", function(connInfo, originalEvent) {
			var signal = connInfo.connection.getParameters().signal;
			signal = (signal) ? signal : synthbio.gui.displayConnection(connInfo.connection).signal;

			signal.fromEndpoint = synthbio.gui.getEndpointIndex(connInfo.connection.endpoints[0]);
			signal.toEndpoint = synthbio.gui.getEndpointIndex(connInfo.connection.endpoints[1]);

			connCount++;
			var lbl = "<a id=\"conn" + connCount + "\" href=#>";
			lbl += (signal.protein) ? signal.protein : "Choose protein";
			lbl += "</a>";
			connInfo.connection.getOverlay("label").setLabel(lbl);
			
			var el = $("#conn" + connCount, 0);
			synthbio.requests.getCDSs(function(response){
				var proteins='';
				$.each(response, function(i, cds){
					proteins+='<li><a href=\"#\">'+cds.name+'</a></li>';
				});
				el.html("<ul class=\"nav\">" +
					"<li class=\"dropdown\">" +
						"<a href=\"#\" class=\"dropdown-toggle\" data-toggle=\"dropdown\" id=\""+  connCount  +"\">Choose protein<b class=\"caret\"></b></a>"+
							"<ul class=\"dropdown-menu\">" +
								proteins +	
							"</ul> " +
						"</li>" +
					"</ul>"
				);
			});
			el.on("click", function(){
				$(connCount).textContent= "bla";
			});
		});

/*			var el = $("#conn" + connCount, 0);
			el.click(function(){
				var lp = $('#list-proteins');
				lp.modal("show");
				$('#list-proteins tbody').on("click", "tr", function() {
					lp.modal("hide");
					
					var prot = $("td", this, 0).html();
					el.html(prot); //Update label
					signal.protein = prot; //Update signal in model
				});
			});
		});

		$('#list-proteins').on('hide', function(){
			$('#list-proteins tbody').off("click", "tr");
		});
*/

		// listen for disposal of connections; delete endpoints if necessary
		jsPlumb.bind("jsPlumbConnectionDetached", function(connInfo, originalEvent) {
			if (connInfo.sourceId == "gate-input" && !connInfo.sourceEndpoint.connections.length)
				jsPlumb.deleteEndpoint(connInfo.sourceEndpoint);
			if (connInfo.targetId == "gate-output" && !connInfo.targetEndpoint.connections.length)
				jsPlumb.deleteEndpoint(connInfo.targetEndpoint);

			synthbio.gui.removeDisplaySignal(connInfo.connection.id);
		});

		jsPlumb.draggable("gate-input", {handle: "h4"});
		//$("gate-output").draggable({ });
		jsPlumb.draggable("gate-output", {handle: "h4", start: function() { $(".output").css("right", "auto");}});

		var oep = jQuery.extend(true, {	
			anchor:"Continuous",
			deleteEndpointsOnDetach: false
		}, synthbio.gui.outputEndpoint);
		var iep = jQuery.extend(true, {
			anchor:"Continuous",
			deleteEndpointsOnDetach: false
		}, synthbio.gui.inputEndpoint);

		jsPlumb.makeSource("gate-input", oep);
		jsPlumb.makeTarget("gate-output", iep);

		synthbio.gui.reset();
	});
});