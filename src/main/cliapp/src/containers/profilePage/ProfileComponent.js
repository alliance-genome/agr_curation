import React, { useState } from 'react';
import { Card } from 'primereact/card';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { useSessionStorage } from '../../service/useSessionStorage';


export const ProfileComponent = () => {

  const [userInfo] = useSessionStorage('userInfo', {});
  const [localUserInfo] = useSessionStorage('localUserInfo', {});
  const [oktaToken] = useState(JSON.parse(localStorage.getItem('okta-token-storage')));

  const userInfos = [
    { name: "Name", value: userInfo.name },
    { name: "Email", value: userInfo.email },

    { name: "Okta ID", value: userInfo.sub },
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
