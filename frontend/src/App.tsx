import { render, Component } from 'inferno';
import { Provider } from 'inferno-redux';
import { BrowserRouter, Route } from 'inferno-router';

// Load all the themes, fonts and icon packs. //
import './css/dark-theme.css';
import './css/light-theme.css';
import 'material-icons/css/material-icons.css';
import 'material-icons/iconfont/material-icons.css';
import 'opensans-npm-webfont';

import { store } from './Store';
import LandingPage from './components/pages/LandingPage';
import Navbar from './components/Navbar';
import SettingsPage from './components/pages/SettingsPage';
import FeedPage from './components/pages/FeedPage';
import LoginPage from './components/pages/LoginPage';

render(
    <Provider store={store}>
        <BrowserRouter>
            <div>
                <Navbar/>
                <Route path="/login" component={LoginPage}/>
                <Route path="/feed" component={FeedPage}/>
                <Route path="/settings" component={SettingsPage}/>
                <Route exact path="/" component={LandingPage}/>
            </div>
        </BrowserRouter>
    </Provider>,
    document.getElementById("app-root")
);
