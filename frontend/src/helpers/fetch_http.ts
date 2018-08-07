import { Promise } from 'es6-promise';
import msgpack from 'msgpack-lite';

// Automatically install the polyfill. //
require('es6-promise/auto');

export function fetchHTTP(url: string, options?: {}) {
    return new Promise<any>((resolve, reject) => {
        let request = new XMLHttpRequest();
        request.responseType = "arraybuffer";
        request.addEventListener('load', () => {
            if (request.response) {
                resolve(msgpack.decode(new Uint8Array(request.response)));
            } else {
                reject('No response in request object!');
            }
        });
        request.addEventListener('abort', () => {
            reject('Abort!');
        });
        request.addEventListener('timeout', () => {
            reject('Timed out!');
        })
        request.open('GET', url);
        request.send();
    });
}