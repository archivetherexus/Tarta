import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';

class AboutUs extends Component<any, any> {
    constructor() {
        super()
    }

    render() {
        const { i18n } = this.props;
        return (
             <div>{i18n('New Component!')}</div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(AboutUs);