package org.cynic.music_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.DurationConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig;
import com.hazelcast.config.CacheSimpleConfig.ExpiryPolicyFactoryConfig.TimedExpiryPolicyFactoryConfig.ExpiryPolicyType;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.github.resilience4j.springboot3.circuitbreaker.autoconfigure.CircuitBreakerAutoConfiguration;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.cynic.music_api.framework.authentication.CustomAuthenticationEntryPoint;
import org.cynic.music_api.framework.converter.CustomJwtAuthenticationConverter;
import org.cynic.music_api.framework.converter.http.CustomMappingJackson2HttpMessageConverter;
import org.cynic.music_api.framework.decoder.CustomJwtDecoder;
import org.cynic.music_api.framework.handler.CustomResponseErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.client.RestTemplate;

@SpringBootConfiguration(proxyBeanMethods = false)
@ImportAutoConfiguration({
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
    TransactionAutoConfiguration.class,
    JpaRepositoriesAutoConfiguration.class,
    LiquibaseAutoConfiguration.class,

    CacheAutoConfiguration.class,

    AopAutoConfiguration.class,
    CircuitBreakerAutoConfiguration.class,

    OAuth2ResourceServerAutoConfiguration.class,

    JacksonAutoConfiguration.class,

    RestTemplateAutoConfiguration.class,
    HttpMessageConvertersAutoConfiguration.class,

    ServletWebServerFactoryAutoConfiguration.class,
    DispatcherServletAutoConfiguration.class,
    WebMvcAutoConfiguration.class
})
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
    @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})
})
@EnableJpaRepositories
@EnableCaching
@EntityScan("org.cynic.music_api.domain.entity")
@EnableConfigurationProperties({
    Configuration.ApiConfiguration.class
})
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

    public Configuration() {
        Optional.of(LOGGER)
            .filter(it -> it.isInfoEnabled(Constants.AUDIT_MARKER))
            .map(_ -> getClass())
            .map(Class::getPackage)
            .ifPresent(it -> LOGGER.info(Constants.AUDIT_MARKER,
                    "[{}-{}] STARTED",
                    it.getImplementationTitle(),
                    it.getImplementationVersion()
                )
            );
    }

    @Bean
    public HazelcastInstance hazelcastInstance(@Value("${hazelcast.config}") Resource configuration) throws IOException {
        ExpiryPolicyFactoryConfig oneHourExpiry = new ExpiryPolicyFactoryConfig(
            new TimedExpiryPolicyFactoryConfig(
                ExpiryPolicyType.CREATED,
                new DurationConfig(1L, TimeUnit.HOURS)
            )
        );
        return Hazelcast.newHazelcastInstance(
            new XmlConfigBuilder(configuration.getInputStream())
                .build()
                .addCacheConfig(
                    new CacheSimpleConfig(Constants.ARTISTS_SPACE_NAME)
                        .setExpiryPolicyFactoryConfig(oneHourExpiry)
                )
                .addCacheConfig(
                    new CacheSimpleConfig(Constants.ARTIST_TOP_ALBUMS_SPACE_NAME)
                        .setExpiryPolicyFactoryConfig(oneHourExpiry)
                )
        );
    }

    @Bean
    public Supplier<RestTemplate> restTemplateCreator(RestTemplateBuilder restTemplateBuilder, ApiConfiguration configuration) {
        return () -> restTemplateBuilder
            .rootUri(configuration.uri)
            .setReadTimeout(configuration.timeout().read())
            .setConnectTimeout(configuration.timeout.connection())
            .errorHandler(new CustomResponseErrorHandler())
            .messageConverters(new CustomMappingJackson2HttpMessageConverter())
            .build();
    }

    @ConfigurationProperties(prefix = "api")
    @ConfigurationPropertiesBinding
    public record ApiConfiguration(String uri, TimeoutApiConfiguration timeout) {

        public record TimeoutApiConfiguration(Duration connection, Duration read) {

        }
    }

    @EnableWebSecurity
    @EnableMethodSecurity
    @SpringBootConfiguration(proxyBeanMethods = false)
    public static class SecurityAutoConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.security.token.validate", havingValue = "false")
        public JwtDecoder jwtDecoder() {
            return new CustomJwtDecoder();
        }

        @Bean
        public AuthenticationEntryPoint authenticationEntryPoint(ObjectMapper objectMapper) {
            return new CustomAuthenticationEntryPoint(objectMapper);
        }

        @Bean
        public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
            return new CustomJwtAuthenticationConverter();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
            AuthenticationEntryPoint authenticationEntryPoint,
            JwtDecoder jwtDecoder,
            Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {
            return http
                .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(it -> it.authenticationEntryPoint(authenticationEntryPoint)
                    .jwt(t -> t.decoder(jwtDecoder).jwtAuthenticationConverter(jwtAuthenticationConverter))
                )
                .build();
        }
    }
}