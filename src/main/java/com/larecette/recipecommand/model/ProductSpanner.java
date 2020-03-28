package com.larecette.recipecommand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductSpanner {
    private Double proteins;

    private Double carbohydrates;

    private Double fats;

    private Double calories;

    private String name;

    private String measureUnit;

    private String id;

}