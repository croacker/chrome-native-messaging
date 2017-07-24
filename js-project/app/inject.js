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
         * Отправить событие о получении версии Native application
         */
        getVersionPocessResponse: function (response) {
            var extensionResponseVersio = new CustomEvent('extensionResponseVersion', {
                detail: {
                    method: 'extensionResponseVersion',
                    data: response.data
                }
            });
            extensionEventBus.dispatchEvent(extensionResponseVersio);
        },

        /**
         * Отправить событие о завершении работы Native application
         */
        shutdownPocessResponse: function (response) {
            var extensionResponseVersio = new CustomEvent('extensionResponseShutdown', {
                detail: {
                    method: 'extensionResponseShutdown',
                    data: response.data
                }
            });
            extensionEventBus.dispatchEvent(extensionResponseVersio);
        },

        /**
         * Отправить событие о получении версии JRE
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

        startScanPocessResponse: function (response) {
            var extensionResponseStartScan = new CustomEvent('extensionResponseStartScan', {
                detail: {
                    method: 'extensionResponseStartScan',
                    data: response.data
                }
            });
            extensionEventBus.dispatchEvent(extensionResponseStartScan);
        },

        /**
         * Вернуть в приложение результат выполнения печати вложений
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
         * Вернуть в приложение результат выполнения печати штрих-кода
         */
        printBarcodePocessResponse: function (response) {
            var extensionResponsePrintAttachments = new CustomEvent('extensionResponsePrintBarcode', {
                detail: {
                    method: 'extensionResponsePrintBarcode',
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

    extensionEventBus.addEventListener('tnGetVersion', eventProcessor.onTnGetVersion);
    extensionEventBus.addEventListener('tnShutdown', eventProcessor.onTnShutdown);
    extensionEventBus.addEventListener('tnGetSystemInfo', eventProcessor.onTnGetSystemInfo);
    extensionEventBus.addEventListener('tnPrintAttachments', eventProcessor.onTnPrintAttachments);
    extensionEventBus.addEventListener('tnPrintBarcode', eventProcessor.onTnPrintBarcode);
    extensionEventBus.addEventListener('tnStartScan', eventProcessor.onTnStartScan);
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
 * Обработка вызова от background.js.
 */
if (!window.top.injectStatus) {
    chrome.runtime.onMessage.addListener(
        function (request, sender, sendResponse) {
            console.log(request);
            transoilBrowserExtension.dispatchNativeResponse(request);
        });
    window.top.injectStatus = 'injected';
}

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
     * Приложение выполнило запрос версии Native application
     */
    this.onTnGetVersion = function (event) {
        var request = me.getRequest(event.detail);
        sendMessageToBackgroundJs(request);
    };

    /**
     * Приложение выполнило запрос остановки Native application
     */
    this.onTnShutdown = function (event) {
        var request = me.getRequest(event.detail);
        sendMessageToBackgroundJs(request);
    };

    /**
     * Приложение выполнило запрос системной информации, в частности версии jre
     */
    this.onTnGetSystemInfo = function (event) {
        var request = me.getRequest(event.detail);
        sendMessageToBackgroundJs(request);
    };

    this.onTnStartScan = function (event) {
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
    this.onTnPrintBarcode = function (event) {
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