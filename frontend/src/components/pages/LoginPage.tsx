import { Component } from 'inferno';
import { connect } from 'inferno-redux';
//import msgpack from 'msgpack-lite';
import { fetchHTTP } from '../../helpers/fetch_http';

class LoginPage extends Component<any, any> {
    
    constructor() {
        super();
        
        this.state = {
            state: 0,
        }; 
    }
    
    componentDidMount() {
        if (this.usernameRef !== null) {
            this.usernameRef.focus();
        }
    }
    
    onKeyDown(e: KeyboardEvent) {
        if (e.which == 13) {
            this.onLoginSubmit();
        }
    }
    
    onLoginSubmit() {
        if (this.passwordRef !== null
            && this.passwordRef.value.length > 0
            && this.usernameRef !== null
            && this.usernameRef.value.length > 0) {
            
            var username = this.usernameRef.value;
            var password = this.passwordRef.value;

            this.setState({
                state: 1
            });


            fetchHTTP('http://localhost:3000/user/login', {
                username, 
                password
            }, {post: true, form: true}).then(r => {
                if (r.status == 'OK') {
                    this.context.store.dispatch({
                        type: 'SET_sessionID',
                        newSessionID: r.sessionID,
                    });
                    console.log("New session ID: " + r.sessionID);
                    this.context.router.history.push('/');

                    fetchHTTP('http://localhost:3000/user/name', {
                        sessionID: r.sessionID,
                    }).then(username => {
                        console.log('Username: ' + username);
                    });
                } else {
                    alert(r.reason);
                }
            });
            fetchHTTP('http://localhost:3000/list2')
                .then(r => console.log(r))
                .catch(e => console.error(e));
        }
    }
        
    handleKeyDown = this.onKeyDown.bind(this);

    handleLoginClick = this.onLoginSubmit.bind(this);

    handleClickGoBack = () => this.context.router.history.push('/');

    usernameRef: HTMLInputElement | null = null;

    passwordRef: HTMLInputElement | null = null;

    render() {
        const { i18n } = this.props;
        const { state } = this.state;
            
        return state == 0 ? (
            <div className="login-page popup" onKeyDown={this.handleKeyDown}>
                <div className="sugar-heading sugar-relative">
                    <i className="mi mi-arrow-back navigation-go-back" onClick={this.handleClickGoBack}/>
                    {i18n('Login to Tarta')}
                </div>
                <br />
                <input className="sugar-input sugar-shadow form-size" placeholder="Username" ref={(u) => this.usernameRef = u} type="text" />
                <br />
                <input className="sugar-input sugar-shadow form-size" placeholder="Password" ref={(p) => this.passwordRef = p} type="password" />
                <br />
                <button className="sugar-button form-size sugar-bold" onClick={this.handleLoginClick}>{i18n('Login')}</button>
            </div>
        ) : (
            <div className="login-page popup">
                <div className="sugar-heading sugar-relative">
                    <i className="mi mi-arrow-back navigation-go-back" onClick={this.handleClickGoBack}/>
                    {i18n('Login to Tarta')}
                </div>
                <br />
                <br />
                {'Loading...'}
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(LoginPage);