package com.tugasakhir.elasticsearchkelompok19.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;


/*
 *   Anotasi @Configuration :
 *   - menandakan bahwa class ini merupakan konfigurasi
 * */
@Configuration
public class ElasticConfig extends AbstractElasticsearchConfiguration {

    Logger log = LoggerFactory.getLogger(ElasticConfig.class);

    /*  Anotasi @Value digunakan untuk mengambil value dari file application.properties
     *   dan telah dispesifikasikan sebelumnya seperti elasticsearch.url
     * */
    @Value("${elasticsearch.url}")
    public String elasticUrl;


    /*  @Bean digunakan untuk membuat object
     *   Dalam hal ini anotasi @Bean digunakan untuk membuat object client
     *   yang digunakan untuk koneksi terhadap elasticsearch*/
    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        try {
//            final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
//                    .connectedTo(elasticUrl)
//                    .withConnectTimeout(20_000)
//                    .withSocketTimeout(20_000)
//                    .withBasicAuth("elastic","AavHx0Hr0uew1ENtRb4QXsQd")
//                    .build();

            final CredentialsProvider cprov = new BasicCredentialsProvider();
            cprov.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials("elastic","cBvd4z7y7kbiyjGna7sf3Xi8"));
            RestClientBuilder.HttpClientConfigCallback callback = httpAsyncClientBuilder -> {
                httpAsyncClientBuilder.disableAuthCaching();
                return httpAsyncClientBuilder.setDefaultCredentialsProvider(cprov);
            };

            RestClientBuilder build = RestClient.builder(
                    new HttpHost(elasticUrl,9243,"https")
            );
            build.setHttpClientConfigCallback(callback);

            log.info("Connected to Elasticsearch!");

            RestHighLevelClient rhc = new RestHighLevelClient(build);

            boolean pingResult = rhc.ping(RequestOptions.DEFAULT);
            log.info("pingResult=" + pingResult);

            return rhc;
        } catch (Exception e) {
            log.error("Error connecting to Elasticsearch! : " + e.getMessage());
            return null;
        }
    }
}
