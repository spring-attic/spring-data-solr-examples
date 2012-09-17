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

import org.springframework.data.solr.example.model.SearchableProduct;

import at.pagu.soldockr.core.query.Field;

/**
 * @author Christoph Strobl
 */
public enum SolrSearchableFields implements Field {

  ID(SearchableProduct.ID_FIELD), NAME(SearchableProduct.NAME_FIELD), PRICE(SearchableProduct.PRICE_FIELD), AVAILABLE(SearchableProduct.AVAILABLE_FIELD), CATEGORY(SearchableProduct.CATEGORY_FIELD), WEIGHT(
      SearchableProduct.WEIGHT_FIELD), POPULARITY(SearchableProduct.POPULARITY_FIELD);

  private final String fieldName;

  private SolrSearchableFields(String fieldName) {
    this.fieldName = fieldName;
  }

  @Override
  public String getName() {
    return fieldName;
  }

}
