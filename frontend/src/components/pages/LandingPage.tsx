import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';

class LandingPage extends Component<any, any> {
    render() {
        return (
            <div className="panel">
                {'Welcome to this landing page!'}
                <br />
            </div>
        );
    }
}

export default connect((state:any) => ({
}))(LandingPage);