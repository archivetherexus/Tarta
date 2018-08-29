import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import { Link } from 'inferno-router';
import { fetchHTTP } from '../../helpers/fetch_http';

class ResetPage extends Component<any, any> {
    constructor() {
        super()
    }

    onResetClick() {
        fetchHTTP<void>('http://localhost:3000/reset', {
            sessionID: this.props.session,
        }).then(() => this.context.router.history.push('/'));
    }

    handleResetClick = this.onResetClick.bind(this);

    render() {
        const { i18n } = this.props;
        return (
             <div>
                {i18n('Press reset to... reset')}
                <br />
                <button onClick={this.handleResetClick}>{i18n('Reset')}</button>
             </div>
        );
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
    session: state.session,
}))(ResetPage);