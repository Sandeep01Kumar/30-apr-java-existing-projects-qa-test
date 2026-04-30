package com.jspider.spring_boot_simple_crud_with_mysql.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


/**
 * JPA entity representing a single product row in the database.
 *
 * <p>This class is the canonical data contract used across the persistence layer
 * ({@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository}),
 * the data access layer
 * ({@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao}),
 * and the inbound HTTP layer
 * ({@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}),
 * where it is used as both the request body type and the response payload type for the
 * {@code /product/**} endpoints.</p>
 *
 * <p>The {@code @Entity} annotation registers this class with the Jakarta Persistence
 * provider (Hibernate), causing it to participate in the JPA managed-state lifecycle and
 * to be mapped to a relational database table whose name is derived from the class name
 * by Hibernate's default naming strategy (typically {@code product} on MySQL; H2 may
 * capitalize differently). The {@code @Id}-annotated field {@code id} marks the primary
 * key of that table.</p>
 *
 * <p>The Lombok {@code @Data} annotation generates, at compile time, public getters and
 * setters for every field, plus {@code equals(Object)}, {@code hashCode()},
 * {@code toString()}, and a no-arguments constructor. These generated members are not
 * authored source code and therefore do not carry their own Javadoc; the documented
 * behavior of each field below describes the contract that the generated accessors expose.</p>
 *
 * <p>The {@code @Schema(name = "product class", description = "this is product entity class")}
 * annotation contributes this type to the OpenAPI / Swagger UI schema list under the
 * declared schema name {@code "product class"} (quoted verbatim from the source). The
 * schema is rendered live by the Springdoc OpenAPI integration at
 * {@code /swagger-ui/index.html} and {@code /v3/api-docs}.</p>
 *
 * <p><b>Known issue:</b> The class does not declare a {@code @Version} field; therefore
 * JPA optimistic locking is NOT enabled. Concurrent updates of the same row will silently
 * overwrite each other with last-writer-wins semantics. See technical specification
 * Section 5.2.5.3.</p>
 *
 * <p><i>Source: technical specification Section 5.2.5, Section 5.2.5.2, Section 5.2.5.3.</i></p>
 *
 * @see com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository
 * @see com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao
 * @see com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController
 * @since 1.0.0
 */
@Entity
@Data
@Schema(name = "product class",description = "this is product entity class")
public class Product {

	/**
	 * Primary key of the product row, declared as a Java {@code int}.
	 *
	 * <p>This field carries the {@code @Id} annotation, marking it as the JPA identifier
	 * for the {@link Product} entity.</p>
	 *
	 * <p><b>Known issue:</b> The {@code @Id} field is NOT annotated with
	 * {@code @GeneratedValue}, so JPA does NOT auto-generate primary keys. Callers MUST
	 * supply a unique {@code id} value explicitly when invoking
	 * {@code POST /product/saveProduct}. The endpoint is therefore non-idempotent at the
	 * contract level &mdash; a re-submission with the same {@code id} will fail with a
	 * primary-key violation, while a re-submission with a different {@code id} will create
	 * a duplicate-data row. See technical specification Section 5.2.5.2.</p>
	 *
	 * @since 1.0.0
	 */
	@Id
	private int id;
	/**
	 * Plain product name as a {@link String}.
	 *
	 * <p>No length constraint is declared via {@code @Column(length = ...)}, so persistence
	 * relies on the JPA provider's default column type (typically {@code VARCHAR(255)} on
	 * MySQL and a similar default on H2). No Bean Validation annotation such as
	 * {@code @NotBlank} or {@code @NotNull} is declared, so a {@code null} or empty string
	 * passes through unchallenged at the controller and persistence boundaries.</p>
	 *
	 * @since 1.0.0
	 */
	private String name;
	/**
	 * Plain product color as a {@link String}.
	 *
	 * <p>No length constraint is declared via {@code @Column(length = ...)}, so persistence
	 * relies on the JPA provider's default column type (typically {@code VARCHAR(255)} on
	 * MySQL). No Bean Validation annotation such as {@code @NotBlank} or {@code @NotNull}
	 * is declared, so a {@code null} or empty string passes through unchallenged.</p>
	 *
	 * @since 1.0.0
	 */
	private String color;
	/**
	 * Product price expressed as a Java {@code double}.
	 *
	 * <p>The field carries
	 * {@code @Schema(description = "price datatype is double")} (quoted verbatim from
	 * source per Rule R-019), which is rendered by Springdoc into the OpenAPI schema as
	 * advisory documentation only.</p>
	 *
	 * <p><b>Note:</b> The {@code @Schema} description is purely advisory and is NOT
	 * enforced by any Bean Validation annotation such as {@code @Min(value = ...)} or
	 * {@code @DecimalMin(...)}. Negative or zero prices pass through both the controller
	 * and the persistence layer unchallenged.</p>
	 *
	 * @since 1.0.0
	 */
	@Schema(description = "price datatype is double")
	
	private double price;
	 
	
	 
}
