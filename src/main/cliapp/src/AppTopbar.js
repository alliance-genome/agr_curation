import React, { useState, useRef } from 'react';
import { useQuery } from 'react-query';
import { Link } from 'react-router-dom';
import { Menu } from 'primereact/menu';
import classNames from 'classnames';
import { ApiVersionService } from './service/ApiVersionService';

export const AppTopbar = (props) => {
  const menu = useRef(null);
  const menuItems = [
    {
      items: [
        {
          label: 'Profile',
          icon: 'pi pi-profile',
          command:(e) => {
                  window.location.hash = "/profile"
               }
        },
        {
          label: 'Logout',
          icon: 'pi pi-sign-out',
          command: props.logout
        }
      ]
    }
  ];

  const [apiVersion, setApiVersion] = useState({ "version": "0.0.0" });

  const apiService = new ApiVersionService();

  useQuery(['getApiVersion', apiVersion],
    () => apiService.getApiVersion(), {
      onSuccess: (data) => {
        //console.log(data);
        setApiVersion(data);
      },
      onError: (error) => {
      console.log(error);
      },
      keepPreviousData: true,
      refetchOnWindowFocus: false
    }
  );

  return (
      <div className="layout-topbar">
         {
            props.authState?.isAuthenticated &&
            <>
               <Link to="/" className="layout-topbar-logo">
                  AGR Curation: {apiVersion.version}
               </Link>
               <button type="button" className="p-link layout-menu-button layout-topbar-button" onClick={props.onToggleMenuClick}>
                  <i className="pi pi-bars" />
               </button>
            </>
         }
         <button type="button" className="p-link layout-topbar-menu-button layout-topbar-button" onClick={props.onMobileTopbarMenuClick}>
            <i className="pi pi-ellipsis-v" />
         </button>
         {
            props.authState?.isAuthenticated &&
            <ul className={classNames("layout-topbar-menu lg:flex origin-top", { 'layout-topbar-menu-mobile-active': props.mobileTopbarMenuActive })}>
               <li>
                  <Menu model={menuItems} popup ref={menu} id="popup_menu" />
                  <i className="pi pi-user" style={{'fontSize': '1.5em'}} onClick={(event) => menu.current.toggle(event)} aria-controls="popup_menu" aria-haspopup />
               </li>
            </ul>
         }
      </div>
  );

}
