package com.ib.util.validation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BadValidation {

    @JsonInclude(value = Include.NON_NULL)
    private Long timestamp;
    private String message;
    @JsonInclude(value = Include.NON_NULL)
    private String error;
    @JsonInclude(value = Include.NON_NULL)
    private String path;
}