import React from 'react';

export const AppTopbar = (props) => {

  const button = props.authState && props.authState.isAuthenticated ?
    <button onClick={props.logout} className="p-link layout-menu-button p-col-1" style={{'fontSize': '1.25em'}} >Logout</button> :
    null;

  return (
    <div className="layout-topbar clearfix p-grid">
      <button type="button" className="p-link layout-menu-button p-col-10" onClick={props.onToggleMenu}>
        <span className="pi pi-bars" />
      </button>
      {button}
    </div>
  );
}
