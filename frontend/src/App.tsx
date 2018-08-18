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
import CreatePostPage from './components/pages/CreatePostPage';
import AdminPage from './components/pages/AdminPage';
import Footer from './components/Footer';
import PrivacyPolicyPage from './components/pages/PrivacyPolicyPage';
import AboutUsPage from './components/pages/AboutUsPage';

render(
    <Provider store={store}>
        <BrowserRouter>
            <div>
                <Navbar />
                <Route path="/login" component={LoginPage} />
                <Route path="/feed" component={FeedPage} />
                <Route path="/admin" component={AdminPage} />
                <Route path="/settings" component={SettingsPage} />
                <Route path="/create" component={CreatePostPage} />
                <Route path="/privacy_policy" component={PrivacyPolicyPage} />
                <Route path="/about_us" component={AboutUsPage} />
                <Route exact path="/" component={LandingPage} />
                <Footer />
            </div>
        </BrowserRouter>
    </Provider>,
    document.getElementById("app-root")
);
