import React, { useState } from 'react';
import classNames from 'classnames';
import { CSSTransition } from 'react-transition-group';

export const AppProfile = (props) => {

  const [expanded, setExpanded] = useState(false);
  const button = props.authState && props.authState.isAuthenticated ?
    <button type="button" onClick={props.logout} className="p-link"><i className="pi pi-fw pi-power-off" /><span>Logout</span></button> :
    null;

  const onClick = (event) => {
    setExpanded(prevState => !prevState);
    event.preventDefault();
  }

  return (
    <div className="layout-profile">
      <button className="p-link layout-profile-link" onClick={onClick}>
        <span className="username">{props.email}</span>
        <i className="pi pi-fw pi-cog" />
      </button>
      <CSSTransition classNames="p-toggleable-content" timeout={{ enter: 1000, exit: 450 }} in={expanded} unmountOnExit>
        <ul className={classNames({ 'layout-profile-expanded': expanded })}>
          <li>{button}</li>
        </ul>
      </CSSTransition>
    </div>
  );

}
