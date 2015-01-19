package com.genesearch.domain;

import com.genesearch.model.GeneHomologue;
import com.genesearch.model.Homology;
import com.genesearch.object.edit.HomologyEdit;
import com.genesearch.repository.GeneHomologueRepository;
import com.genesearch.repository.HomologyRepository;
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
public class HomologyDomain {

    @Autowired
    private HomologyRepository homologyRepository;
    @Autowired
    private GeneHomologueRepository geneHomologueRepository;

    public List<HomologyEdit> find(Long geneId) {

        List<HomologyEdit> result = new ArrayList<HomologyEdit>();
        List<Long> homologueIdList = new ArrayList<Long>();

        List<GeneHomologue> geneHomologueList = geneHomologueRepository.find(geneId);
        for(GeneHomologue geneHomologue : geneHomologueList) {
            homologueIdList.add(geneHomologue.getHomology().getId());
        }

        for(Homology homology : homologyRepository.find(homologueIdList)) {
            result.add(HomologyEdit.create(homology));
        }
        return result;
    }

    public Set<Homology> update(List<HomologyEdit> homologyEditList) {
        Set<Homology> result = new HashSet<Homology>();

        for (HomologyEdit ed : homologyEditList) {

            Homology forAddEntity = null;

//            Homologue existing = homologueRepository.find(ed.getPrimaryIdentifier(), ed.getSymbol(), ed.getOrganismName(), ed.getType(), ed.getDatasetsName());
            Homology existing = homologyRepository.findById(ed.getId());

            if (existing == null) {
                Homology newEntity = new Homology();
                newEntity.setPrimaryIdentifier(ed.getPrimaryIdentifier());
                newEntity.setSymbol(ed.getSymbol());
                newEntity.setOrganismName(ed.getOrganismName());
                newEntity.setDatasetsName(ed.getDatasetsName());
                newEntity.setType(ed.getType());

                homologyRepository.save(newEntity);

                forAddEntity = newEntity;
            }
            else {
                existing.update(ed);

                forAddEntity = existing;
            }

            result.add(forAddEntity);
        }

        return result;
    }
}