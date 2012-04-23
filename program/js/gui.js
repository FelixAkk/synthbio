// GUI JavaScript Document
/**
 * One date object that can be used wherever we need time
 */
var date = new Date();

/**
 * Ping server to check for connection 'vitals'. Shown a warning if things go really bad.
 */
function pingServer() {
	var t = date.getTime();
	$.ajax("/Ping")
		.done(function(data) {
			$('#ping').html('Server status: <b>Connected to server <em class="icon-connected"></em> [latency: ' + (date.getTime() - t) + 'ms]</b>');
			this.fCount = 0; // On success, reset to 0 to restart counting.
		})
		.fail(function(data) {
			$('#ping').html('Server status: <b>Warning: not connected to server! <em class="icon-failed"></em></b>');
			$('#ping').attr('class', 'failed');
			// Keep counting untill the dialog was shown
			if(this.fCount  <= this.limit+1) {
				this.fCount++;
			}
			// Show the dialog once
			if(this.fCount == this.limit) {
				$('#connection-modal').modal();
			}
		})
		.always(function() { setTimeout(pingServer, this.frequency); });
}
// Configure/initialize function by setting properties
pingServer.fCount = 0; // Failure count: The amount of times that connection attempts have failed. Resets to 0 on success.
pingServer.limit = 3; // Amount of times after which a dialog should prompt the user about the failures.
pingServer.frequency = 5000 // The delay between ping calls in milliseconds.

$(document).ready(function() {
	
	/**
	 * Activate zhe Dropdowns Herr Doktor!
	 */
	$('.dropdown-toggle').dropdown();
	
	/**
	 * Load proteins from server.
	 */
	$('#list-proteins').on('show', function(){
		synthbio.requests.getCDSs(function(response){
			if(response instanceof String){
				$('#list-proteins tbody td').html(data.response);
				return;
			}
			var html='';
			$.each(response, function(i, cds){
				html+='<tr><td>'+cds.name+'</td><td>'+cds.k2+'</td><td>'+cds.d1+'</td><td>'+cds.d2+'</td></tr>';
			});
			$('#list-proteins tbody').html(html);
		});
			
	});


	setTimeout(pingServer, 500);
});
