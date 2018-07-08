import { createStore, combineReducers,  } from 'redux';

import theme from './reducers/theme';

export const store = createStore(combineReducers({
    theme,
}));