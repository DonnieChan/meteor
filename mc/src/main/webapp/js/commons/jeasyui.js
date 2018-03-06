/**
 * jeasyui
 * 
 * author: liuchaohong
 */ 

/**
 * 重设高度
 */
function setJeasyuiHeight(id){
	id = startWith(id, '#');
	if(id==''){
		return;
	}
	
	var c = $(id);
	var p = c.layout('panel','center');	// get the center panel
	var oldHeight = p.panel('panel').outerHeight();
	p.panel('resize', {height:'auto'});
	var newHeight = p.panel('panel').outerHeight();
	c.layout('resize',{
		height: (c.height() + newHeight - oldHeight)
	});
}


/**
 * 合法有效的jquery对象
 */ 
function isLegalJQueryObject(jQObj){
	if(isOrUndefinedNull(jQObj) || jQObj.length==0){
		return false;
	}
	
	return true;
}

/** maxOffsetHeightByJQueryObject */
function maxOffsetHeightByJQueryObject(items) {
	if(isOrUndefinedNull(items)){
		return 0;
	}
	
	if(items.length==0){
		return 0;
	}
	
	var itemJq;
	var maxHeight = 0, itemHeight = 0;
	for(var i = 0; i < items.length; i++){
		itemJq = $(items[i]);
		if(!isLegalJQueryObject(itemJq) || itemJq.is(":visible")==false){
			itemHeight = 0;
		}else{
			itemHeight = itemJq.offset().top + itemJq.outerHeight();
		}
		maxHeight = Math.max(maxHeight, itemHeight);
	}
	
	return maxHeight;
}

/**
 * juiId bottomIdInJui 封装好了的
 * juiId --> div#scheduleFrameJeasyui 
 * bottomIdInJui --> div[name='beCalledToReSetIFrameSizeOnParentPage']
 * getAutoFitJeasyuiHeightByName("div#scheduleFrameJeasyui", "div[name='beCalledToReSetIFrameSizeOnParentPage']")
 * maxOffsetHeightByJQueryObject($("div#scheduleFrameJeasyui"))
 * maxOffsetHeightByJQueryObject($("div#scheduleFrameJeasyui div[name='beCalledToReSetIFrameSizeOnParentPage']"))
 *
 * 	class="easyui-layout layout easyui-fluid"  id='${iFrameId}Jeasyui'
 * 		class="panel layout-panel layout-panel-west layout-split-west easyui-fluid"
 * 			class="panel-header"
 * 			class="panel-body layout-body"  data-options="region:'west',split:true"  id='${iFrameId}JeasyuiWest'
 * 		class="panel layout-panel layout-panel-center"
 * 			class="panel-body panel-body-noheader layout-body"  data-options="region:'center'"  id='${iFrameId}JeasyuiCenter'
 */
function getAutoFitJeasyuiHeightByName(juiId, bottomIdInJui){
	if(isOrUndefinedNullEmpty(juiId) || isOrUndefinedNullEmpty(bottomIdInJui)){
		return 0;
	}
	
	var juiIdTop = $(juiId).offset().top;
	var bottomHeight = maxOffsetHeightByJQueryObject($(juiId+' '+bottomIdInJui));
	var juiRegionHeight = maxOffsetHeightByJQueryObject($(juiId+" div[data-options*='region:']"));
	var juiBodyHeight = maxOffsetHeightByJQueryObject($(juiId+" div[class*='panel-body'][class*='layout-body']"));
	var juiMaxHeight = Math.max(bottomHeight, Math.max(juiRegionHeight, juiBodyHeight));
	var juiHeight = juiMaxHeight - juiIdTop;
	
	return juiHeight; 
}
