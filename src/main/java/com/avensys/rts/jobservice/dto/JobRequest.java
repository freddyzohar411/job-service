package com.avensys.rts.jobservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobRequest {

    private AccountInformation accountInformation;
    private JobOpeningInformation jobOpeningInformation;
    private JobCommercials jobCommercials;
    private String jobRemarks;
}
