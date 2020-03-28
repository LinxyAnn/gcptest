package com.larecette.recipecommand.controller;

import com.larecette.recipecommand.model.RecipeSpanner;
import com.larecette.recipecommand.service.RecipeServiceSpanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * Endpoint for POST, PATCH and DELETE recipes form Spanner Database and Elasticsearch
 * Elasticsearch is additional solution until the kafka is implemented.
 * Provides data consistency
 */


@Slf4j
@RestController
@RequestMapping("recipe")
public class RecipeController {

    private final RecipeServiceSpanner recipeServiceSpanner;

    public RecipeController(RecipeServiceSpanner recipeServiceSpanner) {
        this.recipeServiceSpanner = recipeServiceSpanner;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<RecipeSpanner>> getAllRecipes() {
        log.info("Get all recipes from Spanner Database");
        List<RecipeSpanner> recipeSpannerList = recipeServiceSpanner.getAllRecipes();
        return new ResponseEntity<>(recipeSpannerList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RecipeSpanner> getRecipeById(@PathVariable("id") String recipeId) {
        log.info("Get recipe by id = {} from Spanner Database", recipeId);
        RecipeSpanner recipeSpanner = recipeServiceSpanner.getRecipeById(recipeId);
        if (recipeSpanner == null) {
            log.info("Recipe id =  {} not found", recipeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info(recipeSpanner.toString());
            return new ResponseEntity<>(recipeSpanner, HttpStatus.OK);
        }
    }

    /**
     * Add new recipe with ingredient.
     * If a recipe is created, it is also created in Elasticsearch.
     *
     * @param recipeSpanner RequestBody without id, difficulty and calories.
     * @return response status and created entity or null.
     */

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createRecipe(@Valid @RequestBody RecipeSpanner recipeSpanner, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            Map<String, List<String>> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
        }

        RecipeSpanner recipeSpannerNew = recipeServiceSpanner.createRecipe(recipeSpanner);
        if (recipeSpannerNew != null) {
            log.info("Create recipe id = {} in Spanner", recipeSpannerNew.getRecipeId());
            log.info(recipeSpannerNew.toString());
        }
        return new ResponseEntity<>(recipeSpannerNew, HttpStatus.CREATED);
    }

    /**
     * Update recipe by ingredient, if it exist in database.
     *
     * @param recipeId      must not be {@literal null}
     * @param recipeSpanner RequestBody with updated fields.
     * @return response status OK and updated entity or null and NOT_FOUND.
     */
    @PatchMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateRecipe(
            @PathVariable("id") String recipeId,
            @Valid @RequestBody RecipeSpanner recipeSpanner, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            Map<String, List<String>> errors = ControllerUtils.getErrors(bindingResult);
            return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
        }

        RecipeSpanner recipeSpannerResult = recipeServiceSpanner.updateRecipe(recipeSpanner, recipeId);
        if (recipeSpannerResult == null) {
            log.info("Recipe with id {} not found in database", recipeId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            log.info("Update recipe id = {} in Spanner:", recipeId);
            log.info(recipeSpannerResult.toString());
            return new ResponseEntity<>(recipeSpannerResult, HttpStatus.OK);
        }
    }

    /**
     * Delete recipe from database
     *
     * @param recipeId must not be {@literal null}
     * @return response status NO_CONTENT if recipe deleted, NOT_FOUND otherwise.
     */
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> deleteRecipe(@PathVariable("id") String recipeId) {
        Optional<RecipeSpanner> recipeSpanner = recipeServiceSpanner.findRecipeById(recipeId);
        if (recipeSpanner.isPresent()) {
            recipeServiceSpanner.deleteRecipe(recipeId);
            log.info("Delete recipe with id {} from Spanner", recipeId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.info("Recipe with id {} not found in database", recipeId);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        return new ResponseEntity<>("not acceptable due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
