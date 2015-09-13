$(function(){
		//主楼信息采集分类
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='2'){
				$('.room2').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='3'){
				$('.room3').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='4'){
				$('.room4').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='5'){
				$('.room5').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='6'){
				$('.room6').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='7'){
				$('.room7').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info').each(function(i){
				if($('.info:eq('+i+')').html().substr(2,1)=='8'){
				$('.room8').append($('.info:eq('+i+')').html()+"<br/><br/>");
			};
			});
			//中海楼信息采集分类
			$('.info_1').each(function(i){
				if($('.info_1:eq('+i+')').html().substr(3,2)=='03'){
				$('.room31').append($('.info_1:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info_1').each(function(i){
				if($('.info_1:eq('+i+')').html().substr(3,2)=='04'){
				$('.room41').append($('.info_1:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info_1').each(function(i){
				if($('.info_1:eq('+i+')').html().substr(3,2)=='05'){
				$('.room71').append($('.info_1:eq('+i+')').html()+"<br/><br/>");
			};
			});
			
			$('.info_1').each(function(i){
				if($('.info_1:eq('+i+')').html().substr(3,2)=='06'){
				$('.room81').append($('.info_1:eq('+i+')').html()+"<br/><br/>");
			};
			});
			//下拉列表选项事项
			var selectInfo;
				$('#select1').change(function(){
					selectInfo=$('#select1').val();
					if(selectInfo=='二楼'){
						$('.room2').show();
						$('.room2').siblings('div').hide();
					}
					
					if(selectInfo=='三楼'){
						$('.room3').show();
						$('.room3').siblings('div').hide();
					}
					
					if(selectInfo=='四楼'){
						$('.room4').show();
						$('.room4').siblings('div').hide();
					}
					
					if(selectInfo=='五楼'){
						$('.room5').show();
						$('.room5').siblings('div').hide();
					}
					
					if(selectInfo=='六楼'){
						$('.room6').show();
						$('.room6').siblings('div').hide();
					}
					
					if(selectInfo=='七楼'){
						$('.room7').show();
						$('.room7').siblings('div').hide();
					}
					
					if(selectInfo=='八楼'){
						$('.room8').show();
						$('.room8').siblings('div').hide();
					}
				});
				
				$('#select2').change(function(){
					selectInfo=$('#select2').val();
					
					if(selectInfo=='三楼'){
						$('.room31').show();
						$('.room31').siblings('div').hide();
					}
					
					if(selectInfo=='四楼'){
						$('.room41').show();
						$('.room41').siblings('div').hide();
					}
					
					if(selectInfo=='五楼'){
						$('.room51').show();
						$('.room51').siblings('div').hide();
					}
					
					if(selectInfo=='六楼'){
						$('.room61').show();
						$('.room61').siblings('div').hide();
					}
					
				});
		});