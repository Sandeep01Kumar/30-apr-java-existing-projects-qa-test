package com.jspider.spring_boot_simple_crud_with_mysql.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Auxiliary REST controller exposing simple date and arithmetic utility endpoints
 * under the base path {@code /student}.
 *
 * <p>This controller is a deliberately minimal, demonstration-only companion to the
 * primary {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}.
 * It is <b>not</b> part of the product CRUD feature set; it exists purely to demonstrate
 * the {@link org.springframework.web.bind.annotation.RestController @RestController}
 * stereotype and Spring MVC path-variable binding through two trivial handlers.
 *
 * <p><b>Stereotype:</b> The class is annotated with
 * {@link org.springframework.web.bind.annotation.RestController @RestController}, which
 * combines {@link org.springframework.stereotype.Controller @Controller} and
 * {@link org.springframework.web.bind.annotation.ResponseBody @ResponseBody}. As a
 * consequence, every handler method returns a value that Spring serializes directly into
 * the HTTP response body (JSON for object types, plain text for {@link String} and
 * primitive return types) without rendering a view.
 *
 * <p><b>Mapping:</b> The class-level
 * {@link org.springframework.web.bind.annotation.RequestMapping @RequestMapping(value = "/student")}
 * declaration anchors all handler URLs at the {@code /student} prefix, so the
 * fully-qualified endpoints exposed by this class are
 * {@code GET /student/getTodayDate} and {@code POST /student/addition/{a1}/{b1}}.
 *
 * <p><b>Note:</b> This class is <b>deliberately not annotated with
 * {@link org.springframework.web.bind.annotation.CrossOrigin @CrossOrigin}</b> &mdash; it
 * does <b>not</b> participate in the permissive CORS policy applied to
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}.
 * Browser requests originating from foreign origins will therefore be subject to the
 * default same-origin policy enforced by the user-agent. The class is also
 * <b>deliberately not annotated with the Springdoc OpenAPI
 * {@code io.swagger.v3.oas.annotations.tags.Tag} annotation</b> &mdash; its endpoints
 * appear under the default OpenAPI grouping (rather than a named tag) in the generated
 * Swagger UI surface auto-rendered by {@code springdoc-openapi-starter-webmvc-ui} at
 * {@code /swagger-ui/index.html}.
 *
 * <p>(See technical specification Section 5.2.3.)
 *
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/student")
public class StudentController {

	/**
	 * Returns the current server-side date as an ISO-8601 string with a trailing space character.
	 *
	 * <p>This handler is bound to {@code GET /student/getTodayDate} via
	 * {@link org.springframework.web.bind.annotation.GetMapping @GetMapping(value = "/getTodayDate")}.
	 * It accepts no input &mdash; neither query parameters, path variables, headers, nor a request
	 * body &mdash; and produces a string composed of {@link java.time.LocalDate#now()} concatenated
	 * with the literal {@code " "} (a single trailing space). The trailing space is part of the
	 * declared response contract and is preserved verbatim from the source implementation.
	 *
	 * <p>The returned date reflects the JVM's current default time zone at the moment of invocation;
	 * no time-zone parameter is accepted and no time-zone conversion is performed.
	 *
	 * <p>This endpoint is the {@code /student}-tier counterpart of
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController#getTodaysDate()};
	 * the two are semantically equivalent and serve as parallel demonstrations of the
	 * {@link org.springframework.web.bind.annotation.GetMapping @GetMapping} stereotype.
	 *
	 * <p>(See technical specification Section 2.1.11.)
	 *
	 * @return the current server-side date in ISO-8601 form (e.g., {@code "2026-04-30 "})
	 *         followed by a single trailing space character
	 * @since 1.0.0
	 */
	@GetMapping(value = "/getTodayDate")
	public String getTodaysDate() {
		
		return LocalDate.now()+" ";
	}
	
	/**
	 * Returns the integer sum of two operands supplied as URL path variables.
	 *
	 * <p>This handler is bound to {@code POST /student/addition/{a1}/{b1}} via
	 * {@link org.springframework.web.bind.annotation.RequestMapping @RequestMapping}
	 * with {@code method = RequestMethod.POST}. The use of {@code @RequestMapping}
	 * (rather than the more idiomatic
	 * {@link org.springframework.web.bind.annotation.PostMapping @PostMapping}) is preserved
	 * verbatim from the source implementation. Despite the {@code POST} verb, the handler
	 * consumes <b>no</b> request body &mdash; both operands are taken exclusively from the URL.
	 *
	 * <p><b>URL placeholder &harr; parameter binding:</b> the URL contains two placeholders
	 * named {@code {a1}} and {@code {b1}}, while the local Java parameters declared by this
	 * method are named {@code a} and {@code b}. The
	 * {@link org.springframework.web.bind.annotation.PathVariable @PathVariable(name = "...")}
	 * annotations bridge the two name spaces explicitly: {@code @PathVariable(name = "a1") int a}
	 * binds the URL placeholder {@code {a1}} to the local parameter {@code a}, and
	 * {@code @PathVariable(name = "b1") int b} binds the URL placeholder {@code {b1}} to the
	 * local parameter {@code b}. The decoupling of placeholder names from local parameter names
	 * is a Spring MVC idiom and is intentional, not a defect.
	 *
	 * <p><b>Known issue:</b> No overflow handling is performed &mdash; supplying operands whose
	 * sum exceeds {@link java.lang.Integer#MAX_VALUE} or falls below
	 * {@link java.lang.Integer#MIN_VALUE} will silently wrap around per Java integer arithmetic
	 * semantics (the {@code +} operator on {@code int} values is two's-complement modular).
	 * Callers must constrain inputs to a safe range; the controller does not validate operand
	 * magnitudes. (See technical specification Section 2.1.12.)
	 *
	 * <p>(See technical specification Section 2.1.12.)
	 *
	 * @param a the first addend, bound from URL placeholder {@code {a1}} via
	 *          {@code @PathVariable(name = "a1")}
	 * @param b the second addend, bound from URL placeholder {@code {b1}} via
	 *          {@code @PathVariable(name = "b1")}
	 * @return the integer sum {@code a + b}; subject to silent two's-complement wrap-around
	 *         when the mathematical sum lies outside the {@code int} range
	 * @since 1.0.0
	 */
	@RequestMapping(value = "/addition/{a1}/{b1}",method = RequestMethod.POST)
	public int getAdditionOfTwoNumber(@PathVariable(name = "a1") int a,@PathVariable(name = "b1") int b) {
		
		return a+b;
	}
}
