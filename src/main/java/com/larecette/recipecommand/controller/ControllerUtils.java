package com.larecette.recipecommand.controller;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ControllerUtils {

    static Map<String, List<String>> getErrors(BindingResult bindingResult) {
        Map<String, List<String>> fieldErrorWithMessages = bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(
                                fieldError -> fieldError.getDefaultMessage(),
                                Collectors.toList())));
        return fieldErrorWithMessages;
    }

}
