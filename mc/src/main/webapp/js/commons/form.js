/**
 * form
 * form提交表单、设置焦点
 * 创建form(可为临时)，以提交url，防止浏览器拦截
 * 
 * author: taosheng
 */ 

/** 
 * 防被拦截的弹出新页面
 * 利用创建一个临时的form
 */
function openUrl( url, target ){
	if(isOrUndefinedNullEmpty(url)){
		alert('url must not be empty!');
	}
	if(isOrUndefinedNullEmpty(target)){
		target = '_blank';
	}
	
	var f=document.createElement("form");
	f.setAttribute("action" , url );
	f.setAttribute("method" , 'post' );
	f.setAttribute("target" , target );
	f.setAttribute("style" , 'display:none' );
	document.body.appendChild(f);
	f.submit();
}

function openUrlOnMap(action, map){
	var tmpDate = new Date().format("yyyyMMddhhmmss");
	var f=document.createElement("form");
	f.setAttribute("id" , "form_1213_"+tmpDate );
	f.setAttribute("action" , action );
	f.setAttribute("method" , 'post' );
	f.setAttribute("target" , '_blank' );
	f.setAttribute("style" , 'display:none' );
	
	var array = map.keys();
	for(var i in array) {
		f.appendChild(createElement('input', array[i], map.get(array[i])));
		// $(f).append('<input id="' + array[i] +'" name="' + array[i] +'" type="text" value="' + map.get(array[i]) +'" />');
	}
	f.appendChild(createElement('input', 'tmpDate', tmpDate));
	
	document.body.appendChild(f);
	f.submit();
}
  
function createElement(type, id, value){
	var f=document.createElement(type);
	f.setAttribute("id" , id );
	f.setAttribute("name" , id );
	f.setAttribute("value" , value );
	return f;
}


/**
 * 查询数据页面的元素，并设置 focus
 */
function submitFormAndFocus(formId, focusId) {
	
	// 查找数据
	$(formId).submit();
	
	// 设置焦点在最上面
	var focusItem;
	focusItem = $(focusId);
	if(focusItem.length == 0){
		focusItem = document.getElementById("mainFrame").contentWindow.$(focusId);
	}
	focusItem.focus();
	
}

/**
 * 查询数据页面的元素，并设置 focus
 */
function focus(focusId) {
	
	// 设置焦点在最上面
	var focusItem;
	focusItem = $(focusId);
	if(focusItem.length == 0){
		focusItem = document.getElementById("mainFrame").contentWindow.$(focusId);
	}
	focusItem.focus();
	
}

/**
 * 查询数据页面的元素
 * pageNumberType in (pageMin, pageBack, pageNext, pageMax)
 * 
 * dataForm = "form#dataForm"
 * dataPageForm = "form#dataPageForm"
 * focusId = "select#moduleCode"
 */
function queryDataFormWithPage(dataForm, dataPageForm, pageNumberType, focusId) {
	if(isOrUndefinedNullEmpty(dataForm) || isOrUndefinedNullEmpty(dataPageForm)){
		return ;
	}
	
	// 确定要查找的页面数
	var pageNumber = 1;
	if(isOrUndefinedNullEmpty(pageNumberType)){
		pageNumber = $(dataPageForm+" input#pageNumber").attr('value');
	} else { 
		pageNumber = $(dataForm+" input#"+pageNumberType).val();
	}
	if(pageNumber==null){
		pageNumber = 1;
	}
	$(dataForm+" input#pageNumber").attr('value', pageNumber);
	
	// 查找数据
	$(dataForm).submit();
	
	// 设置焦点在最上面
	focus(focusId);
	
	// 设置分页里面的数据
	$(dataPageForm+" input#pageMax").attr('value', $(dataForm+" input#pageMax").attr('value'));
	$(dataPageForm+" input#pageNumber").attr('value', $(dataForm+" input#pageNumber").attr('value'));
	
	// 看是否需要禁掉超链接
	var pageBack = $(dataForm+" input#pageBack").val();
	var pageNext = $(dataForm+" input#pageNext").val();
	var pageMax = $(dataForm+" input#pageMax").val();
	
	// pageBack
	if( pageBack<=0 ){
		$(dataPageForm+" a[name=pageBack]").attr("style", "");
		$(dataPageForm+" a[name=pageBack]").attr("onclick", "");
	} else {
		$(dataPageForm+" a[name=pageBack]").attr("style", "text-decoration:none; cursor:text; color:black;");
		$(dataPageForm+" a[name=pageBack]").attr("onclick", "queryDataFormWithPage('pageBack')");
	}
	// pageNext
	if( pageNext>pageMax ){
		$(dataPageForm+" a[name=pageNext]").attr("style", "");
		$(dataPageForm+" a[name=pageNext]").attr("onclick", "");
	} else {
		$(dataPageForm+" a[name=pageNext]").attr("style", "text-decoration:none; cursor:text; color:black;");
		$(dataPageForm+" a[name=pageNext]").attr("onclick", "queryDataFormWithPage('pageNext')");
	}

}
