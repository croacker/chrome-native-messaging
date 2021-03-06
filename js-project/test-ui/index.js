/**
 * Добвить сообщение в вывод.
 * @param msg сообщение в виде строки
 */
function log(msg) {
    console.log(msg);
    var nativeResponse = document.getElementById('native-response');
    var li = document.createElement('li');
    li.innerHTML = msg;
    nativeResponse.appendChild(li);
}

/**
 * Подписаться на событие Browser extension.
 * @param eventName наименование события
 */
function subscribeToExtensionEvent(eventName) {
    window.top.addEventListener(eventName, function (event) {
        console.log('receive EVENT:' + eventName);
        log(JSON.stringify(event.detail));
    });
}

/**
 * Отправить сообщение Browser extension.
 * @param eventName
 * @param detail
 */
function sendMessageToExtension(eventName, detail) {
    var event = new CustomEvent(eventName, {detail: detail});
    window.top.dispatchEvent(event);
}

/**
 * Зарегистрировать события нажатия на кнопках.
 */
document.addEventListener('DOMContentLoaded', function () {
    document.getElementById('version-button').addEventListener('click', function () {
        sendMessageToExtension('tnGetVersion', {method: 'getVersion', data: 'hostApp'});
    });

    document.getElementById('jreinfo-button').addEventListener('click', function () {
        sendMessageToExtension('tnGetSystemInfo', {method: 'getSystemInfo', data: 'jre'});
    });

    var servletUrl = document.getElementById('printdocs-url');
    document.getElementById('printdocs-button').addEventListener('click', function () {
        if (servletUrl.value) {
            sendMessageToExtension('tnPrintAttachments', {method: 'printAttachments', data: servletUrl.value});
            log(servletUrl.value);
        } else {
            log('Not specified print attachments URL!');
        }
    });

    var barcodeUrl = document.getElementById('printbarcode-url');
    document.getElementById('printbarcode-button').addEventListener('click', function () {
        if (barcodeUrl.value) {
            sendMessageToExtension('tnPrintBarcode', {method: 'printBarcode', data: barcodeUrl.value});
            log(barcodeUrl.value);
        } else {
            log('Not specified barcode URL!');
        }
    });

    document.getElementById('startscan-button').addEventListener('click', function () {
        sendMessageToExtension('tnStartScan', {
            method: 'startScan',
            data: '{uploadUrl:servletUrl.value, userName:dzamorin.croc.ru}'
        });
    });

    document.getElementById('shutdown-button').addEventListener('click', function () {
        sendMessageToExtension('tnShutdown', {method: 'shutdown', data: 'hostApp'});
    });

    subscribeToResponseEvents();
});

/**
 * Подписаться на необходимые события.
 */
function subscribeToResponseEvents() {
    subscribeToExtensionEvent('extensionResponseVersion');
    subscribeToExtensionEvent('extensionResponseSystemInfo');
    subscribeToExtensionEvent('extensionResponsePrintAttachments');
    subscribeToExtensionEvent('extensionResponsePrintBarcode');
    subscribeToExtensionEvent('extensionResponseStartScan');
    subscribeToExtensionEvent('extensionResponseShutdown');
}