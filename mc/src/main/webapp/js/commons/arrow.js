/**
 * arrow
 * 涉及 光标处操作(插入字符串) 等等
 * 
 * author: taosheng
 */ 

/**
 * 插入日期变量
 * \${DateUtils.addDays(tdate, -1)?string('yyyy-MM-dd')}
 * \${tdate?string('yyyy-MM-dd')}
 */
function insertDateFormatParam(txtId,dateOffId,dateFormatId){
	txtId = startWith(txtId, '#');
	dateOffId = startWith(dateOffId, '#');
	dateFormatId = startWith(dateFormatId, '#');
	if(txtId=='' || dateOffId=='' || dateFormatId==''){
		return;
	}
	
	var text = $(txtId).attr('value');
	var dateOff = $(dateOffId).val();
	var dateFormatParam = $(dateFormatId).attr('value');
	if(dateOff=='0'){
		dateFormatParam = "\${tdate?string('"+dateFormatParam+"')}";
	}else{
		dateFormatParam = "\${DateUtils.addDays(tdate, "+dateOff+")?string('"+dateFormatParam+"')}";
	}
	
	appendAfterArrow(txtId, dateFormatParam);
}

/**
 * 插入自定义参数
 */
function insertFlowParam(txtId, paramId){
	txtId = startWith(txtId, '#');
	paramId = startWith(paramId, '#');
	if(txtId=='' || paramId==''){
		return;
	}
	
	var text = $(txtId).attr('value');
	var flowParam = $(paramId).attr('value');
	flowParam = "\${"+flowParam+"}";
	
	appendAfterArrow(txtId, flowParam);
}

/**
 * 在id的光标后，插入data
 */
function appendAfterArrow(id, data){
	id = startWith(id, '#');
	if(id==''){
		return;
	}
	
	var text = $(id).attr('value');
	var startIndex = $(id)[0].selectionStart;
	text = text.substr(0,startIndex) + data + text.substr(startIndex);
	
	$(id).attr('value', text);
}
