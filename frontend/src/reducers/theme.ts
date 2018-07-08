declare class ThemeAction {
    type: string;
    themeName?: string;
}

function setTheme (themeName: string) {
    const styles = document.styleSheets;
    const searchFor = "default-css-" + themeName + "-theme-css";
    for (let i = 0; i < styles.length; i++) {
        const style = styles[i];
        const parent: any = style.ownerNode;
        if (/default\-css\-[a-zA-Z0-9]*\-theme\-css/.test(parent.id)) {
            style.disabled = parent.id !== searchFor;
        }
    }
}

export default function theme(state = 'light', action: ThemeAction) {
    switch(action.type) {
        case 'SET_THEME':
            setTheme(action.themeName || "error");
            return action.themeName;
        default:
            setTheme(state);
            return state;
    }
}