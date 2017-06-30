var extId = 'bmfbcejdknlknpncfpeloejonjoledha';
var application = 'com.croc.external_app';
var port = null;

function connectToExtension() {
  port = chrome.runtime.connect(application);

  port.onMessage.addListener(log);

  port.onDisconnect.addListener(function (e) {
    log('unexpected disconnect');

    port = null;
  });
}

function sendMessageToExtension() {
  chrome.runtime.sendMessage(extId, { greeting: "hello", andgretting:'andgoos' }, function (response) {
		console.log(response);
	});
}

function log(msg) {
  console.log(msg);
}

document.addEventListener('DOMContentLoaded', function () {
  // document.getElementById('javaversion-button').addEventListener(
  //   'click', sendMessageToExtension);
});