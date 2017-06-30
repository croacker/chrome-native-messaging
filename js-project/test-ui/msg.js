var extId = "bmfbcejdknlknpncfpeloejonjoledha";
var application = 'com.croc.external_app';
var port = null;

function sendExtMessageCert() {
  chrome.runtime.sendMessage(extId, { type: "cert" },
    function (response) {

      if (!response.success)
        console.log("worked");

      if (response.success)
        alert('ok');

      document.getElementById('response').innerHTML += "<p>" + JSON.stringify(response) + "</p>";
    });
}

function sendExtMessageSign() {
  chrome.runtime.sendMessage(extId, { type: "sign", content: "<signedingoajdkasd>", thumbprint: "c914cafe6b7ec2d01a0c709ec4e7a4afa0081e67" },
    function (response) {
      if (!response.success)
        console.log("did not work");

      if (response.success)
        alert('ok');

      document.getElementById('response').innerHTML += "<p>" + JSON.stringify(response) + "</p>";
    });
}

function sendGetJavaVersionMessage() {
  port = chrome.runtime.connect(application);

  port.onMessage.addListener(log);

  port.onDisconnect.addListener(function (e) {
    log('unexpected disconnect');

    port = null;
  });
}

function subsToExtension() {
chrome.runtime.onMessage.addListener(
  function(request, sender, sendResponse) {
    if (request.cmd == "any command") {
      sendResponse({ result: "any response from background" });
    } else {
      sendResponse({ result: "error", message: `Invalid 'cmd'` });
    }
    // Note: Returning true is required here!
    //  ref: http://stackoverflow.com/questions/20077487/chrome-extension-message-passing-response-not-sent
    return true; 
  });
}

function log(msg) {
  console.log(msg);

  // var e = document.createElement('pre');
  // e.appendChild(document.createTextNode(typeof msg === 'object' ? JSON.stringify(msg) : msg));
  // document.getElementById('log').appendChild(e);
}

document.addEventListener('DOMContentLoaded', function () {
  document.getElementById('sign-button').addEventListener(
    'click', sendExtMessageSign);
  document.getElementById('cert-button').addEventListener(
    'click', sendExtMessageCert);
  document.getElementById('javaversion-button').addEventListener(
    'click', subsToExtension);
});