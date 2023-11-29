package com.bearsoft.charityrun.services.models.interfaces;

import com.bearsoft.charityrun.models.domain.enums.CourseType;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class BibNumberGeneratorServiceImpl implements BibNumberGeneratorService {

    private static final Random random = new Random();

    @Override
    public int generateBibNumber(CourseType courseType) {
        return switch (courseType) {
            case CROSS -> 1000 + random.nextInt(1000);
            case HALF_MARATHON -> 2000 + random.nextInt(1000);
            case MARATHON -> 4000 + random.nextInt(1000);
        };
    }
}

