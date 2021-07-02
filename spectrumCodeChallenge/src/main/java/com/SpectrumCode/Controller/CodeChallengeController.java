package com.SpectrumCode.Controller;

import com.SpectrumCode.Model.Entity.Output;
import com.SpectrumCode.Service.DataSortExporterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
public class CodeChallengeController {

    @Autowired
    DataSortExporterService dataSortExporterService;

    @GetMapping("/data")
    public Output getData(){
        return dataSortExporterService.getData();
    }

    @GetMapping("/data/{org}")
    public Output getDataByOrg(@PathVariable String org){
        return dataSortExporterService.getDataByOrg(org);
    }

    @GetMapping("/export")
    public String writeToFile() throws JsonProcessingException {
        return dataSortExporterService.exportData();
    }

    @GetMapping("ascSort")
    public Output sortAscending(@RequestParam String by){
        return dataSortExporterService.sortAscending(by);
    }

    @GetMapping("descSort")
    public Output sortDescending(@RequestParam String by){
        return dataSortExporterService.sortDescending(by);
    }

    @GetMapping("/healthCheck")
    public String healthCheck(){
        return "I am still breathing....";
    }


}
