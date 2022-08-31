function search() {
	$('#error').hide();
	$('#intro').hide();
	$('#listWords').empty();

	var allEnds = $('#word').val().trim().endsWith('*');
	var expected = normalize($('#word').val().trim()).toLowerCase();
	if (expected === '') {
		return;
	}

	var splitWords = true;
	if (expected.match(/^[a-z]+$/)) {
		// лацінкавы піньінь
	}else if (expected.match(/^[\u0400-\u04FF\'\-]+$/)) {
		// кірыліца
	}else if (expected.match(/^[\u4E00-\u9FFF]+$/)) {
		// CJK
		splitWords = false;
	} else {
		$('#error').text('Тэкст для пошуку мусіць утрымліваць толькі лацінку, альбо толькі кірыліцу, альбо толькі іерогліфы');
		$('#error').show();
		return;
	}

	//var list = new Array();
	$('#articles article').each(function() {
		var show;
		var th = $(this);
		var foundAttr = 'none';
		if (splitWords) {
			var headerText = normalize(th.children("ah").first().text());
			var header1Text = headerText.replaceAll(/[^a-z]/g, "")
			var text = normalize(th.text());
			var headerWords = headerText.split(/[^0-9a-z\u4E00-\u9FFF\u0400-\u04FF\'\-]/);//.split(/[\s\[\]\.,:«»;]/);
			var words = text.split(/[^0-9a-z\u4E00-\u9FFF\u0400-\u04FF\'\-]/);//.split(/[\s\[\]\.,:«»;]/);
			if (allEnds) {
				show = words.some(w => w.startsWith(expected));
				if (header1Text.startsWith(expected)) {
					foundAttr = 'header1';
				} else if (headerWords.some(w => w.startsWith(expected))) {
					foundAttr = 'header2';
				} else if (show) {
					foundAttr = 'text';
				}
			} else {
				show = words.some(w => w == expected);
				if (header1Text == expected) {
					foundAttr = 'header1';
				} else if (headerWords.some(w => w == expected)) {
					foundAttr = 'header2';
				} else if (show) {
					foundAttr = 'text';
				}
			}
		} else {
			var headerText = th.children("ah").first().text();
			var header1Text = headerText.replaceAll(/[^\u4E00-\u9FFF]/g, "")
			var text = th.text();
			show = text.indexOf(expected) >= 0;
			if (header1Text == expected) {
				foundAttr = 'header1';
			} else if (headerText.indexOf(expected) >= 0) {
				foundAttr = 'header2';
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
	var list = $('#articles article').filter(':visible');
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
	list.detach().appendTo($('#articles'));
}

// адкідаем дыякрытыку(з націскамі) і IPA
function normalize(text) {
	return text.toLowerCase().normalize('NFD').replaceAll('е\u0308', 'ё').replaceAll('и\u0306', 'й').replaceAll('у\u0306', 'ў').replaceAll(/[\u0300-\u036F\u0250-\u02AF]/g, "").replaceAll('*', '');
}

$(document).ready(function() {
	$('#searchform').attr('action', 'javascript:search()');
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
