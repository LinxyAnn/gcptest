package com.larecette.recipecommand.model;

import lombok.*;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Interleaved;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Recipe Entity for Spanner Database
 */

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "RECIPE")
public class RecipeSpanner {

    @PrimaryKey
    @Column(name = "RECIPE_ID")
    private String recipeId;

    @NotNull
    @NotBlank(message = "Please provide recipe name")
    @Column(name = "RECIPE_NAME")
    private String recipeName;

    @NotNull
    @NotBlank(message = "Please provide recipe description")
    @Column(name = "DESCRIPTION")
    private String description;

    @NotNull
    @Min(value = 1, message = "time must not be less than 0")
    @Column(name = "TIME")
    private Integer time;

    @Column(name = "DIFFICULTY")
    private Integer difficulty;

    @NotNull
    @NotBlank(message = "Please provide recipe cuisine")
    @Column(name = "CUISINE")
    private String cuisine;

    @Column(name = "CALORIES")
    private Double calories;

    @NotNull
    @Interleaved
    @EqualsAndHashCode.Exclude
    private List<IngredientSpanner> ingredientList;

}
