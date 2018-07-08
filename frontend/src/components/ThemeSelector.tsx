import { render, Component } from 'inferno';
import { connect } from 'inferno-redux';

class ThemeSelector extends Component<any, any> {
    handleSwitchTheme: () => void;

    constructor(props: any) {
        super(props);
        this.handleSwitchTheme = this.switchTheme.bind(this);
    }

    switchTheme() {
        const { store } = this.context;
        const themeName =  this.props.theme === 'dark' ? 'light' : 'dark';
        store.dispatch({
            type: 'SET_THEME',
            themeName: themeName,
        })
    }

    render(): any {
        const { theme } = this.props;
        return (
            <div>
                {'Current theme: ' + theme}
                <br/>
                <button onClick={this.handleSwitchTheme}>Press to switch!</button>
            </div>
        );
    }
}

export default connect((state:any) => ({
    theme: state.theme
}))(ThemeSelector);