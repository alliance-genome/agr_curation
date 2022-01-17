import React from 'react';
export const AppTopbar = (props) => {

  const sidebarToggle = props.authState && props.authState.isAuthenticated ?
    <button type="button" className="p-link layout-menu-button" onClick={props.onToggleMenu}>
      <span className="pi pi-bars" />
    </button> :
    null;

  return (
    <div className="layout-topbar clearfix">
      {sidebarToggle}
    </div>
  );
}
