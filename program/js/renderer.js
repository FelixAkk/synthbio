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
	
	var proteins = new Array();
	
	synthbio.requests.getCDSs(function(response){
		$.each(response, function(i, cds){
			proteins[i] = cds.name; 
		});
	});
	
	//Check namespace = ok?
	synthbio.availableCDS = function(){
		var prot = proteins;
		//Improve this
		var taken = $(".wire").val();
		$.each(taken, function(i, used){
			var index = $.inArray(used,prot);
			if(index>-1){
				prot.splice(index,1);
			}
		});
		return prot;
	}
	
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

				var UUID = "input" + ++inputCounter;
				var src = jsPlumb.addEndpoint("gate-input", synthbio.gui.outputEndpoint, { anchor: "Continuous", uuid:UUID });
				jsPlumb.connect({source: src, target: opt.target});

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
			connCount++;
			connInfo.connection.getOverlay("label").setLabel("<a class=\"wires\" id=\"conn" + connCount + "\" href=#>Choose protein</a>");
			
			var el = $("#conn" + connCount, 0);
			el.html("<select class=\"wire\" id=\"wire"+connCount+"\">" +
					"<option value=\"0\" selected=\"selected\">Choose protein</option>" +
					"</select>"
			);
			
			el.on("click", function(){
				var prots='';
				var available = synthbio.availableCDS();
				var wire = $("#wire"+connCount);
				alert(available);
				$.each(available, function(i, cds){
					prots+="<option value=\""+cds+"\">"+cds+"</option> ";
				});
				wire.html(prots);
			});
		});

/*			var el = $("#conn" + connCount, 0);
			el.click(function(){
				var lp = $('#list-proteins');
				lp.modal("show");
				$('#list-proteins tbody').on("click", "tr", function() {
					lp.modal("hide");
					el.html($("td", this, 0).html());
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
		});

		//jsPlumb.draggable("gate-input");
		jsPlumb.draggable("gate-output");

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

		//Workaround for bug in jQuery/jsPlumb (Firefox only)
		jsPlumb.addEndpoint("grid-container").setVisible(false);
	});
});