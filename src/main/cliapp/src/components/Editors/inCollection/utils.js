import { buildAutocompleteFilter, autocompleteSearch } from "../../../utils/utils"; 
import { SearchService } from "../../../service/SearchService";

export const inCollectionSearch = (event, setFiltered, setQuery) => {
  const searchService = new SearchService();
  const autocompleteFields = ["name"];
  const endpoint = "vocabularyterm";
  const filterName = "taxonFilter";
  const otherFilters = {
    vocabularyFilter: {
      "vocabulary.name": {
        queryString: "Allele collection vocabulary"
      }
    }
  }
  setQuery(event.query);
  const filter = buildAutocompleteFilter(event, autocompleteFields);
  autocompleteSearch(searchService, endpoint, filterName, filter, setFiltered, otherFilters);
}