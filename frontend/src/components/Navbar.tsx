import { render, Component } from 'inferno';
import { connect } from 'inferno-redux';
import { NavLink, withRouter } from 'inferno-router';

class Navbar extends Component<any, any> {

    navbar: HTMLElement | null = null;
    
    onToggleNavbar() {
        if (this.navbar !== null) {
            this.navbar.classList.toggle("responsive")
        }
    }

    handleOnToggleNavbar = this.onToggleNavbar.bind(this)


    render() {
        return (
            <div className="navbar" ref={(n) => this.navbar = n}>   
                <div className="navbar-container">
                    <NavLink activeClassName="active" exact to="/feed">{'Feed'}</NavLink>
                    <NavLink activeClassName="active" exact to="/settings">{'Settings'}</NavLink>
                    <NavLink activeClassName="active" exact to="/information">{'Information'}</NavLink>
                    <a href="javascript:void(0);" className="icon" onClick={this.handleOnToggleNavbar}>
                        <i className="mi mi-menu"></i>
                    </a>
                </div>
                <div className="navbar-logo">
                    <i className="mi mi-school" />
                </div>
            </div>
      );
    }
}

export default withRouter(connect((state:any) => ({
}))(Navbar));