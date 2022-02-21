import React, { useState } from 'react';
import { useQuery } from 'react-query';
import { Link } from 'react-router-dom';
import classNames from 'classnames';
import { ApiVersionService } from './service/ApiVersionService';

export const AppTopbar = (props) => {

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
            <Link to="/" className="layout-topbar-logo">
              AGR Curation: {apiVersion.version}
            </Link>

            <button type="button" className="p-link  layout-menu-button layout-topbar-button" onClick={props.onToggleMenuClick}>
                <i className="pi pi-bars"/>
            </button>

            <button type="button" className="p-link layout-topbar-menu-button layout-topbar-button" onClick={props.onMobileTopbarMenuClick}>
                <i className="pi pi-ellipsis-v" />
            </button>

                <ul className={classNames("layout-topbar-menu lg:flex origin-top", {'layout-topbar-menu-mobile-active': props.mobileTopbarMenuActive })}>
                    <li>
                        <button className="p-link layout-topbar-button" onClick={props.onMobileSubTopbarMenuClick}>
                            <i className="pi pi-calendar"/>
                            <span>Events</span>
                        </button>
                    </li>
                    <li>
                        <button className="p-link layout-topbar-button" onClick={props.onMobileSubTopbarMenuClick}>
                            <i className="pi pi-cog"/>
                            <span>Settings</span>
                        </button>
                    </li>
                    <li>
                        <button className="p-link layout-topbar-button" onClick={props.onMobileSubTopbarMenuClick}>
                            <i className="pi pi-user"/>
                            <span>Profile</span>
                        </button>
                    </li>
                </ul>
        </div>
    );
}
