package geeks.dongnea.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig { // 기존 파일 이름을 유지합니다.

	@Bean
	public OpenAPI openAPI() {
		String jwtSchemeName = "bearerAuth";
		SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);

		Components components = new Components()
				.addSecuritySchemes(jwtSchemeName, new SecurityScheme()
						.name(jwtSchemeName)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT"));

		return new OpenAPI()
				.info(new Info()
						.title("동네 (Dongne) API 문서")
						.description("인하대 동아리 통합 플랫폼 - PRD v1.1 기반")
						.version("1.1"))
				.addSecurityItem(securityRequirement)
				.components(components);
	}
}