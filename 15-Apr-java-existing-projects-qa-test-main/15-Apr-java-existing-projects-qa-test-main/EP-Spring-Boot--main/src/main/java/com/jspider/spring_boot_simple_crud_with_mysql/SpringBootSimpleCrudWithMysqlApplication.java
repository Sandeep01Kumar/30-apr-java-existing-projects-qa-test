package com.jspider.spring_boot_simple_crud_with_mysql;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * JVM entry point and Spring Boot bootstrap class for the Product-Crud-Operation REST service.
 *
 * <p>This class is annotated with {@link org.springframework.boot.autoconfigure.SpringBootApplication},
 * which is itself a meta-annotation combining {@code @Configuration},
 * {@code @EnableAutoConfiguration}, and {@code @ComponentScan}. It triggers Spring Boot's
 * auto-configuration machinery and establishes this package
 * ({@code com.jspider.spring_boot_simple_crud_with_mysql}) as the root for component scanning, so
 * that controllers, DAO beans, repositories, and the
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure} envelope are
 * discovered and wired into the application context at startup.
 *
 * <p>The class also carries an
 * {@link io.swagger.v3.oas.annotations.OpenAPIDefinition} declaration that supplies the API
 * metadata surfaced through the auto-generated Swagger UI at {@code /swagger-ui/index.html} and the
 * OpenAPI JSON descriptor at {@code /v3/api-docs}, both rendered by the
 * {@code springdoc-openapi-starter-webmvc-ui} dependency declared in {@code pom.xml}. The declared
 * metadata is preserved verbatim from the source as the contractual documentation surface and
 * appears in the generated OpenAPI document exactly as written below:
 * <ul>
 *   <li>title: {@code "Product-Crud-Operation"}</li>
 *   <li>version: {@code "1.0.0"}</li>
 *   <li>description: {@code "we perform crud operartion with mysql db"} &mdash; the misspelling
 *       <em>operartion</em> is preserved verbatim because it is part of the declared OpenAPI
 *       metadata contract; see technical specification Section 7.3.2.1 and Agent Action Plan
 *       Rule R-004 (Verbatim preservation of OpenAPI metadata). It is intentionally <strong>not</strong>
 *       "corrected" by this documentation effort.</li>
 *   <li>contact URL: {@code "https://www.w3schools.com/"} (preserved verbatim; contact name and
 *       email are intentionally empty strings as declared in the source)</li>
 * </ul>
 *
 * <p>After {@link org.springframework.boot.SpringApplication#run(Class, String...)} returns, the
 * bootstrap method emits the post-startup banner {@code "All Right Sudhir..........."} (with eleven
 * trailing dots) to standard output, signalling that the embedded Tomcat container is bound and the
 * application is ready to accept HTTP traffic on the port declared in
 * {@code src/main/resources/application.properties} (default {@code 8090}). The banner doubles as a
 * smoke-test signal in the deployment guide.
 *
 * <p>Source: technical specification Section 5.2.1 (Bootstrap) and Section 7.3.2.1 (OpenAPI
 * Annotations).
 *
 * @see org.springframework.boot.SpringApplication
 * @see org.springframework.boot.autoconfigure.SpringBootApplication
 * @see io.swagger.v3.oas.annotations.OpenAPIDefinition
 * @see io.swagger.v3.oas.annotations.info.Info
 * @see io.swagger.v3.oas.annotations.info.Contact
 * @since 1.0.0
 */
@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Product-Crud-Operation",
				description = "we perform crud operartion with mysql db",
				version = "1.0.0",
				contact = @Contact(
						name = "",
						email = "",
						url = "https://www.w3schools.com/"
						)
				)
		)
public class SpringBootSimpleCrudWithMysqlApplication {

	/**
	 * Launches the Spring Boot application by delegating to
	 * {@link org.springframework.boot.SpringApplication#run(Class, String...)} and then prints the
	 * post-startup banner to standard output.
	 *
	 * <p>This is the JVM entry point invoked either by the packaged executable JAR via
	 * {@code java -jar target/spring-boot-simple-crud-with-mysql-0.0.1-SNAPSHOT.jar} or by the Maven
	 * Wrapper command {@code ./mvnw spring-boot:run}. It returns no value; control transfers to the
	 * embedded Tomcat container started by Spring Boot, which keeps the JVM alive until the process
	 * is terminated (for example by {@code Ctrl+C} or {@code kill}).
	 *
	 * <p>After {@code SpringApplication.run(...)} returns, the method emits the literal banner
	 * {@code "All Right Sudhir..........."} (with exactly eleven trailing dots) to {@code System.out}
	 * via {@link java.io.PrintStream#println(String)} as a visual confirmation that the Spring
	 * application context started successfully and the embedded servlet container is bound to its
	 * configured port (default {@code 8090}, as declared in
	 * {@code src/main/resources/application.properties}).
	 *
	 * <p>Source: technical specification Section 5.2.1.2 (Bootstrap behavior).
	 *
	 * @param args  command-line arguments forwarded verbatim to
	 *              {@link org.springframework.boot.SpringApplication#run(Class, String...)}; may
	 *              carry Spring Boot externalized configuration overrides such as
	 *              {@code --server.port=8090},
	 *              {@code --spring.datasource.url=jdbc:mysql://localhost:3306/products_db},
	 *              {@code --spring.datasource.username=root}, or
	 *              {@code --spring.jpa.hibernate.ddl-auto=update}. Pass an empty array (or simply no
	 *              arguments) to run with all defaults, in which case the application falls back to
	 *              the in-memory H2 datasource at {@code jdbc:h2:mem:testdb} per Spring Boot's
	 *              embedded-database auto-configuration.
	 * @since 1.0.0
	 */
	public static void main(String[] args) {
		SpringApplication.run(SpringBootSimpleCrudWithMysqlApplication.class, args);
		
		System.out.println("All Right Sudhir...........");
	}

}
