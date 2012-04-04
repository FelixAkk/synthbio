// GUI JavaScript Document

$(document).ready(function() {
	
	/**
	 * Activate zhe Dropdowns Herr Doktor!
	 */
	$('.dropdown-toggle').dropdown();
	
	/**
	 * Load proteins from server.
	 */
	$('#list-proteins').on('show', function(){
		$.ajax({
			url: "/ListProteins",
			dataType: "json",
			success: function(response){
				//on failure, display error message
				if(!response.success){
					$('#list-proteins tbody td').html(data.response);
					return;
				}
				
				//display data in the table
				var rows='';
				for(i in response.data){
					var row=response.data[i];
					rows+='<tr>'+
						'<td>'+row.name+'</td>'+
						'<td>'+row.k2+'</td>'+
						'<td>'+row.d1+'</td>'+
						'<td>'+row.d2+'</td></tr>';
				}
				$('#list-proteins tbody').html(rows);
			}
		});
			
	});
});
