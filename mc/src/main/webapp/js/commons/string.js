/**
 * string
 * 补充js中string对象的方法、以及在此基础上进行的加工处理
 * 获取url参数、预格式文本 等等
 * 
 * author: taosheng
 */ 

/**
 * Or Undefined Null Empty
 */
function isOrUndefinedNullEmpty(data) {
	return isUndefined(data) || isNull(data) || isEmpty(data) ;
}

/**
 * Or Undefined Null 
 */
function isOrUndefinedNull(data) {
	return isUndefined(data) || isNull(data) ;
}

function isUndefined(data) {
	return typeof(data)=="undefined" ;
}

function isNull(data) {
	return data==null ;
}

function isEmpty(data) {
	return data=='' ;
}

/**
 * 用jQuery判断id是否为空 
 */
function isJQueryEmpty(jid) {
	if(isOrUndefinedNull(jid)){
		return true;
	}
	
	return $(id).length==0 ;
}

/**
 * 看能不能作为合法的key存在 
 */
function isLegalCode(data) {
	if(isOrUndefinedNull(data)){
		return false;
	}
	
	return !isNull(data.match(/^[^0-9]+[_A-Za-z0-9]*$/g));
}

/**
 * 获取所有的内容详情，异步加载
 */
function makeSureLegalCode(code, message) {  
	if(isOrUndefinedNullEmpty(message)){
		message = '错误！代号，必须是只包括数字字母下划线，且不能是数字开头！';
	}
	if(isOrUndefinedNullEmpty(code) || !isLegalCode(code.trim())){
		alert(message);
	}
}

/** trim() method for String */
String.prototype.trim=function() {
	return this.replace(/(^\s*)|(\s*$)/g,'');
};

/**
 * prefix成为str的前置
 */
function startWith(str, prefix){
	if(typeof(str)=="undefined" || str==null){
		return '';
	}
	if(typeof(prefix)=="undefined" || prefix==null || prefix==''){
		return str;
	}
	 
	if(str.substr(0,prefix.length)!=prefix){
		str = prefix+str;
	}
	
	return str;
}

/**
 * 按分隔符划分的第1个item，若为delim，则返回str
 */
function firstItemByDelim(str, delim){
	if(isOrUndefinedNull(str) || isOrUndefinedNull(delim)){
		return '';
	}
	var idx = str.indexOf(delim);
	if(idx==-1){
		return str;
	}
	return str.substring(0,idx+1);
}

/**
 * 预格式文本
 */
function getPreText(data) {
	if(isOrUndefinedNullEmpty(data)){
		return '';
	}
	
	data = data.replace(/&/g, "&amp;");
	data = data.replace(/</g, "&lt;");
	data = data.replace(/>/g, "&gt;");
	data = data.replace(/ /g, "&nbsp;");
	data = data.replace(/\r\n/g, "<br>");
	data = data.replace(/\r/g, "<br>");
	data = data.replace(/\n/g, "<br>");
	
	return data;
}

/**
 * 获取url参数
 */
function getParam(url,name){ 
	var pattern = new RegExp("[?&]"+name+"\=([^&]+)", "g");
	var matcher = pattern.exec(url);
	var items = null;
	if(null != matcher){
		try{
			items = decodeURIComponent(decodeURIComponent(matcher[1]));
		}catch(e){
			try{
				items = decodeURIComponent(matcher[1]);
			}catch(e){
				items = matcher[1];
			}
		}
	}
	return items;
};

