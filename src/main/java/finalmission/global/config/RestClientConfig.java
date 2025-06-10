package finalmission.global.config;

import finalmission.external.HolidayRestClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(HolidayProperties.class)
@RequiredArgsConstructor
public class RestClientConfig {

    private final HolidayProperties holidayProperties;

    @Bean
    public HolidayRestClient holidayRestClient() {
        return new HolidayRestClient(
                restClientBuilder().build(),
                holidayProperties.baseUrl(),
                holidayProperties.serviceKey()
        );
    }

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
