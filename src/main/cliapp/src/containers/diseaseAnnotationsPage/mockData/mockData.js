export const data = {
	"results": [
		{
			"type": "AGMDiseaseAnnotation",
			"modInternalId": "mockModInternalId",
			"createdBy": {
				"dateCreated": "2022-07-25T21:18:13.558556Z",
				"dateUpdated": "2022-07-25T21:18:13.558556Z",
				"internal": false,
				"obsolete": false,
				"id": 10740932,
				"uniqueId": "MGI:curation_staff"
			},
			"relatedNotes": [
				{
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-05-22T21:20:00.253489Z",
					"dbDateUpdated": "2023-05-22T21:20:00.25349Z",
					"id": 82884270,
					"freeText": "Yeast GRS1 is homologous to human GARS1, and has been used to study Charcot-Marie-Tooth disease type 2D and severe infantile-onset spinal muscular atrophy",
					"noteType": {
						"internal": false,
						"obsolete": false,
						"id": 7662728,
						"name": "disease_summary"
					}
				}
			],
			"conditionRelations": [{ "handle": "condition relations handle test" }],
			"with": [
				{
					"curie": "with_test_curie",
					"geneSymbol": {
						"displayText": "with_test_symbol"
					}
				}
			],
			"secondaryDataProvider": {
				"sourceOrganization": {
					"abbreviation": "test provider"
				}
			},
			"geneticSex": {
				"name": "genetic sex test"
			},
			"updatedBy": {
				"dateCreated": "2022-07-25T21:18:13.558556Z",
				"dateUpdated": "2022-07-25T21:18:13.558556Z",
				"internal": false,
				"obsolete": false,
				"id": 10740932,
				"uniqueId": "MGI:curation_staff"
			},
			"diseaseGeneticModifierRelation": {"name": "disease genetic modifier relation test"},
			"sgdStrainBackground": {
				"curie": "sgd test curie",
				"name": "SGD Strain Background test"
			}, 
			"diseaseGeneticModifiers": [{"symbol": "disease genetic modifier test"}],
			"diseaseQualifiers": [{"name": "disease qualifiers test"}],
			"dateCreated": "2017-06-08T14:15:35Z",
			"dateUpdated": "2017-06-08T14:15:35Z",
			"internal": false,
			"obsolete": false,
			"dbDateCreated": "2022-11-18T16:33:33.683726Z",
			"dbDateUpdated": "2023-03-16T15:24:38.954678Z",
			"id": 18394375,
			"uniqueId": "MGI:5560505|DOID:0050545|AGRKB:101000000827851",
			"diseaseAnnotationObject": {
				"internal": false,
				"obsolete": false,
				"dbDateUpdated": "2023-04-02T22:02:52.730635Z",
				"curie": "DOID:0050545",
				"name": "visceral heterotaxy",
				"namespace": "disease_ontology",
				"definition": "A physical disorder characterized by the abnormal distribution of the major visceral organs within the chest and abdomen.",
				"definitionUrls": [
					"url:http://en.wikipedia.org/wiki/Situs_ambiguus"
				],
				"subsets": [
					"DO_rare_slim"
				],
				"synonyms": [
					{
						"internal": false,
						"obsolete": false,
						"id": 12282324,
						"name": "heterotaxia"
					},
					{
						"internal": false,
						"obsolete": false,
						"id": 12282325,
						"name": "situs ambiguus"
					}
				]
			},
			"negated": true,
			"relation": {
				"dateCreated": "2022-01-26T09:40:54.020724Z",
				"dateUpdated": "2022-01-26T09:40:54.020726Z",
				"internal": false,
				"obsolete": false,
				"id": 6363603,
				"name": "is_model_of",
				"definition": "is_model_of"
			},
			"evidenceCodes": [
				{
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-04-09T22:01:36.100471Z",
					"curie": "ECO:0000033",
					"name": "author statement supported by traceable reference",
					"namespace": "eco",
					"definition": "A type of author statement in which the author makes a statement that is not supported by information in that particular publication, but rather can be traced to a reference cited by that publication.",
					"definitionUrls": [
						"GO:TAS",
						"ECO:RCT"
					],
					"subsets": [
						"agr_eco_terms"
					],
					"synonyms": [
						{
							"internal": false,
							"obsolete": false,
							"id": 12272565,
							"name": "traceable author statement"
						}
					],
					"abbreviation": "TAS"
				}
			],
			"singleReference": {
				"internal": false,
				"obsolete": false,
				"dbDateUpdated": "2022-11-18T16:33:21.46375Z",
				"curie": "AGRKB:101000000827851",
				"crossReferences": [
					{
						"internal": false,
						"obsolete": false,
						"id": 74063345,
						"referencedCurie": "MGI:5284969",
						"displayName": "MGI:5284969"
					}
				]
			},
			"annotationType": {
				"internal": false,
				"obsolete": false,
				"id": 7662755,
				"name": "manually_curated"
			},
			"dataProvider": {
				"internal": false,
				"obsolete": false,
				"id": 75083698,
				"sourceOrganization": {
					"internal": false,
					"obsolete": false,
					"id": 20308680,
					"uniqueId": "MGI",
					"abbreviation": "MGI",
					"fullName": "Mouse Genome Informatics",
					"homepageResourceDescriptorPage": {
						"internal": false,
						"obsolete": false,
						"dbDateCreated": "2023-02-20T09:26:39.240245Z",
						"dbDateUpdated": "2023-05-11T22:00:03.831263Z",
						"id": 41750686,
						"name": "homepage",
						"urlTemplate": "http://www.informatics.jax.org/"
					}
				}
			},
			"diseaseAnnotationSubject": {
				"type": "AffectedGenomicModel",
				"internal": false,
				"obsolete": false,
				"dbDateUpdated": "2023-05-17T12:33:39.453344Z",
				"curie": "MGI:5560505",
				"taxon": {
					"internal": false,
					"obsolete": false,
					"curie": "NCBITaxon:10090",
					"name": "Mus musculus"
				},
				"dataProvider": {
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-05-17T12:33:39.451734Z",
					"id": 75611693,
					"sourceOrganization": {
						"internal": false,
						"obsolete": false,
						"id": 20308680,
						"uniqueId": "MGI",
						"abbreviation": "MGI",
						"fullName": "Mouse Genome Informatics",
						"homepageResourceDescriptorPage": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-02-20T09:26:39.240245Z",
							"dbDateUpdated": "2023-05-11T22:00:03.831263Z",
							"id": 41750686,
							"name": "homepage",
							"urlTemplate": "http://www.informatics.jax.org/"
						}
					},
					"crossReference": {
						"internal": false,
						"obsolete": false,
						"dbDateCreated": "2023-05-17T12:33:39.450658Z",
						"dbDateUpdated": "2023-05-17T12:33:39.450659Z",
						"id": 80324218,
						"referencedCurie": "MGI:5560505",
						"displayName": "MGI:5560505",
						"resourceDescriptorPage": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-02-20T09:26:39.239734Z",
							"dbDateUpdated": "2023-05-11T22:00:03.830784Z",
							"id": 41750685,
							"name": "genotype",
							"urlTemplate": "http://www.informatics.jax.org/allele/genoview/MGI:[%s]"
						}
					}
				},
				"name": "Rfx3<sup>b2b1213Clo</sup>/Rfx3<sup>b2b1213Clo</sup>  [background:] C57BL/6J-Rfx3<sup>b2b1213Clo</sup>",
				"subtype": {
					"internal": false,
					"obsolete": false,
					"id": 40545169,
					"name": "genotype"
				}
			},
			"inferredGene": {
				"type": "Gene",
				"createdBy": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-05-17T10:04:44.939207Z",
					"dbDateUpdated": "2023-05-17T10:04:44.939209Z",
					"id": 80081033,
					"uniqueId": "MGI"
				},
				"updatedBy": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-05-17T10:04:44.939207Z",
					"dbDateUpdated": "2023-05-17T10:04:44.939209Z",
					"id": 80081033,
					"uniqueId": "MGI"
				},
				"internal": false,
				"obsolete": false,
				"dbDateUpdated": "2023-05-17T10:05:06.055796Z",
				"curie": "MGI:106582",
				"taxon": {
					"internal": false,
					"obsolete": false,
					"curie": "NCBITaxon:10090",
					"name": "Mus musculus"
				},
				"dataProvider": {
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-05-17T10:05:06.055328Z",
					"id": 75132764,
					"sourceOrganization": {
						"internal": false,
						"obsolete": false,
						"id": 20308680,
						"uniqueId": "MGI",
						"abbreviation": "MGI",
						"fullName": "Mouse Genome Informatics",
						"homepageResourceDescriptorPage": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-02-20T09:26:39.240245Z",
							"dbDateUpdated": "2023-05-11T22:00:03.831263Z",
							"id": 41750686,
							"name": "homepage",
							"urlTemplate": "http://www.informatics.jax.org/"
						}
					},
					"crossReference": {
						"internal": false,
						"obsolete": false,
						"dbDateCreated": "2023-05-17T10:05:06.054841Z",
						"dbDateUpdated": "2023-05-17T10:05:06.054842Z",
						"id": 80081812,
						"referencedCurie": "MGI:106582",
						"displayName": "MGI:106582",
						"resourceDescriptorPage": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-02-20T09:26:39.235386Z",
							"dbDateUpdated": "2023-05-11T22:00:03.81929Z",
							"id": 41750677,
							"name": "gene",
							"urlTemplate": "http://www.informatics.jax.org/marker/MGI:[%s]"
						}
					}
				},
				"geneType": {
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-02-19T22:00:41.015903Z",
					"curie": "SO:0001217",
					"name": "protein_coding_gene",
					"namespace": "sequence",
					"definition": "A gene that codes for an RNA that can be translated into a protein.",
					"subsets": [
						"Alliance_of_Genome_Resources"
					],
					"synonyms": [
						{
							"internal": false,
							"obsolete": false,
							"id": 12304001,
							"name": "protein coding gene"
						}
					]
				},
				"geneSymbol": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-03-03T20:23:28.769215Z",
					"dbDateUpdated": "2023-05-17T10:05:06.06461Z",
					"id": 51339916,
					"nameType": {
						"internal": false,
						"obsolete": false,
						"id": 20308690,
						"name": "nomenclature_symbol",
						"definition": "A symbol for an object: e.g., pax6<sup>Leca2</sup>."
					},
					"formatText": "Rfx3",
					"displayText": "Rfx3"
				},
				"geneFullName": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-03-03T20:23:28.77077Z",
					"dbDateUpdated": "2023-05-17T10:05:06.063304Z",
					"id": 51339917,
					"nameType": {
						"internal": false,
						"obsolete": false,
						"id": 20308691,
						"name": "full_name",
						"definition": "The full length name of an entity: e.g., broad angular dumpy."
					},
					"formatText": "regulatory factor X, 3 (influences HLA class II expression)",
					"displayText": "regulatory factor X, 3 (influences HLA class II expression)"
				}
			},
			"inferredAllele": {
				"type": "Allele",
				"internal": false,
				"obsolete": false,
				"dbDateUpdated": "2023-04-14T21:55:23.136544Z",
				"curie": "MGI:5560494",
				"taxon": {
					"internal": false,
					"obsolete": false,
					"curie": "NCBITaxon:10090",
					"name": "Mus musculus"
				},
				"dataProvider": {
					"internal": false,
					"obsolete": false,
					"id": 75738124,
					"sourceOrganization": {
						"internal": false,
						"obsolete": false,
						"id": 20308680,
						"uniqueId": "MGI",
						"abbreviation": "MGI",
						"fullName": "Mouse Genome Informatics",
						"homepageResourceDescriptorPage": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-02-20T09:26:39.240245Z",
							"dbDateUpdated": "2023-05-11T22:00:03.831263Z",
							"id": 41750686,
							"name": "homepage",
							"urlTemplate": "http://www.informatics.jax.org/"
						}
					}
				},
				"references": [
					{
						"internal": false,
						"obsolete": false,
						"dbDateUpdated": "2022-11-18T16:33:21.46375Z",
						"curie": "AGRKB:101000000827851",
						"crossReferences": [
							{
								"internal": false,
								"obsolete": false,
								"id": 74063345,
								"referencedCurie": "MGI:5284969",
								"displayName": "MGI:5284969"
							}
						]
					},
					{
						"internal": false,
						"obsolete": false,
						"dbDateCreated": "2022-11-28T18:50:49.104161Z",
						"dbDateUpdated": "2022-11-28T18:50:49.104162Z",
						"curie": "AGRKB:101000000284070",
						"crossReferences": [
							{
								"internal": false,
								"obsolete": false,
								"id": 74907086,
								"referencedCurie": "PMID:25807483",
								"displayName": "PMID:25807483"
							},
							{
								"internal": false,
								"obsolete": false,
								"id": 74907085,
								"referencedCurie": "PMCID:PMC4617540",
								"displayName": "PMCID:PMC4617540"
							},
							{
								"internal": false,
								"obsolete": false,
								"id": 74907083,
								"referencedCurie": "DOI:10.1038/nature14269",
								"displayName": "DOI:10.1038/nature14269"
							},
							{
								"internal": false,
								"obsolete": false,
								"id": 74907084,
								"referencedCurie": "MGI:5644076",
								"displayName": "MGI:5644076"
							}
						]
					}
				],
				"inCollection": {
					"createdBy": {
						"dateCreated": "2022-05-06T12:26:36.696662Z",
						"dateUpdated": "2022-05-06T12:26:36.696665Z",
						"internal": false,
						"obsolete": false,
						"id": 7825552,
						"allianceMember": {
							"internal": false,
							"obsolete": false,
							"id": 20308683,
							"uniqueId": "WB",
							"abbreviation": "WB",
							"fullName": "WormBase",
							"homepageResourceDescriptorPage": {
								"internal": false,
								"obsolete": false,
								"dbDateCreated": "2023-02-20T09:26:39.609757Z",
								"dbDateUpdated": "2023-05-11T22:00:04.659745Z",
								"id": 41750788,
								"name": "homepage",
								"urlTemplate": "https://www.wormbase.org/"
							}
						},
					},
					"updatedBy": {
						"dateCreated": "2022-05-06T12:26:36.696662Z",
						"dateUpdated": "2022-05-06T12:26:36.696665Z",
						"internal": false,
						"obsolete": false,
						"id": 7825552,
						"allianceMember": {
							"internal": false,
							"obsolete": false,
							"id": 20308683,
							"uniqueId": "WB",
							"abbreviation": "WB",
							"fullName": "WormBase",
							"homepageResourceDescriptorPage": {
								"internal": false,
								"obsolete": false,
								"dbDateCreated": "2023-02-20T09:26:39.609757Z",
								"dbDateUpdated": "2023-05-11T22:00:04.659745Z",
								"id": 41750788,
								"name": "homepage",
								"urlTemplate": "https://www.wormbase.org/"
							}
						},
					},
					"dateCreated": "2022-11-15T22:01:18.89272Z",
					"dateUpdated": "2022-11-15T22:01:18.895014Z",
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2022-11-15T22:01:18.905486Z",
					"dbDateUpdated": "2022-11-15T22:01:18.905488Z",
					"id": 15414972,
					"name": "B2B/CvDC",
					"definition": "The National Heart, Lung, and Blood Institute (NHLBI) Bench to Bassinet (B2B) Program of translational research in pediatric cardiovascular disease"
				},
				"isExtinct": false,
				"alleleSymbol": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-04-14T21:55:23.146643Z",
					"dbDateUpdated": "2023-04-14T21:55:23.146645Z",
					"id": 70002674,
					"nameType": {
						"internal": false,
						"obsolete": false,
						"id": 20308690,
						"name": "nomenclature_symbol",
						"definition": "A symbol for an object: e.g., pax6<sup>Leca2</sup>."
					},
					"formatText": "Rfx3<sup>b2b1213Clo</sup>",
					"displayText": "Rfx3<sup>b2b1213Clo</sup>"
				},
				"alleleFullName": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-04-14T21:55:23.147861Z",
					"dbDateUpdated": "2023-04-14T21:55:23.147862Z",
					"id": 70002675,
					"nameType": {
						"internal": false,
						"obsolete": false,
						"id": 20308691,
						"name": "full_name",
						"definition": "The full length name of an entity: e.g., broad angular dumpy."
					},
					"formatText": "Bench to Bassinet Program (B2B/CVDC), mutation 1213 Cecilia Lo",
					"displayText": "Bench to Bassinet Program (B2B/CVDC), mutation 1213 Cecilia Lo"
				}
			}
		}
	],
	"totalResults": 1,
	"returnedRecords": 1
};
