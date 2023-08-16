import { buildAutocompleteFilter, autocompleteSearch } from "../../../utils/utils"; 
import { SearchService } from "../../../service/SearchService";


export const taxonSearch = (event, setFiltered, setQuery) => {
  const searchService = new SearchService();
  const autocompleteFields = ["curie", "name", "crossReferences.referencedCurie", "secondaryIdentifiers", "synonyms.name"];
  const endpoint = "ncbitaxonterm";
  const filterName = "taxonFilter";
  setQuery(event.query);
  const filter = buildAutocompleteFilter(event, autocompleteFields);
  autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered);
}