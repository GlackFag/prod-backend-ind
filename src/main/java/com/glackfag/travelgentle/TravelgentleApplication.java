package com.glackfag.travelgentle;

import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.sql.SQLException;
import java.time.ZoneId;

@SpringBootApplication
@PropertySources({
        @PropertySource("classpath:static/responses.properties"),
        @PropertySource("classpath:static/validationComments.properties"),
        @PropertySource("classpath:templates/responseTemplates.properties"),
        @PropertySource(value = "classpath:/templates/prompt.yml", encoding = "UTF-8")
})
public class TravelgentleApplication {
    private final DataSource dataSource;

    public TravelgentleApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static void main(String[] args) {
        SpringApplication.run(TravelgentleApplication.class, args);
    }

    @Bean
    public ZoneId zoneId() {
        return ZoneId.of("Europe/Moscow");
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     * USE FOR GIGACHAT ONLY
     * @return RestTemplate that doesn't verify SSL certificates
     */
    @Bean
    public RestTemplate certificatelessRestTemplate() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        TrustManager[] trustManagers = new TrustManager[]{new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }

            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }};

        sslContext.init(null, trustManagers, null);

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        return new RestTemplateBuilder().requestFactory(SimpleClientHttpRequestFactory.class)
                .build();
    }

    @Bean
    public RestTemplate defualtRestTemplate() {
        return new RestTemplate();
    }


    @Bean
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        return new TelegramBotsApi(DefaultBotSession.class);
    }

    @PostConstruct
    private void createTables() {
        try {
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new EncodedResource(new ClassPathResource("create_tables.sql")),
                    true, true, "--", ";", "/*", "*/");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
