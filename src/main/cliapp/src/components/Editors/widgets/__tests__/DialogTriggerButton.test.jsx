import { render, fireEvent } from '@testing-library/react';
import { DialogTriggerButton } from '../DialogTriggerButton';

describe('<DialogTriggerButton />', () => {
	it('prefers displayText over addText', () => {
		const { getByRole } = render(<DialogTriggerButton onOpen={vi.fn()} displayText="Notes(3) " addText="Add Note" />);

		expect(getByRole('button')).toHaveTextContent('Notes(3)');
		expect(getByRole('button')).not.toHaveTextContent('Add Note');
	});

	it('renders displayHtml as markup in preference to displayText', () => {
		const { container } = render(
			<DialogTriggerButton onOpen={vi.fn()} displayHtml="brat<sup>11</sup>" displayText="ignored" addText="Add" />
		);

		expect(container.querySelector('sup')).toHaveTextContent('11');
		expect(container.textContent).not.toContain('ignored');
	});

	// The widget knows nothing about which dialog it opens, so the caller closes over that.
	it('calls onOpen with the click event and nothing else', () => {
		const onOpen = vi.fn();
		const { getByRole } = render(<DialogTriggerButton onOpen={onOpen} addText="Add Note" />);

		fireEvent.click(getByRole('button'));

		expect(onOpen).toHaveBeenCalledTimes(1);
		expect(onOpen.mock.calls[0][0]).toHaveProperty('type', 'click');
	});
});
