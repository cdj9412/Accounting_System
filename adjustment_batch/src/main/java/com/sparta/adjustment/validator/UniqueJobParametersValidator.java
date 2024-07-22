package com.sparta.adjustment.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class UniqueJobParametersValidator implements JobParametersValidator {
    // uniqueId 파라미터가 반드시 포함되도록 설정
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if (!parameters.getParameters().containsKey("uniqueId")) {
            throw new JobParametersInvalidException("uniqueId parameter is missing");
        }
    }
}
