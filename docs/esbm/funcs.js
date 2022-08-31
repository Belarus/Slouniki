function search(inText) {
	console.log("search "+inText);
	$('#error').hide();
	$('#intro').hide();
	$('#listWords').empty();
	$('#btnSearch2').text("Шукаць '"+$('#word').val()+"' у тэксце артыкулаў");
	$('#btnSearch2').show();

	var allEnds = $('#word').val().trim().endsWith('*');
	var expected = normalize($('#word').val().replaceAll('*', '').trim());
	if (expected === '') {
		return;
	}

	$('#articles article').each(function() {
		var show;
		var th = $(this);
		var foundAttr = 'none';
		var headerText = normalize(th.children("ah").first().text());
		var text = inText ? normalize(th.text()) : "";
		var headerWords = headerText.split(/[^0-9a-z\u0400-\u04FF\'\-]/);//.split(/[\s\[\]\.,:«»;]/);
		var words = inText ? text.split(/[^0-9a-z\u0400-\u04FF\'\-]/) : [];
		if (allEnds) {
			show = inText ? words.some(w => w.startsWith(expected)) : false;
			if (headerText.startsWith(expected)) {
				foundAttr = 'header1';
				show = true;
			} else if (headerWords.some(w => w.startsWith(expected))) {
				foundAttr = 'header2';
				show = true;
			} else if (show) {
				foundAttr = 'text';
			}
		} else {
			show = inText ? words.some(w => w == expected) : false;
			if (headerText == expected) {
				foundAttr = 'header1';
				show = true;
			} else if (headerWords.some(w => w == expected)) {
				foundAttr = 'header2';
				show = true;
			} else if (show) {
				foundAttr = 'text';
			}
		}
		if (show) {
			th.show();
		} else {
			th.hide();
		}
		th.attr('where', foundAttr);
	});
	/*var list = $('#articles article').filter(':visible');
	list.sort((a,b) => {
		var wa = a.getAttribute('where');
		var wb = b.getAttribute('where');
		if (wa == wb) {
			return parseInt(a.getAttribute('order')) - parseInt(b.getAttribute('order'));
		} else if (wa < wb) {
			return -1;
		} else {
			return 1;
		}
	});
	list.detach().appendTo($('#articles'));*/
}

function normalize(text) {
	return text.toLowerCase().replaceAll("\u0301", "");
}

$(document).ready(function() {
	$('#searchform').attr('action', 'javascript:search(false)');
	$('#btnSearch2').on('click', function() {search(true);});
	$('#word').keypress(function(e) {
		if (e.keyCode == 13) {
			e.preventDefault();
			$('#btnSearch').click();
		}
	});
	$('body').click(function(e) {
		if (!$(e.target).parents('.popover').length) {
			$('.popover').popover('hide');
		}
	});
	$(".expand").hide();
	$(".expandShow").attr("href", "#");
	$(".expandHide").attr("href", "#");
	$(".expandShow").click(function() {
		$(this).closest("div").find(".expand").show();
		$(this).hide();
		return false;
	});
	$(".expandHide").click(function() {
		$(this).closest("div").find(".expand").hide();
		$(this).closest("div").find(".expandShow").show();
		return false;
	});
	$(".accordion-button2").click(function(e) {
		setTimeout(function() {
			$('#intro')[0].scrollIntoView();
		}, 300);
	});
	var order = 1;
	$('#articles article').each(function() {
		$(this).attr('order', order);
		order++;
	});
	$("span[title]").attr('tabindex', 0);
	$("span[title]").focus(e => {
		$("#titleshow").text(e.target.getAttribute("title"));
		var offsets = e.target.getBoundingClientRect();
		$("#titleshow").css({top: offsets.bottom, left: offsets.left}).show();
	});
	$("span[title]").blur(e => {
		$("#titleshow").hide();
	});
	$('#input').show();
});
