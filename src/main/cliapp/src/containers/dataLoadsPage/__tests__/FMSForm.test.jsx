import { render, screen } from '@testing-library/react';
import { selectInField } from '../../../tools/jest/newEntityFormTestUtils';
import { FMSForm } from '../FMSForm';
import { URLForm } from '../URLForm';

describe('the data load forms', () => {
	it.each([
		['FMSForm', (props) => <FMSForm hideFMS={{ current: false }} {...props} />],
		['URLForm', (props) => <URLForm hideURL={{ current: false }} hideOntology={{ current: true }} {...props} />],
	])('%s passes the Scheduled value chosen to onScheduleActiveChange', (name, renderDataLoadForm) => {
		const onScheduleActiveChange = vi.fn();
		const onChange = vi.fn();
		render(renderDataLoadForm({ newBulkLoad: {}, onChange, onScheduleActiveChange, disableFormFields: false }));
		expect(screen.getByText('Scheduled')).toBeInTheDocument();

		selectInField('Scheduled', 'false');

		expect(onScheduleActiveChange).toHaveBeenCalledWith(false);
		expect(onChange).not.toHaveBeenCalled();
	});
});
