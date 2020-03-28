package com.larecette.recipecommand.service;

import com.larecette.recipecommand.model.IngredientSpanner;
import com.larecette.recipecommand.model.ProductSpanner;
import com.larecette.recipecommand.model.RecipeSpanner;
import com.larecette.recipecommand.repository.IngredientRepositorySpanner;
import com.larecette.recipecommand.repository.ProductRepositorySpanner;
import com.larecette.recipecommand.repository.RecipeRepositorySpanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


/**
 * Service for create, update and delete recipes from Spanner Database.
 */

@Slf4j
@Service
@Configurable
public class RecipeServiceSpanner {

    private final ProductRepositorySpanner productRepositorySpanner;
    private final RecipeRepositorySpanner recipeRepositorySpanner;
    private final IngredientRepositorySpanner ingredientRepositorySpanner;

    @Autowired
    public RecipeServiceSpanner(RecipeRepositorySpanner recipeRepositorySpanner, IngredientRepositorySpanner ingredientRepositorySpanner, ProductRepositorySpanner productRepositorySpanner) {
        this.recipeRepositorySpanner = recipeRepositorySpanner;
        this.ingredientRepositorySpanner = ingredientRepositorySpanner;
        this.productRepositorySpanner = productRepositorySpanner;
    }

    public List<RecipeSpanner> getAllRecipes() {
        return recipeRepositorySpanner.findAll();
    }

    public RecipeSpanner getRecipeById(String recipeId) {
        return recipeRepositorySpanner.findById(recipeId).orElse(null);
    }

    /**
     * Get Recipe by ud from Spanner database
     *
     * @param recipeId String contains UUID. Must not be {@literal null}.
     * @return Optional
     */
    public Optional<RecipeSpanner> findRecipeById(String recipeId) {
        return recipeRepositorySpanner.findById(recipeId);
    }


    /**
     * Create new recipe in Spanner Database.
     *
     * @param recipeSpanner inside recipe entity without id, difficulty and calories
     *                      Ingredient in IngredientList contains just productId and amount,
     *                      another fields request from Product Microservice.
     * @return entity from database with all field
     */
    public RecipeSpanner createRecipe(RecipeSpanner recipeSpanner) {
        //Set recipeId
        String recipeId = UUID.randomUUID().toString();
        recipeSpanner.setRecipeId(recipeId);
        //Create ingredientList
        List<IngredientSpanner> ingredientSpannerList = new ArrayList<>();
        recipeSpanner.getIngredientList().forEach(ingredientSpanner -> ingredientSpannerList.add(createIngredient(ingredientSpanner, recipeId)));
        recipeSpanner.setIngredientList(ingredientSpannerList);
        //Set additional field in Recipe
        recipeSpanner.setDifficulty(calculateDifficulty(recipeSpanner));
        recipeSpanner.setCalories(calculateCalories(recipeSpanner));
        //Save recipe in database
        recipeRepositorySpanner.save(recipeSpanner);
        return recipeSpanner;
    }

    /**
     * Update existing recipe by id.
     * Recreate ingredientList and recalculated calories and difficulty
     *
     * @param recipeSpanner inside recipe entity.
     * @param recipeId      String contains UUID. Must not be {@literal null}.
     * @return update Recipe if it exist in database or null otherwise.
     */
    public RecipeSpanner updateRecipe(RecipeSpanner recipeSpanner, String recipeId) {
        //Check recipeById in database
        RecipeSpanner recipeSpannerFromDb = recipeRepositorySpanner.findById(recipeId).orElse(null);
        if (recipeSpannerFromDb == null) {
            return null;
        }
        //Update usual field in Recipe
        recipeSpannerFromDb.setRecipeName(recipeSpanner.getRecipeName());
        recipeSpannerFromDb.setDescription(recipeSpanner.getDescription());
        recipeSpannerFromDb.setTime(recipeSpanner.getTime());
        recipeSpannerFromDb.setCuisine(recipeSpanner.getCuisine());
        //Update ingredientList
        ingredientRepositorySpanner.deleteAllByRecipeId(recipeId);
        List<IngredientSpanner> ingredientSpannerList = recipeSpanner.getIngredientList().stream()
                .map(ingredientSpanner -> createIngredient(ingredientSpanner, recipeId)).collect(Collectors.toList());
        recipeSpannerFromDb.setIngredientList(ingredientSpannerList);
        //Update calculated field in Recipe
        recipeSpannerFromDb.setDifficulty(calculateDifficulty(recipeSpannerFromDb));
        recipeSpannerFromDb.setCalories(calculateCalories(recipeSpannerFromDb));
        //save updated recipe
        recipeRepositorySpanner.save(recipeSpannerFromDb);
        return recipeSpannerFromDb;
    }

    /**
     * Delete recipe by Id from Spanner Database.
     *
     * @param recipeId String contains UUID. Must not be {@literal null}.
     * @return {@literal true} if recipe with this Id existed and was deleted, {@literal true} otherwise.
     */
    public void deleteRecipe(String recipeId) {
        recipeRepositorySpanner.deleteById(recipeId);
    }



    /*
    Util methods
     */

    /**
     * Fill empty field of Ingredient in Recipe Ingredient.
     *
     * @param ingredientSpanner ingredient with just productId and amount
     * @param recipeId          String contains UUID.
     * @return ingredient with all required field from Product.
     */
    public IngredientSpanner createIngredient(IngredientSpanner ingredientSpanner, String recipeId) {
        ProductSpanner product = getProduct(ingredientSpanner.getProductId());
        if (product != null) {
            ingredientSpanner = convertToIngredientFromProduct(product, recipeId, ingredientSpanner.getAmount());
        }
        return ingredientSpanner;
    }

    /**
     * There are direct GET request to Product Microservice.
     *
     * @param productId from Ingredient
     * @return Product entity from Product Microservice or null.
     */

    public ProductSpanner getProduct(String productId) {
        return productRepositorySpanner.findById(productId).get();
    }


    /**
     * Convert entity Product to Ingredient.
     *
     * @param product  entity from Product Microservice.
     * @param recipeId generated Id from the recipe.
     * @param amount   quantity of each product in the recipe.
     * @return Ingredient with all field from Product.
     */
    public IngredientSpanner convertToIngredientFromProduct(ProductSpanner product, String recipeId, Double amount) {
        return new IngredientSpanner(recipeId,
                UUID.randomUUID().toString(),
                product.getId(),
                product.getName(),
                product.getMeasureUnit(),
                product.getCalories(),
                amount);
    }

    /**
     * Calculate difficulty of the recipe.
     * Algorithm random: 2.1% time + 25% count of ingredient.
     *
     * @param recipeSpanner entity recipe contains time and list of ingredient.
     * @return Integer calories.
     */
    public Integer calculateDifficulty(RecipeSpanner recipeSpanner) {
        double countIngredients = recipeSpanner.getIngredientList().size();
        double time = recipeSpanner.getTime();
        return (int) Math.round((time * 2.1 + countIngredients * 25) / 100);
    }

    /**
     * Calculate calories based on calories of each ingredient.
     *
     * @param recipeSpanner entity recipe with fill Ingredient List.
     * @return Double sum calories for all ingredient.
     */

    public Double calculateCalories(RecipeSpanner recipeSpanner) {
        List<Double> caloriesList = new ArrayList<>();
        recipeSpanner.getIngredientList().forEach(ingredientSpanner -> caloriesList.add(ingredientSpanner.getCalories()));
        return caloriesList.stream().reduce(Double::sum).orElse(null);
    }
}
