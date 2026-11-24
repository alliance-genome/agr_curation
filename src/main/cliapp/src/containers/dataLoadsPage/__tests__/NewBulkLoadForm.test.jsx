import { screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { selectInField } from '../../../tools/jest/newEntityFormTestUtils';

vi.mock('../../../hooks/useAuth', () => ({ useAuth: () => ({ authState: {} }) }));

const { NewBulkLoadForm } = await import('../NewBulkLoadForm');

const dataLoadService = {
	getLoadTypes: () => ['BulkFMSLoad', 'BulkURLLoad'],
	getBackendBulkLoadTypes: () => ['ALLELE'],
};

const renderForm = (newBulkLoad, bulkLoadDispatch = vi.fn()) => {
	renderWithClient(
		<NewBulkLoadForm
			bulkLoadDialog={true}
			setBulkLoadDialog={vi.fn()}
			groups={[{ id: 3, name: 'Alliance' }]}
			newBulkLoad={newBulkLoad}
			bulkLoadDispatch={bulkLoadDispatch}
			disableFormFields={false}
			setDisableFormFields={vi.fn()}
			dataLoadService={dataLoadService}
		/>
	);
	return bulkLoadDispatch;
};

describe('<NewBulkLoadForm />', () => {
	it.each(['BulkFMSLoad', 'BulkURLLoad'])('sets Scheduled to the boolean chosen on a %s', async (type) => {
		const bulkLoadDispatch = renderForm({ type, backendBulkLoadType: 'ALLELE' });
		await screen.findByText('Scheduled');

		selectInField('Scheduled', 'true');

		expect(bulkLoadDispatch).toHaveBeenCalledWith({ field: 'scheduleActive', value: true });
	});

	it('sets the group to the chosen group id', async () => {
		const bulkLoadDispatch = renderForm({ type: 'BulkFMSLoad' });
		await screen.findByText('Group Name');

		selectInField('Group Name', 'Alliance');

		expect(bulkLoadDispatch).toHaveBeenCalledWith({ field: 'group', value: 3 });
	});
});
