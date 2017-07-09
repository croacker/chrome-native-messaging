var sourceName = 'inject.js';
var extId = 'bmfbcejdknlknpncfpeloejonjoledha';

/**
 * Объект extension
 */
var tnBrowserExtension;

/**
 * Шина событий для extension
 */
var extensionEventBus;
/**
 * Обработчик событий
 */
var eventProcessor;

/**
 * Имена элементов управления отладочной формы test-ui/index.html (не СЭД)
 */
var BTN_SYSTEM_INFO = 'systeminfo-button';
var BTN_SHOW_FRAME = 'showframe-button';
var UL_NAME = 'response';

var ulResponse;

/**
 * Инициализация.
 */
(function () {
    /**
     * Объект для хранения информации о сеансе
     */
    transoilBrowserExtension = {
        version: '0.0.1',
        /**
         * Обновить значение версия JRE
         */
        getSystemInfoPocessResponse: function (response) {
            var extensionResponseSystemInfo = new CustomEvent('extensionResponseSystemInfo', {
                detail: {
                    method: 'extensionResponseSystemInfo',
                    data: response.data
                }
            });
            extensionEventBus.dispatchEvent(extensionResponseSystemInfo);

        },

        /**
         * Вернуть в приложение результат выполнения печати
         */
        printAttachmentsPocessResponse: function (response) {
            var extensionResponsePrintAttachments = new CustomEvent('extensionResponsePrintAttachments', {
                detail: {
                    method: 'extensionResponsePrintAttachments',
                    data: response.data
                }
            });
            extensionEventBus.dispatchEvent(extensionResponsePrintAttachments);

        },

        /**
         * Вызвать метод указанный в ответе от native-приложения
         */
        callResponseFunction: function (response) {
            var method = response.method + 'PocessResponse';
            if (transoilBrowserExtension[method]) {
                transoilBrowserExtension[method](response);
            }
        },

        /**
         * Обработать ответ от native-приложения
         */
        dispatchNativeResponse: function (response) {
            if (response.method) {
                transoilBrowserExtension.callResponseFunction(response);
            } else {
                console.log('Unnable to process response, method not defined.');
                console.log(response);
            }
        }
    };

    subscribeToApplicationEvents();
})();

/**
 * Подписка на необходимые события
 */
function subscribeToApplicationEvents(ksedEventBus) {
    if(!eventProcessor){
        eventProcessor = new EventProcessor();
    }
    if (!extensionEventBus) {
        extensionEventBus = new ExtensionEventBus(window.top);
    }

    extensionEventBus.addEventListener('tnGetSystemInfo', eventProcessor.onTnGetSystemInfo);

    extensionEventBus.addEventListener('tnPrintAttachments', eventProcessor.onTnPrintAttachments);
}

/**
 * Отправка сообщения в background.js
 * @param {*} request 
 */
function sendMessageToBackgroundJs(request) {
    try {
        sendMessageIncludeId(request)
    } catch (err) {
        console.error(err);
    }
}

/**
 * Отправка сообщения включая id расширения
 * @param {*} request 
 */
function sendMessageIncludeId(request) {
    console.log('inject.js: sendMessageIncludeId');
    chrome.runtime.sendMessage(extId, request);
}

/**
 * Обработка вызова от background.js
 */
chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log(request);
        transoilBrowserExtension.dispatchNativeResponse(request);
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
 * Декоратор для шины событий
 * @param {*} ksedEventBus 
 */
function ExtensionEventBus(ksedEventBus) {
    var me = this;
    /**
     * Объект КСЕД, выступающий в качестве шины событий
     */
    this.ksedEventBus = ksedEventBus;
    /**
     * Подписаться на событие с наименованием
     */
    this.addEventListener = function (eventName, callback) {
        me.ksedEventBus.addEventListener(eventName, callback);
    }
    /**
     * Возбудить событие
     */
    this.dispatchEvent = function (event) {
        me.ksedEventBus.dispatchEvent(event);
    }
}

/**
 * state-less обработчик событий
 */
function EventProcessor(){
    var me = this;

    /**
     * Приложение выполнило запрос системной информации, в частности версии jre
     */
    this.onTnGetSystemInfo = function (event) {
        var request = me.getRequest(event.detail);
        sendMessageToBackgroundJs(request);
    };

    /**
     * Приложение выполнило запрос на печать
     */
    this.onTnPrintAttachments = function (event) {
        var request = me.getRequest(event.detail);
        sendMessageToBackgroundJs(request);
    };

    /**
     * Приложение выполнило запрос на печать всех файлов
     */
    this.onTnPrintAll = function (event) {
        var request = me.getRequest(event.detail);
        sendMessageToBackgroundJs(request);
    };

    /**
     * Создать объект запроса
     */
    this.getRequest = function(detail){
        return {
            source: sourceName,
            control: '',
            content: detail
        };
    }
}