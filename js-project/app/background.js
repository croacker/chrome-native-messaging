var port;
var application = 'com.croc.external_app';

var transoilTab;

chrome.tabs.onUpdated.addListener(function (tabId, changeInfo, tab) {
    if (needInject(tabId, changeInfo, tab)) {
        transoilTab = new TransoilTab(tabId, changeInfo, tab);
        console.log(transoilTab);
        chrome.tabs.insertCSS(transoilTab.tabId, {
            file: "inject.css"
        });
        chrome.tabs.executeScript(transoilTab.tabId, {
            file: "inject.js"
        },
            function (results) {
                console.log(results);
            });
    }
});

/**
 * Клик на иконке extension, для принудительной установки(запуска) приложения на host'е
 */
chrome.browserAction.onClicked.addListener(function (tab) {
    var port = getPort();
    console.log('Java host application connected');
});

/**
 * Слушатель сообщений от inject.js
 */
chrome.runtime.onMessage.addListener(
    function (request, sender, sendResponse) {
        console.log('background.js: Incoming message');
        sendToNative(request, sendResponse);
    });

/**
 * Отправить сообщение в native-приложение
 * @param {*} request 
 * @param {*} sendResponse 
 */
function sendToNative(request, sendResponse) {
    var json = request.content;
    var port = getPort();
    if (port) {
        var nativeRequest = new NativeRequest({
            tabId: transoilTab.tabId,
            port: port,
            request: request
        });
        console.log('background.js: sendToNative');
        nativeRequest.postMessage(json);
    } else {
        chrome.runtime.sendNativeMessage(application, json, sendResponse);
    }
}

/**
 * Получить созданный порт, либо установить соединение
 */
function getPort() {
    if (!port) {
        port = connectToPort();
    }
    return port;
}

/**
 * Выполнить соединение с native-приложением.
 * @param {*} listener 
 */
function connectToPort(listener) {
    port = chrome.runtime.connectNative(application);
    port.onDisconnect.addListener(function (e) {
        console.log('Java host application connection broken. Was called onDisconnect.');
        if(chrome.extension.lastError){
            console.log("Last error:" + chrome.extension.lastError.message);
        }
        console.log(e);
        port = null;
    });
    return port;
}

function getTabId(tabs) {
    var tabId;
    if (!tabs) {
        console.log('tabs is undefined!');
    } else if (!tabs[0]) {
        console.log('tabs[0] is undefined!');
    } else if (!tabs[0].id) {
        console.log('tabs[0].id is undefined!');
    } else {
        tabId = tabs[0].id;
    }
    tabId = tabId || transoilTab.tabId;

    return tabId;
}

//Только если в mainifest.json указана возможность отправлять запросы со страницы(externally_connectable - особенность безопасности).
chrome.runtime.onMessageExternal.addListener(
    function (request, sender, sendResponse) {
        console.log(request);
        console.log(sender);
        if (sender == '') {
            return;
        }
        if (request.openUrlInEditor) {
            openUrl(request.openUrlInEditor);
        }
        sendResponse('from background.js');
    });

/**
 * Класс для хранения информации о закладке приложения СЭД
 * @param {*} tabId 
 * @param {*} changeInfo 
 * @param {*} tab 
 */
function TransoilTab(tabId, changeInfo, tab) {
    this.tabId = tabId;
    this.changeInfo = changeInfo;
    this.tab = tab;
    return this;
};

/**
 * Класс для выполнения запроса к native-приложению
 * @param {*} tabId 
 * @param {*} request 
 */
function NativeRequest(config) {
    var me = this;
    this.tabId = config.tabId;
    this.port = config.port;
    this.request = config.request;

    this.postMessage = function (json) {
        me.port.onMessage.addListener(function portOnMessageListener(response) {
            console.log('background.js: portOnMessageListener');
            var leaveListenerActive=false;
            if(response.data) {
                //@todo Поправить - прилетает при первом вызове через Job строка, а при вызове
                //через ru.croc.chromenative.job.Job.sendToExtension прилетает объект
                if (typeof(response.data) == 'string') {
                    try {
                        var dataJSON = JSON.parse(response.data);
                        leaveListenerActive = dataJSON.leaveListenerActive;
                    } catch (err) {
                        console.error(err);
                    }
                }else if(typeof response.data=='object'){
                    leaveListenerActive =  response.leaveListenerActive;
                }
            }

            if(!leaveListenerActive) {
                port.onMessage.removeListener(portOnMessageListener);
            }
            if (!response.method) {
                response.method = me.request.content.method;
            }
            sendResponse(response);
        });
        me.port.postMessage(json);
    }

    var sendResponse = function (response) {
        console.log(response);
        chrome.tabs.query({ active: true, currentWindow: true }, function (tabs) {
            if (me.tabId) {
                chrome.tabs.sendMessage(me.tabId, response);
            }            
        });
    }

    return this;
}

/**
 * Проверка на undefined
 * @param {*} variable 
 */
function isUndefined(variable){
    return 'undefined' === typeof variable;
}

/**
 * Ф-я определяющая необходимость внедрения
 * @param {*} tabId 
 * @param {*} changeInfo 
 * @param {*} tab 
 */
function needInject(tabId, changeInfo, tab) {
    var result = false;
    var url = tab.url;
    if ((changeInfo.status == 'complete' || changeInfo.status == 'loading')
          && !isUndefined(url) && url.indexOf("chrome:") == -1) {
        result = true;
    }
    return result;
}