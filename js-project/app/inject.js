var sourceName = 'inject.js';
var extId = 'bmfbcejdknlknpncfpeloejonjoledha';

/**
 * Объект расширения
 */
var tnBrowserExtension;

/**
 * Шина событий для расширения
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

    extensionEventBus.addEventListener('onTnPrintSelected', eventProcessor.onTnPrintSelected);

    extensionEventBus.addEventListener('onTnPrintAll', eventProcessor.onTnPrintAll);
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
    console.log('Call: sendMessageIncludeId');
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
 * Поиск нужного фрейма(К УДАЛЕНИЮ)
 * @param {*} topWindow 
 */
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
     * Приложение выполнило запрос на печать выбранных файлов
     */
    this.onTnPrintSelected = function (event) {
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