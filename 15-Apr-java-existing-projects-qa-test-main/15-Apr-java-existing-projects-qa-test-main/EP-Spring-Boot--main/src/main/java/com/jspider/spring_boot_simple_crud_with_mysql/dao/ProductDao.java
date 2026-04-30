package com.jspider.spring_boot_simple_crud_with_mysql.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jspider.spring_boot_simple_crud_with_mysql.entity.Product;
import com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository;

/**
 * Data Access Object for the
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} entity,
 * encapsulating all product-oriented persistence operations behind a single
 * Spring-managed bean.
 *
 * <p>The class carries the Spring {@link org.springframework.stereotype.Repository}
 * stereotype, which registers it as a singleton bean during component scanning by
 * {@code @SpringBootApplication} and enrolls it in Spring's persistence-exception
 * translation: any underlying JPA {@code PersistenceException} thrown by Hibernate is
 * translated into Spring's {@code DataAccessException} hierarchy.</p>
 *
 * <p><b>Architectural note:</b> This class represents a deliberate conflation of what
 * would conventionally be two separate layers &mdash; a service layer (orchestration,
 * transaction management, business logic) and a DAO layer (raw repository delegation).
 * The conflation is intentional per technical specification Section 5.2.4 and is
 * preserved as-is. The class therefore carries business-orchestration semantics
 * (e.g., the {@code findById}-then-{@code save} sequence in
 * {@link #updateProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, Integer)})
 * directly on top of {@link org.springframework.data.jpa.repository.JpaRepository}
 * delegation, without an interposed service.</p>
 *
 * <p>The sole collaborator is
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository},
 * injected via Spring field-based autowiring. This class is in turn invoked exclusively
 * from {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController},
 * which delegates every product-oriented HTTP handler to one of the eight methods
 * declared below.</p>
 *
 * <p><b>Note:</b> The source file imports several Spring Web types ({@code HttpStatus},
 * {@code ResponseEntity}, {@code PathVariable}, {@code PutMapping}, {@code RequestBody})
 * that are not referenced by any method body. Per Rule R-003 (no source-code semantic
 * changes), these unused imports are preserved as-is and are documented here so that
 * future readers do not remove them speculatively.</p>
 *
 * <p><i>Source: technical specification Section 5.2.4 &mdash; Component Details: DAO and
 * Repository Layer; Section 5.2.4.4 &mdash; Update Race Window.</i></p>
 *
 * @see com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository
 * @see com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController
 * @see com.jspider.spring_boot_simple_crud_with_mysql.entity.Product
 * @since 1.0.0
 */
@Repository
public class ProductDao {

	/**
	 * Spring Data JPA repository for the {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * entity, injected via field-based autowiring.
	 *
	 * <p>This is the sole collaborator of {@link ProductDao}. Every method on this DAO
	 * delegates to one or more methods on
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository}
	 * &mdash; either the inherited {@code JpaRepository} methods ({@code save},
	 * {@code saveAll}, {@code findAll}, {@code findById}) or the three custom methods
	 * declared on the repository interface ({@code findByName},
	 * {@code getProductByPrice}, {@code deleteProductByPrice}).</p>
	 *
	 * <p>Field-based injection is used here as in the source; per Rule R-003, the
	 * injection style is not refactored to constructor-based injection.</p>
	 *
	 * @see com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository
	 * @since 1.0.0
	 */
	@Autowired
	ProductRepository productRepository;

	/**
	 * Persists a single {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * by delegating to {@link org.springframework.data.jpa.repository.JpaRepository#save(Object)}.
	 *
	 * <p>This method is the sole insert path for individual products in the application.
	 * It is invoked by
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}'s
	 * {@code POST /product/saveProduct} handler. Because the underlying {@code save}
	 * call is non-transactional at this layer (no {@code @Transactional} annotation is
	 * present on either this method or its containing class), Spring opens a brief
	 * implicit transaction via the JPA EntityManager for the duration of the persist
	 * operation and commits immediately on return.</p>
	 *
	 * <p>Note that the {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * entity does not declare {@code @GeneratedValue} on its {@code id} field; callers
	 * MUST therefore supply a unique {@code id} value on the request body, otherwise the
	 * underlying database will reject the insert with a primary-key violation. See
	 * technical specification Section 5.2.5.2.</p>
	 *
	 * @param product the {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *                to persist; must not be {@code null}; the {@code id} field must be
	 *                supplied explicitly because the entity does not auto-generate keys
	 * @return the persisted {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         instance returned by
	 *         {@link org.springframework.data.jpa.repository.JpaRepository#save(Object)};
	 *         in practice the same instance with managed state attached
	 * @since 1.0.0
	 */
	public Product saveProductDao(Product product) {
		return productRepository.save(product);
	}

