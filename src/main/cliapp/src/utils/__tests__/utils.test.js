import { describe, expect, it } from '@jest/globals';
import { deleteInvalidFilters, deleteInvalidSorts } from '../utils';
import 'core-js/features/structured-clone';


describe('deleteInvalidFilters', () => {
  it('All Valid fields', () => {
    const localStorageFilters = {
      "objectFilter": {
        "object.name": {
          "queryString": "j",
          "tokenOperator": "AND"
        },
        "object.curie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "negated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "modEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    };
    const newFilters = deleteInvalidFilters(localStorageFilters);
    expect(newFilters).toEqual(localStorageFilters);
  });

  it('One invalid field', () => {
    const localStorageFilters = {
      "objectFilter": {
        "object.invalidName": {
          "queryString": "j",
          "tokenOperator": "AND"
        },
        "object.curie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "negated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "modEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    };

    const newFilters = deleteInvalidFilters(localStorageFilters);
    expect(newFilters).toEqual({
      "negatedFilter": {
        "negated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "modEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    });
  });

  it('All invalid fields', () => {
    const localStorageFilters = {
      "objectFilter": {
        "object.invalidName": {
          "queryString": "j",
          " tokenOperator": "AND"
        },
        "object.invalidCurie": {
          "queryString": "j",
          "tokenOperator": "AND"
        }
      },
      "negatedFilter": {
        "invalidNegated": {
          "queryString": "true",
          "tokenOperator": "OR"
        }
      },
      "modentityidFilter": {
        "invalidModEntityId": {
          "queryString": "s",
          "tokenOperator": "AND"
        }
      }
    };

    const newFilters = deleteInvalidFilters(localStorageFilters);
    expect(newFilters).toEqual({});
  });
});

describe('deleteInvalidSorts', () => {
  it('All Valid fields', () => {
    const localMultiSortMeta = [
      {
        "field": "negated",
        "order": 1
      },
      {
        "field": "object.name",
        "order": 1
      },
      {
        "field": "subject.symbol",
        "order": 1
      }
    ];
    const newSorts = deleteInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual(localMultiSortMeta);
  });

  it('One invalid field', () => {
    const localMultiSortMeta = [
      {
        "field": "invalidNegated",
        "order": 1
      },
      {
        "field": "object.name",
        "order": 1
      },
      {
        "field": "subject.symbol",
        "order": 1
      }
    ];

    const newSorts = deleteInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual([
      {
        "field": "object.name",
        "order": 1
      },
      {
        "field": "subject.symbol",
        "order": 1
      }
    ]);
  });

  it('All invalid fields', () => {
    const localMultiSortMeta = [
      {
        "field": "invalidNegated",
        "order": 1
      },
      {
        "field": "object.invalidName",
        "order": 1
      },
      {
        "field": "subject.invalidSymbol",
        "order": 1
      }
    ];

    const newSorts = deleteInvalidSorts(localMultiSortMeta);
    expect(newSorts).toEqual([]);
  });
});


