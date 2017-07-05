var sourceName = 'inject.js';
var extId = 'bmfbcejdknlknpncfpeloejonjoledha';

/**
 * Имена элементов управления отладочной формы test-ui/index.html (не СЭД)
 */
var BTN_SYSTEM_INFO = 'systeminfo-button';
var BTN_SHOW_FRAME = 'showframe-button';
var UL_NAME = 'response';

/**
 * Имена элементов управления СЭД
 */
var TN_BTN_SYSTEM_INFO = 'SystemInfoButtonComponent_systemInfo_0';
var TN_BTN_PRINT_PRINT_ALL = 'button_PrintAttachmentContainer_btnPrintAll_0';
var TN_BTN_PRINT_PRINT_SELECTED = 'button_PrintAttachmentContainer_btnPrint_0';

/**
 * Ссылки на элементы управления СЭД
 */
var tnButtonSystemInfo;
var tnButtonPrintAll;
var tnButtonPrintSelected;

var ulResponse;

/**
 * Инициализация.
 */
(function () {
    var tnButtonSystemInfo = document.body.getElementsByTagName('frame')[3]
        .contentDocument.getElementsByName(TN_BTN_SYSTEM_INFO)[0];
    if (tnButtonSystemInfo && !tnButtonSystemInfo.dataset.isInitialized) {
        tnButtonSystemInfo.addEventListener('click', sendMessageToBackgroundJs);
        tnButtonSystemInfo.dataset.isInitialized = true;
        tnButtonSystemInfo.dataset.requestData = JSON.stringify({ method: "systemInfo", data: "java" });
        tnButtonSystemInfo.dataset.responseCallbackName = 'updateJavaVersion';
    }

    /**
     * Объект для хранения информации о сеансе
     */
    window.transoilBrowserExtension = {
        version: '0.0.1',

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
        callSetFunction: function (response) {
            var method = 'set' + response.method;
            if (window.transoilBrowserExtension[method]) {
                window.transoilBrowserExtension[method](response);
            }
        },

        /**
         * Обработать ответ от native-приложения
         */
        dispatchNativeResponse: function (response) {
            if (response.method) {
                window.transoilBrowserExtension.callSetFunction(response);
            } else {
                console.log('Unnable to process response, method not defined.');
                console.log(response);
            }
        }
    };

    setTimeout(findPrintAttachmentListAppletButtons, 5000);
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
 * Отладочная функция, для добавления ответов в список <ul> с id == UL_NAME
 * 
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

/**
 * Найти кнопки PrintAttachmentListApplet и добавить необходимую функциональность.
 */
function findPrintAttachmentListAppletButtons() {
    var windowContent = new WindowContent(window.top);
    var childFrame = windowContent.getMainactionChildChildFrame();

    if (childFrame) {
        var contentDocument = childFrame.contentDocument;
        tnButtonPrintAll = contentDocument.getElementById(TN_BTN_PRINT_PRINT_ALL);
        if (tnButtonPrintAll) {
            console.log(tnButtonPrintAll);
        }

        tnButtonPrintSelected = contentDocument.getElementById(TN_BTN_PRINT_PRINT_SELECTED);
        if (tnButtonPrintSelected) {
            console.log(tnButtonPrintSelected);
        }
    }
    setTimeout(findPrintAttachmentListAppletButtons, 5000);
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

function WindowContent(topWindow) {
    var me = this;
    this.topWindow = topWindow;

    this.getViewFrame = function () {
        return this.topWindow.document.getElementsByTagName('frame')['view'];
    }

    this.getWorkareaFrame = function () {
        var result;
        var viewFrame = me.getViewFrame();
        if (viewFrame) {
            result = viewFrame.contentDocument.getElementsByTagName('frame')['workarea'];
        }
        return result;
    }

    this.getContentFrame = function () {
        var result;
        var workareaFrame = me.getWorkareaFrame();
        if (workareaFrame) {
            result = workareaFrame.contentDocument.getElementsByTagName('frame')['content'];
        }
        return result;
    }

    this.getMainactionFrame = function () {
        var result;
        var contentFrame = me.getContentFrame();
        if (contentFrame) {
            var childFrames = contentFrame.contentDocument.getElementsByTagName('frame');
            if (childFrames.length != 0) {
                var childOfChild = childFrames[0].contentDocument.getElementsByTagName('frame')
                if (childOfChild.length != 0) {
                    var mainactionDiv = childOfChild[0].contentDocument.getElementById('mainactionframe');
                    if (mainactionDiv) {
                        result = mainactionDiv.getElementsByTagName('iframe')[0];
                    }
                }
            }
        }
        return result;
    }

    this.getMainactionChildFrame = function () {
        var result;
        var mainactionFrame = me.getMainactionFrame();
        if (mainactionFrame) {
            result = mainactionFrame.contentDocument.getElementsByTagName('iframe')[0];
        }
        return result;
    }

    this.getMainactionChildChildFrame = function () {
        var result;
        var childFrame = me.getMainactionChildFrame();
        if (childFrame) {
            result = childFrame.contentDocument.getElementsByTagName('frame')[0];
        }
        return result;
    }
}