declare class SessionAction {
    type: string;
    newSessionID?: string;
}

export default function theme(state = '', action: SessionAction) {
    switch(action.type) {
        case 'SET_SESSION_ID':
            return action.newSessionID;
        default:
            return state;
    }
}