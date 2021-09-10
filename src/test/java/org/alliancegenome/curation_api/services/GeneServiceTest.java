package org.alliancegenome.curation_api.services;

import io.quarkus.test.junit.mockito.InjectMock;
import org.alliancegenome.curation_api.dao.GeneDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeneServiceTest {

    @InjectMock
    GeneDAO geneDAO;

    GeneService geneService;

    @BeforeEach
    void setUp() {
        geneService = new GeneService();
    }

    @Test
    void init() {

    }

    @Test
    void getByIdOrCurie() {
    }
}