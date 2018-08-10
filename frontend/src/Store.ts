import { createStore, combineReducers,  } from 'redux';

import theme from './reducers/theme';
import i18n from './reducers/i18n';
import session from './reducers/session';

export const store = createStore(combineReducers({
    session,
    theme,
    i18n,
}));