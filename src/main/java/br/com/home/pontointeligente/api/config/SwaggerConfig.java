package br.com.home.pontointeligente.api.config;

import br.com.home.pontointeligente.api.enums.PerfilEnum;
import br.com.home.pontointeligente.api.security.utils.JwtTokenUtil;
import io.swagger.models.auth.ApiKeyAuthDefinition;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spi.service.contexts.SecurityContextBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.ApiKeyVehicle;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@Profile("dev")
@EnableOpenApi
public class SwaggerConfig {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30)
                .select()
				.apis(RequestHandlerSelectors.basePackage("br.com.home.pontointeligente.api.controllers").or(RequestHandlerSelectors.basePackage("br.com.home.pontointeligente.api.security.controllers")))
				.paths(PathSelectors.any())
                .build()
				.securitySchemes(Collections.singletonList(apiKey()))
                .securityContexts(Arrays.asList(securityContext()))
				////.globalResponses(HttpMethod.GET, responseMessageForGET())
				.apiInfo(apiInfo());
				////.securitySchemes(Arrays.asList(apiKey()));

	}

	private List<Response> responseMessageForGET()
	{
		return new ArrayList<>() {{
			add(new ResponseBuilder()
					.code("500")
					.build());
			add(new ResponseBuilder()
					.code("403")
					.build());
		}};
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Ponto Inteligente API")
				.description("Documentação da API de acesso aos endpoints do Ponto Inteligente.").version("1.0")
				.build();
	}

	private ApiKey apiKey() {
		return new ApiKey("API Token", "Authorization", "header");
	}

	private BasicAuth basicAuth()
	{
		return new BasicAuth("Bearer");
	}

	private SecurityContext securityContext() {
		return new SecurityContextBuilder()
				.securityReferences(defaultAuth())
				.build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope
				= new AuthorizationScope(PerfilEnum.ROLE_ADMIN.toString(), "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Arrays.asList(
				new SecurityReference("API Token", new AuthorizationScope[0]));
	}

	/*
	@Bean
	public SecurityConfiguration security() {
		String token;
		try {
			UserDetails userDetails = this.userDetailsService.loadUserByUsername("admin@kazale.com");
			token = this.jwtTokenUtil.obterToken(userDetails);
		} catch (Exception e) {
			token = "";
		}

        return SecurityConfigurationBuilder.builder()
                .clientId("Authorization")
                .clientSecret("Bearer" + token)
                ///.realm("pets")
                .appName("ponto-inteligente")
                .scopeSeparator(",")
                .build();
		////return new SecurityConfiguration(null, null, null, null, "Bearer " + token, ApiKeyVehicle.HEADER, "Authorization", ",");
	}
	*/


}
