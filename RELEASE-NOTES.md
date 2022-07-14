## v0.6.0
# AGR curation release notes
* New Features
		* SCRUM-1537 make condition relation handle table editable by @cmpich in #491
		* SCRUM-1537 validate null handle, disallow same handle/pub combination by @cmpich in #511
		* SCRUM-1537 add auto-complete on reference column on condition-relation-handle-table by @cmpich in #518
		* Filters icon added to Genric Data Table Component by @kthorat-prog in #505
		* Filters Label added on Roweditor frozen column by @kthorat-prog in #513
		* Obsolete Column added to disease annotations table by @kthorat-prog in #514
* Data Load
		* SCRUM-1609, SCRUM-1611, SCRUM-1612 Loading and testing of obsolete field by @markquintontulloch in #503
* Maintenance
		* Misc refactors by @adamgibs in #504
		* Column header names changed for 2 columns by @kthorat-prog in #508
		* SCRUM-1603 added VocabularyTermUpdate view to return vocabulary field by @adamgibs in #510
		* SCRUM-1598 Added DiseaseAnnotationUpdate view to conditions by @adamgibs in #512
		* null check added to col variable in useEffect by @kthorat-prog in #517
		* fixed autocomplete hover bug by @adamgibs in #520
		* SCRUM-516 SCRUM 1537 create getEntityType() method to select and display the auto-complete list, auto-complete for DA table by @cmpich in #519
		* SCRUM-1675 added check for conditions with no handle or reference by @adamgibs in #521
## v0.4.0
* Data loading
   * SCRUM-1205 Anatomy stage ontologies by @markquintontulloch in #3117
   * SCRUM-1319 Add AuditedObject fields to disease annotations by @markquintontulloch in #314 
   * SCRUM-1205 MmusDv load update by @markquintontulloch in #324
   * SCRUM-737 update - SGD strain background filter by @markquintontulloch in #327
   * SCRUM-1298 Validate uniqueId doesn't exist after editing experimentalCondition by @markquintontulloch in #344
   * SCRUM-731 disease qualifiers by @markquintontulloch in #338
   * SCRUM-1326 Add condition summary to experimental conditions by @markquintontulloch in #352
   * SCRUM-1155 Limit ZECO terms for conditionClass to AGR SLIM by @markquintontulloch in #355
   * SCRUM-1369 Hide Parental Populations column in AGM table by default by @markquintontulloch in #353
   * Make ExperimentalCondition uniqueId generation consistent by @markquintontulloch in #360
   * SCRUM-1427 Submission of notes with disease annotations by @markquintontulloch in #392
   * SCRUM-1435 Backend code for conditionRelation editing via diseaseAnnotation edits by @markquintontulloch in #401
   * fix AGM creation logic by @cmpich in #370
   * SCRUM-1232 - Add Xenopus ontologies by @markquintontulloch in #424
   * SCRUM-1232 Enable multiple ontology loads from single OWL file by @markquintontulloch in #425
   * SCRUM-1232 Add additional namespace for XBA load by @markquintontulloch in #428 
* UI Features
   * UI reskin by @adamgibs in #305, #321, #334
   * Editor Refactor by @adamgibs in #309
   * Header freeze added by @kthorat-prog in #340
   * Added log server to list of other links by @oblodgett in #317
   * Changed button to use primreact styles by @oblodgett in #339
   * Changed links to open in a new tab by @oblodgett in #341
   * Template made for all datatables across website by @kthorat-prog in #357
   * SCRUM-1318 added filter field for condition relations handle by @adamgibs in #380
   * Added ellipsis & Tooltips for title,abstract & citation columns in Literature Ref Table by @kthorat-prog in #384
   * SCRUM-1403 Autopopulate 'Modified By' field on editing by @markquintontulloch in #381
   * Date modified and Modified fields are updated immediately on success by @kthorat-prog in #399
   * Generic data assignment done for immediate row update & Frozen Column removed by @kthorat-prog in #400
   * Edit Column is frozen and positioned to extreme left side of table by @kthorat-prog in #419
   * Dashboard list of table names sorted alphabetically for both tables by @kthorat-prog in #369
   * Literature Reference added to Dashboard & its Layout is changed to ma… by @kthorat-prog in #374
   * SCRUM-1426 display handle instead of conditions if available by @cmpich in #391
* UI Maintenance
   * Remove columns with work still to do on UI by @markquintontulloch in #332
   * The ID column made non-sortable by @kthorat-prog in #331
   * SCRUM-737 Disable sgdStrainBackground editing for non-gene annotations by @markquintontulloch in #337
   * reduced DATable width by @adamgibs in #342
   * SCRUM-1368 turned filtering for AGM table name column back on by @adamgibs in #358
   * SCRUM-734 Fix spelling mistake by @markquintontulloch in #366
   * Misc refactor items by @adamgibs in #368
   * Header backgroundcolor added to make it opaque by @kthorat-prog in #376
   * SCRUM-1393 added default column order state to DA table by @adamgibs in #372
   * Changed dialog width, refactored list and changed dialog title by @adamgibs in #375
   * To make the error message float above the cell width for visibility by @kthorat-prog in #383
   * Taxon_ID column display changed and sort order is based on name now by @kthorat-prog in #394
   * Taxon display changed by @kthorat-prog in #398
   * SCRUM-1423 Changes done by Olin only by @kthorat-prog in #405
