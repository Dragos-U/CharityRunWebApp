package com.bearsoft.charityrun.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PARTICIPANT','ROLE_USER')")
public class CourseController {

}
