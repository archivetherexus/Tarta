import { Promise } from 'es6-promise';

// Automatically install the polyfill. //
declare var require: any
require('es6-promise/auto');

function encodeQueryData(data: any) {
    let parameters = [];
    for (let d in data) {
        parameters.push(encodeURIComponent(d) + '=' + encodeURIComponent(data[d]));
    }
    return parameters.join('&');
 }

export function fetchHTTP<T>(url: string, data?: {[index: string]: any}, options?: {form: boolean, post: boolean}) {
    
    // Create the body to send. //
    let toSend: any = null; 
    if (data !== undefined && typeof data === 'object') {
        if (options !== undefined && options.form) {
            toSend = new FormData();
            for (let key in data) {
                toSend.set(key, data[key]);
            }
        } else {
            toSend = encodeQueryData(data);
        }
    } else {
        toSend = data;
    }

    // Returns a promise that sends the request. //
    return new Promise<T>((resolve, reject) => {
        let request = new XMLHttpRequest();
        request.responseType = "text";
        request.addEventListener('load', () => {
            if (request.status === 404) {
                reject('Status code was 404')
            } else if (request.status === 400 && request.response) {
                let stringData = request.response;
                alert(String(stringData)); // TODO: Prettier alert.
                reject(stringData);
            } else if (request.response) {
                let data = JSON.parse(request.response);
                console.log(data);
                resolve(<T>data);
            } else {
                console.error('No response in request object for: ' + url);
                reject('No response in request object!');
            }
        });
        request.addEventListener('abort', () => {
            reject('Abort!');
        });
        request.addEventListener('timeout', () => {
            reject('Timed out!');
        });
        if (options != null && (options.post || options.form)) {
            request.open('POST', url);
        } else if (toSend == null) {
            request.open('GET', url);
        } else {
            request.open('GET', url + '?' + toSend);
        }
        request.send(toSend);
    });
}