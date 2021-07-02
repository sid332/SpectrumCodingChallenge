package com.SpectrumCode.Model.Entity;


import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data
@ToString
public class Organization {

    String organization;
    Long release_count;
    Double total_labor_hours;
    Boolean all_in_production;
    Set<String> licenses;
    Set<Integer> most_active_months;

}
