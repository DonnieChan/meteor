/**
 * audit
 * 审计日志
 * 
 * author: liuchaohong
 */ 

/**
 * 审计日志
 */
function auditlog(ip,projectName,userName,method,methodGroup){
	$.getJSON("http://auditlog.game.yy.com/rpc/AuditLogWebService/createAuditLog?ip="+ip+
			"&projectName="+projectName+"&userName="+userName+"&method="+method+"&methodGroup="+methodGroup+'&output=json&callback=?',
	function(data) {
	});
}	
