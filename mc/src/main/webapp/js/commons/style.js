/**
 * style
 * 涉及 style css 操作等等
 * 
 * author: taosheng
 */ 

/**
 * 是否可见
 */
function isVisible(id){
	id = startWith(id, '#');
	if(id==''){
		return;
	}
	
	return $(id).is(":visible");
}
 
/**
 * 若隐藏则pop显示，若显示则隐藏 
 */
function toggleAndPopItem(event, id, toDisplayOnTop) {  
	id = startWith(id, '#');
	if(id==''){
		return;
	}
	
	if($(id).is(":visible")){
		$(id).hide(); 
	}else{
		popItemLocation(event, id, toDisplayOnTop);
	}
	
	onLoadWindowSelf();
}

/**
 * 设置div的弹出位置 
 */
function popItemLocation(event, id, toDisplayOnTop) {  
	id = startWith(id, '#');
	if(id==''){
		return;
	}
	
	$(id).show();
	
	var oEvent = event;
	var oLeft = oEvent.pageX ;
	var oTop = oEvent.pageY ;
	
	if(isOrUndefinedNull(toDisplayOnTop)){
		toDisplayOnTop = true;
	}
	
	var height = $(id).outerHeight();
	if(isOrUndefinedNull(height)){
		height = 0;
	}else if(isNaN(height)){
		height = parseInt(height);
	}
	
	var newHeight = oTop - height;
	if(newHeight < 0){
		if(toDisplayOnTop==true){
			newHeight = 0;
		}else{
			screenY = oEvent.screenY;
			newTop = Math.min(oTop, screenY);
			newHeight = oTop - newTop/3 ;
		}
	}
	
	$(id).css("position", "absolute");
	$(id).css("left", "8%");
	$(id).css("top", (newHeight) + "px");
	$(id).css("width", "80%");
	$(id).css("z-index", "1999");
	// $(id).css("margin", "0 auto");
	
	return false;
}

/**
 * 水平 滚动条的宽度 .horizontal
 * 垂直 滚动条的宽度 .vertical
 */
var __scrollBarWidth = null;
function getScrollBarWidth() {
	if (__scrollBarWidth) return __scrollBarWidth;
	
	var scrollBarHelper = document.createElement("div");
	// if MSIE
	// 如此设置的话，scroll bar的最大宽度不能大于100px（通常不会）。
	scrollBarHelper.style.cssText = "overflow:scroll;width:100px;height:100px;"; 
	// else OTHER Browsers:
	// scrollBarHelper.style.cssText = "overflow:scroll;";
	document.body.appendChild(scrollBarHelper);
	if (scrollBarHelper) {
		__scrollBarWidth = {
			horizontal: scrollBarHelper.offsetHeight - scrollBarHelper.clientHeight,
			vertical: scrollBarHelper.offsetWidth - scrollBarHelper.clientWidth
		};
	}
	document.body.removeChild(scrollBarHelper);
	return __scrollBarWidth;
}


/**
 * 垂直 滚动条的宽度 .vertical
 * 就是浏览器的边框，当我们在获取页面的offsetHeight高度时是包括了浏览器的边框的，浏览器的边框是2个像素，
 * 所以这时无论在任何情况下clientHeight 始终是小于offsetHeight的，这就使得即使没有滚动条它也为true,
 * 因此我们要修正这个错误，代码应该这样改，在offsetHeight上减去4个像素
 */
function hasVerticalScrollBar() {
	return document.documentElement.clientHeight < document.documentElement.offsetHeight-4 ;
}


/**
 * 水平 滚动条的宽度 .horizontal
 */
function hasHorizontalScrollBar() {
	return document.documentElement.clientWidth < document.documentElement.offsetWidth-4 ;
}


/**
 * 获取style
 * getStyle(document.getElementById("box1"), 'marginTop')
 * ## style区别，重要
 * document.getElementById("mainFrame").parentNode.parentNode.parentNode.style.paddingLeft
 * 2%
 * document.defaultView.getComputedStyle(document.getElementById("mainFrame").parentNode.parentNode.parentNode, null)['paddingLeft']
 * 32.15px
 */
function getStyle(obj,attr){
	var ie = !+"\v1";//简单判断ie6~8
	if(attr=="backgroundPosition"){//IE6~8不兼容backgroundPosition写法，识别backgroundPositionX/Y
		if(ie){
			return obj.currentStyle.backgroundPositionX +" "+obj.currentStyle.backgroundPositionY;
		}
	}
	if(obj.currentStyle){
		return obj.currentStyle[attr];
	}else{
		return document.defaultView.getComputedStyle(obj,null)[attr];
	}
}


/**
 * 计算像素值
 * 307.2px --> 307.2
 */
function pxOfStyle(pxStyle) {
	pxStyle = parseFloat(pxStyle);
	if(isNaN(pxStyle)){
		return 0;
	}
	return pxStyle ;
}


/**
 * MBP: margin border padding
 */
function pxOfMBPOrientation(obj,orientation) {
	if(isOrUndefinedNullEmpty(obj)){
		return 0;
	}
	var mbps = new Array('margin','border','padding');
	var pxValue = 0;
	var pxStyle;
	for(var i in mbps){
		pxStyle = getStyle(obj,mbps[i]+orientation)
		pxValue = pxValue + pxOfStyle(pxStyle);
	}
	return pxValue ;
}
function pxOfMBPLeft(obj) {
	return pxOfMBPOrientation(obj,'Left') ;
}
function pxOfMBPRight(obj) {
	return pxOfMBPOrientation(obj,'Right') ;
}
function pxOfMBPTop(obj) {
	return pxOfMBPOrientation(obj,'Top') ;
}
function pxOfMBPBottom(obj) {
	return pxOfMBPOrientation(obj,'Bottom') ;
}


/**
 * mbp宽度，至 head/body
 * var element = document.getElementById("mainFrame")
 */
function pxOfMBPWidthToParent(element) {
	if(isOrUndefinedNullEmpty(element)){
		return 0;
	}
	var nodeName = element.nodeName.toUpperCase();
	if(isOrUndefinedNullEmpty(nodeName) || nodeName=='HEAD' || nodeName=='BODY'){
		return 0;
	}
	
	var pxValue = pxOfMBPLeft(element) + pxOfMBPRight(element);
	pxValue = pxValue + pxOfMBPWidthToParent(element.parentNode);
	return pxValue ;
}
