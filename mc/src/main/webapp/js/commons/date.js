/**
 * date
 * 日期/时间 相关
 * 包括：日期控件wdatePicker 相关
 * 
 * author: liuchaohong taosheng
 */ 

/**
 * 前N天
 */
Date.prototype.format = function(fmt)
{
	  var o = {
		"M+" : this.getMonth()+1,                 //月份
		"d+" : this.getDate(),                    //日
		"h+" : this.getHours(),                   //小时
		"m+" : this.getMinutes(),                 //分
		"s+" : this.getSeconds(),                 //秒
		"q+" : Math.floor((this.getMonth()+3)/3), //季度
		"S"  : this.getMilliseconds()             //毫秒
	  };
	  if(/(y+)/.test(fmt))
		fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
	  for(var k in o)
		if(new RegExp("("+ k +")").test(fmt))
	  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
	  return fmt;
}

function getDate(day){
	var edate=new Date( new Date().getTime()  + (day*24*60*60*1000) ).format("yyyy-MM-dd");
	return edate;
}

function getDateFormat(day){
	var edate=new Date( new Date().getTime()  + (day*24*60*60*1000) ).format("yyyy-MM-dd HH:mm:ss");
	return edate;
}

/**
 * 昨天
 */
function getYesterday(tformat){
	if(isOrUndefinedNullEmpty(tformat)){
		tformat = "yyyy-MM-dd";
	}
	var edate=new Date( new Date().getTime()  + (-1*24*60*60*1000) ).format("yyyy-MM-dd");
	return edate;
}

/**
 * 改变日期
 */
function changeToDateFormat(tdate) {
	var d = new Date(parseInt(tdate));
	return d.format("yyyy-MM-dd hh:mm:ss");
}

/**
 * 下线时间
 */
function offlineTimeChange(val){
	var days = 365;
	if(val=='oneMonth'){
		days = 30;
	}else if(val=='threeMonth'){
		days = 90;
	}else if(val=='sixMonth'){
		days = 180;
	}else if(val=='oneYear'){
		days = 360;
	}
	$("#offlineTimeStr").val(getDate(days));
	$("#offlineTimeDisabled").val(getDate(days));
}

/**
 * 设置div的弹出位置 
 * id = '_my97DP'
 * object = parent.parent.document
 * wdatePickerLocation(event, '_my97DP', parent.parent.document)
 */
function wdatePickerLocation(event, id, object) {  
	if(isOrUndefinedNull(event) || isOrUndefinedNullEmpty(id) || isOrUndefinedNull(object)){
		return;
	}
	
	var oEvent=event;
	var oLeft=oEvent.pageX ;
	var oTop=oEvent.pageY ;
	
	object.getElementById(id).style.top = oTop + "px";
	
	return false;
}

