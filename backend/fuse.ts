import { FuseBox } from 'fuse-box';

const fuse = FuseBox.init({
    homeDir : "src",
    //target : 'server@esnext',
    output : ".fusebox/dist/$name.js"
});
fuse.bundle("app")
    .instructions("> index.ts")//.hmr()//.watch()
    .completed(proc => proc.start())
fuse.run();