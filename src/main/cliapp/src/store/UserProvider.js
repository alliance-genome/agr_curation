import React, { useReducer } from 'react';
import { UserContext } from './UserContext';

const defaultSettings = {};

const userSettingsReducer = (state, action) => {
  switch (action.type) {
    case 'UPDATE_COLUMNS':
      return {
        ...state,
        [action.table]: {
          ...state[action.table],
          columns: action.columns
        }
      };
    case 'UPDATE_MULTISORTMETA':
      return {
        ...state,
        [action.table]: {
          ...state[action.table],
          multiSortMeta: action.multiSortMeta
        }
      };
    default:
      return defaultSettings;
  }
};


export const UserProvider = (props) => {

  const [settingsState, dispatchSettingsAction] = useReducer(userSettingsReducer, defaultSettings);

  const updateColumnsHandler = (columns, table) => {
    dispatchSettingsAction({
      type: 'UPDATE_COLUMNS',
      table: table,
      columns: columns
    });
  }

  const updateMultiSortMetaHandler = (multiSortMeta, table) => {
    dispatchSettingsAction({
      type: 'UPDATE_MULTISORTMETA',
      table: table,
      multiSortMeta: multiSortMeta
    });
  }

  const userSettingsContext = {
    ...settingsState,
    updateColumns: updateColumnsHandler,
    updateMultiSortMeta: updateMultiSortMetaHandler
  };

  return (
    <UserContext.Provider value={userSettingsContext}>
      {props.children}
    </UserContext.Provider>
  )
}