* API Feature
   * SCRUM-1435 Add validation endpoint by @markquintontulloch in #402
   * SCRUM-1391 non-loaded allele removal by @mluypaert in #411
   * Added code for integration with Literature Reference ES Server by @oblodgett in #359
   * SCRUM-1371 + SCRUM-1396: non-loaded gene and agm removal by @mluypaert in #436   
* API Maintenance
   * SCRUM-1364 Add field name to response by @markquintontulloch in #350
   * SCRUM-1412 Fix inconsistency in inchiKey field naming by @markquintontulloch in #377
   * SCRUM-734 property bridge fix by @markquintontulloch in #363
   * Fixed params for tracking number of results by @oblodgett in #365
   * Use .keyword field for searching in the ABC SCRUM-1400 by @oblodgett in #367
   * Removed logging verbosity for loads by @oblodgett in #395
   * Updated ordering by @oblodgett in #397
   * Added comment for upcoming sprint (MERGE AFTER REVIEW) by @oblodgett in #416
   * Added stopped loads to be started (MERGE AFTER REVIEW) by @oblodgett in #417
   * paper handle fixes: index definition by @cmpich in #373
   * SCRUM-1318 handle condition updates, include handle info in unique ID by @cmpich in #385
   * SCRUM-1415 Set UniqueID for new ExperimentalCondition entities by @markquintontulloch in #386
   * Changed exception so they will print in the logs by @oblodgett in #430
   * SCRUM-1499 Improve error reporting for ConditionRelations and RelatedNotes attached to DAs by @markquintontulloch in #433 
* Other
   * Add MIT LICENSE file by @chris-grove in #421 
   * Removed duplicate code PR into PR by @oblodgett in #426
   * SCRUM-1432 VocabularyTerm validation and integration tests by @markquintontulloch in #396 
   * SCRUM-1279 by @markquintontulloch in #307
   * Scrum 1020 by @kthorat-prog in #319
   * SCRUM-1320 Switching of disease annotation enums to VocabularyTerms by @markquintontulloch in #318
   * Scrum 1296 by @adamgibs in #326
   * Switch vocabularyTerm lookup from search to find by @markquintontulloch in #328
   * SCRUM-1317 by @markquintontulloch in #346
   * Scrum 1341 by @adamgibs in #361
   * SCRUM-1318 by @adamgibs in #362
   * SCRUM-1393 by @adamgibs in #378
   * SCRUM-1394 by @adamgibs in #371
   * Scrum 1377a by @cmpich in #364
   * SCRUM-1315 by @adamgibs in #410
   * SCRUM-1401 by @adamgibs in #412
   * SCRUM-1315 by @adamgibs in #413
   * SCRUM-1401 by @adamgibs in #390
* DevOps
   * SCRUM-1366 merge from beta to alpha by @markquintontulloch in #351
   * Merge v0.3.0-rc3 changes back into alpha by @mluypaert in #356
   * Merge-back production to beta after v0.3.0 release creation by @mluypaert in #404
   * SCRUM-1417: Release/v0.4.0-rc1 by @mluypaert in #407   
   * Alpha deployment trigger update proposal by @mluypaert in #418
   * Release/v0.4.0 rc2 by @oblodgett in #423
   * PR merge/beta by @oblodgett in #432
   * Release/v0.4.0 rc4 by @kthorat-prog in #442
   * Release/v0.4.0 by @kthorat-prog in #446
   * SCRUM-1424 Renamed migration files to clear patch release version number by @mluypaert in #389
* Bugs
   * Fixed API Token Login by @oblodgett in #313
   * Fixed status' on startup by @oblodgett in #315
   * Fixed obsolete on vocabulary pages by @oblodgett in #316
   * Fixed disease relation validation by @oblodgett in #320
   * Vocab UI fix by @markquintontulloch in #323
   * The changes to reflect in table as new term/vocab added is added. by @kthorat-prog in #330
   * Fix 'Cannot convert undefined or null to object' error by @markquintontulloch in #333
   * SCRUM-1205 fix for missing term by @markquintontulloch in #335
   * ControlledVocabularyDropdown fixes by @markquintontulloch in #336
   * SCRUM-1306 fixed synonyms bug for subject and with fields by @adamgibs in #343
   * SCRUM-1360 fixed dropdown refocus bug by @adamgibs in #345
   * SCRUM-1364 Experimental condition bug by @markquintontulloch in #347
   * ErrorMessage location is changed to under the editor for Editable tables by @kthorat-prog in #382
   * Table Header bleed through content fixed for other tables by @kthorat-prog in #379
   * Autocomplete search result for synonym fixed for Genetic Modifier field by @kthorat-prog in #408
   * SCRUM-1315 added code to address column reording issue by @adamgibs in #414
   * Bleed through in frozen edit column fixed by @kthorat-prog in #420
   * Fixed Frozen header and frozen column issue by @kthorat-prog in #422
   * Error message box alignment fixed by @kthorat-prog in #434
   * ZIndex made highest for errormessages component by @kthorat-prog in #435
   * Minor fixes by @markquintontulloch in #427
   * Quick fix for annotation returned object by @oblodgett in #415
   * SCRUM-1416 added null check to ListTableCell by @adamgibs in #388
  
