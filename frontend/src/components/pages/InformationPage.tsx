import { Component } from 'inferno';
import { connect } from 'inferno-redux';

class InformationPage extends Component<any, any> {
    render() {
        return (<div className="information-page"/>)
    }
}

export default connect((state:any) => ({
}))(InformationPage);