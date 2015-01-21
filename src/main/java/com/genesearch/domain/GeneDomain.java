package com.genesearch.domain;

import com.genesearch.model.Gene;
import com.genesearch.model.OntologyAnnotation;
import com.genesearch.object.edit.*;
import com.genesearch.object.response.SearchGeneResponse;
import com.genesearch.repository.GeneRepository;
import com.genesearch.repository.OntologyAnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 16.01.2015.
 */
@Service
public class GeneDomain {

    @Autowired
    private HomologyDomain homologyDomain;
    @Autowired
    private SequenceFeatureDomain sequenceFeatureDomain;
    @Autowired
    private GeneRepository geneRepository;
    @Autowired
    private OntologyAnnotationRepository ontologyAnnotationRepository;

    public GeneEdit showFull(Long id) {
        Gene gene = geneRepository.find(id);

        Long geneId = gene.getId();

        List<SearchGeneResponse> searchGeneResponseList = new ArrayList<SearchGeneResponse>();

        List<OntologyAnnotation> ontologyAnnotationList = ontologyAnnotationRepository.find(geneId);

        Set<GenotypeEdit> genotypeEditList = new HashSet<GenotypeEdit>();
        Set<LiteratureEdit> literatureEditList = new HashSet<LiteratureEdit>();

        for(OntologyAnnotation g : ontologyAnnotationList) {
            genotypeEditList.add(new GenotypeEdit(g.getBaseAnnotationsSubjectBackgroundName(), g.getBaseAnnotationsSubjectZygosity()));
            literatureEditList.add(new LiteratureEdit(g.getPubmedId(), g.getDoi()));
            searchGeneResponseList.add(SearchGeneResponse.create(g));
        }

        List<HomologyEdit> homologyEditList = homologyDomain.find(geneId);
        List<SequenceFeatureEdit> sequenceFeatureList = sequenceFeatureDomain.find(geneId);

        GeneEdit geneEdit = GeneEdit.create(gene);
        geneEdit.setHomologyEditList(homologyEditList);
        geneEdit.setSequenceFeatureEditList(sequenceFeatureList);
        geneEdit.setGenotypeEditList(genotypeEditList);
        geneEdit.setLiteratureEditList(literatureEditList);

        geneEdit.setGeneAnnotationList(searchGeneResponseList);

        return geneEdit;
    }

    public GeneEdit update(GeneEdit geneEdit) {

        Gene gene = geneRepository.findById(geneEdit.getId());

        gene.update(geneEdit);


/**
 * The code below needed in case of updating homologues of gene
 *
 * */

//        Set<Homologue> newHomologues = homologueDomain.update(geneEdit.getHomologueEditList());
//
//        Iterator<GeneHomologue> it = gene.getGeneHomologueSet().iterator();
//
//        while(it.hasNext()) {
//            GeneHomologue existing = it.next();
//            if(!newHomologues.contains(existing.getHomologue())) {
//                it.remove();
//            }
//        }
//
//        for(Homologue hm : newHomologues) {
//            GeneHomologue existing = geneHomologueRepository.findOne(gene.getId(), hm.getId());
//            gene.getGeneHomologueSet().add(existing != null ? existing : new GeneHomologue(gene, hm));
//        }

        geneRepository.save(gene);

        return geneEdit;
    }

//    public Page<SearchGeneResponse> search(SearchGeneRequest request) {
//
//        Page<Gene> searchResult = geneRepository.search(request);
//
//        List<SearchGeneResponse> responses = new ArrayList<SearchGeneResponse>();
//        for(Gene gene : searchResult.getContent()) {
//            responses.add(SearchGeneResponse.create(gene));
//        }
//
//        return new PageImpl<SearchGeneResponse>(responses, request, searchResult.getTotalElements());
//    }

}
