import React from 'react';
//perhaps we should consider replacing moment with another data/time library. Their documentation recomends
//against using it in new projects
import Moment from 'react-moment';
import { DialogCard } from './DialogCard';
import { DialogCardRow} from './DialogCardRow';

export const DialogTop = ({ report }) => {
  return (
    <DialogCardRow>
      <DialogCard topText="Name">
        {report.name}
      </DialogCard>
      <DialogCard topText="Status">
        {report.curationReportStatus}
      </DialogCard>
      <DialogCard topText="Date Created">
        <Moment format="MMM Do YYYY" date={report.dateCreated} />
      </DialogCard>
      <DialogCard topText="Date Updated">
        <Moment format="MMM Do YYYY" date={report.dateUpdated} />
      </DialogCard>
    </DialogCardRow>
  )
};
