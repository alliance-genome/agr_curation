import { createContext } from 'react';

export const initialSiteContext = {
  apiVersion: null,
  apiToken: null,
};

export const siteContextReducer = (state, action) => {
  switch (action.type) {
    case 'SET_API_VERSION':
      return { ...state, apiVersion: action.payload };
    case 'SET_API_TOKEN':
      return { ...state, apiToken: action.payload };
    default:
      return state;
  }
};

// Provide default context shape including dispatch stub
export const SiteContext = createContext({
  ...initialSiteContext,
  dispatch: () => {},
});
