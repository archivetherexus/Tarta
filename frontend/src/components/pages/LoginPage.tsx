import { Component } from 'inferno';
import { connect } from 'inferno-redux';

class LoginPage extends Component<any, any> {
    render() {
        return (
        <div className="panel">
            {'This is not implemented yet!'}
        </div>
        );
    }
}

export default connect((state:any) => ({
}))(LoginPage);