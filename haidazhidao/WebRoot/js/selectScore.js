$(function(){
			$(".scoreName").each(function(i){
				$(this).toggle(function(){
					$(this).css("backgroundColor","#eee");
					$('.info:eq('+i+')').show(100);
					$(this).parent().siblings().find("div").hide();
					$(this).parent().siblings().find("h4").removeClass("pause");
					$(this).parent().siblings().find("h4").addClass("play");
					$(this).parent().siblings().find("td").css("backgroundColor","transparent");
					//alert($('.play1').css("id","play"+i));
					$('.play1:eq('+i+')').addClass("pause");
					$('.play1:eq('+i+')').removeClass("play");
				},function(){
					$('.info:eq('+i+')').hide();
					$('.play1:eq('+i+')').addClass("play");
					$('.play1:eq('+i+')').removeClass("pause");
					$(this).css("backgroundColor","transparent");
				});
			});
		});$(function(){
			$(".scoreName").each(function(i){
				$(this).toggle(function(){
					$('.info:eq('+i+')').show(100);
					//alert($('.play1').css("id","play"+i));
					$('.play1:eq('+i+')').addClass("pause");
					$('.play1:eq('+i+')').removeClass("play");
				},function(){
					$('.info:eq('+i+')').hide();
					$('.play1:eq('+i+')').addClass("play");
					$('.play1:eq('+i+')').removeClass("pause");
				});
			});
		});