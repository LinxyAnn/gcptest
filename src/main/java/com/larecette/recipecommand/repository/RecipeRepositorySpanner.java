package com.larecette.recipecommand.repository;

import com.larecette.recipecommand.model.RecipeSpanner;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

import java.util.List;

/**
 * Repository for Recipe Entity Spanner Database
 */

public interface RecipeRepositorySpanner extends SpannerRepository<RecipeSpanner, String> {

    /**
     * Override method findAll change return param to List
     *
     * @return List<RecipeSpanner> instead Iterator
     */
    @Override
    List<RecipeSpanner> findAll();

    RecipeSpanner findByRecipeId(String recipeId);

}
