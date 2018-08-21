import { render, Component, createComponentVNode } from 'inferno';
import { VNodeFlags } from 'inferno-vnode-flags';
import { Provider, connect } from 'inferno-redux';
import { BrowserRouter, Route, withRouter} from 'inferno-router';

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

function renderSubpage(component: any, props: any = {}) {
    return () => (
        <div>
            <div className="app-upper-content">
                <Navbar />
                {createComponentVNode(VNodeFlags.ComponentUnknown, component, props)}
            </div>
            <Footer />  
        </div>
    );
}

render(
    <Provider store={store}>
        <BrowserRouter>
            <div className="app-upper-content">
                <Route path="/login" render={renderSubpage(LoginPage)} />
                <Route path="/feed" render={renderSubpage(FeedPage)} />
                <Route path="/admin" render={renderSubpage(AdminPage)} />
                <Route path="/settings" render={renderSubpage(SettingsPage)} />
                <Route path="/create" render={renderSubpage(CreatePostPage)} />
                <Route path="/privacy_policy" render={renderSubpage(PrivacyPolicyPage)} />
                <Route path="/about_us" render={renderSubpage(AboutUsPage)} />
                <Route exact path="/" render={renderSubpage(LandingPage)} />  
            </div>
        </BrowserRouter>
    </Provider>,
    document.getElementById("app-root")
);
