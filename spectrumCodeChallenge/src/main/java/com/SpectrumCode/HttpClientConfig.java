package com.SpectrumCode;

import lombok.extern.log4j.Log4j2;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@Configuration
@Log4j2
@Component
public class HttpClientConfig {

    private static final int CONNECT_TIMEOUT = 30000;
    private static final int REQUEST_TIMEOUT = 30000;
    private static final int SOCKET_TIMEOUT = 60000;

    private static final int MAX_TOTAL_CONNECTIONS = 50;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 30000;

    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager(){
        SSLContextBuilder builder = new SSLContextBuilder();
        try{
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        }
        catch(NoSuchAlgorithmException | KeyStoreException ex){
            log.error("Connection Manager Intialization failed; cause: " + ex.getMessage(), ex);
        }

        SSLConnectionSocketFactory sslsf = null;
        try{
            sslsf = new SSLConnectionSocketFactory(builder.build());
        }
        catch(KeyManagementException | NoSuchAlgorithmException ex){
            log.error("Connection Manager Initialization failed: cause :" + ex.getMessage(), ex);
        }
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
        .<ConnectionSocketFactory>create().register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        connectionManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        return connectionManager;
    }

    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(){
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator(response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while(it.hasNext()){
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();

                if(value != null && param.equalsIgnoreCase("timeout")){
                    return Long.parseLong(value)*1000;
                }
            }
            return DEFAULT_KEEP_ALIVE_TIME;
        };
    }

    @Bean
    public CloseableHttpClient httpClient(){
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();
        return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).setConnectionManager(poolingConnectionManager())
                .setKeepAliveStrategy(connectionKeepAliveStrategy()).build();
    }

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplate(clientRequestFactory());
        return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientRequestFactory(){
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setHttpClient(httpClient());
        return clientHttpRequestFactory;
    }

    @Bean
    public TaskScheduler taskScheduler(){
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("spectrumThreadScheduler");
        scheduler.setPoolSize(10);
        return scheduler;
    }
}
