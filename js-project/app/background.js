var port;
var application = 'com.croc.external';

chrome.app.runtime.onLaunched.addListener(function () {
    chrome.app.window.create('main.html', {
        bounds: {
            width: 640,
            height: 480
        }
    });
});

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