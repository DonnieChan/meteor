/**
 * iframe
 * 初始化、设置 iframe的高宽度 等等
 * 
 * author: taosheng
 */ 

/** 初始化设置iframe */
function initSetIFrame(iFrameId) {
	return;
	if(isOrUndefinedNullEmpty(iFrameId)){
		return;
	}
	var iframe = document.getElementById(iFrameId);
	iframe.height = iframe.contentWindow.document.documentElement.scrollHeight;
	reSetIFrameHeight(iFrameId);
}

/** 重设 MainFrame 高度 */
function setMainFrameHeight(iFrameId) {
	reSetIFrameHeight(iFrameId);
}

/** 重设 iframe 高度 */
function reSetIFrameHeight(iFrameId) {
	if(isOrUndefinedNullEmpty(iFrameId)){
		return;
	}
	var iFrameDoc = document.getElementById(iFrameId).contentWindow.document;
	var popHeight = getAutoFitPageHeightByName(iFrameDoc);
	
	var scrHeight = 0; // iFrameDoc.documentElement.scrollHeight;
	var minHeight = window.top==window.self ? minMainFrameHeight() : 900;
	
	var curHeight = Math.max(popHeight, Math.max(scrHeight, minHeight));
	document.getElementById(iFrameId).height = curHeight;
	
	var juiId = "div#" + iFrameId + "Jeasyui"; // div#scheduleFrameJeasyui
	var jui = $(juiId);
	if(isLegalJQueryObject(jui)){
		var bottomIdInJui = "div[name='beCalledToReSetIFrameSizeOnParentPage']";
		var juiHeight = getAutoFitJeasyuiHeightByName(juiId, bottomIdInJui) + 5; // 禁止为0，border的高度 in jeasyui
		
		// 
		jui.css('height', juiHeight);
		
		// var juiRegion = $(juiId + " > div[class*=layout-panel-]"); // layout-panel-  后面故意有减号
		$(juiId + " > div[class*=layout-panel-west]").css('height', juiHeight);
		$(juiId + " > div[class*=layout-panel-east]").css('height', juiHeight);
		$(juiId + " > div[class*=layout-panel-center]").css('height', juiHeight);
	}
}

/** 计算页面最合理的高度 */
function getAutoFitPageHeightByName(doc, name) {
	if(isOrUndefinedNull(doc)){
		return 0;
	}
	
	var customPageHeight = getAutoFitPageHeightByCustomTag(doc, name);
	if(customPageHeight == 0){
		return $(doc).outerHeight(); // doc.documentElement.scrollHeight;
	}else{
		return customPageHeight + 10; // 预留给未知的高度，内嵌的iframe的高度 比 父页面 少5px
	}
}

/** 计算页面最合理的高度，利用name为"getNameOfReSetIFrameSizeOnParent()"的div的高度 */
function getAutoFitPageHeightByCustomTag(doc, name) {
	if(isOrUndefinedNull(doc)){
		return 0;
	}
	if(isOrUndefinedNullEmpty(name)){
		name = getNameOfReSetIFrameSizeOnParent();
	}
	
	var items = doc.getElementsByName(name);
	return maxOffsetHeightByJQueryObject(items);
}

/** name为"beCalledToReSetIFrameSizeOnParentPage" */
function getNameOfReSetIFrameSizeOnParent(){
	return "beCalledToReSetIFrameSizeOnParentPage";
}


/** 重设宽度 */
function reSetIFrameWidth(iFrameId){
	if(isOrUndefinedNull(iFrameId)){
		return ;
	}
	document.getElementById(iFrameId).width = document.getElementById(iFrameId).contentWindow.document.documentElement.scrollWidth + getScrollBarWidth().vertical;
}

/** mainFrame的最小高度 */
function minMainFrameHeight() {
	var cltHeight;
	var topHeight = window.top.innerHeight;
	if( typeof( topHeight ) == 'number' ){
		cltHeight = topHeight - 150;
	}else{
		cltHeight = window.screen.availHeight - 150; // document.body.clientHeight;
	}
	return cltHeight;
}

/**
 * 修改iframe高宽度
 */
function onLoadWindowParent() {
	if(window.parent!=null && window.parent!=window.self){
		window.parent.onLoadWindowSelf();
	}
}

/** 页面及相关的高宽度信息 */
function infoOfHeightWidth() {
	var info = 
			'Width: ' + document.body.clientWidth
			+ ', Height: ' + document.body.clientHeight
			+ ', ' + document.getElementById("${iframeId}").height 
			+ ', ' + document.getElementById("${iframeId}").contentWindow.document.documentElement.scrollHeight 
		;
	return info;
}


/**
 * 修改iframe高宽度 old
 */
function reSizeIFrame_deleted() {
	if(window.parent!=null && window.parent!=window.self){
		window.parent.onLoadIframe2();
	}
}
function reSizeIFrameToTop_deleted() {
	resetTopIframeWidth("mainFrame");
	if(window.parent!=null && window.parent!=window.self){
		window.parent.onLoadIframe2();
	}
}
function reSizeIFrameWidthBack_deleted(iframe) {
	if(isOrUndefinedNullEmpty(iframe)){
		return;
	}
	document.getElementById(iframe).width = '100%';
	if(window.parent!=null && window.parent!=window.self){
		window.parent.reSizeIFrameWidthBack(iframe);
	}
	if(window.parent!=null && window.parent==window.self){
		resetTopIframeWidth(iframe);
	}
}


/**
 * 优化宽度    old
 */
function resetTopIframeWidth(iframe){
	if(isOrUndefinedNull(iframe)){
		return ;
	}
	var vertScrollBarWidth = getScrollBarWidth().vertical;
	var element = document.getElementById(iframe);
	element.width = window.top.screen.width - vertScrollBarWidth - pxOfMBPWidthToParent(element);
	// document.getElementById("mainFrame").width = '100%';
	// document.getElementById("mainFrame").contentWindow.document.getElementById("flowFrame").width = '100%';
}

