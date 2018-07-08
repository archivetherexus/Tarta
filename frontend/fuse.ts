import {
    FuseBox,
    Sparky,
    EnvPlugin,
    CSSResourcePlugin,
    CSSPlugin,
    WebIndexPlugin,
    QuantumPlugin,
    PostCSSPlugin,
    Bundle,
} from 'fuse-box';

import transformInferno from 'ts-transform-inferno';
import transformClasscat from 'ts-transform-classcat';

let fuse: FuseBox;
let app: Bundle;

let isProduction = false;

Sparky.task('config', () => {

    const productionPlugins = isProduction ? [
        QuantumPlugin({
            bakeApiIntoBundle: 'app',
            treeshake: true,
            uglify: true,
        }),
    ] : [];

    const postCSSPlugins = [
        require('postcss-import'),
        require('postcss-css-variables'),
        require('postcss-preset-env'),
    ];

    fuse = new FuseBox({
        homeDir: 'src',
        hash: isProduction,
        output: '.fusebox/dist/$name.js',
        cache: !isProduction,
        sourceMaps: !isProduction,
        transformers: {
            before: [transformClasscat(), transformInferno()],
        },
        plugins: [
            EnvPlugin({ NODE_ENV: isProduction ? 'production' : 'development' }),
            [PostCSSPlugin(postCSSPlugins), CSSResourcePlugin(), CSSPlugin()],
            WebIndexPlugin({
                title: 'Tarta Development',
                template: 'src/index.html',
            }),
            ...productionPlugins,
        ],
    });
    app = fuse.bundle('app').instructions('>App.tsx');
});

Sparky.task('clean', () => Sparky.src('dist/').clean('dist/'));

Sparky.task('env', () => (isProduction = true));

Sparky.task('dev', ['clean', 'config'], () => {
    fuse.dev();
    app.hmr().watch();
    return fuse.run();
});

Sparky.task('prod', ['clean', 'env', 'config'], () => {
    return fuse.run();
});