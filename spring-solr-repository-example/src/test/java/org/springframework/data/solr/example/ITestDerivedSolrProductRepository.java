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
package org.springframework.data.solr.example;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;
import org.springframework.data.solr.example.model.Product;
import org.springframework.data.solr.example.repository.CustomSolrRepositoryImpl;
import org.springframework.data.solr.example.repository.DerivedSolrProductRepository;
import org.springframework.data.solr.repository.support.SolrRepositoryFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Christoph Strobl
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/springframework/data/solr/example/applicationContext.xml")
public class ITestDerivedSolrProductRepository extends AbstractSolrIntegrationTest {

	@Autowired
	SolrOperations solrOperations;

	private DerivedSolrProductRepository repo;

	@Before
	public void setUp() {
		// Create new repository instance using Factory and inject custom implementation
		repo = new SolrRepositoryFactory(this.solrOperations).getRepository(DerivedSolrProductRepository.class,
				new CustomSolrRepositoryImpl(this.solrOperations));
	}

	@After
	public void tearDown() {
		solrOperations.delete(new SimpleQuery(new SimpleStringCriteria("*:*")));
		solrOperations.commit();
	}

	@Test
	public void testCRUD() {
		Assert.assertEquals(0, repo.count());

		Product initial = createProduct(1);
		repo.save(initial);
		Assert.assertEquals(1, repo.count());

		Product loaded = repo.findOne(initial.getId());
		Assert.assertEquals(initial.getName(), loaded.getName());

		loaded.setName("changed named");
		repo.save(loaded);
		Assert.assertEquals(1, repo.count());

		loaded = repo.findOne(initial.getId());
		Assert.assertEquals("changed named", loaded.getName());

		repo.delete(loaded);
		Assert.assertEquals(0, repo.count());
	}

	@Test
	public void testDerivedQueryFindByPopularity() {
		Assert.assertEquals(0, repo.count());

		List<Product> baseList = createProductList(10);
		repo.save(baseList);

		Assert.assertEquals(baseList.size(), repo.count());

		Page<Product> popularProducts = repo.findByPopularity(20, new PageRequest(0, 10));
		Assert.assertEquals(1, popularProducts.getTotalElements());

		Assert.assertEquals("2", popularProducts.getContent().get(0).getId());
	}

	@Test
	public void testAnnotatedQueryfindByAvailableFalseUsingAnnotatedQuery() {
		Assert.assertEquals(0, repo.count());

		List<Product> baseList = createProductList(10);
		repo.save(baseList);

		Assert.assertEquals(baseList.size(), repo.count());

		Page<Product> unavailableProducts = repo.findByAvailableFalseUsingAnnotatedQuery(new PageRequest(0, 10));
		Assert.assertEquals(5, unavailableProducts.getTotalElements());
		for (Product product : unavailableProducts) {
			Assert.assertFalse(product.isAvailable());
		}
	}

	@Test
	public void testCustomRepositoryImplementation() {
		Product initial = createProduct(1);
		repo.save(initial);
		Assert.assertEquals(1, repo.count());

		Page<Product> page = repo.findProductsByCustomImplementation(initial.getName(), new PageRequest(0, 10));

		Assert.assertEquals(1, page.getTotalElements());
	}

	@Test
	public void testPartialUpdate() {
		Product initial = createProduct(1);
		initial.setCategories(Arrays.asList("cat-1"));
		repo.save(initial);

		Product loaded = repo.findOne(initial.getId());
		Assert.assertEquals(1, loaded.getCategories().size());

		List<String> categoryUpdate = Arrays.asList("cat-1", "cat-2", "cat-3");
		repo.updateProductCategory(initial.getId(), categoryUpdate);

		loaded = repo.findOne(initial.getId());
		Assert.assertEquals(3, loaded.getCategories().size());
		Assert.assertEquals(categoryUpdate, loaded.getCategories());
	}
}
