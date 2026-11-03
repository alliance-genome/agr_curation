import React from 'react';
import { render, screen } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { FitTextHeading } from '../FitTextHeading';

// jsdom lays nothing out, so the heading's widths are derived from its font size: its text is as wide as
// textWidthPerPixel times the font size plus textWidthOverhead, and it has AVAILABLE_WIDTH to fit into.
const STYLED_FONT_SIZE = 40;
const AVAILABLE_WIDTH = 500;

const mockLayout = (textWidthPerPixel, textWidthOverhead = 0) => {
	const fontSize = (element) => parseFloat(element.style.fontSize) || STYLED_FONT_SIZE;
	vi.spyOn(window, 'getComputedStyle').mockImplementation(() => ({ fontSize: `${STYLED_FONT_SIZE}px` }));
	vi.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockImplementation(() => AVAILABLE_WIDTH);
	vi.spyOn(HTMLElement.prototype, 'scrollWidth', 'get').mockImplementation(function () {
		return Math.max(AVAILABLE_WIDTH, fontSize(this) * textWidthPerPixel + textWidthOverhead);
	});
};

describe('FitTextHeading', () => {
	afterEach(() => {
		vi.restoreAllMocks();
		vi.unstubAllGlobals();
	});

	it('Renders its HTML on one line', () => {
		render(<FitTextHeading html="Allele: Pax6<sup>Sey</sup>" />);

		const heading = screen.getByRole('heading');
		expect(heading.querySelector('sup')).toHaveTextContent('Sey');
		expect(heading).toHaveClass('white-space-nowrap');
	});

	it('Keeps its styled size when the text fits', () => {
		mockLayout(10);
		render(<FitTextHeading html="Allele: Pax6" />);

		expect(screen.getByRole('heading').style.fontSize).toEqual('');
	});

	it('Shrinks until the text fits', () => {
		mockLayout(20);
		render(<FitTextHeading html="Allele: a longer symbol" />);

		const heading = screen.getByRole('heading');
		expect(parseFloat(heading.style.fontSize)).toBeLessThanOrEqual(25);
		expect(parseFloat(heading.style.fontSize)).toBeGreaterThanOrEqual(24);
		expect(heading).not.toHaveAttribute('title');
	});

	it('Stops at half its styled size and offers the full text on hover', () => {
		mockLayout(100);
		render(<FitTextHeading html="Allele: an extremely long symbol" />);

		const heading = screen.getByRole('heading');
		expect(heading.style.fontSize).toEqual(`${STYLED_FONT_SIZE / 2}px`);
		expect(heading).toHaveAttribute('title', 'Allele: an extremely long symbol');
	});

	it('Settles on the largest size that fits when the first estimate is too large', () => {
		// A fixed overhead makes the width-proportional estimate of 23px overshoot; 22px is the largest fit.
		mockLayout(20, 60);
		render(<FitTextHeading html="Allele: a longer symbol" />);

		expect(screen.getByRole('heading').style.fontSize).toEqual('22px');
	});

	it('Returns to its styled size when given text that fits', () => {
		mockLayout(100);
		const { rerender } = render(<FitTextHeading html="Allele: an extremely long symbol" />);
		vi.restoreAllMocks();
		mockLayout(10);

		rerender(<FitTextHeading html="Allele: Pax6" />);

		const heading = screen.getByRole('heading');
		expect(heading.style.fontSize).toEqual('');
		expect(heading).not.toHaveAttribute('title');
	});

	it('Refits when its width changes, not when only its height does', () => {
		let onResize = null;
		vi.stubGlobal(
			'ResizeObserver',
			class {
				constructor(callback) {
					onResize = callback;
				}
				observe() {}
				disconnect() {}
			}
		);
		let availableWidth = AVAILABLE_WIDTH;
		mockLayout(20);
		vi.spyOn(HTMLElement.prototype, 'clientWidth', 'get').mockImplementation(() => availableWidth);
		render(<FitTextHeading html="Allele: a longer symbol" />);
		const heading = screen.getByRole('heading');
		expect(heading.style.fontSize).toEqual('25px');

		heading.style.fontSize = '30px';
		onResize();
		expect(heading.style.fontSize).toEqual('30px');

		availableWidth = 400;
		onResize();
		expect(heading.style.fontSize).toEqual('20px');
	});
});