## v0.3.0
 * Data loading
   * Implemented NCBITaxonTerm class (SCRUM-338)
   * Enabled linkML format disease annotation submission (SCRUM-1203, SCRUM-122)
   * Loaded and added UI table for read and edit of Experminetal conditions data (SCRUM-1083, SCRUM-1082, SCRUM-725, SCRUM-1153, SCRUM-1154, SCRUM-1194)
   * Added better load reporting (counts and errors) (#289)
   * Fixed loading consistency and performance (SCRUM-1280, SCRUM-1281)
   * Added type suffix on data load download files (SCRUM-1067)
 * UI improvements
   * Added additional developer links in UI (#270, #271)
   * Enabled data customization session persistence (SCRUM-1036)
   * Enabled checkbox selection filter option for limited-option fields (SCRUM-1022)
 * Other
   * Added API authentication (SCRUM-1045)
   * Implemented flyway as DB schema managment solution (SCRUM-1054)
   * Serveral other fixes and improvements (#243, #245, SCRUM-853, #257, #263, #266, #274, #276, #293)

## v0.2.0
 * Added UI login (authentication) (SCRUM-1034)
 * Enabled filter for Name in Genes Table (SCRUM-1058)
 * Added NCBI Taxonomy ontologies, including Id validation and autopopulation (SCRUM-338, SCRUM-1065)
 * Improved data load error handling display (SCRUM-972)
 * Enabled bulk submission of LinkML JSON files for disease annotations (SCRUM-984)
 * Fixed allele description sort & filter bug (SCRUM-1048)
 * Several UI fixes and improvements (SCRUM-852)

## v0.1.0
 * Added and enabled update/edit on additional disease annotation properties
    * disease field (SCRUM-492, #110, #111, #112, #113, #114)
    * disease relation field (SCRUM-486, SCRUM-962, #119, #120, #132, #134, #135, #136, #137, #152, #153)
    * with field (SCRUM-706, #139, #142, #144, #145, #148)
    * evidence code (SCRUM-881, SCRUM-879)
    * negated field (SCRUM-498, SCRUM-498)
 * Changed curie to unique id for disease annotations #224
 * Loaded various ontologies:
    * MA (SCRUM-857, #141)
    * DAO (SCRUM-887)
    * EMAPA (SCRUM-892)
    * WBbt (SCRUM-873)
    * MP (SCRUM-948, #150)
    * ZFA (SCRUM-868)
    * XCO (SCRUM-102)
    * ZECO (SCRUM-102)
    * GO (SCRUM-897, #160)
    * CHEBI (SCRUM-902)
    * DO (SCRUM-277)
    * SO (SCRUM-1004, SCRUM-1006, SCRUM-1003)
    * Added obsolete colunm (SCRUM-1021)
 * Added Molecule class - loading and read-only diplay (SCRUM-335, #118, #122, #123, #124, #128, #143, #207, #213)
 * Added controlled vocabulary (SCRUM-570, #140)
 * Improved integration testing and automated run at PR validation (SCRUM-280, SCRUM-257, SCRUM-274)
 * UI improvements
    * Various search improvements (SCRUM-600, SCRUM-817, SCRUM-818, #131)
    * prevent updates adding obsolete DO terms (SCRUM-594)
    * Enabled sort and autosuggest on name/symbol fields for object/with columns (SCRUM-970, SCRUM-955)
    * Other autosuggest improvements (SCRUM-715)
    * Enabled user modification of UI data table display (SCRUM-1028)
    * various other fixes and improvements (SCRUM-851, SCRUM-971, SCRUM-499, SCRUM-969, SCRUM-710, #159, ...)
 * LinkML model updates (SCRUM-510)
 * Implemented automatic update loading of input files (#201)
 * Enabled central application logging (SCRUM-547)

## v0.0.31
 * Setup additional application environments
    * Alpha environment (SCRUM-451, SCRUM-459, #83)
    * Beta environment (SCRUM-461, #102)
 * Cosmetic UI changes (SCRUM-568, #104, #100, #98, #97, #94)
 * Enabled in-table editing in disease annotation table
    * disease field (SCRUM-492, #90, #93, #101, #89)
    * subject field (SCRUM-473, #87, #86, #85, #84, #82, #75)
 * Added API endpoints to load disease annotations with evidence codes (SCRUM-322, #99)
 * Reduced reindexing failure rate (#95)
 * Integrated application compilation into Docker image building (#91)
 * Added integration testing for basic gene loading (SCRUM-248, #81, #80, #77, #74, #73, #72)
 * Added several testfile for later inclusion in integration testing (#71)
 * Temporarily disabled auto scaling (commit b54ebb8 in #76)
 * Other minor changes (#96, #92, #78, #69)
