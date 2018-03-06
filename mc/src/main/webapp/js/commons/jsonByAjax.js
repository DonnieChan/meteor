/**
 * jsonByAjax.js
 * 通过ajax获取json数据
 * 
 * author: taosheng
 */ 

/**   */

/**
 * 查询项目的展示描述信息
 */
function getDataForAjaxFromTreeNode(treeNode){ 
	var projectId = treeNode.ext['projectId'];
	var fileId = treeNode.ext['fileId'];
	var data = { "projectId":projectId, "fileId":fileId, "dtime":new Date().format("yyyyMMddhhmmss") };
	
	return data;
}
	
/**
 * 查询项目的展示描述信息
 */
function getInfoOnViewById(ctx, treeNode){ 
	var url = ctx + '/schedule/getInfoOnViewById.do';
	var data = getDataForAjaxFromTreeNode(treeNode);
	var rValue = getJsonResultByAjax(url, data);
	
	return rValue;
}


/**
 * 查询项目的展示描述信息
 */
function defFileSysExtByTreeNode(ctx, treeNode, ignoreError){ 
	if(isOrUndefinedNull(ignoreError)){
		ignoreError = true;
	}
	
	var url = ctx + '/schedule/getModelById.do';
	var data = getDataForAjaxFromTreeNode(treeNode);
	var rValue = getJsonResultByAjax(url, data);
	
	return rValue;
}
function defFileSysExtByFileId(ctx, fileId, ignoreError){ 
	if(isOrUndefinedNull(ignoreError)){
		ignoreError = true;
	}
	
	var url = ctx + '/schedule/nPId/getFileById.do';
	var data = { "fileId":fileId, "dtime":new Date().format("yyyyMMddhhmmss") };
	var rValue = getJsonResultByAjax(url, data, ignoreError);
	
	return rValue;
}

/**
 * 查询项目的展示描述信息
 */
function getJsonResultByAjax(url, data, ignoreError){ 
	if(isOrUndefinedNull(ignoreError)){
		ignoreError = true;
	}
	
	var succes = 0;
	var rValue = null;
	if(isOrUndefinedNull(url) || isOrUndefinedNull(data)){
		return rValue;
	}
	
	$.ajax({
		type: "POST",
		url: url,
		data: data,
		async: false,
		cache: false,
		dataType: "json",
		success: function(result){
			succes = 1;
			rValue = result;
		},
		error: function(XMLHttpRequest, textStatus, errorThrown){
			if(ignoreError){
				return null;
			}else{
				alert(errorThrown);
			}
		}
	});
	
	return rValue;
}
