package com.larecette.recipecommand.repository;

import com.larecette.recipecommand.model.IngredientSpanner;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

import java.util.List;

public interface IngredientRepositorySpanner extends SpannerRepository<IngredientSpanner, String> {

    boolean existsByIngredientId(String ingredientId);

    void deleteAllByRecipeId(String recipeId);

    List<IngredientSpanner> findAllByProductId(String productId);

    IngredientSpanner findFirstByProductId(String productId);

}
