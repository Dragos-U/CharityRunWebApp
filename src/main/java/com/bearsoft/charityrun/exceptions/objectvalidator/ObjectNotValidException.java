package com.bearsoft.charityrun.exceptions.objectvalidator;


import lombok.Data;

import java.util.Set;

@Data
public class ObjectNotValidException extends RuntimeException{

    private final Set<String> errorMessages;
}
