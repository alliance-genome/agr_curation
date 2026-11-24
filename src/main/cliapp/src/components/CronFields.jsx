import React, { useRef } from 'react';
import { InputText } from 'primereact/inputtext';
import { BooleanSelect } from './Editors/widgets/BooleanSelect';
import { OverlayPanel } from 'primereact/overlaypanel';

/**
 * The Scheduled and Cron Schedule fields of a data load or report form.
 *
 * @param {object} newItem - the data load or report being created
 * @param {Function} onChange - called with the Cron Schedule input's change event
 * @param {(value: boolean) => void} onScheduleActiveChange - called with the value chosen for Scheduled
 * @returns {JSX.Element}
 */
export const CronFields = ({ newItem, onChange, onScheduleActiveChange }) => {
	const op = useRef(null);

	return (
		<div>
			<>
				<div className="field">
					<label htmlFor="scheduled">Scheduled</label>
					<BooleanSelect
						id="scheduled"
						value={newItem.scheduleActive}
						onChange={onScheduleActiveChange}
						placeholder="Select if Scheduled"
						name="Scheduled"
					/>
				</div>
				<div className="field">
					<label htmlFor="cronSchedule">Cron Schedule</label>
					<i
						id="cronInfo"
						className="pi pi-info-circle"
						style={{ fontSize: '0.75em', margin: '0.5em', color: 'blue' }}
						onMouseOver={(e) => op.current.toggle(e)}
					></i>
					<OverlayPanel ref={op}>
						For more information, click this{' '}
						<a
							target="_blank"
							rel="noopener noreferrer"
							href="http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html"
						>
							{' '}
							link
						</a>
					</OverlayPanel>
					<InputText
						value={newItem.cronSchedule}
						onChange={onChange}
						placeholder="Enter Cron Schedule"
						name="cronSchedule"
					/>
				</div>
			</>
		</div>
	);
};
