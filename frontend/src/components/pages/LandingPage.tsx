import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';

class LandingPage extends Component<any, any> {
    render() {
        return (
            <div className="panel">
                <Link to="/login">Please login first</Link>
                <br />
                <br />
                {'Welcome to this landing page!'}
                <br />
                <Link to="/feed">{'Feed'}</Link>
                <br />
            </div>
        );
    }
}

export default connect((state:any) => ({
}))(LandingPage);