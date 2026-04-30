package com.jspider.spring_boot_simple_crud_with_mysql.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao;
import com.jspider.spring_boot_simple_crud_with_mysql.entity.Product;
import com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller acting as the inbound HTTP boundary for product CRUD operations.
 *
 * <p>This controller is the primary entry point for the {@code Product} resource and
 * exposes its handlers under the base path {@code /product} (singular) per the
 * declared {@link org.springframework.web.bind.annotation.RequestMapping}
 * {@code value = "/product"}. All endpoint paths in this class are therefore
 * relative to {@code /product} (for example {@code /product/saveProduct},
 * {@code /product/findAllProduct}, {@code /product/getProduct/&#123;id&#125;}).
 * The plural form {@code /products} is <em>not</em> used; see technical specification
 * Section 5.2.2.1.</p>
 *
 * <p>The class is stereotyped with
 * {@link org.springframework.web.bind.annotation.RestController}, which combines
 * {@link org.springframework.stereotype.Controller} and
 * {@link org.springframework.web.bind.annotation.ResponseBody}. As a result, every
 * handler method serializes its return value directly to the HTTP response body using
 * the configured Jackson message converter (JSON by default).</p>
 *
 * <p>The annotation {@code @CrossOrigin(value = "")} declares a permissive CORS
 * policy for cross-origin browser callers (feature F-015 / technical specification
 * Section 5.2.2). The annotation
 * {@code @Tag(name = "productcontroller", description = "this is controller class")}
 * declares the Springdoc / OpenAPI grouping under which these endpoints appear in
 * the auto-generated Swagger UI; the tag values are quoted verbatim from the source
 * and are intentionally preserved as declared.</p>
 *
 * <p>Persistence delegation is performed against the autowired
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao}, which in
 * turn delegates to a Spring Data JPA repository. Successful single-product save and
 * envelope-style update responses are returned via the autowired
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
 * envelope.</p>
 *
 * <p><b>Thread-safety:</b> The autowired
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
 * is a Spring {@code @Component} singleton; the same instance is mutated per request
 * via setters before being returned to Jackson, causing field-aliasing under
 * concurrent traffic. See technical specification Section 5.2.6.4.</p>
 *
 * <p><b>Known issue:</b> The two {@code updateProduct} handlers in this controller
 * exhibit a deliberate response-style duplication. The envelope-style
 * {@code PUT /product/updateProduct/&#123;id&#125;} (feature F-007) propagates any
 * {@link RuntimeException} (for example, an id-not-found case where the DAO throws)
 * uncaught, surfacing to the caller as HTTP 500. The
 * {@link org.springframework.http.ResponseEntity}-style {@code PUT /product/&#123;id&#125;}
 * (feature F-008) catches the exception and translates absence to HTTP 404. See
 * technical specification Section 5.2.8.2.</p>
 *
 * @see com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao
 * @see com.jspider.spring_boot_simple_crud_with_mysql.entity.Product
 * @see com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure
 * @since 1.0.0
 */
@RestController
@RequestMapping(value = "/product")
@CrossOrigin(value = "")
@Tag(name = "productcontroller", description = "this is controller class")
public class ProductController {

	/**
	 * Autowired DAO collaborator that performs all persistence operations for the
	 * {@code Product} entity on behalf of this controller.
	 *
	 * <p>Every handler method in {@link ProductController} delegates its database
	 * read, write, update, and delete work to this field rather than calling the
	 * Spring Data repository directly. See
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao}.</p>
	 *
	 * @since 1.0.0
	 */
	@Autowired
	ProductDao productDao;

	/**
	 * Autowired generic response envelope used to return envelope-style payloads
	 * from the single-product save handler and the envelope-style update handler.
	 *
	 * <p>The envelope carries a numeric status code, a free-form description, and
	 * the payload itself; see
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
	 * for its shape.</p>
	 *
	 * <p><b>Thread-safety:</b> This field references a Spring {@code @Component}
	 * singleton; every handler method on this controller mutates the same instance
	 * via setters before returning it, causing field-aliasing under concurrent
	 * traffic. See technical specification Section 5.2.6.4.</p>
	 *
	 * @since 1.0.0
	 */
	@Autowired
	ResponseStructure<Product> responseStructure;

	/**
	 * Returns the current server-side date as a string (feature F-010).
	 *
	 * <p>Mapped to {@code GET /product/getTodayDate}. The implementation evaluates
	 * {@link java.time.LocalDate#now()} on each invocation and concatenates a
	 * single trailing space character, so the returned value is the ISO-8601
	 * date string ({@code YYYY-MM-DD}) followed by a literal space (for example
	 * {@code "2025-04-30 "}).</p>
	 *
	 * <p>The handler is timezone-sensitive: it reflects the JVM's default time
	 * zone at the moment of invocation and accepts no input.</p>
	 *
	 * @return the current date as an ISO-8601 string followed by a single trailing
	 *         space character.
	 * @since 1.0.0
	 */
	@GetMapping(value = "/getTodayDate")
	public String getTodaysDate() {

		return LocalDate.now() + " ";
	}

	/**
	 * Persists a single product and returns an envelope-style response (feature F-001).
	 *
	 * <p>Mapped to {@code POST /product/saveProduct}. The request body is bound from
	 * incoming JSON to a
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} instance
	 * via {@link org.springframework.web.bind.annotation.RequestBody}. Persistence
	 * is delegated to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#saveProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product)},
	 * which forwards the call to Spring Data JPA's {@code save}.</p>
	 *
	 * <p>The autowired
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
	 * is mutated in place: on a non-null DAO result the envelope's status code is
	 * set to {@link org.springframework.http.HttpStatus#OK} (numeric value 200), the
	 * description is set to a success message, and the persisted entity is set as
	 * the data payload; on a null DAO result the status code is set to
	 * {@link org.springframework.http.HttpStatus#NOT_ACCEPTABLE} (numeric value 406)
	 * and the data payload is the same {@code null} reference. The same envelope
	 * instance is returned on both branches.</p>
	 *
	 * <p>The companion {@link io.swagger.v3.oas.annotations.Operation} annotation
	 * declares four
	 * {@link io.swagger.v3.oas.annotations.responses.ApiResponse} entries (HTTP
	 * status codes 200, 400, 406, 500) for the OpenAPI schema. Note that the
	 * status code embedded in the returned envelope's {@code statusCode} field is
	 * <em>not</em> the actual HTTP status line: this handler always responds with
	 * HTTP 200 unless an unhandled exception propagates.</p>
	 *
	 * @param product the product instance to persist; bound from the JSON request
	 *                body. Caller-supplied identifier semantics apply because
	 *                {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *                does not declare {@code @GeneratedValue}.
	 * @return an envelope-style
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
	 *         whose {@code data} field holds the persisted entity on success or
	 *         {@code null} on save failure, and whose {@code statusCode} and
	 *         {@code apiDescription} reflect the outcome.
	 * @since 1.0.0
	 */
	@PostMapping(value = "/saveProduct")
	@Operation(description = "it will save one object at a time",
	responses = {
			@ApiResponse(responseCode = "200", description = "Product saved successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input, object not saved"),
			@ApiResponse(responseCode = "406", description = "Not acceptable, validation failed"),
			@ApiResponse(responseCode = "500", description = "Internal server error") }

	)
	public ResponseStructure<Product> saveProductController(@RequestBody Product product) {

		System.out.println(product);

		Product product2 = productDao.saveProductDao(product);

		if (product2 != null) {
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setApiDescription("save product Secessfully...");
			responseStructure.setData(product2);
			return responseStructure;
		} else {

			responseStructure.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
			responseStructure.setApiDescription("data not saved something went wrong");
			responseStructure.setData(product2);
			return responseStructure;
		}

	}

	/**
	 * Bulk-saves a list of products in a single request (feature F-002).
	 *
	 * <p>Mapped to {@code POST /product/saveProducts}. The request body is bound
	 * from a JSON array to a {@link java.util.List} of
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * instances. The list is forwarded as-is to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#saveMultipleProductDao(java.util.List)},
	 * which delegates to Spring Data JPA's {@code saveAll}.</p>
	 *
	 * <p>This is an overload of {@link #saveProductController(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product)}
	 * (single-product save). Note that this overload returns the persisted list
	 * directly rather than wrapping it in
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}.</p>
	 *
	 * @param products the list of products to persist; bound from the JSON array
	 *                 in the request body. The list may not be {@code null}.
	 * @return the list of products as returned by Spring Data JPA's {@code saveAll};
	 *         the returned entries reflect the persisted state.
	 * @since 1.0.0
	 */
	@PostMapping(value = "/saveProducts")
	public List<Product> saveProductController(@RequestBody List<Product> products) {

		System.out.println(products);
		return productDao.saveMultipleProductDao(products);
	}

	/**
	 * Returns every persisted product in the database (feature F-003).
	 *
	 * <p>Mapped to {@code GET /product/findAllProduct}. The handler delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#displayAllProductDao()},
	 * which forwards the call to Spring Data JPA's {@code findAll}.</p>
	 *
	 * <p>This handler exposes <em>no</em> pagination, sorting, or result-limit
	 * parameters: the entire {@code product} table is materialized into the
	 * returned list and serialized as a single JSON array. Callers that anticipate
	 * a large dataset should be aware of the memory and bandwidth implications of
	 * an unbounded fetch.</p>
	 *
	 * @return an unbounded {@link java.util.List} of every
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         row currently present in the database; an empty list is returned
	 *         when the table is empty.
	 * @since 1.0.0
	 */
	@GetMapping(value = "/findAllProduct")
	public List<Product> findAllProductController() {

		return productDao.displayAllProductDao();
	}

	/**
	 * Looks up a single product by its primary key (feature F-004).
	 *
	 * <p>Mapped to {@code GET /product/getProduct/&#123;id&#125;}. The handler
	 * delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#getProductByIdDao(java.lang.Integer)},
	 * which internally calls
	 * {@code productRepository.findById(id).orElse(null)} via an
	 * {@link java.util.Optional} unwrap.</p>
	 *
	 * <p>On a miss the DAO returns {@code null}, which Spring's message converter
	 * serializes as the literal JSON token {@code null} with an HTTP 200 status
	 * line. <em>There is no HTTP 404 mapping for the missing-id case in this
	 * handler.</em></p>
	 *
	 * @param id the primary key of the product to fetch; bound from the
	 *           {@code &#123;id&#125;} path segment via
	 *           {@link org.springframework.web.bind.annotation.PathVariable}
	 *           {@code (name = "id")}.
	 * @return the matching
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product},
	 *         or {@code null} when no row exists with the supplied id.
	 * @since 1.0.0
	 */
	@GetMapping(value = "/getProduct/{id}")
	public Product getProductByIdController(@PathVariable(name = "id") Integer id) {

		return productDao.getProductByIdDao(id);
	}

	/**
	 * Looks up products whose {@code name} column equals the supplied value
	 * (feature F-005).
	 *
	 * <p>Mapped to {@code GET /product/getProductByName/&#123;name&#125;}. The
	 * lookup is performed by a Spring Data JPA derived query
	 * ({@code findByName}) declared on
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository};
	 * the handler delegates via
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#getProductByNameDao(java.lang.String)}.</p>
	 *
	 * <p>The method is named {@code getProductByNameDao} despite living on the
	 * controller; the naming is preserved verbatim from the source.</p>
	 *
	 * @param name the product name to match exactly; bound from the
	 *             {@code &#123;name&#125;} path segment via
	 *             {@link org.springframework.web.bind.annotation.PathVariable}
	 *             {@code (name = "name")}.
	 * @return a {@link java.util.List} of every
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         whose {@code name} column equals the supplied value; an empty list
	 *         is returned when no rows match.
	 * @since 1.0.0
	 */
	@GetMapping(value = "/getProductByName/{name}")
	public List<Product> getProductByNameDao(@PathVariable(name = "name") String name) {
		return productDao.getProductByNameDao(name);
	}

	/**
	 * Looks up products by their exact price value (feature F-006).
	 *
	 * <p>Mapped to {@code GET /product/getProductByPrice/&#123;price&#125;}. Unlike
	 * the name lookup ({@link #getProductByNameDao(java.lang.String)}), this
	 * lookup is performed by a native SQL query declared on
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository}
	 * via {@code @Query(value = ..., nativeQuery = true)}; the handler delegates
	 * via
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#getProductByPriceDao(double)}.</p>
	 *
	 * <p>Comparison is performed against the raw {@code double} column value;
	 * floating-point equality semantics apply, so callers must supply the price
	 * exactly as it was persisted.</p>
	 *
	 * @param price the exact price to match; bound from the
	 *              {@code &#123;price&#125;} path segment via
	 *              {@link org.springframework.web.bind.annotation.PathVariable}
	 *              {@code (name = "price")}.
	 * @return a {@link java.util.List} of every
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         whose {@code price} column equals the supplied value; an empty list
	 *         is returned when no rows match.
	 * @since 1.0.0
	 */
	@GetMapping(value = "/getProductByPrice/{price}")
	public List<Product> getProductByPriceController(@PathVariable(name = "price") double price) {
		return productDao.getProductByPriceDao(price);
	}

	/**
	 * Deletes every product whose price equals the supplied value (feature F-009).
	 *
	 * <p>Mapped to {@code DELETE /product/deleteProductByPrice/&#123;price&#125;}.
	 * The handler delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#deleteProductByPriceDao(double)},
	 * which forwards the call to the
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository#deleteProductByPrice(double)}
	 * declaration. That repository method carries the
	 * {@code @Modifying @Transactional @Query} triple and is the only
	 * {@code @Transactional} boundary in the codebase.</p>
	 *
	 * <p>The method returns {@code void}; Spring responds with HTTP 200 and an
	 * empty response body on success. No 404 is emitted when the supplied price
	 * matches zero rows.</p>
	 *
	 * @param price the price; every row whose {@code price} column equals this
	 *              value is removed. Bound from the {@code &#123;price&#125;} path
	 *              segment via
	 *              {@link org.springframework.web.bind.annotation.PathVariable}
	 *              {@code (name = "price")}.
	 * @since 1.0.0
	 */
	@DeleteMapping(value = "/deleteProductByPrice/{price}")
	public void deleteProductByPriceController(@PathVariable(name = "price") double price) {

		productDao.deleteProductByPriceDao(price);
	}
	
	//update
	
	

	/**
	 * Updates an existing product and returns an envelope-style response
	 * (feature F-007).
	 *
	 * <p>Mapped to {@code PUT /product/updateProduct/&#123;id&#125;}. The request
	 * body is bound to a
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * (the {@code userproduct} parameter) and the path variable {@code id}
	 * identifies the row to update. The handler delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#updateProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, java.lang.Integer)},
	 * which performs a {@code findById}-then-{@code save} sequence and copies the
	 * supplied {@code name}, {@code color}, and {@code price} fields onto the
	 * loaded entity (the persisted id is preserved).</p>
	 *
	 * <p>On a non-null DAO result the autowired
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
	 * is mutated to carry status code
	 * {@link org.springframework.http.HttpStatus#OK} (200) and the updated entity;
	 * on a null DAO result it is mutated to carry status code
	 * {@link org.springframework.http.HttpStatus#NOT_ACCEPTABLE} (406) and the
	 * {@code null} payload. The same singleton envelope instance is returned on
	 * both branches.</p>
	 *
	 * <p>The companion {@link io.swagger.v3.oas.annotations.Operation} annotation
	 * declares four
	 * {@link io.swagger.v3.oas.annotations.responses.ApiResponse} entries (HTTP
	 * status codes 200, 400, 406, 500) for the OpenAPI schema. As with
	 * {@link #saveProductController(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product)},
	 * the status code carried in the envelope's {@code statusCode} field is
	 * <em>not</em> the actual HTTP status line.</p>
	 *
	 * <p><b>Known issue:</b> Any {@link RuntimeException} thrown by
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#updateProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, java.lang.Integer)}
	 * (for example the explicit {@code RuntimeException("Product not found with ID:
	 * ...")} thrown when the lookup misses) propagates uncaught from this handler,
	 * surfacing to the caller as HTTP 500. Contrast with the sibling overload
	 * {@link #updateProduct(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, java.lang.Integer)}
	 * which catches the exception and translates absence to HTTP 404. See
	 * technical specification Section 5.2.8.2.</p>
	 *
	 * @param userproduct the request body holding the fields to copy onto the
	 *                    existing entity; bound from JSON via
	 *                    {@link org.springframework.web.bind.annotation.RequestBody}.
	 *                    Note the parameter name is {@code userproduct} (single
	 *                    word, lowercase) and is preserved verbatim from source.
	 * @param id          the primary key of the product to update; bound from the
	 *                    {@code &#123;id&#125;} path segment via
	 *                    {@link org.springframework.web.bind.annotation.PathVariable}
	 *                    {@code (name = "id")}.
	 * @return an envelope-style
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.responses.ResponseStructure}
	 *         carrying the updated entity on success or {@code null} on failure
	 *         (with the corresponding numeric status code embedded in the
	 *         envelope).
	 * @throws RuntimeException propagated uncaught when the DAO throws it
	 *                          (typically when no row exists with the supplied
	 *                          {@code id}); the container surfaces this as an
	 *                          HTTP 500 response.
	 * @since 1.0.0
	 */
	@PutMapping(value = "/updateProduct/{id}")
	@Operation(description = "it will update one object at a time",
	responses = {
			@ApiResponse(responseCode = "200", description = "Product Update successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input, object not saved"),
			@ApiResponse(responseCode = "406", description = "Not acceptable, validation failed"),
			@ApiResponse(responseCode = "500", description = "Internal server error") }

	)
	public ResponseStructure<Product> updateProductController(@RequestBody Product userproduct, @PathVariable(name = "id") Integer id) {
        
		 

		Product product2 = productDao.updateProductDao(userproduct, id);

		if (product2 != null) {
			responseStructure.setStatusCode(HttpStatus.OK.value());
			responseStructure.setApiDescription("update product Secessfully...");
			responseStructure.setData(product2);
			return responseStructure;
		} else {

			responseStructure.setStatusCode(HttpStatus.NOT_ACCEPTABLE.value());
			responseStructure.setApiDescription("data not saved something went wrong");
			responseStructure.setData(product2);
			return responseStructure;
		}

	}

	
	/**
	 * Updates an existing product, returning a {@link org.springframework.http.ResponseEntity}
	 * and translating absence to HTTP 404 (feature F-008).
	 *
	 * <p>Mapped to {@code PUT /product/&#123;id&#125;} (relative to the class-level
	 * base path). The request body is bound to a
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * (the {@code product} parameter) and the path variable {@code id} identifies
	 * the row to update. The handler delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#updateProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, java.lang.Integer)}
	 * inside a try/catch on {@link RuntimeException}. On success the updated
	 * entity is wrapped in a
	 * {@link org.springframework.http.ResponseEntity} with HTTP 200; on failure
	 * the catch block returns a
	 * {@link org.springframework.http.ResponseEntity} with HTTP 404
	 * ({@link org.springframework.http.HttpStatus#NOT_FOUND}).</p>
	 *
	 * <p>Note that {@link org.springframework.web.bind.annotation.PathVariable} on
	 * the {@code id} parameter does <em>not</em> declare an explicit {@code name},
	 * so Spring uses the parameter name {@code id} directly as the path-variable
	 * binding key.</p>
	 *
	 * <p><b>Known issue:</b> This handler is a deliberate pedagogical duplication
	 * of {@link #updateProductController(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, java.lang.Integer)}
	 * to contrast response styles. The envelope-style sibling propagates a
	 * {@link RuntimeException} as HTTP 500; this handler catches it and returns
	 * HTTP 404 via {@link org.springframework.http.ResponseEntity}. See technical
	 * specification Section 5.2.8.2.</p>
	 *
	 * @param product the request body holding the fields to copy onto the existing
	 *                entity; bound from JSON via
	 *                {@link org.springframework.web.bind.annotation.RequestBody}.
	 * @param id      the primary key of the product to update; bound from the
	 *                {@code &#123;id&#125;} path segment via
	 *                {@link org.springframework.web.bind.annotation.PathVariable}.
	 * @return a {@link org.springframework.http.ResponseEntity} carrying the
	 *         updated
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         with HTTP 200 ({@link org.springframework.http.HttpStatus#OK}) on
	 *         success, or HTTP 404
	 *         ({@link org.springframework.http.HttpStatus#NOT_FOUND}) when the
	 *         DAO throws (typically when no row exists with the supplied id).
	 * @since 1.0.0
	 */
	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@RequestBody Product product, @PathVariable Integer id) {
	    try {
	        Product updatedProduct = productDao.updateProductDao(product, id);
	        return new ResponseEntity<Product>(updatedProduct, HttpStatus.OK);
	    } catch (RuntimeException e) {
	        return new ResponseEntity<Product>(HttpStatus.NOT_FOUND); // Make sure this line ends with ;
	    }
	}

	
	
	
 
	
}
