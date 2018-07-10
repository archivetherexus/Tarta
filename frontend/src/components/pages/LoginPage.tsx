import { Component } from 'inferno';
import { connect } from 'inferno-redux';

class LoginPage extends Component<any, any> {

    handleClickGoBack = () => this.context.router.history.push('/');

    render() {
        const { i18n } = this.props;

        return (
            <div className="login-page popup">
                <div className="sugar-heading sugar-relative">
                    <i className="mi mi-arrow-back navigation-go-back" onClick={this.handleClickGoBack}/>
                    {i18n('Login to Tarta')}
                </div>
                <br />
                <input className="sugar-input sugar-shadow form-size" autoFocus type="text" placeholder="Username"/>
                <br />
                <input className="sugar-input sugar-shadow form-size" type="password" placeholder="Password"/>
                <br />
                <button className="sugar-button form-size sugar-bold">{i18n('Login')}</button>
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(LoginPage);