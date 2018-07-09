import { Component } from 'inferno';
import { connect } from 'inferno-redux';

class SettingsPage extends Component<any, any> {

    handleSwitchLanguage = (e: any) => this.context.store.dispatch({
        type: 'SET_LANGUAGE',
        languageName: e.target.value
    });

    handleSwitchTheme = () => this.context.store.dispatch({
        type: 'SET_THEME',
        themeName: this.props.theme === 'dark' ? 'light' : 'dark',
    })

    render() {
        const { i18n, theme } = this.props;
        return (
            <div className="panel">
                <h2>{i18n('Settings')}</h2>

                {'Toggle theme: '}
                <button onClick={this.handleSwitchTheme}>{theme}</button>
                <br/>

                {'Change language: '}
                <select defaultValue={i18n('_language_name')} onChange={this.handleSwitchLanguage}>
                    <option value="english">{'English'}</option>
                    <option value="swedish">{'Svenska'}</option>
                </select>
            </div>
        )
    }
}

export default connect((state:any) => ({
    i18n: state.i18n,
    theme: state.theme,
}))(SettingsPage);