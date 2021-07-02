package com.SpectrumCode;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Log4j2
public class Configurations {

    private static final String PROP_FILE = "application.properties";

    private static Properties prop = null;

    private void loadProps() throws IOException{
        InputStream input = getClass().getClassLoader().getResourceAsStream(PROP_FILE);
        if(input == null){
           log.error("Unable to load properties");
           return;
        }
        prop = new Properties();
        prop.load(input);
    }

    public String getProperty(String propName) throws IOException{
        if(prop == null){
            loadProps();
        }
        return prop.getProperty(propName);
    }
}
