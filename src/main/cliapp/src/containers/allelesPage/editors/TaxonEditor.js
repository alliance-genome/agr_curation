
	export const TaxonEditor = ({ search, initialValue, }) => {
		return (
			<>
				<AutocompleteEditor
					search={taxonSearch}
					initialValue={props.rowData.taxon?.curie}
					rowProps={props}
					fieldName='taxon'
					onValueChangeHandler={onTaxonValueChange}
				/>
				<ErrorMessageComponent
					errorMessages={errorMessagesRef.current[props.rowIndex]}
					errorField='taxon'
				/>
			</>
		);
	};