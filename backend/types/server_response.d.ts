declare module 'http' {
    interface ServerResponse {
        json(obj: any): void;
    }
}