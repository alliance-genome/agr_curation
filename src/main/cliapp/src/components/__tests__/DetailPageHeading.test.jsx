import React from 'react';
import { render, screen } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { DetailPageHeading } from '../DetailPageHeading';

const AGRKB_CURIE = 'AGRKB:101000000000001';

const headingText = () => screen.getByRole('heading', { level: 1 }).textContent;

describe('DetailPageHeading', () => {
	it('Shows the label and primary external ID', () => {
		render(
			<DetailPageHeading
				entityType="Allele"
				entity={{ primaryExternalId: 'MGI:5146840', modInternalId: 'MGI:1', curie: AGRKB_CURIE }}
				label="Pax6"
			/>
		);

		expect(headingText()).toEqual('Allele: Pax6 (MGI:5146840)');
	});

	it('Shows the MOD internal ID when there is no primary external ID', () => {
		render(
			<DetailPageHeading entityType="Allele" entity={{ modInternalId: 'MGI:1', curie: AGRKB_CURIE }} label="Pax6" />
		);

		expect(headingText()).toEqual('Allele: Pax6 (MGI:1)');
	});

	it('Shows the curie when there is no primary external or MOD internal ID', () => {
		render(<DetailPageHeading entityType="Allele" entity={{ curie: AGRKB_CURIE }} label="Pax6" />);

		expect(headingText()).toEqual(`Allele: Pax6 (${AGRKB_CURIE})`);
	});

	it('Shows the identifier alone when there is no label', () => {
		render(<DetailPageHeading entityType="Allele" entity={{ curie: AGRKB_CURIE }} />);

		expect(headingText()).toEqual(`Allele: ${AGRKB_CURIE}`);
	});

	it('Renders the label as HTML', () => {
		render(<DetailPageHeading entityType="Allele" entity={{ curie: AGRKB_CURIE }} label="Pax6<sup>Sey</sup>" />);

		expect(screen.getByRole('heading', { level: 1 }).querySelector('sup')).toHaveTextContent('Sey');
	});

	it('Names the page by its entity type while the entity has no identifier', () => {
		render(<DetailPageHeading entityType="Allele" entity={{}} label="Pax6" />);

		expect(headingText()).toEqual('Allele Detail Page');
	});
});
