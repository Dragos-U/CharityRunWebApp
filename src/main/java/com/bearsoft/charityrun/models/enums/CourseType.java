package com.bearsoft.charityrun.models.enums;

import lombok.Getter;

@Getter
public enum CourseType {

    CROSS(10),
    HALF_MARATHON(21.0975),
    MARATHON(42.195);

    private double courseLength;

    CourseType(double courseLength) {
        this.courseLength = courseLength;
    }
}
