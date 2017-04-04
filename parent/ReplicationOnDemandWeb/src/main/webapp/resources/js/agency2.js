function widgetizer() {
	$('.widget').find('h1').addClass("noTopGap sectionHeader");
	$('.widget').append('<p class="noBottomGap">&nbsp;</p></div>');
	$('.widget').wrapInner('<div class="inside" />');
	$('.widget').append('<div class="bottom-left"></div><div class="bottom-right">');
	$('.widget').prepend('<div class="top-left"></div><div class="top-right"></div>'); 

	$('.widget2').find('h1').addClass("noTopGap sectionHeader");
	$('.widget2').append('<p class="noBottomGap">&nbsp;</p></div>');
	$('.widget2').wrapInner('<div class="inside" />');
	$('.widget2').append('<div class="bottom-left"></div><div class="bottom-right">');
	$('.widget2').prepend('<div class="top-left"></div><div class="top-right"></div>'); 
	
}

function initMenu() {

	$("#mainMenuBar").css({zIndex : 2000});
	$(".topnav").css({zIndex : 2001});
	$(".topnav li").css({zIndex : 2002});
	$(".topnav li div").css({zIndex : 2003});
	$(".subnav").css({zIndex : 2004});
	$(".subnav li").css({zIndex : 2005});
	
	$("ul.topnav li div").mouseover(function() { //When trigger is hovered...
		//Following events are applied to the subnav itself (moving subnav up and down)
		$(this).parent().find("ul.subnav").slideDown('fast').show(); //Drop down the subnav on click
		$('a.nav',this).css('background', 'url(https://www.geointel.nga.mil/hp4/images/topnav_hover_dkr.gif) no-repeat center top');
		$('a.nav',this).css('color', '#FFD800');
		
		$(this).parent().hover(function() {
		}, function(){	
			$(this).parent().find("ul.subnav").slideUp('fast'); //When the mouse hovers out of the subnav, move it back up
			$('a.nav',this).css('background', 'none');
			$('a.nav',this).css('color', '#FFF');
		});
	});
	
	$("ul.topnav li div").keyup(function() { //When trigger is focused via tab key...
		//Following events are applied to the subnav itself (moving subnav up and down)
		$(this).parent().find("ul.subnav").slideDown('fast').show(); //Drop down the subnav on click
		$('a.nav',this).css('background', 'url(https://www.geointel.nga.mil/hp4/images/topnav_hover_dkr.gif) no-repeat center top');
		$('a.nav',this).css('color', '#FFD800');
	});
	
	$("ul.subnav li:last-child").focusout(function() {
		$(this).parent().parent().find("ul.subnav").slideUp('fast'); //When the key tabs out of the subnav, move it back up
		$('a.nav',$(this).parent().parent().parent()).css('background', 'none');
		$('a.nav',$(this).parent().parent().parent()).css('color', '#FFF');
	}); 

}
