import polka from 'polka';    

function one(req: any, res: any, next: any) {
    req.hello = 'world';
    next();
}
  
function two(req: any, res: any, next: any) {
    req.foo = '...needs better demo 😔';
    next();
}
  
polka()
    .use(one, two)
    .get('/', (req: any, res) => {
        res.end("Hello World!")
    })
    .get('/users/:id', (req: any, res) => {
        console.log(`~> Hello, ${req.hello}`);
        res.end(`User: ${req.params.id}`);
    })
    .listen(3000).then((_:any) => {
        console.log(`> Running on localhost:3000`);
    });