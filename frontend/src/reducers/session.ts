declare class SessionAction {
    type: string;
    newSessionID?: string;
}

function getSavedSessionID(): string {
    try {
        return localStorage.getItem('tarta-session-id') || 'no-session';
    } catch(_) {
        return 'no-session';
    }
}

export default function session(state = getSavedSessionID(), action: SessionAction) {
    switch(action.type) {
        case 'SET_SESSION_ID':
            try {
                localStorage.setItem('tarta-session-id', action.newSessionID || 'no-session');
            } catch(_) {}
            return action.newSessionID;
        default:
            return state;
    }
}