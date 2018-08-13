import { Promise } from 'es6-promise';
import msgpack from 'msgpack-lite';

// Automatically install the polyfill. //
require('es6-promise/auto');

function encodeQueryData(data: any) {
    let parameters = [];
    for (let d in data) {
        parameters.push(encodeURIComponent(d) + '=' + encodeURIComponent(data[d]));
    }
    return parameters.join('&');
 }

export function fetchHTTP(url: string, data?: {[index: string]: any}, options?: {form: boolean, post: boolean}) {
    
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
    return new Promise<any>((resolve, reject) => {
        let request = new XMLHttpRequest();
        request.responseType = "arraybuffer";
        request.addEventListener('load', () => {
            if (request.status === 404) {
                reject('Status code was 404')
            } else if (request.response) {
                let data = msgpack.decode(new Uint8Array(request.response));
                //console.log(data);
                resolve(data);
            } else {
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