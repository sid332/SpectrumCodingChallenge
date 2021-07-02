package com.SpectrumCode.Service;

import com.SpectrumCode.Configurations;
import com.SpectrumCode.Model.Entity.Content;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import com.SpectrumCode.Model.Entity.Organization;
import com.SpectrumCode.Model.Entity.Output;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class DataSortExporterService {


    @Autowired
    RestTemplate restTemplate;

    private Configurations config = new Configurations();


    private String getEndPoint(){
        String endPoint = "";
        try {
            endPoint = config.getProperty("targetEndPoint");
        } catch (IOException e) {
            log.error("could not load the properties");
        }
        return endPoint;
    }

    /*

     */
    public Output getData() {
        //gather the data from the endpoint
        Content content = restTemplate.getForObject(getEndPoint(), Content.class);

        List<Organization> organizations = new ArrayList<>();
        Map<String, Double> totalLaborHours = content.getTotalLaborHours();
        Map<String, Set<String>> licenses = content.getLicenses();
        Map<String, Boolean> prodCheck = content.isProd();
        Map<String, Set<Integer>> dates = content.getDates();
        Map<String, Long> count = content.getCount();

        count.entrySet().stream().forEach(i-> {
            Organization org = new Organization();
            org.setOrganization(i.getKey());
            org.setLicenses(licenses.get(i.getKey()));
            org.setTotal_labor_hours(totalLaborHours.get(i.getKey()));
            org.setMost_active_months(dates.get(i.getKey()));
            org.setAll_in_production(prodCheck.get(i.getKey()));
            org.setRelease_count(count.get(i.getKey()));

            organizations.add(org);
        });
        Output output = new Output();
        output.setOrganizations(organizations);
        return output;
    }

    public Output getDataByOrg(String org) {
        Output output = getData();
        List<Organization> organizations = output.getOrganizations().stream().filter(i-> i.getOrganization().equalsIgnoreCase(org))
                .collect(Collectors.toList());
        output.setOrganizations(organizations);
        return output;
    }

    public String exportData() throws JsonProcessingException {
        Output output = getData();
        HttpServletResponse response = null;
        response.addHeader("Content-Disposition", "attachment; filename=org-"+ LocalDateTime.now() + ".csv");

        try {
            ICsvBeanWriter fileWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.EXCEL_PREFERENCE );
            String[] headers = {"Organization", "release_count", "total_labor_hours", "all_in_production", "licenses", "most_active_months"};
            String[] values = {"Organization", "release_count", "total_labor_hours", "all_in_production", "licenses", "most_active_months"};

            fileWriter.writeHeader(headers);

            for (Organization i : output.getOrganizations()) {
                fileWriter.write(i, values);
            }
            fileWriter.close();
        }
        catch(IOException ex){
            log.error("Error occured while writing to file. cause : ", ex.getMessage());
        }
       ObjectMapper mapper = new ObjectMapper(new JsonFactory());
        return mapper.writeValueAsString(output);
    }

    public Output sortAscending(String by) {
        Output output = getData();
        List<Organization> organizations = new ArrayList<>();
        if(by.equalsIgnoreCase("organization")){
            organizations = output.getOrganizations().stream().sorted(Comparator.comparing(Organization::getOrganization)).
                    collect(Collectors.toList());
        }
        if(by.equalsIgnoreCase("laborHours")){
            organizations = output.getOrganizations().stream().sorted(Comparator.comparing(Organization::getTotal_labor_hours)).
                    collect(Collectors.toList());
        }
        if(by.equalsIgnoreCase("releaseCount")){
            organizations = output.getOrganizations().stream().sorted(Comparator.comparing(Organization::getRelease_count)).
                    collect(Collectors.toList());
        }
        output.setOrganizations(organizations);
        return output;
    }

    public Output sortDescending(String by) {
        Output output = getData();
        List<Organization> organizations = new ArrayList<>();
        if(by.equalsIgnoreCase("organization")){
            organizations = output.getOrganizations().stream().sorted(Comparator.comparing(Organization::getOrganization).reversed()).
                    collect(Collectors.toList());
        }
        if(by.equalsIgnoreCase("laborHours")){
            organizations = output.getOrganizations().stream().sorted(Comparator.comparing(Organization::getTotal_labor_hours).reversed()).
                    collect(Collectors.toList());
        }
        if(by.equalsIgnoreCase("releaseCount")){
            organizations = output.getOrganizations().stream().sorted(Comparator.comparing(Organization::getRelease_count).reversed()).
                    collect(Collectors.toList());
        }
        output.setOrganizations(organizations);
        return output;
    }


}
