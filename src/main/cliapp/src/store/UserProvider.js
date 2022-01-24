import React, { useReducer } from 'react';
import { UserContext } from './UserContext';

const defaultSettings = {};

const userSettingsReducer = (state, action) => {
  if (action.type === 'UPDATE_COLUMNS') {
    return {
      ...state,
      [action.table]: {
        columns: action.columns
      }
    };
  };
  return defaultSettings;
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

  const userSettingsContext = {
    ...settingsState,
    updateColumns: updateColumnsHandler
  };

  return (
    <UserContext.Provider value={userSettingsContext}>
      {props.children}
    </UserContext.Provider>
  )
}
