var sourceName = 'inject.js';
var extId = 'bmfbcejdknlknpncfpeloejonjoledha';
var BTN_SYSTEM_INFO = 'systeminfo-button';
var BTN_SHOW_FRAME = 'showframe-button';
var UL_NAME = 'response';

var ulResponse;

/**
 * Инициализация.
 */
(function () {
    createScriptInPage();
    var btnSystemInfoTn = document.body.getElementsByTagName('frame')[3]
        .contentDocument.getElementsByName('SystemInfoButtonComponent_systemInfo_0')[0];
    if (btnSystemInfoTn && !btnSystemInfoTn.dataset.isInitialized) {
        btnSystemInfoTn.addEventListener('click', sendMessageToBackgroundJs);
        btnSystemInfoTn.dataset.isInitialized = true;
        btnSystemInfoTn.dataset.requestData = JSON.stringify({ method: "systemInfo", data: "java" });
        btnSystemInfoTn.dataset.responseCallbackName = 'updateJavaVersion';
    }

    setTimeout(function () {
        /*console.log('Call setTimeout function ');
        var Ext = window.top.frames['view'].frames['workarea'].frames['content'].Ext;
        console.log(Ext);
        if (Ext) {
            Ext.getCmp('a_button_cmpFilter_btnRefresh_0').on('click', function () { alert('clicke!') });
        }*/
        var Ext = window.top.transoilBrowserExtension2.sendMessage();
        if (Ext) {
            Ext.getCmp('a_button_cmpFilter_btnRefresh_0').on('click', function () { alert('clicke!') });
        }
    }, 5000);

    window.transoilBrowserExtension = {
        /**
         * Обновить значение версия JRE
         */
        setsystemInfo: function (response) {
            window.top.document.body.getElementsByTagName('frame')[4]
                .contentDocument.getElementsByTagName('frame')[2]
                .contentDocument.getElementsByTagName('frame')[2]
                .contentDocument.getElementById('systemInfoJreVersion').innerHTML = response.data;
            console.log('window.transoilBrowserExtension.setsystemInfo');
            console.log(response);
        },

        /**
         * Вызвать метод указанный в ответе от native-приложения
         */
        callSetFunction: function(response){
            var method = 'set' + response.method;
            if(window.transoilBrowserExtension[method]){
                window.transoilBrowserExtension[method](response);
            }
        },

        /**
         * Обработать ответ от native-приложения
         */
        dispatchNativeResponse: function(response){
            if(response.method){
                window.transoilBrowserExtension.callSetFunction(response);
            }else{
                console.log('Unnable to process response, method not defined.');
                console.log(response);
            }            
        }
    };

    window.transoilBrowserExtension.version = '0.0.1';
})();

/**
 * Слушатель события нажатия на кнопку получения системной информации.
 */
function sendMessageToBackgroundJs() {
    var request = { source: sourceName, control: this.id, content: JSON.parse(this.dataset.requestData) };
    var responseCallbackName = this.dataset.responseCallbackName;
    try {
        sendMessageIncludeId(request, responseCallbackName)
    } catch (err) {
        console.error(err);
        sendMessageExcludeId(request, responseCallbackName);
    }
}

function sendMessageIncludeId(request, responseCallbackName) {
    console.log('Call: sendMessageIncludeId');
    chrome.runtime.sendMessage(extId, request, function (response) {
        console.log(response);        
    });
}

function sendMessageExcludeId(request, responseCallbackName) {
    console.log('Call: sendMessageExcludeId');
    chrome.runtime.sendMessage(request, function (response) {
        console.log(response);
    });
}

/**
 * Обработка вызова от background.js
 */
chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log(request);
        window.transoilBrowserExtension.dispatchNativeResponse(request);
        //addToList(request);
    });

/**
 * Отладочная функция, для добавления ответов в список с id == UL_NAME
 * @param {*} msg 
 */
function addToList(msg) {
    if (!ulResponse) {
        ulResponse = document.getElementById(UL_NAME);
    }
    var liElement = document.createElement('li');
    liElement.innerHTML = JSON.stringify(msg);
    ulResponse.appendChild(liElement);
}

///НЕ ИСПОЛЬЗУЕТСЯ
function createScriptInPage() {
    var s = document.createElement('script');
    // TODO: Впоследствие можно перенести внедрение объекта на страницу в отдельный файл и загружать не в виде текста
    // а как chrome.extension.getURL('transoilBrowserExtension.js'); ВАЖНО - нужно будет добавить файл в manifest.json:web_accessible_resources 
    /*s.textContent = 'var transoilBrowserExtension = {' +
        'sendMessage:function(msg){' +
        'chrome.runtime.sendMessage("bmfbcejdknlknpncfpeloejonjoledha", {source:"inject.js",control:"systeminfo-button",content:{method:"systemInfo", data: "java"}}, function(response){console.log(response);});' +
        '}};';*/
        s.textContent = 'var transoilBrowserExtension2 = {' +
        'sendMessage:function(msg){' +
        "return window.top.frames['view'].frames['workarea'].frames['content'].Ext;" +
        '}};';
    (document.head || document.documentElement).appendChild(s);
}