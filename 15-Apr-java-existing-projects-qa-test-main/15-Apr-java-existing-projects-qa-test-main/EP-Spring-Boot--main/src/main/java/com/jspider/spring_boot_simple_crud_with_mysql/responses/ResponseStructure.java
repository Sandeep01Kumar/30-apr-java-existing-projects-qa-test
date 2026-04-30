package com.jspider.spring_boot_simple_crud_with_mysql.responses;

import org.springframework.stereotype.Component;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Generic envelope wrapping a status code, an API description, and a payload of type {@code T}
 * for uniform JSON response shaping across the application's REST surface.
 *
 * <p>This class bundles three concerns into one envelope so that controllers can return a
 * consistent JSON structure to clients regardless of the operation: a numeric {@code statusCode},
 * a free-form {@code apiDescription} string explaining the outcome, and a generic {@code data}
 * payload typed at the callsite. Typical usage binds {@code T} to a domain entity such as
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}, producing
 * {@code ResponseStructure<Product>} as injected into
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}.</p>
 *
 * <p>Lombok's {@code @Data} annotation generates getters, setters, {@code equals},
 * {@code hashCode}, and {@code toString} for all three fields at compile time, so this source
 * file declares only the fields and relies on Lombok-generated accessors at runtime.</p>
 *
 * <p>The {@code @Component} stereotype registers a single Spring-managed bean of this type
 * with the application context. Because the default Spring scope is <i>singleton</i>, exactly
 * one instance exists per {@code ApplicationContext} and is reused for every injection point
 * and every request. This is a deliberate convenience for the educational scope of the project
 * and is preserved verbatim by this documentation effort.</p>
 *
 * <p>The {@code @Schema(hidden = true)} Springdoc annotation suppresses this class from the
 * generated OpenAPI document and from the Swagger UI's "Schemas" section, so although the
 * envelope is the actual wire format for many endpoints, it is intentionally not surfaced as
 * a discoverable schema in the live API documentation. See technical specification
 * Section 7.3.2.4.</p>
 *
 * <p><b>Thread-safety:</b> This class is annotated with {@code @Component}, which makes it a
 * Spring singleton. Controllers (e.g.,
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}) inject
 * and mutate the same instance per request, causing field-aliasing under concurrent traffic:
 * a request that sets {@code statusCode = 201} can be observed by an in-flight concurrent
 * request that sees the modified envelope before its own setter calls overwrite the fields.
 * See technical specification Section 5.2.6.4.</p>
 *
 * <p><i>Source: technical specification Section 5.2.6 (Component Details &mdash; Response Envelope),
 * Section 5.2.6.4 (Thread-safety hazard), Section 7.3.2.4 (Schema visibility).</i></p>
 *
 * @param <T> the payload type carried by the {@code data} field; bound at the injection
 *            callsite (e.g., {@code ResponseStructure<Product>} in
 *            {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}).
 *            May be a single entity, a list, or any application data type.
 * @see com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController
 * @since 1.0.0
 */
@Data
@Component
@Schema(hidden = true)
public class ResponseStructure<T> {

	/**
	 * Numeric HTTP-style status code populated by the controller to indicate the operation's outcome.
	 *
	 * <p>Common values populated by {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}
	 * include {@code 200} (general success), {@code 201} (created), {@code 404} (not found),
	 * and {@code 500} (server error). This field is a free-form numeric code and is <b>not</b>
	 * automatically aligned with the actual HTTP response status line emitted by Spring MVC; the
	 * envelope's {@code statusCode} is JSON-body content only. To set the HTTP response status,
	 * controllers must use {@code ResponseEntity} or {@code @ResponseStatus} as in the
	 * {@code ResponseEntity}-style overload of {@code updateProduct} on
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}.</p>
	 *
	 * @since 1.0.0
	 */
	private int statusCode;
	/**
	 * Free-form human-readable description string explaining the operation outcome.
	 *
	 * <p>Examples of values populated verbatim by
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}:
	 * {@code "save product Secessfully..."} (with the source's intentional spelling, preserved
	 * per Rule R-019) on a successful save, {@code "update product Secessfully..."} on a
	 * successful update, or {@code "data not saved something went wrong"} on a failed save.
	 * The string is intended for client-side display or logging and is not machine-parsed by
	 * the framework.</p>
	 *
	 * @since 1.0.0
	 */
	private String apiDescription;
	/**
	 * The generic payload carried by this envelope, typed by the enclosing class's {@code <T>}
	 * parameter and resolved at the callsite injection point.
	 *
	 * <p>For example, when the controller injects {@code ResponseStructure<Product>}, the
	 * {@code data} field carries a single {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * instance. The field may also carry a {@code List<Product>}, a primitive wrapper, or any
	 * other application data type bound to {@code T} at the injection site.</p>
	 *
	 * @since 1.0.0
	 */
	private T data;
}
