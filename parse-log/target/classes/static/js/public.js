function ajaxPost(url, data, callback) {
	$.post("/log/list", data, function (result) {
		if (result.resultCode === 'success') {
			callback(result);
		} else if (result.resultCode === 'failed' && result.resultMsg !== undefined && result.resultMsg !== '') {
			alert(result.resultMsg);
		} else {
			alert("未知错误！");
		}
	});
}

function serializeNotNull(serStr) {
	return serStr.split("&").filter(function (str) {
		return !str.endsWith("=")
	}).join("&");
}