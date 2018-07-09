import { createStore, combineReducers,  } from 'redux';

import theme from './reducers/theme';
import i18n from './reducers/i18n';

export const store = createStore(combineReducers({
    theme,
    i18n,
}));