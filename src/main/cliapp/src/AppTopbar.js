import React, { useRef } from 'react';
import { Link } from 'react-router-dom';
import { Menu } from 'primereact/menu';
import classNames from 'classnames';

export const AppTopbar = (props) => {
  const menu = useRef(null);
  const items = [
    {
      items: [
        {
          label: 'Logout',
          icon: 'pi pi-sign-out',
          command: props.logout
        }
      ]
    }
  ];

  return (
    <div className="layout-topbar">
      <Link to="/" className="layout-topbar-logo">
        <img src={props.layoutColorMode === 'light' ? 'assets/layout/images/logo-dark.svg' : 'assets/layout/images/logo-white.svg'} alt="logo" />
        <span>SAKAI</span>
      </Link>
    {
      props.authState?.isAuthenticated &&
      <button type="button" className="p-link  layout-menu-button layout-topbar-button" onClick={props.onToggleMenuClick}>
        <i className="pi pi-bars" />
      </button>
    }
      <button type="button" className="p-link layout-topbar-menu-button layout-topbar-button" onClick={props.onMobileTopbarMenuClick}>
        <i className="pi pi-ellipsis-v" />
      </button>
      {
        props.authState?.isAuthenticated &&
        <ul className={classNames("layout-topbar-menu lg:flex origin-top", { 'layout-topbar-menu-mobile-active': props.mobileTopbarMenuActive })}>
          <li>
            <Menu model={items} popup ref={menu} id="popup_menu" />
            <i className="pi pi-user" onClick={(event) => menu.current.toggle(event)} aria-controls="popup_menu" aria-haspopup />
          </li>
        </ul>
      }
    </div>
  );
}
