$(function(){
		var len=$("li").length;	//获取多少个li
		var page1=len-1;
		var page=0;
		//alert(len);
		$('#myform ul li').each(function(i){
			
   			$(this).click(function(){
  			//$('#main').children('div:eq('+i+')').show().siblings('div').hide(); 
			alert(i+1);
		//$(".list_2").show();
			
  });
});
   		var scrollFunc = function (e) {
        var direct = 0;
		
        e = e || window.event;
        if (e.wheelDelta) {  //判断浏览器IE，谷歌滑轮事件             
            if (e.wheelDelta > 0) { //当滑轮向上滚动时
               // alert("滑轮向上滚动");
			 
				if(page==0){
						var ii=len-1
						$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
						page=len-1;
					}
					
				 else{
					  $('#main').children('div:eq('+page+')').prev().show().siblings('div').hide(); 									                      page--;
					 }
				
            }
			
            if (e.wheelDelta < 0) { //当滑轮向下滚动时
               // alert("滑轮向下滚动");
			  
				if(page==len-1){
					var ii=len-1
				   	$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
					page=0; 
				   }
				   else{
					   $('#main').children('div:eq('+page+')').show().siblings('div').hide(); 								                       page++;
					   }
            }
        } else if (e.detail) {  //Firefox滑轮事件
            if (e.detail> 0) { //当滑轮向上滚动时
               // alert("滑轮向上滚动");
				if(page==0){
						var ii=len-1
						$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
						page=len-1;
					}
					
				 else{
					  $('#main').children('div:eq('+page+')').prev().show().siblings('div').hide(); 									                      page--;
					 }
            }
            if (e.detail< 0) { //当滑轮向下滚动时
                //alert("滑轮向下滚动");
				if(page==len-1){
					var ii=len-1
				   	$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
					page=0; 
				   }
				   else{
					   $('#main').children('div:eq('+page+')').show().siblings('div').hide(); 								                       page++;
					   }
            }
        }
        ScrollText(direct);
    }
    //给页面绑定滑轮滚动事件
	/*
    if (document.addEventListener) {
        document.addEventListener('DOMMouseScroll', scrollFunc, false);
    }*/
    //滚动滑轮触发scrollFunc方法
	// = document.onmousewheel
    window.onmousewheel = scrollFunc;
	});  