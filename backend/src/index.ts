import polka from 'polka';    
import { ServerResponse } from 'http';
import { start } from 'repl';

function one(req: any, res: any, next: any) {
    req.hello = 'world';

    next();
}
  
function two(req: any, res: any, next: any) {
    req.foo = '...needs better demo 😔';
    next();
}

function cors(req: any, res: ServerResponse, next: any) {
    res.setHeader("Access-Control-Allow-Origin", "*"); //"localhost:4444");
    next();
}

ServerResponse.prototype.json = function(this: ServerResponse, obj: any) {
    this.end(JSON.stringify(obj));
};

polka()
    .use(cors, one, two)
    .get('/', (req: any, res) => {
        res.end("Hello World!")
    })
    .get('/list', (req: any, res) => {
        res.json({test: 'Hello World'});
    })
    .get('/users/:id', (req: any, res) => {
        console.log(`~> Hello, ${req.hello}`);
        res.end(`User: ${req.params.id}`);
    })
    .listen(3000).then((_:any) => {
        console.log(`> Running on localhost:3000`);
    });