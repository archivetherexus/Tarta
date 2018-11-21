import { Component } from 'inferno';
import { connect } from 'inferno-redux';
import ExpandableList from '../layout/ExpandableList';

class InformationPage extends Component<any, any> {
    render() {
        const { i18n } = this.props;
        return (<div className="information-page">
            {i18n('Settings')}
            <ExpandableList data={[
                {title: 'Hello', component: 'World!'},
                {title: 'Number', component: 12},
                {title: 'Comp', component: (<button>{'A button!'}</button>)}
            ]} />
        </div>)
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
}))(InformationPage);