	/**
	 * Persists a batch of {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * instances by delegating to
	 * {@link org.springframework.data.jpa.repository.JpaRepository#saveAll(Iterable)}.
	 *
	 * <p>This method is invoked by
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}'s
	 * {@code POST /product/saveProducts} handler. The bulk save is internally
	 * implemented by the JpaRepository proxy as a series of individual {@code save}
	 * invocations within the same EntityManager session; it is not guaranteed to
	 * produce a single SQL batch unless Hibernate's {@code hibernate.jdbc.batch_size}
	 * property is configured (which the project's {@code application.properties} does
	 * not set, per technical specification Section 8.8.4).</p>
	 *
	 * <p>The parameter is named {@code product} (singular) in the source rather than
	 * {@code products}; the parameter name is preserved verbatim per Rule R-003.</p>
	 *
	 * @param product the {@link java.util.List} of
	 *                {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *                instances to persist; must not be {@code null}; each element must
	 *                carry a unique {@code id} for the same reason described in
	 *                {@link #saveProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product)}
	 * @return the {@link java.util.List} of persisted
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         instances returned by
	 *         {@link org.springframework.data.jpa.repository.JpaRepository#saveAll(Iterable)},
	 *         in the same order as the input
	 * @since 1.0.0
	 */
	public List<Product> saveMultipleProductDao(List<Product> product) {
		return productRepository.saveAll(product);
	}

	/**
	 * Retrieves every persisted {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * by delegating to
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findAll()}.
	 *
	 * <p>This method is invoked by
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController}'s
	 * {@code GET /product/findAllProduct} handler. The returned list is unbounded
	 * &mdash; pagination, sorting, and filtering are NOT applied. For datasets larger
	 * than a few thousand rows this approach is unsuitable; the absence of pagination
	 * is a deliberate design choice for this educational project per technical
	 * specification Section 2.1.3.</p>
	 *
	 * @return the unbounded {@link java.util.List} of every
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         row in the {@code product} table; an empty list when the table is empty
	 *         (never {@code null})
	 * @since 1.0.0
	 */
	public List<Product> displayAllProductDao() {
		return productRepository.findAll();
	}

	/**
	 * Retrieves a single {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * by primary key, returning {@code null} when no row matches.
	 *
	 * <p>This method delegates to
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findById(Object)},
	 * which returns an {@link java.util.Optional}; the implementation collapses the
	 * Optional to either the contained {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * or the literal {@code null} via the ternary expression
	 * {@code optional.isPresent() ? optional.get() : null}.</p>
	 *
	 * <p><b>Important contract note:</b> Absence of a row yields a {@code null} return
	 * value, NOT a thrown exception. Callers are responsible for null-checking the
	 * return value. The {@code GET /product/getProduct/&#123;id&#125;} controller method
	 * does not currently translate {@code null} into HTTP 404; it returns a 200 OK with
	 * a {@code null} JSON body. See technical specification Section 2.1.4.</p>
	 *
	 * @param id the primary-key identifier of the
	 *           {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *           to look up; boxed {@link Integer} (auto-unboxed by Spring's path-variable
	 *           binding when invoked through the controller)
	 * @return the matching
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         instance when found; {@code null} when no row matches the supplied {@code id}
	 * @since 1.0.0
	 */
	public Product getProductByIdDao(Integer id) {

		Optional<Product> optional = productRepository.findById(id);

		return optional.isPresent() ? optional.get() : null;

	}

	/**
	 * Retrieves all {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * rows whose {@code name} field matches the supplied value exactly.
	 *
	 * <p>This method delegates to the derived-query method
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository#findByName(String)},
	 * which Spring Data JPA implements automatically by parsing the method name into
	 * the equivalent JPQL query ({@code SELECT p FROM Product p WHERE p.name = :name}).
	 * The comparison is case-sensitive and uses exact-match semantics (no wildcards).</p>
	 *
	 * @param name the exact value to match against the {@code Product.name} column;
	 *             must not be {@code null}; case-sensitive equality is applied
	 * @return a {@link java.util.List} of
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         entities whose {@code name} field equals the supplied value; an empty
	 *         list when no rows match (never {@code null})
	 * @since 1.0.0
	 */
	public List<Product> getProductByNameDao(String name) {
		
		return productRepository.findByName(name);

	}
	
	/**
	 * Retrieves all {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * rows whose {@code price} column equals the supplied value, executed as a native
	 * SQL query.
	 *
	 * <p>This method delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository#getProductByPrice(double)},
	 * which carries an {@code @Query(value = "select * from product where price=?",
	 * nativeQuery = true)} annotation. The native SQL is passed verbatim to the JDBC
	 * driver after positional parameter substitution, and the result-set rows are mapped
	 * back to {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * instances by Hibernate using the entity's column mapping.</p>
	 *
	 * @param price the exact price value to match against the {@code price} column;
	 *              floating-point equality semantics apply (no tolerance window)
	 * @return a {@link java.util.List} of
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         entities whose {@code price} column equals the supplied value; an empty
	 *         list when no rows match (never {@code null})
	 * @since 1.0.0
	 */
	public List<Product> getProductByPriceDao(double price){
		return productRepository.getProductByPrice(price);
	}
	
