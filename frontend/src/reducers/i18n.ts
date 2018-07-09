declare class I18nAction {
    type: string;
    languageName?: string;
}

import english from '../constants/english.language.json';
import swedish from '../constants/swedish.language.json';

const languages: {[index: string]: {[index: string]: string}} = {
    'english': english,
    'swedish': swedish,
};

function loadLanguage(languageName: string) {
    const language = languages[languageName];
    return function(toLocalise: string) {
        if (toLocalise in language) {
            return language[toLocalise];
        } else {
            return toLocalise;
        }
    }
}

export default function i18n(language = loadLanguage('english'), action: I18nAction) {
    switch(action.type) {
        case 'SET_LANGUAGE':
            return loadLanguage(action.languageName || 'english')
        default:
            return language;
    }
}