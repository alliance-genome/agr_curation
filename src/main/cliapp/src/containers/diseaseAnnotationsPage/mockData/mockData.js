export const data = {
	"type": "AGMDiseaseAnnotation",
	"createdBy": {
			"internal": false,
			"obsolete": false,
			"dbDateCreated": "2022-09-08T13:13:22.60487Z",
			"dbDateUpdated": "2022-09-08T13:13:22.604883Z",
			"id": 10975050,
			"uniqueId": "WB:WBPerson324"
	},
	"dateCreated": "2017-05-22T21:41:16Z",
	"dateUpdated": "2017-05-18T00:00:00Z",
	"internal": false,
	"obsolete": false,
	"dbDateUpdated": "2023-05-10T21:10:07.902263Z",
	"id": 7823872,
	"uniqueId": "WB:WBStrain00024340|DOID:11723|AGRKB:101000000624376",
	"modEntityId": "WBDOannot00000430",
	"object": {
			"internal": false,
			"obsolete": false,
			"dbDateUpdated": "2023-04-02T22:01:27.833619Z",
			"curie": "DOID:11723",
			"name": "Duchenne muscular dystrophy",
			"namespace": "disease_ontology",
			"definition": "A muscular dystrophy that has_material_basis_in X-linked mutations in the DMD gene found on the X chromosome. It is characterized by rapidly progressing muscle weakness and muscle atrophy initially involving the lower extremities and eventually affecting the whole body. It affects males whereas females can be carriers. The symptoms start before the age of six and may appear at infancy.",
			"definitionUrls": [
					"url:http://omim.org/entry/300377",
					"url:http://www.genome.gov/19518854",
					"url:http://en.wikipedia.org/wiki/Duchenne_muscular_dystrophy"
			],
			"subsets": [
					"DO_FlyBase_slim",
					"NCIthesaurus",
					"DO_rare_slim"
			],
			"synonyms": [
					{
							"internal": false,
							"obsolete": false,
							"id": 12272324,
							"name": "Muscular dystrophy, Duchenne"
					}
			]
	},
	"negated": false,
	"diseaseRelation": {
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
					"dbDateUpdated": "2023-04-09T22:01:36.106481Z",
					"curie": "ECO:0000315",
					"name": "mutant phenotype evidence used in manual assertion",
					"namespace": "eco",
					"definition": "A type of mutant phenotype evidence that is used in a manual assertion.",
					"definitionUrls": [
							"ECO:MCC",
							"GOECO:IMP"
					],
					"subsets": [
							"valid_with_protein_complex",
							"valid_with_protein",
							"valid_with_chemical_entity",
							"valid_with_gene",
							"agr_eco_terms"
					],
					"synonyms": [
							{
									"internal": false,
									"obsolete": false,
									"id": 3224780,
									"name": "IMP"
							},
							{
									"internal": false,
									"obsolete": false,
									"id": 12272371,
									"name": "inferred from mutant phenotype"
							}
					],
					"abbreviation": "IMP"
			},
			{
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-04-09T22:01:36.10941Z",
					"curie": "ECO:0007013",
					"name": "combinatorial experimental and author inference evidence used in manual assertion",
					"namespace": "eco",
					"definition": "A type of combinatorial evidence from author knowledge and experimental evidence that is used in a manual assertion.",
					"definitionUrls": [
							"ECO:RCT"
					],
					"subsets": [
							"agr_eco_terms"
					],
					"synonyms": [
							{
									"internal": false,
									"obsolete": false,
									"id": 12275783,
									"name": "combinatorial evidence from author knowledge and experimental evidence used in manual assertion"
							}
					],
					"abbreviation": "CEA"
			}
	],
	"conditionRelations": [
			{
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2022-11-18T19:15:44.372919Z",
					"dbDateUpdated": "2023-05-10T21:10:07.896665Z",
					"id": 18559617,
					"uniqueId": "ameliorated_by|ZECO:0000111|WB:WBMol:00004976",
					"conditionRelationType": {
							"internal": false,
							"obsolete": false,
							"id": 7662733,
							"name": "ameliorated_by"
					},
					"conditions": [
							{
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2022-10-17T20:04:57.855012Z",
									"dbDateUpdated": "2023-05-10T21:10:07.897199Z",
									"id": 13586399,
									"uniqueId": "ZECO:0000111|WB:WBMol:00004976",
									"conditionClass": {
											"internal": false,
											"obsolete": false,
											"curie": "ZECO:0000111",
											"name": "chemical treatment",
											"definition": "Experimental condition in which the fish is treated with a chemical substance. This treatment could be administered by adding the chemical substance to the tank water, injections, or by consumption.",
											"subsets": [
													"ZECO_0000267"
											]
									},
									"conditionSummary": "chemical treatment:Methazolamide",
									"conditionChemical": {
											"internal": false,
											"obsolete": false,
											"dbDateUpdated": "2023-02-13T00:01:11.299345Z",
											"curie": "WB:WBMol:00004976",
											"name": "Methazolamide",
											"namespace": "molecule"
									}
							}
					]
			}
	],
	"singleReference": {
			"updatedBy": {
					"dateCreated": "2022-05-03T18:33:16.445309Z",
					"dateUpdated": "2022-05-03T18:33:16.445322Z",
					"internal": false,
					"obsolete": false,
					"id": 7685810,
					"uniqueId": "Chris|Grove|chris@wormbase.org",
					"firstName": "Chris",
					"lastName": "Grove",
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
									"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
									"id": 41750788,
									"name": "homepage",
									"urlTemplate": "https://www.wormbase.org/"
							}
					},
					"oktaId": "00u347gob7EQ6WsMz5d7",
					"oktaEmail": "chris@wormbase.org",
			},
			"dateUpdated": "2023-05-10T21:04:13.711711Z",
			"internal": false,
			"obsolete": false,
			"dbDateCreated": "2022-11-18T19:15:43.96129Z",
			"dbDateUpdated": "2023-05-10T21:04:13.714669Z",
			"curie": "AGRKB:101000000624376",
			"crossReferences": [
					{
							"internal": false,
							"obsolete": false,
							"id": 74009206,
							"referencedCurie": "WB:WBPaper00035094",
							"displayName": "WB:WBPaper00035094"
					},
					{
							"internal": false,
							"obsolete": false,
							"id": 74009205,
							"referencedCurie": "PMID:19648295",
							"displayName": "PMID:19648295"
					},
					{
							"internal": false,
							"obsolete": false,
							"id": 74009204,
							"referencedCurie": "DOI:10.1093/hmg/ddp358",
							"displayName": "DOI:10.1093/hmg/ddp358"
					}
			]
	},
	"annotationType": {
			"internal": false,
			"obsolete": false,
			"id": 7662755,
			"name": "manually_curated"
	},
	"geneticSex": {
			"internal": false,
			"obsolete": false,
			"id": 7662743,
			"name": "hermaphrodite"
	},
	"relatedNotes": [
			{
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-05-10T21:10:07.88597Z",
					"dbDateUpdated": "2023-05-10T21:10:07.885972Z",
					"id": 79682347,
					"freeText": "RNAi-mediated knockdown of cah-4 (carbonic anhydrase) in conjunction with the drug Methazolamide (MTZ) significantly reduced the muscle degeneration in dys-1; hlh-1 double mutants which model Duchenne muscular dystrophy.",
					"noteType": {
							"internal": false,
							"obsolete": false,
							"id": 7662728,
							"name": "disease_summary"
					}
			}
	],
	"dataProvider": {
			"updatedBy": {
					"dateCreated": "2022-05-03T18:33:16.445309Z",
					"dateUpdated": "2022-05-03T18:33:16.445322Z",
					"internal": false,
					"obsolete": false,
					"id": 7685810,
					"uniqueId": "Chris|Grove|chris@wormbase.org",
					"firstName": "Chris",
					"lastName": "Grove",
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
									"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
									"id": 41750788,
									"name": "homepage",
									"urlTemplate": "https://www.wormbase.org/"
							}
					},
					"oktaId": "00u347gob7EQ6WsMz5d7",
					"oktaEmail": "chris@wormbase.org",
			},
			"dateUpdated": "2023-05-11T13:02:13.870496Z",
			"internal": false,
			"obsolete": false,
			"dbDateUpdated": "2023-05-11T13:02:13.902999Z",
			"id": 75083701,
			"sourceOrganization": {
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
							"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
							"id": 41750788,
							"name": "homepage",
							"urlTemplate": "https://www.wormbase.org/"
					}
			},
			"crossReference": {
					"internal": false,
					"obsolete": false,
					"dbDateCreated": "2023-05-10T21:11:40.718605Z",
					"dbDateUpdated": "2023-05-11T13:02:13.902006Z",
					"id": 79683607,
					"referencedCurie": "DOID:162",
					"displayName": "DOID:162",
					"resourceDescriptorPage": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-02-20T09:26:39.841702Z",
							"dbDateUpdated": "2023-05-04T22:00:09.990079Z",
							"id": 41750841,
							"name": "disease/wb",
							"urlTemplate": "https://www.wormbase.org/resources/disease/[%s]"
					}
			}
	},
	"diseaseGeneticModifiers": [
			{
					"type": "Gene",
					"createdBy": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2022-09-08T13:13:22.467843Z",
							"dbDateUpdated": "2022-09-08T13:13:22.467853Z",
							"id": 10975049,
							"uniqueId": "WB:curator"
					},
					"updatedBy": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2022-09-08T13:13:22.467843Z",
							"dbDateUpdated": "2022-09-08T13:13:22.467853Z",
							"id": 10975049,
							"uniqueId": "WB:curator"
					},
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-05-16T12:27:55.919419Z",
					"curie": "WB:WBGene00000282",
					"taxon": {
							"internal": false,
							"obsolete": false,
							"curie": "NCBITaxon:6239",
							"name": "Caenorhabditis elegans"
					},
					"dataProvider": {
							"internal": false,
							"obsolete": false,
							"dbDateUpdated": "2023-05-16T12:27:55.918864Z",
							"id": 75799694,
							"sourceOrganization": {
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
											"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
											"id": 41750788,
											"name": "homepage",
											"urlTemplate": "https://www.wormbase.org/"
									}
							},
							"crossReference": {
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-05-16T12:27:55.91828Z",
									"dbDateUpdated": "2023-05-16T12:27:55.918282Z",
									"id": 79685390,
									"referencedCurie": "WB:WBGene00000282",
									"displayName": "WBGene00000282",
									"resourceDescriptorPage": {
											"internal": false,
											"obsolete": false,
											"dbDateCreated": "2023-02-20T09:26:39.602562Z",
											"dbDateUpdated": "2023-05-04T22:00:09.592619Z",
											"id": 41750773,
											"name": "gene",
											"urlTemplate": "https://www.wormbase.org/db/get?name=[%s];class=Gene"
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
							"dbDateCreated": "2023-01-31T00:24:38.263833Z",
							"dbDateUpdated": "2023-05-16T12:27:55.946102Z",
							"id": 27209548,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308690,
									"name": "nomenclature_symbol",
									"definition": "A symbol for an object: e.g., pax6<sup>Leca2</sup>."
							},
							"formatText": "cah-4",
							"displayText": "cah-4",
							"synonymScope": {
									"internal": false,
									"obsolete": false,
									"id": 20308698,
									"name": "exact"
							}
					},
					"geneFullName": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-01-31T00:24:38.264351Z",
							"dbDateUpdated": "2023-05-16T12:27:55.945003Z",
							"id": 27209549,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308691,
									"name": "full_name",
									"definition": "The full length name of an entity: e.g., broad angular dumpy."
							},
							"formatText": "Carbonic AnHydrase 4",
							"displayText": "Carbonic AnHydrase 4"
					},
					"geneSystematicName": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-01-31T00:24:38.26478Z",
							"dbDateUpdated": "2023-05-16T12:27:55.948689Z",
							"id": 27209550,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308692,
									"name": "systematic_name",
									"definition": "A systematic name: e.g., CG4889<sup>1</sup>."
							},
							"formatText": "R01E6.3",
							"displayText": "R01E6.3",
							"synonymScope": {
									"internal": false,
									"obsolete": false,
									"id": 20308698,
									"name": "exact"
							}
					},
					"geneSynonyms": [
							{
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-01-31T00:24:38.265213Z",
									"dbDateUpdated": "2023-05-16T12:27:55.947112Z",
									"id": 27209551,
									"nameType": {
											"internal": false,
											"obsolete": false,
											"id": 20308697,
											"name": "unspecified",
											"definition": "Unclassified name"
									},
									"formatText": "CELE_R01E6.3",
									"displayText": "CELE_R01E6.3"
							}
					]
			}
	],
	"diseaseGeneticModifierRelation": {
			"internal": false,
			"obsolete": false,
			"id": 7662744,
			"name": "ameliorated_by"
	},
	"subject": {
			"type": "AffectedGenomicModel",
			"internal": false,
			"obsolete": false,
			"dbDateUpdated": "2023-01-31T01:04:28.075722Z",
			"curie": "WB:WBStrain00024340",
			"taxon": {
					"internal": false,
					"obsolete": false,
					"curie": "NCBITaxon:6239",
					"name": "Caenorhabditis elegans"
			},
			"dataProvider": {
					"internal": false,
					"obsolete": false,
					"id": 75643867,
					"sourceOrganization": {
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
									"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
									"id": 41750788,
									"name": "homepage",
									"urlTemplate": "https://www.wormbase.org/"
							}
					}
			},
			"name": "LS587"
	},
	"assertedGenes": [
			{
					"type": "Gene",
					"createdBy": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2022-09-08T13:13:22.467843Z",
							"dbDateUpdated": "2022-09-08T13:13:22.467853Z",
							"id": 10975049,
							"uniqueId": "WB:curator"
					},
					"updatedBy": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2022-09-08T13:13:22.467843Z",
							"dbDateUpdated": "2022-09-08T13:13:22.467853Z",
							"id": 10975049,
							"uniqueId": "WB:curator"
					},
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-05-16T12:29:04.108802Z",
					"curie": "WB:WBGene00001131",
					"taxon": {
							"internal": false,
							"obsolete": false,
							"curie": "NCBITaxon:6239",
							"name": "Caenorhabditis elegans"
					},
					"dataProvider": {
							"internal": false,
							"obsolete": false,
							"dbDateUpdated": "2023-05-16T12:29:04.108264Z",
							"id": 77774850,
							"sourceOrganization": {
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
											"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
											"id": 41750788,
											"name": "homepage",
											"urlTemplate": "https://www.wormbase.org/"
									}
							},
							"crossReference": {
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-05-16T12:29:04.107675Z",
									"dbDateUpdated": "2023-05-16T12:29:04.107677Z",
									"id": 79687048,
									"referencedCurie": "WB:WBGene00001131",
									"displayName": "WBGene00001131",
									"resourceDescriptorPage": {
											"internal": false,
											"obsolete": false,
											"dbDateCreated": "2023-02-20T09:26:39.602562Z",
											"dbDateUpdated": "2023-05-04T22:00:09.592619Z",
											"id": 41750773,
											"name": "gene",
											"urlTemplate": "https://www.wormbase.org/db/get?name=[%s];class=Gene"
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
							"dbDateCreated": "2023-01-31T00:25:02.951255Z",
							"dbDateUpdated": "2023-05-16T12:29:04.133359Z",
							"id": 27213467,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308690,
									"name": "nomenclature_symbol",
									"definition": "A symbol for an object: e.g., pax6<sup>Leca2</sup>."
							},
							"formatText": "dys-1",
							"displayText": "dys-1",
							"synonymScope": {
									"internal": false,
									"obsolete": false,
									"id": 20308698,
									"name": "exact"
							}
					},
					"geneFullName": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-01-31T00:25:02.951815Z",
							"dbDateUpdated": "2023-05-16T12:29:04.132335Z",
							"id": 27213468,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308691,
									"name": "full_name",
									"definition": "The full length name of an entity: e.g., broad angular dumpy."
							},
							"formatText": "DYStrophin related 1",
							"displayText": "DYStrophin related 1"
					},
					"geneSystematicName": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-01-31T00:25:02.952262Z",
							"dbDateUpdated": "2023-05-16T12:29:04.135691Z",
							"id": 27213469,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308692,
									"name": "systematic_name",
									"definition": "A systematic name: e.g., CG4889<sup>1</sup>."
							},
							"formatText": "F15D3.1",
							"displayText": "F15D3.1",
							"synonymScope": {
									"internal": false,
									"obsolete": false,
									"id": 20308698,
									"name": "exact"
							}
					},
					"geneSynonyms": [
							{
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-01-31T00:25:02.952724Z",
									"dbDateUpdated": "2023-05-16T12:29:04.134271Z",
									"id": 27213470,
									"nameType": {
											"internal": false,
											"obsolete": false,
											"id": 20308697,
											"name": "unspecified",
											"definition": "Unclassified name"
									},
									"formatText": "CELE_F15D3.1",
									"displayText": "CELE_F15D3.1"
							}
					]
			},
			{
					"type": "Gene",
					"createdBy": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2022-09-08T13:13:22.467843Z",
							"dbDateUpdated": "2022-09-08T13:13:22.467853Z",
							"id": 10975049,
							"uniqueId": "WB:curator"
					},
					"updatedBy": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2022-09-08T13:13:22.467843Z",
							"dbDateUpdated": "2022-09-08T13:13:22.467853Z",
							"id": 10975049,
							"uniqueId": "WB:curator"
					},
					"internal": false,
					"obsolete": false,
					"dbDateUpdated": "2023-05-16T12:30:11.68387Z",
					"curie": "WB:WBGene00001948",
					"taxon": {
							"internal": false,
							"obsolete": false,
							"curie": "NCBITaxon:6239",
							"name": "Caenorhabditis elegans"
					},
					"dataProvider": {
							"internal": false,
							"obsolete": false,
							"dbDateUpdated": "2023-05-16T12:30:11.683344Z",
							"id": 75345916,
							"sourceOrganization": {
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
											"dbDateUpdated": "2023-05-04T22:00:09.611705Z",
											"id": 41750788,
											"name": "homepage",
											"urlTemplate": "https://www.wormbase.org/"
									}
							},
							"crossReference": {
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-05-16T12:30:11.682793Z",
									"dbDateUpdated": "2023-05-16T12:30:11.682794Z",
									"id": 79688778,
									"referencedCurie": "WB:WBGene00001948",
									"displayName": "WBGene00001948",
									"resourceDescriptorPage": {
											"internal": false,
											"obsolete": false,
											"dbDateCreated": "2023-02-20T09:26:39.602562Z",
											"dbDateUpdated": "2023-05-04T22:00:09.592619Z",
											"id": 41750773,
											"name": "gene",
											"urlTemplate": "https://www.wormbase.org/db/get?name=[%s];class=Gene"
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
							"dbDateCreated": "2023-01-31T00:25:27.919073Z",
							"dbDateUpdated": "2023-05-16T12:30:11.711922Z",
							"id": 27217422,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308690,
									"name": "nomenclature_symbol",
									"definition": "A symbol for an object: e.g., pax6<sup>Leca2</sup>."
							},
							"formatText": "hlh-1",
							"displayText": "hlh-1",
							"synonymScope": {
									"internal": false,
									"obsolete": false,
									"id": 20308698,
									"name": "exact"
							}
					},
					"geneFullName": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-01-31T00:25:27.920177Z",
							"dbDateUpdated": "2023-05-16T12:30:11.710941Z",
							"id": 27217423,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308691,
									"name": "full_name",
									"definition": "The full length name of an entity: e.g., broad angular dumpy."
							},
							"formatText": "Helix Loop Helix 1",
							"displayText": "Helix Loop Helix 1"
					},
					"geneSystematicName": {
							"internal": false,
							"obsolete": false,
							"dbDateCreated": "2023-01-31T00:25:27.920666Z",
							"dbDateUpdated": "2023-05-16T12:30:11.715489Z",
							"id": 27217424,
							"nameType": {
									"internal": false,
									"obsolete": false,
									"id": 20308692,
									"name": "systematic_name",
									"definition": "A systematic name: e.g., CG4889<sup>1</sup>."
							},
							"formatText": "B0304.1",
							"displayText": "B0304.1",
							"synonymScope": {
									"internal": false,
									"obsolete": false,
									"id": 20308698,
									"name": "exact"
							}
					},
					"geneSynonyms": [
							{
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-01-31T00:25:27.921145Z",
									"dbDateUpdated": "2023-05-16T12:30:11.712846Z",
									"id": 27217425,
									"nameType": {
											"internal": false,
											"obsolete": false,
											"id": 20308697,
											"name": "unspecified",
											"definition": "Unclassified name"
									},
									"formatText": "MyoD1",
									"displayText": "MyoD1"
							},
							{
									"internal": false,
									"obsolete": false,
									"dbDateCreated": "2023-01-31T00:25:27.921591Z",
									"dbDateUpdated": "2023-05-16T12:30:11.713741Z",
									"id": 27217426,
									"nameType": {
											"internal": false,
											"obsolete": false,
											"id": 20308697,
											"name": "unspecified",
											"definition": "Unclassified name"
									},
									"formatText": "CELE_B0304.1",
									"displayText": "CELE_B0304.1"
							}
					]
			}
	]
}
