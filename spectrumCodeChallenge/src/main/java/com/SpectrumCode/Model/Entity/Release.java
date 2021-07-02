package com.SpectrumCode.Model.Entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Release {

    DateObject date;
    String description;
    String name;
    Double laborHours;
    String organization;
    String repositoryURL;
    String status;
    Permissions permissions;
    String vcs;
}
