const modTableSettings = {
	RGDStaff:	{
		DiseaseAnnotations:{
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta:[],		
			selectedColumnNames: [
				"Subject", "Disease Relation", "Negated", "Disease", "Reference", "Evidence Code",
				"Related Notes", "Experimental Conditions", "Disease Qualifiers", "Inferred Gene",
				"Asserted Genes", "Inferred Allele", "Asserted Allele", "Data Provider",
				"Secondary Data Provider", "Updated By", "Date Updated", "Created By",
				"Date Created", "Internal", "Obsolete", 
			],
			filters:{
				dataProviderFilter: {
					"dataProvider.abbreviation": { queryString: "RGD", tokenOperator: "AND" },
					"dataProvider.fullName": { queryString: "RGD", tokenOperator: "AND" },
					"dataProvider.shortName": { queryString: "RGD", tokenOperator: "AND" }
				}
			}, 
			isFirst: false,
			tableKeyName: "DiseaseAnnotations",
			tableSettingsKeyName: "DiseaseAnnotationsTableSettings",
		}
	},
	SGDStaff:	{
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
				"Subject", "Disease Relation", "Negated", "Disease",
				"Reference", "Evidence Code", "With", "SGD Strain Background",
				"Annotation Type", "Data Provider", "Updated By", "Date Updated",
				"Created By", "Date Created", "Internal", "Obsolete"
			],
			filters: {
				dataProviderFilter: {
					"dataProvider.abbreviation": { queryString: "SGD", tokenOperator: "AND" },
					"dataProvider.fullName": { queryString: "SGD", tokenOperator: "AND" },
					"dataProvider.shortName": { queryString: "SGD", tokenOperator: "AND" }
				}
			},
			isFirst: false,
			tableKeyName: "DiseaseAnnotations",
			tableSettingsKeyName: "DiseaseAnnotationsTableSettings"
		}
	},
	WBStaff:	{
		DiseaseAnnotations: {
			page: 0,
			rows: 50,
			first: 0,
			filters: {
					modentityidFilter: {
							modEntityId: { queryString: "WBDOannot ", tokenOperator: "AND" }
					},
					dataProviderFilter: {
							"dataProvider.abbreviation": { queryString: "WB", tokenOperator: "AND" },
							"dataProvider.fullName": { queryString: "WB", tokenOperator: "AND" },
							"dataProvider.shortName": { queryString: "WB", tokenOperator: "AND" }
					}
			},
			isFirst: false,
			tableKeyName: "DiseaseAnnotations",
			multiSortMeta: [
					{ field: "modEntityId", order: -1 }
			],
			selectedColumnNames: [
					"MOD Annotation ID", "Subject", "Asserted Genes", "Disease Relation",
					"Negated", "Disease", "Reference", "Evidence Code", "Related Notes",
					"Experimental Conditions", "Genetic Sex", "Genetic Modifier Relation",
					"Genetic Modifier","Data Provider", "Updated By", "Date Updated", "Created By",
					"Date Created", "Internal", "Obsolete", 
			],
			tableSettingsKeyName: "DiseaseAnnotationsTableSettings"
		}
	},
	FBStaff:	{
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
					"Obsolete", "Reference", "Subject", "Negated",
					"Disease Relation", "Disease", "Evidence Code", "Inferred Gene",
					"Genetic Modifier Relation", "Genetic Modifier", "Related Notes", "Data Provider",
					"Updated By", "Date Updated", "Created By", "Date Created" 
			],
			filters: {
				obsoleteFilter: {
					obsolete: { queryString: "false", tokenOperator: "OR" }
				},
					dataProviderFilter: {
							"dataProvider.abbreviation": { queryString: "FB", tokenOperator: "AND" },
							"dataProvider.fullName": { queryString: "FB", tokenOperator: "AND" },
							"dataProvider.shortName": { queryString: "FB", tokenOperator: "AND" }
					}
			},
			isFirst: false,
			tableKeyName: "DiseaseAnnotations",
			tableSettingsKeyName: "DiseaseAnnotationsTableSettings"
		}
	},
	ZFINStaff:	{
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
					"Subject", "Experiments", "Disease Relation", "Disease",
					"Evidence Code", "Reference", "Inferred Gene", "Inferred Allele",
					"Data Provider", "Updated By", "Date Updated", "Created By",
					"Date Created", "Obsolete"
			],
			filters: {
					dataProviderFilter: {
							"dataProvider.abbreviation": { queryString: "ZFIN", tokenOperator: "AND" },
							"dataProvider.fullName": { queryString: "ZFIN", tokenOperator: "AND" },
							"dataProvider.shortName": { queryString: "ZFIN", tokenOperator: "AND" }
					}
			},
			isFirst: false,
			tableKeyName: "DiseaseAnnotations",
			tableSettingsKeyName: "DiseaseAnnotationsTableSettings"
		}
	},
	MGIStaff:	{
		DiseaseAnnotations: {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: [
					"Internal", "Obsolete", "Subject", "Disease Relation",
					"Negated", "Disease", "Reference", "Evidence Code",
					"Related Notes", "Inferred Gene", "Inferred Allele", "Data Provider",
					"Updated By", "Date Updated", "Created By", "Date Created",
			],
			filters: {
					dataProviderFilter: {
							"dataProvider.abbreviation": { queryString: "MGI", tokenOperator: "AND" },
							"dataProvider.fullName": { queryString: "MGI", tokenOperator: "AND" },
							"dataProvider.shortName": { queryString: "MGI", tokenOperator: "AND" }
					},
					obsoleteFilter: {
							obsolete: { queryString: "false", tokenOperator: "OR" }
					}
			},
			isFirst: false,
			tableKeyName: "DiseaseAnnotations",
			tableSettingsKeyName: "DiseaseAnnotationsTableSettings"
		}
	}
}

export function getModTableState(table){
	const oktaToken = JSON.parse(localStorage.getItem('okta-token-storage'));
	const mod = oktaToken?.accessToken?.claims?.Groups?.filter(group => group.includes("Staff"));
	return modTableSettings[mod][table]; 
};

export function getDefaultTableState(table, defaultColumnNames, defaultVisibleColumns){
	return {
			page: 0,
			first: 0,
			rows: 50,
			multiSortMeta: [],
			selectedColumnNames: defaultVisibleColumns ? defaultVisibleColumns : defaultColumnNames,
			filters: {},
			isFirst: false,
			tableKeyName: table,
			tableSettingsKeyName: `${table}TableSettings`
	}
} 
