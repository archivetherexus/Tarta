import { render, Component } from 'inferno';

import './css/app.css';

class App extends Component<any, any> {
    render(): any {
        return (<div>Oi!</div>)
    }
}

render(
    <App/>,
    document.getElementById("app-root")
);
