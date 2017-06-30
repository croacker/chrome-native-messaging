var port;
var application = 'com.croc.external_app';

chrome.tabs.onUpdated.addListener(function () {
	chrome.tabs.insertCSS(null, {
		file: "inject.css"
	});
	chrome.tabs.executeScript(null, {
		file: "inject.js"
	},
		function () {
		});
});

chrome.runtime.onMessage.addListener(
	function (request, sender, sendResponse) {
		sendToNative(request, sendResponse);
	});

chrome.browserAction.onClicked.addListener(function (tab) {
	var port = getPort();
	console.log('Java host application connected');
});

///Отправить сообщение в native-приложение
function sendToNative(request, sendResponse) {
	var json = request.content;
    var port = getPort();
	if (port) {
		port.postMessage(json);
	} else {
		chrome.runtime.sendNativeMessage(application, json, sendResponse);
	}
}

function getPort(){
	if(!port){
		port = connectToPort();
	}
	return port;
}

function connectToPort(listener) {
	listener = listener || onMessageListener
	port = chrome.runtime.connectNative(application);
	port.onMessage.addListener(listener);
	port.onDisconnect.addListener(function (e) {
		console.log('Java host application connection broken. Was called onDisconnect.');
		console.log(e);
		port = null;
	});
	return port;
}

function onMessageListener(msg) {
	console.log(msg);
	chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
		var tabId = getTabId(tabs);
		if (tabId) {
			chrome.tabs.sendMessage(tabId, msg, function (response) {
				if (response) {
					console.log(response);
				}
			});
		}
	});
}

function getTabId(tabs) {
	var tabId;
	if (!tabs) {
		console.log('tabs is undefined!');
	} else if (!tabs[0]) {
		console.log('tabs[0] is undefined!');
	} else if (!tabs[0].id) {
		console.log('tabs[0].id is undefined!');
	} else {
		tabId = tabs[0].id;
	}
	return tabId;
}

//Только если в mainifest.json указана возможность отправлять запросы со страницы(externally_connectable - особенность безопасности).
chrome.runtime.onMessageExternal.addListener(
	function (request, sender, sendResponse) {
		console.log(request);
		console.log(sender);
		if (sender == '') {
			return;
		}
		if (request.openUrlInEditor) {
			openUrl(request.openUrlInEditor);
		}
		sendResponse('from background.js');
	});