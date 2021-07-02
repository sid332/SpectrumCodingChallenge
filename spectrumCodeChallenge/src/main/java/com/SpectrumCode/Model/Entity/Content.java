package com.SpectrumCode.Model.Entity;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@Data
@ToString
public class Content {
    String agency;
    String version;
    List<Release> releases;

    public Map<String, Double> getTotalLaborHours(){
        Map<String, Double> totalLabourHours = this.getReleases().stream().collect(Collectors.groupingBy(Release::getOrganization,
                Collectors.summingDouble(Release::getLaborHours)));
        return totalLabourHours;

    }

    public Map<String, Set<String>> getLicenses(){
        Map<String, Set<String>> licenses = this.getReleases().stream().collect(Collectors.groupingBy(Release::getOrganization,
                mapping(i->i.getPermissions().getLicenses().stream().map(License::getName).collect(Collectors.joining(", ")),
                        toSet())));
        return licenses;
    }

    public Map<String, Boolean> isProd(){
        Map<String, Boolean> prodEntities = this.getReleases().stream().collect(Collectors.toMap(Release::getOrganization,
                i-> i.getStatus().equalsIgnoreCase("Production"),(older, newer)->older));
        return prodEntities;
    }

    public Map<String, Set<Integer>> getDates(){
        Map<String, Set<Integer>> dates = this.getReleases().stream().collect(Collectors.groupingBy(Release::getOrganization,
                mapping(i-> LocalDateTime.parse(i.getDate().getCreated()).getMonth().getValue(), toSet())));
        return dates;
    }

    public Map<String, Long> getCount(){
        Map<String, Long> count = this.getReleases().stream().collect(Collectors.groupingBy(Release::getOrganization, Collectors.counting()));
        return count;
    }
}
