# AGR curation release notes

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