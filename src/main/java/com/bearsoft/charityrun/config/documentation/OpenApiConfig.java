package com.bearsoft.charityrun.config.documentation;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Dragos",
                        email = "bear.soft.development@gmail.com"
                ),
                description = "OpenApi Documentation for Charity Run Web App",
                title = "OpenApi specification - Dragos",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "LOCAL_ENV",
                        url = "https://localhost:443"
                ),
                @Server(
                        description = "PRODUCTION_ENV",
                        url = "https://3.125.33.241"
                ),
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
