$(function(){
		var len=$("li").length;	//��ȡ���ٸ�li
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
        if (e.wheelDelta) {  //�ж������IE���ȸ軬���¼�             
            if (e.wheelDelta > 0) { //���������Ϲ���ʱ
               // alert("�������Ϲ���");
			 
				if(page==0){
						var ii=len-1
						$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
						page=len-1;
					}
					
				 else{
					  $('#main').children('div:eq('+page+')').prev().show().siblings('div').hide(); 									                      page--;
					 }
				
            }
			
            if (e.wheelDelta < 0) { //���������¹���ʱ
               // alert("�������¹���");
			  
				if(page==len-1){
					var ii=len-1
				   	$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
					page=0; 
				   }
				   else{
					   $('#main').children('div:eq('+page+')').show().siblings('div').hide(); 								                       page++;
					   }
            }
        } else if (e.detail) {  //Firefox�����¼�
            if (e.detail> 0) { //���������Ϲ���ʱ
               // alert("�������Ϲ���");
				if(page==0){
						var ii=len-1
						$('#main').children('div:eq('+ii+')').show().siblings('div').hide();
						page=len-1;
					}
					
				 else{
					  $('#main').children('div:eq('+page+')').prev().show().siblings('div').hide(); 									                      page--;
					 }
            }
            if (e.detail< 0) { //���������¹���ʱ
                //alert("�������¹���");
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
    //��ҳ��󶨻��ֹ����¼�
	/*
    if (document.addEventListener) {
        document.addEventListener('DOMMouseScroll', scrollFunc, false);
    }*/
    //�������ִ���scrollFunc����
	// = document.onmousewheel
    window.onmousewheel = scrollFunc;
	});  