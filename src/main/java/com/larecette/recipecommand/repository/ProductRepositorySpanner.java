package com.larecette.recipecommand.repository;

import com.larecette.recipecommand.model.IngredientSpanner;
import com.larecette.recipecommand.model.ProductSpanner;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

public interface ProductRepositorySpanner extends SpannerRepository<ProductSpanner, String> {
}
