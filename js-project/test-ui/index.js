var extId = 'bmfbcejdknlknpncfpeloejonjoledha';
var application = 'com.croc.external_app';

function connectToExtension() {
  port = chrome.runtime.connect(application);

  port.onMessage.addListener(log);

  port.onDisconnect.addListener(function (e) {
    log('unexpected disconnect');

    port = null;
  });
}

function sendMessageToExtension() {
  chrome.runtime.sendMessage(extId, { greeting: "hello", andgretting: 'andgoos' }, function (response) {
    console.log(response);
  });
}

function log(msg) {
  console.log(msg);
  var nativeResponse = document.getElementById('native-response');
  var li = document.createElement('li');
  li.innerHTML = msg;
  nativeResponse.appendChild(li);
}

function subscribeToExtensionEvent(eventName) {
  window.top.addEventListener(eventName, function (event) {
    console.log('receive EVENT:' + eventName);
    log(JSON.stringify(event.detail));
  });
}

function sendMessageToExtension(eventName, detail) {
  var event = new CustomEvent(eventName, { detail: detail });
  window.top.dispatchEvent(event);
}

document.addEventListener('DOMContentLoaded', function () {

  document.getElementById('jreinfo-button').addEventListener('click', function () {
    subscribeToExtensionEvent('extensionResponseSystemInfo');
    sendMessageToExtension('tnGetSystemInfo', { method: 'getSystemInfo', data: 'jre' });
  });

  var servletUrl = document.getElementById('printdocs-url');
  document.getElementById('printdocs-button').addEventListener('click', function () {
    if (servletUrl.value) {
      subscribeToExtensionEvent('extensionResponsePrintAttachments');
      sendMessageToExtension('tnPrintAttachments', { method: 'printAttachments', data: servletUrl.value });
      log(servletUrl.value);
    }else{
      console.log('Not specified print attachments URL!');
    }
  });

  var barcodeUrl = document.getElementById('printbarcode-url');
  document.getElementById('printbarcode-button').addEventListener('click', function () {
    if (barcodeUrl.value) {
      subscribeToExtensionEvent('extensionResponsePrintBarcode');
      sendMessageToExtension('tnPrintBarcode', { method: 'printBarcode', data: barcodeUrl.value });
      log(barcodeUrl.value);
    }else{
      console.log('Not specified barcode URL!');
    }
  });

});