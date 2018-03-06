/**
 * ztree
 * ztree的功能补充
 * 
 * author: taosheng
 */ 

/** 展开 全部节点 
 * expandNode (node, expandFlag, sonSign, focus, callbackFlag)
 */
function expandAllNodes(treeId, expandFlag) {
	var treeObj = $.fn.zTree.getZTreeObj(treeId);
	var nodes = treeObj.getNodes();
	expandNodes(treeObj, nodes, expandFlag);
}

/** 展开 已选中的全部节点 */
function expandSelectedNodes(treeId, expandFlag) {
	var treeObj = $.fn.zTree.getZTreeObj(treeId);
	var nodes = treeObj.getSelectedNodes();
	expandNodes(treeObj, nodes, expandFlag);
}

function expandNodes(treeObj, nodes, expandFlag) {
	for(var i=0; i<nodes.length; i++){
		treeObj.expandNode(nodes[i], expandFlag, true, false, true);
	}
	onLoadWindowSelf();
}

function clearInput(){
	document.getElementById("searchName").value = ""; // 置空
	document.getElementById("searchName").focus();
}


/** 展开当前节点的孩子节点 
 * expandChildrenNode (node, expandFlag, sonSign, focus, callbackFlag)
 * sonSign = true 表示 全部子孙节点 进行与 expandFlag 相同的操作
 * sonSign = false 表示 只影响此节点，对于其 子孙节点无任何影响
 */
function expandChildrenNode(treeId, treeNode, expandFlag) {
	var treeObj = $.fn.zTree.getZTreeObj(treeId);
	if(!isOrUndefinedNull(treeNode)){
		treeObj.expandNode(treeNode, expandFlag, false, false, true);
	}
}

/** 搜索功能 */
function doPageSearchTree(treeId, searchVal){
	var searchVal = searchVal.toLowerCase().trim();
	var treeObj = $.fn.zTree.getZTreeObj(treeId);
	var nodes = treeObj.getNodesByFilter(searchTreeFilter, false, null, searchVal); // 查找节点集合
	var showNodeMap = new Array();
	for(var i=0; i<nodes.length; i++){
		var node = nodes[i];
		recurChildren(node, showNodeMap);
		while(node){
			showNodeMap[node.uniqueName] = node;
			node = node.getParentNode();
		}
	}
	var allNodes = treeObj.transformToArray(treeObj.getNodes());
	for(var i=0; i<allNodes.length; i++){
		var node = allNodes[i];
		if(showNodeMap[node.uniqueName]!=node){
			treeObj.hideNode(node);
		}else{
			treeObj.showNode(node);
		}
	}
}

/** 递归遍历子节点 */
function recurChildren(node, showNodeMap){
	if(node.children){
		for (var i=0, l=node.children.length; i<l; i++) {
			var child = node.children[i];
			showNodeMap[child.uniqueName] = child;
			recurChildren(child, showNodeMap);
		}
	}
}

/** ztree Fileter */
function searchTreeFilter(node, searchVal){
	if(node.name && node.name.toLowerCase().indexOf(searchVal)>-1){
		return true;
	}else if(node.uniqueName && node.uniqueName.toLowerCase().indexOf(searchVal)>-1){
		return true;
	}else{
		return false;
	}
}

