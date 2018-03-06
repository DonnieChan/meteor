/**
 * exists
 * 判断 函数/变量 是否存在
 * 
 * author: liuchaohong taosheng
 */ 

/** 是否存在指定函数  */
function isExitsFunction(funcName) {
	try {
		if (typeof(eval(funcName)) == "function") {
			return true;
		}
	} catch(e) {
	}
	
	return false;
}

/** 是否存在指定变量  */ 
function isExitsVariable(variableName) {
	try {
		if (typeof(variableName) == "undefined") {
			//alert("value is undefined"); 
			return false;
		} else {
			//alert("value is true"); 
			return true;
		}
	} catch(e) {
	}
	
	return false;
}

