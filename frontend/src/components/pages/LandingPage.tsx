import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';

class LandingPage extends Component<any, any> {
    
    handleClickLoginButton = () => this.context.router.history.push('/login');
    handleClickFindOutMore = () => this.context.router.history.push('/feed'); // TODO: Point to the correct path.
    
    render() {
        const { i18n } = this.props;
        return (
            <div className="landing-page panel">
                <div className="main-info">
                    <div className="main-title">
                        {'Tarta is a new way to interact with your school!'}
                        <br /><br />
                        <button onClick={this.handleClickLoginButton} className="sugar-button login-button equal-button-width">{i18n('Login now')}</button>
                        <br />
                        {'or'}
                        <br />
                        <button onClick={this.handleClickFindOutMore} className="sugar-button find-out-more-button equal-button-width">{i18n('Find out more')}</button>
                    </div>
                </div>
                <div className="more-info">
                    {'More information...'}
                </div>
            </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(LandingPage);