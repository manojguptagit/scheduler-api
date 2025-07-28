package com.home.schedule.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger/OpenAPI configuration for the Job Scheduler API.
 * 
 * This configuration class customizes the OpenAPI documentation
 * with detailed information about the API, including:
 * - API title, description, and version
 * - Contact information
 * - License details
 * - Server configurations
 * 
 * The generated documentation will be available at:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/api-docs
 * 
 * @author Job Scheduler Team
 * @version 1.0.0
 * @since 2024
 */
@Configuration
public class SwaggerConfig {

    /**
     * Configure the OpenAPI documentation
     * 
     * @return OpenAPI configuration object
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Job Scheduler API")
                        .description("A comprehensive REST API for job scheduling and management.\n\n" +
                                "## Features\n" +
                                "- **Job Management**: Create, update, delete, and manage jobs\n" +
                                "- **Scheduling**: Configure cron-based schedules for job execution\n" +
                                "- **Execution Tracking**: Monitor job executions and their status\n" +
                                "- **Dependencies**: Manage job dependencies and workflows\n" +
                                "- **Notifications**: Configure and track job notifications\n" +
                                "- **Statistics**: View job performance metrics and analytics\n" +
                                "- **Audit Logging**: Complete audit trail for all operations\n\n" +
                                "## Authentication\n" +
                                "Currently, the API does not require authentication for development purposes.\n" +
                                "In production, implement proper authentication and authorization.\n\n" +
                                "## Rate Limiting\n" +
                                "The API implements rate limiting to prevent abuse.\n" +
                                "Please respect the rate limits when making requests.\n\n" +
                                "## Error Handling\n" +
                                "The API returns appropriate HTTP status codes and error messages.\n" +
                                "Check the response body for detailed error information.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Job Scheduler Team")
                                .email("support@scheduler.com")
                                .url("https://github.com/scheduler-api"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.scheduler.com")
                                .description("Production Server")
                ));
    }
} 