	/**
	 * Deletes all {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * rows whose {@code price} column equals the supplied value.
	 *
	 * <p>This method delegates to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.repository.ProductRepository#deleteProductByPrice(double)},
	 * which carries the {@code @Modifying}, {@code @Transactional}, and
	 * {@code @Query(value = "delete from product where price=?", nativeQuery = true)}
	 * annotation triple.</p>
	 *
	 * <p><b>Architectural note:</b> The {@code @Transactional} boundary is declared on
	 * the repository method, NOT on this DAO method. Because of this delegation, the
	 * delete is executed within a Spring-managed transaction that is opened just before
	 * the SQL DELETE and committed immediately after. This is the only
	 * {@link org.springframework.transaction.annotation.Transactional} boundary in the
	 * codebase per technical specification Section 5.2.4.</p>
	 *
	 * @param price the exact price value to match against the {@code price} column for
	 *              deletion; floating-point equality semantics apply
	 * @since 1.0.0
	 */
	public void deleteProductByPriceDao(double price) {
		
		productRepository.deleteProductByPrice(price);
	}
	
	/**
	 * Updates the mutable business fields ({@code name}, {@code color}, {@code price})
	 * of an existing {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 * row identified by {@code id}, preserving the original primary-key value.
	 *
	 * <p>The implementation is a read-then-write sequence:
	 * {@link org.springframework.data.jpa.repository.JpaRepository#findById(Object)} is
	 * invoked first; if the {@link java.util.Optional} is present, the existing managed
	 * entity is mutated via Lombok-generated setters
	 * ({@code setName}, {@code setColor}, {@code setPrice}) and then re-persisted via
	 * {@link org.springframework.data.jpa.repository.JpaRepository#save(Object)}; if the
	 * {@link java.util.Optional} is empty, a {@link RuntimeException} with message
	 * {@code "Product not found with ID: " + id} is thrown unconditionally.</p>
	 *
	 * <p>The {@code id} field of the supplied {@code product} argument is deliberately
	 * NOT copied onto the existing entity &mdash; the original primary key is preserved.
	 * This is intentional per technical specification Section 2.1.7.</p>
	 *
	 * <p><b>Known issue:</b> Absence of {@code @Transactional} splits the read and write
	 * into two separate transactions, opening a lost-update window between detached-state
	 * mutation and write-back. Concurrent invocations may produce inconsistent results
	 * because the entity is detached between the {@code findById} read and the
	 * {@code save} write &mdash; another writer may commit a different version of the
	 * row in that window, and this method's subsequent {@code save} will silently
	 * overwrite the concurrent change with last-writer-wins semantics. The
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} entity also
	 * does not declare a {@code @Version} field, so JPA optimistic locking cannot detect
	 * the conflict. See technical specification Section 5.2.4.4.</p>
	 *
	 * <p>This method is invoked from two distinct controller methods that handle the
	 * outcome differently:</p>
	 * <ul>
	 * <li>The envelope-style {@code PUT /product/updateProduct/&#123;id&#125;} handler
	 *     (F-007) does NOT catch the {@code RuntimeException}; it propagates uncaught
	 *     to a Spring default HTTP 500 response.</li>
	 * <li>The {@code ResponseEntity}-style {@code PUT /product/&#123;id&#125;} handler
	 *     (F-008) wraps this call in a try/catch and translates the
	 *     {@code RuntimeException} into an HTTP 404 response.</li>
	 * </ul>
	 * <p>The deliberate two-style update duplication is documented in technical
	 * specification Section 5.2.8.2.</p>
	 *
	 * @param product the request body
	 *                {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *                whose {@code name}, {@code color}, and {@code price} fields are
	 *                copied onto the existing entity; the {@code id} field of this
	 *                argument is ignored
	 * @param id      the primary-key identifier of the existing
	 *                {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *                to update; boxed {@link Integer}; if no row matches, a
	 *                {@link RuntimeException} is thrown
	 * @return the persisted
	 *         {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         after the field updates have been written back
	 * @throws RuntimeException with message {@code "Product not found with ID: " + id}
	 *                          when no row matches the supplied {@code id} (i.e., when
	 *                          {@link org.springframework.data.jpa.repository.JpaRepository#findById(Object)}
	 *                          returns an empty {@link java.util.Optional})
	 * @since 1.0.0
	 */
	public Product updateProductDao(Product product, Integer id) {
	    Optional<Product> optional = productRepository.findById(id);

	    if (optional.isPresent()) {
	        Product existingProduct = optional.get();

	        // Do NOT update the ID; it should remain unchanged
	        existingProduct.setName(product.getName());
	        existingProduct.setColor(product.getColor());
	        existingProduct.setPrice(product.getPrice());

	        return productRepository.save(existingProduct);
	    } else {
	        // You can handle not found case as you wish
	        throw new RuntimeException("Product not found with ID: " + id);
	    }
	}
	
	 
	
	
}
