var HC = HC || {};

$(function() {
  console.log("start");
  $('.btn-conversion').click(HC.Conversion.convert);
  
});


HC.Conversion = {
		convert: function() {
		    var selectColumns = [];
		    
		    var value = $('#outParam').val();
		    var array =[];
		    if (value.indexOf(',')>=0) {
		    	array = value.trim().split(',');
		    } else if (value&&value.trim().length) {
		    	array.push(value.trim());
		    }
		    
		    array.forEach(function(item) {
		      if (item&&item.trim()) {
		    	  selectColumns.push({
	  		        columnName: item.trim(),
	  		        comment: ''
	  		      });
	  		      console.log(item.trim());
		      }
  		    });
		    console.log(selectColumns);
		    
		    var whereColumns = [];
		    value = $('#inParam').val();
		    array =[];
		    if (value.indexOf(',')>=0) {
		    	array = value.trim().split(',');
		    } else if (value&&value.trim().length) {
		    	array.push(value.trim());
		    }
		    
		    array.forEach(function(item) {
		      if (item&&item.trim()) {
		    	  whereColumns.push({
	  		        columnName: item.trim(),
	  		        comment: ''
	  		      });
	  		      console.log(item.trim());
		      }
  		    });
		    console.log(whereColumns);

		    if(!$('#serviceId').val()) {
		      $('#serviceId').css('background','red');
		      return;
		    } else {
		    	$('#serviceId').css('background','white');
		    }
		    if(!$('#author').val()) {
		    	$('#author').css('background','red');
		      return;
		    } else {
		    	$('#author').css('background','white');
		    }
		    if(selectColumns.length === 0) {
		    	$('#outParam').css('background','red');
		      return;
		    }else {
		    	$('#outParam').css('background','white');
		    }
		    if(whereColumns.length === 0) {
		    	$('#inParam').css('background','red');
		      return;
		    }else {
		    	$('#inParam').css('background','white');
		    }
		    
		    HC.Conversion.sendRequest({
		      data: JSON.stringify({
		        selectColumns: selectColumns,
		        whereColumns: whereColumns,
		        serviceId: $('#serviceId').val(),
		        author: $('#author').val(),
		        procedureName: $('#procedureName').val(),
		      }),
		      url: '/convert',
		      success: function(downloadId) {
		        // Download
		        window.location.href = '/download' + '?downloadId=' + downloadId;
		      }
		    });
		  },
		  
		  sendRequest: function (options) {
			    var settings = $.extend(
			        {
			          type: 'POST',
			        },
			        HC.Conversion.ajaxCommonOptions(options),
			        options,
			        {
			          success: function (retData) {
			            if (retData.success) {
			              options.success && options.success(retData.response);
			            } else {
			              if (options.error) {
			                options.error(retData.messages);
			              } else {
			                console.log('Error request');
			              }
			            }
			          }//success
			        }
			    );
			    $.ajax(settings);
			  },
			  
			  ajaxCommonOptions: function (options) {
				    return {
				      contentType: 'application/json; charset=utf-8',
				      async: true,
				      cache: false,
				      processData: false,
				      error: function (jqXHR, textStatus, errorThrown) {
				        if (options.error) {
				          options.error(jqXHR, textStatus, errorThrown);
				        } else {
				          console.log(jqXHR, textStatus, errorThrown);
				        }
				      },
				      complete: function (retData) {
				        if (options.complete) {
				          options.complete(retData);
				        }
				      }
				    };
			  }
};