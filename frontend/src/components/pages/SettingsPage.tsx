import { Component } from 'inferno';
import { connect } from 'inferno-redux';

import ThemeSelector from '../ThemeSelector';

class SettingsPage extends Component<any, any> {
    render() {
        return (
            <div className="panel">
                {'Hello World!'}
                <ThemeSelector/>
            </div>
        )
    }
}

export default connect((state:any) => ({
}))(SettingsPage);