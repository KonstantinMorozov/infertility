package com.genesearch.repository;

import com.genesearch.model.Gene;
import com.genesearch.model.GeneHomologue;
import com.genesearch.model.Homologue;
import com.genesearch.object.edit.GeneEdit;
import com.genesearch.object.request.SearchGeneRequest;
import com.genesearch.object.response.GeneResponse;
import org.hibernate.Criteria;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by user on 03.01.2015.
 */
@Repository
public class GeneRepository extends ModelRepository<Gene> {

    @Autowired
    private HomologueRepository homologueRepository;
    @Autowired
    private GeneHomologueRepository geneHomologueRepository;

    public Gene find(String primaryIdentifier, String symbol, String organismName, String ncbi) {
        Criteria c = getSession().createCriteria(getEntityClass(), "gn");

        Conjunction and = new Conjunction();

        safeAddRestrictionEqOrNull(and, "gn.primaryIdentifier", primaryIdentifier);
        safeAddRestrictionEqOrNull(and, "gn.symbol", symbol);
        safeAddRestrictionEqOrNull(and, "gn.organismName", organismName);
        safeAddRestrictionEqOrNull(and, "gn.ncbi", ncbi);

        c.add(and);

        c.setProjection(Projections.countDistinct("gn.id"));
        long total = (Long) c.uniqueResult();

        c.setProjection(null);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        List<Gene> result = c.list();

        if(result.size() == 0) {
            return null;
        }
        return result.get(0);
    }

    public Gene find(String primaryIdentifier) {
        Criteria c = getSession().createCriteria(getEntityClass(), "gn");
        Conjunction and = new Conjunction();
        safeAddRestrictionEq(and, "gn.primaryIdentifier", primaryIdentifier);
        c.add(and);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        List<Gene> result = c.list();

        if(result.size() == 0) {
            return null;
        }

        return result.get(0);
    }


    public Page<Gene> search(SearchGeneRequest request) {
        Criteria c = getSession().createCriteria(getEntityClass(), "gn");

        Conjunction and = new Conjunction();

        safeAddRestrictionEq(and, "gn.id", request.getId());
        safeAddRestrictionEq(and, "gn.name", request.getName());
        safeAddRestrictionEq(and, "gn.dsc", request.getDsc());

        c.add(and);

        c.setProjection(Projections.countDistinct("gn.id"));
        long total = (Long) c.uniqueResult();

        c.setProjection(null);
        c.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

        if (request.getSort() != null) {
            for (Sort.Order order : request.getSort()) {
                String property = order.getProperty();
                if("id".equals(property)) {
                    property = "gn.id";
                } else if("name".equals(property)) {
                    property = "gn.name";
                } else if("dsc".equals(property)) {
                    property = "gn.dsc";
                }

                Order ord;
                if (order.isAscending()) {
                    ord = Order.asc(property);
                } else {
                    ord = Order.desc(property);
                }
                c.addOrder(ord);
            }
        }

        c.setFirstResult(request.getOffset());
        c.setMaxResults(request.getPageSize());


        return new PageImpl<Gene>(c.list(), request, total);
    }
}
