/**
 * metadata
 * 元数据
 * 
 * author: liuchaohong
 */ 

/**
 * 提交表单
 * @param {} pageNumber
 * @returns {} 
 */
function queryForm(pageNumber){
	$("#pageNumber").val(pageNumber);
	$('#searchForm').submit();
}

/**
 * 更新数据库注释
 */
function updateDbComment(dbName){
	var dbComment = $('#dbComment').val();
	if(dbComment == ''){
		alert(dbName+'库的注释不可为空！');
	}else{
		var url = "/metadata/updateDbComment.do?dbName="+dbName+"&dbComment="+dbComment;
		$.ajax({
			type: "POST",
			url: url,
			async: true,
			cache: false,
			success: function(data){
				if(data=1){
					alert('保存成功！');
				}
			}
		});
	}
}

/**
 * 更新表注释	
 */
function updateTableComment(dbName,tableName){
	var tableComment = $('#tableComment').val();
	if(tableComment == ''){
		alert('表注释不可为空！');
	}else{
		var url = "/metadata/updateTableComment.do?dbName="+dbName+"&tableName="+tableName+"&tableComment="+tableComment;
		$.ajax({
			type: "POST",
			url: url,
			async: true,
			cache: false,
			success: function(data){
				if(data=1){
					alert('保存成功！');
				}
			}
		});
	}
}

/**
 * 更新表字段注释
 */
function updateFieldComment(dbName,tableName,colName,colCommentId){
	var colComment = $('#'+colCommentId).val();
	if(colComment == ''){
		alert('字段注释不可为空！');
	}else{
		var url = "/metadata/updateFieldComment.do?dbName="+dbName+"&tableName="+tableName+"&colName="+colName+"&colComment="+colComment;
		$.ajax({
			type: "POST",
			url: url,
			async: true,
			cache: false,
			success: function(data){
				if(data=1){
					alert('保存成功！');
				}
			}
		});
	}
}
