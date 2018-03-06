/**
 * defaultValue
 * 默认值
 * 
 * author: taosheng
 */ 

/**
 * 设置默认值
 */
function setDefaultValue(id, data){
	id = startWith(id, '#');
	if(id==''){
		return;
	}
	if(typeof(data)=="undefined" || data==null){
		return;
	}
	
	if($(id).length==0){
		return;
	}
	if($(id).length>1){
		alert('错误：该id['+id+'], 存在多个对应项');
		return;
	}
	var text = $(id).attr('value');
	if(text==''){
		$(id).attr('value', data);
	}
}
