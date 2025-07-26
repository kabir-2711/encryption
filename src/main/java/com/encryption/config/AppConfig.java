package com.encryption.config;

import java.net.http.HttpClient;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.encryption.exception.AppException;
import com.encryption.services.AuthProvider;
import com.google.gson.Gson;
import com.utilities.exceptions.ConfigException;
import com.utilities.log.Log;

import lombok.AllArgsConstructor;

/**
 * The {@code AppConfig} class configures security settings for the Spring Boot
 * application.
 * 
 * <p>
 * This class is the {@code Configuration} class for the application.<br>
 * 
 * This class is responsible for setting up Spring Security, including
 * authentication, authorization, security-related filters and creating
 * {@code Log} object and injecting it in the container as well as initializing
 * other {@code beans} that is required by the application.</br>
 * It also provides custom authentication mechanisms using the
 * {@link AuthenticationProvider}.
 * </p>
 * 
 * <p>
 * Key components of this configuration include:
 * </p>
 * <ul>
 * <li>Configuring authentication using custom
 * {@link AuthenticationProvider}.</li>
 * <li>Defining HTTP security rules for securing end-points.</li>
 * <li>Setting up password encoding using {@link BCryptPasswordEncoder}.</li>
 * <li>Defining custom user details service.</li>
 * <li>Initializing {@link Log} and injecting object in the
 * {@code Application Container} to enable asynchronous logging via KAFKA
 * broker.
 * </ul>
 * 
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * &#64;Configuration
 * &#64;EnableWebSecurity
 * class SecurityConfig {
 * 
 * 	&#64;Bean
 * 	SecurityFilterChain securityFilterChain(HttpSecurity security) {
 * 		// security configurations
 * 	}
 * }
 * </pre>
 * 
 * 
 * @see <a href =
 *      "https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/configuration/EnableWebSecurity.html">
 *      EnableWebSecurity</a>
 * @see <a href =
 *      "https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/annotation/Configuration.html">
 *      Configuration</a>
 * @author Kabir Akware
 */
@EnableKafka
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class AppConfig {

	/**
	 * {@code AppAuthProvider} interface variable
	 */
	private AuthProvider provider;

	/**
	 * Method to create a {@code @Bean} of {@link SecurityFilterChain} to provide a
	 * customized implementation of the security and add that to the filter chain
	 * before the application layer to perform security checks before providing
	 * access to the resource3s in the system
	 * 
	 * @param security {@link HttpSecurity} object
	 * @return {@link SecurityFilterChain} with all the necessary security checks
	 * @throws AppException Thrown when a custom exception occurs
	 */
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity security) throws AppException {
		try {
			return security.csrf(AbstractHttpConfigurer::disable)
					.authorizeHttpRequests(request -> request
							.requestMatchers("/v1/users/register", "/v1/channels/register", "/v1/properties/**")
							.hasAuthority("ADMIN").requestMatchers("/v1/audit/**", "/v1/channels/**", "/v1/users/**")
							.permitAll().requestMatchers("/**").hasAuthority("SYSTEM").anyRequest().authenticated())
					.httpBasic(Customizer.withDefaults())
					.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
					.authenticationProvider(provider.authenticationProvider()).build();
		} catch (Exception e) {
			throw ConfigException
					.getInstance("Exception occurred while instantiating security filter chain: " + e.getMessage());
		}
	}


	@Bean
	Gson g() {
		return new Gson().newBuilder().disableHtmlEscaping().serializeNulls().serializeSpecialFloatingPointValues()
				.setPrettyPrinting().create();
	}

	@Bean
	HttpClient client(@Value("${config.audit-service.timeout}") Long timeout) {
		return HttpClient.newBuilder().connectTimeout(Duration.ofMillis(timeout)).build();
	}
}
