var sourceName = 'inject.js';
var extId = 'bmfbcejdknlknpncfpeloejonjoledha';
var BTN_SYSTEM_INFO = 'systeminfo-button';
var BTN_SHOW_FRAME = 'showframe-button';
var UL_NAME = 'response';

var ulResponse;

(function () {
    var btnSystemInfo = document.getElementById(BTN_SYSTEM_INFO);
    if (btnSystemInfo && !btnSystemInfo.dataset.isInitialized) {
        btnSystemInfo.addEventListener('click', sendMessageToBackgroundJs);
        btnSystemInfo.dataset.isInitialized = true;
        btnSystemInfo.dataset.requestData = JSON.stringify({ method: "systemInfo", data: "java" });
    }

    var btnShowFrame = document.getElementById(BTN_SHOW_FRAME);
    if (btnShowFrame && !btnShowFrame.dataset.isInitialized) {
        btnShowFrame.addEventListener('click', sendMessageToBackgroundJs);
        btnShowFrame.dataset.isInitialized = true;
        btnShowFrame.dataset.requestData = JSON.stringify({ method: "swingTestApplet", data: "java" });
    }
})();

function sendMessageToBackgroundJs() {
    var request = { source: sourceName, control: this.id, content: JSON.parse(this.dataset.requestData) };
    try {
        sendMessageIncludeId(request)
    } catch (err) {
        console.error(err);
        sendMessageExcludeId(request);
    }
}

function sendMessageIncludeId(request) {
    console.log('Call: sendMessageIncludeId');
    chrome.runtime.sendMessage(extId, request, function (response) {
        console.log(response);
    });
}

function sendMessageExcludeId(request) {
    console.log('Call: sendMessageExcludeId');
    chrome.runtime.sendMessage(request, function (response) {
        console.log(response);
    });
}

chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log(request);
        addToList(request);
    });

function addToList(msg) {
    if (!ulResponse) {
        ulResponse = document.getElementById(UL_NAME);
    }
    var liElement = document.createElement('li');
    liElement.innerHTML = JSON.stringify(msg);
    ulResponse.appendChild(liElement);
}