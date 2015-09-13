$(function(){
			//获取div最高高度，然后设置给每一个div
			var height;
			var maxheight=0;
			$("div").each(function(){
				height=$(this).height();
				if(maxheight<height){
				maxheight=height;
				}
			});
			$("div").css("height",maxheight+15+"px");
			
			var textArray= new Array();
			var textArray1= new Array();
			//获取每一个div的内容，相同的设置相同样式
			$("div>.Cname").each(function(){
				
				if($(this).text()==""){
				}else{
					textArray.push($(this).text());
					//alert($(this).text());
					//$("div.Course_Name").addClass("Course_Name1");
				}
			
			});
			//读出数组内容，判断有没有一样的
			var page=1
			for(var i=0;i<textArray.length;i++){
				for(var j=i+1;j<textArray.length;j++){
					if(textArray[i]==textArray[j]){
						textArray.splice(j,1);//$('.play1:eq('+i+')').addClass("play");
						textArray1.push(textArray[i]);
						/*
						$("div>.Cname").each(function(k){
							if($(this).text()==textArray[i]){
								$('div.Course_Name:eq('+k+')').addClass("Course_Name"+page);	
							}
						});*/
	
					}
				}
			}
			//alert(textArray1.length);
			for(var i=0;i<textArray.length;i++){
				$("div>.Cname").each(function(k){
							if($(this).text()==textArray[i]){
								$('div.Course_Name:eq('+k+')').addClass("Course_Name"+page);	
							}
						});
						page++;
			}
					
			
			
		});
		
		
		
		