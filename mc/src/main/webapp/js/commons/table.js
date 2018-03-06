/**
 * table
 * table的初始化、获取table数据 等等
 * 
 * author: taosheng
 */ 

/**
 * 初始化表格格式 
 */
function initTable() {  
	$('table tr:even').addClass('ht-bg-gray91');  // 'table tr:nth-child(3n)'
	$('table th').css('vertical-align', 'middle');
	$('table td').css('vertical-align', 'middle');
}

/**
 * 获取 表tableid 的数据
 */
function getTableData(tableid) {
	var tableInfo = "";
	var tableObj = document.getElementById(tableid);
	for (var i = 0; i < tableObj.rows.length; i++) {    //遍历Table的所有Row
		for (var j = 0; j < tableObj.rows[i].cells.length; j++) {   //遍历Row中的每一列
			tableInfo += tableObj.rows[i].cells[j].innerHTML;   //获取Table中单元格的内容
			tableInfo += "   ";
		}
		tableInfo += "\n";
	}
	return tableInfo;
}	

/**
 * 获取 表tableid 的 指定列 colIdx 的数据
 */
function getTableColumnData(tableid, colIdx) {
	/* tableid = startWith(tableid, '#');
	if(tableid==''){
		return;
	} */
	var tableInfo = "";
	var tableObj = document.getElementById(tableid);
	for (var i = 1; i < tableObj.rows.length; i++) {    //遍历Table的所有Row
			tableInfo += tableObj.rows[i].cells[colIdx].innerHTML;   //获取Table中单元格的内容
		tableInfo += ",";
	}
	return tableInfo;
}

/**
 * 获取 表tableid 的 指定列 colIdx 的数据
 */
function getTableColumnTextContent(tableid, colIdx, delim) {
	if(isOrUndefinedNull(tableid) || isOrUndefinedNull(colIdx)){
		return "";
	}
	if(isOrUndefinedNull(delim)){
		delim = ";";
	}
	
	var tableInfo = delim;
	var tableObj = document.getElementById(tableid);
	for (var i = 1; i < tableObj.rows.length; i++) {    //遍历Table的所有Row
		tableInfo += tableObj.rows[i].cells[colIdx].textContent.trim();   //获取Table中单元格的内容
		tableInfo += delim;
	}
	return tableInfo;
}


/**
 * 查找制定列
 */
function findColValueOnTable(tableId, colFoundId, foundValue){ 
	var tableObj = document.getElementById(tableId);
	var trows = tableObj.rows;
	
	var colContent;
	foundValue = foundValue.trim();
	for (var i = 0; i < trows.length; i++) {
		colContent = trows[i].cells[colFoundId].textContent.trim();
		if(colContent==foundValue){
			return true;
		}
	}
	
	return false;
}
