package com.bearsoft.charityrun.models.email;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MailStructure {

    private String subject;
    private String message;
}
