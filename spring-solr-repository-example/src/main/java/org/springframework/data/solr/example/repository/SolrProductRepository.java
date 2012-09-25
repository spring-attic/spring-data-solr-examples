/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.solr.example.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.FacetQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetPage;
import org.springframework.data.solr.example.model.Product;
import org.springframework.data.solr.repository.SimpleSolrRepository;

/**
 * @author Christoph Strobl
 */
@NoRepositoryBean
public class SolrProductRepository extends SimpleSolrRepository<Product> implements ProductRepository {

	@Override
	public Page<Product> findByPopularity(Integer popularity) {
		Query query = new SimpleQuery(new Criteria(SolrSearchableFields.POPULARITY).is(popularity));
		return getSolrOperations().executeListQuery(query, Product.class);
	}

	@Override
	public FacetPage<Product> findByNameStartingWithAndFacetOnAvailable(String namePrefix) {
		FacetQuery query = new SimpleFacetQuery(new Criteria(SolrSearchableFields.NAME).startsWith(namePrefix));
		query.setFacetOptions(new FacetOptions(SolrSearchableFields.AVAILABLE));
		return getSolrOperations().executeFacetQuery(query, Product.class);
	}

	@Override
	public Page<Product> findByAvailableTrue() {
		Query query = new SimpleQuery(new Criteria(new SimpleField(Criteria.WILDCARD)).expression(Criteria.WILDCARD));
		query.addFilterQuery(new SimpleQuery(new Criteria(SolrSearchableFields.AVAILABLE).is(true)));

		return getSolrOperations().executeListQuery(query, Product.class);
	}
}
