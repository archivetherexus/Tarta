import { render, Component } from 'inferno';
import { Provider } from 'inferno-redux';

// Load all the themes. //
import './css/dark-theme.css';
import './css/light-theme.css';

import ThemeSelector from './components/ThemeSelector';
import { store } from './Store';

class App extends Component<any, any> {
    render(): any {
        return (
            <div>
                {'Welcome to this app!'}
                <ThemeSelector/>
            </div>
        );
    }
}

render(
    <Provider store={store}>
        <App/>
    </Provider>,
    document.getElementById("app-root")
);
