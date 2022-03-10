import React, { useState, useEffect } from 'react';
import { Card } from 'primereact/card';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { ApiVersionService } from '../../service/ApiVersionService';
import { useOktaAuth } from '@okta/okta-react';

export const ProfileComponent = () => {

  const [localUserInfo, setLocalUserInfo] = useState({});
  const [oktaToken] = useState(JSON.parse(localStorage.getItem('okta-token-storage')));

  const { authState, oktaAuth } = useOktaAuth();
  const apiService = new ApiVersionService();

  useEffect(() => {
      if (!authState || !authState.isAuthenticated) {
          setLocalUserInfo(null);
      } else {
          apiService.getUserInfo(authState).then((data) => {
            //localUserInfo.lastName;
            setLocalUserInfo(data);
          }).catch((err) => {
            console.log(err);
          });
      }
  }, [authState, oktaAuth, setLocalUserInfo]); // eslint-disable-line react-hooks/exhaustive-deps


  const userInfos = [
    { name: "Name", value: localUserInfo.firstName + " " + localUserInfo.lastName },
    { name: "Email", value: localUserInfo.email },
    { name: "Okta Token", value: oktaToken.accessToken.accessToken },
    { name: "Curation API Token", value: localUserInfo.apiToken },

  ];

  return (
    <Card title="User Profile" subTitle="">
      <DataTable value={userInfos}>
         <Column field="name" header="Name" />
         <Column field="value" header="Value" />
      </DataTable>
    </Card>
  );
};
