import { render, Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link, withRouter } from 'inferno-router';

class Navbar extends Component<any, any> {
    shouldRender(locationPath: string) {
        return locationPath !== "/" && locationPath !== "/login"
    }

    render() {
        const { i18n, location } = this.props;
        return this.shouldRender(location.pathname) && (
            <div className="footer">   
                    <Link to="/privacy_policy">{i18n('Privacy Policy')}</Link>
                    <Link to="/about_us">{i18n('About Us')}</Link>
            </div>
      );
    }
}

export default withRouter(connect((state:any) => ({
    i18n: state.i18n,
}))(Navbar));