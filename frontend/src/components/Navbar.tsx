import { render, Component } from 'inferno';
import { connect } from 'inferno-redux';
import { NavLink, Link, withRouter } from 'inferno-router';

class Navbar extends Component<any, any> {

    navbar: HTMLElement | null = null;
    
    onToggleNavbar() {
        if (this.navbar !== null) {
            this.navbar.classList.toggle("responsive")
        }
    }

    handleOnToggleNavbar = this.onToggleNavbar.bind(this)

    shouldRender(locationPath: string) {
        return locationPath !== "/" && locationPath !== "/login"
    }

    render() {
        const { i18n, location } = this.props;
        return this.shouldRender(location.pathname) ? (
            <div className="navbar" ref={(n) => this.navbar = n}>   
                <div className="navbar-container">
                    <NavLink activeClassName="active" exact to="/feed">{'Feed'}</NavLink>
                    <NavLink activeClassName="active" exact to="/settings">{i18n('Settings')}</NavLink>
                    <NavLink activeClassName="active" exact to="/information">{'Information'}</NavLink>
                    <a href="javascript:void(0);" className="icon" onClick={this.handleOnToggleNavbar}>
                        <i className="mi mi-menu"></i>
                    </a>
                </div>
                <div className="navbar-logo">
                    <Link to="/"><i className="mi mi-school navbar-logo-icon" /></Link>
                </div>
            </div>
      ) : (
          <span />
      );
    }
}

export default withRouter(connect((state:any) => ({
    i18n: state.i18n,
}))(Navbar));