import React, { useRef } from 'react';
import { Dropdown } from 'primereact/dropdown';
import { InputText } from 'primereact/inputtext';
import { useControlledVocabularyService } from '../../service/useControlledVocabularyService';
import { OverlayPanel } from 'primereact/overlaypanel';

export const CronFields = ({ newBulkLoad, onChange }) => {
  const booleanTerms = useControlledVocabularyService('generic_boolean_terms');
  const op = useRef(null);


  return (
    <div>
      <>
        <div className="p-field">
          <label htmlFor="scheduled">Scheduled</label>
          <Dropdown
            id="scheduled"
            options={booleanTerms}
            value={newBulkLoad.scheduleActive}
            onChange={onChange}
            placeholder={"Select if Scheduled"}
            className='p-col-12'
            name='scheduleActive'
            optionLabel='text'
            optionValue='name'
          />
        </div>
        <div className="p-field">
          <label htmlFor="cronSchedule">Cron Schedule</label>
          <i
            id="cronInfo"
            className="pi pi-info-circle"
            style={{ 'fontSize': '0.75em', 'margin': '0.5em', 'color': 'blue' }}
            onMouseOver={(e) => op.current.toggle(e)}
          ></i>
          <OverlayPanel ref={op}>
            For more information, click this <a href="http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html"> link</a>
          </OverlayPanel>
          <InputText
            value={newBulkLoad.cronSchedule}
            onChange={onChange}
            placeholder='Enter Cron Schedule'
            name='cronSchedule'
          />
        </div>

      </>
    </div>
  );
};
