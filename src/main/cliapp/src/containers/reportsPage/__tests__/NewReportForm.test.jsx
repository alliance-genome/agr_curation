import { fireEvent, screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { getField, selectInField } from '../../../tools/jest/newEntityFormTestUtils';
import { NewReportForm } from '../NewReportForm';

const renderForm = (newReport, reportDispatch = vi.fn()) => {
	renderWithClient(
		<NewReportForm
			newReportDialog={true}
			setNewReportDialog={vi.fn()}
			groups={[]}
			newReport={newReport}
			reportDispatch={reportDispatch}
			reportService={{ createReport: vi.fn(), updateReport: vi.fn() }}
		/>
	);
	return reportDispatch;
};

describe('<NewReportForm />', () => {
	it('sets Scheduled to the boolean chosen', async () => {
		const reportDispatch = renderForm({ name: '' });
		await screen.findByText('Cron Schedule');
		expect(getField('Scheduled')).toHaveTextContent('Select if Scheduled');

		selectInField('Scheduled', 'true');

		expect(reportDispatch).toHaveBeenCalledWith({ field: 'scheduleActive', value: true });
	});

	it('still sets the cron schedule from its text box', async () => {
		const reportDispatch = renderForm({ name: '', scheduleActive: true });
		await screen.findByText('Cron Schedule');
		expect(getField('Scheduled')).toHaveTextContent('true');

		const cronInput = screen.getByPlaceholderText('Enter Cron Schedule');
		fireEvent.change(cronInput, { target: { value: '0 0 1 * * ?' } });

		expect(reportDispatch).toHaveBeenCalledWith({ field: 'cronSchedule', value: '0 0 1 * * ?' });
	});
});
