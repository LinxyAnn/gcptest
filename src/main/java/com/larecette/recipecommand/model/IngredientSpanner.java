package com.larecette.recipecommand.model;

import lombok.*;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

/**
 * Ingredient Entity for Spanner Database
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "INGREDIENT")
public class IngredientSpanner {
    @PrimaryKey
    @Column(name = "RECIPE_ID")
    private String recipeId;

    @PrimaryKey(keyOrder = 2)
    @Column(name = "INGREDIENT_ID")
    private String ingredientId;

    @NotNull
    @Column(name = "PRODUCT_ID")
    private String productId;

    @NotNull
    @Column(name = "NAME")
    private String name;

    @NotNull
    @Column(name = "MEASURE_UNIT")
    private String measureUnit;

    @NotNull
    @Column(name = "CALORIES")
    private Double calories;

    @NotNull
    @DecimalMin(value = "0.1", message = "amount must be more than 0")
    @Column(name = "AMOUNT")
    private Double amount;

}
