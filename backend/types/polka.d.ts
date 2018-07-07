declare module 'polka' {
    import {IncomingMessage, ServerResponse} from 'http';

    class Options {
        // There are currently no options for Polka. //
    }
    
    // Handlers. //
    type MiddlewareHandler = (req: IncomingMessage, res: ServerResponse, next: MiddlewareHandler) => void; 
    type RouteHandler = (req: IncomingMessage, res: ServerResponse) => void;
    
    class Polka {
        // Routing. //
        get(pattern: string, handler: RouteHandler): Polka;
        put(pattern: string, handler: RouteHandler): Polka;
        post(pattern: string, handler: RouteHandler): Polka;
        delete(pattern: string, handler: RouteHandler): Polka;

        // Misc. //
        use(base: MiddlewareHandler | string,  ...fn:MiddlewareHandler[]): Polka;
        listen(port: Number, hostname?: string): Promise<any>;
    }
    
    export default function(opts?: Options): Polka;
}
