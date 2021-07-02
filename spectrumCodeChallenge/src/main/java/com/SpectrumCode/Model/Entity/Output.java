package com.SpectrumCode.Model.Entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class Output {
    List<Organization> organizations;
}
