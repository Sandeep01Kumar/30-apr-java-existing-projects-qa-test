package com.jspider.spring_boot_simple_crud_with_mysql.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.jspider.spring_boot_simple_crud_with_mysql.entity.Product;

/**
 * Spring Data JPA repository contract for the {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
 * entity, extending {@link org.springframework.data.jpa.repository.JpaRepository} to inherit standard CRUD,
 * paging, and sorting operations.
 *
 * <p>This interface is auto-implemented by Spring Data JPA at application startup as a runtime proxy. The
 * proxy bean is created when the surrounding {@code @SpringBootApplication} triggers
 * {@code @EnableJpaRepositories} auto-configuration, and the proxy provides concrete implementations of all
 * standard {@link org.springframework.data.jpa.repository.JpaRepository} methods ({@code save},
 * {@code saveAll}, {@code findAll}, {@code findById}, {@code delete}, {@code deleteAll}, etc.) plus the three
 * custom methods declared below.</p>
 *
 * <p>This interface is the persistence boundary of the application: invocations cross from the JVM (managed
 * Spring beans plus the Hibernate session) into the underlying JDBC layer here. Higher layers
 * ({@link com.jspider.spring_boot_simple_crud_with_mysql.controller.ProductController} and
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao}) interact with this interface only
 * through Spring's dependency injection and rely on the proxy to translate method calls into SQL.</p>
 *
 * <p>The {@code JpaRepository<Product, Integer>} type parameters bind the entity type to
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} and the primary-key type to
 * {@link java.lang.Integer} &mdash; corresponding to the {@code int id} field of
 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}. Per the technical specification
 * Section 5.2.5, the {@code Product.id} field carries {@code @Id} but does NOT carry
 * {@code @GeneratedValue}, so callers must supply IDs explicitly.</p>
 *
 * <p><b>Note:</b> The {@code org.springframework.data.jpa.repository.NativeQuery} import is unused; native
 * query semantics are realized through the {@code @Query(value = "...", nativeQuery = true)} attribute pair
 * on {@link #getProductByPrice(double)} and {@link #deleteProductByPrice(double)}. The import is preserved
 * as-is per Rule R-003 (no source-code semantic changes). See technical specification Section 5.2.4.</p>
 *
 * <p><i>Source: Technical specification Section 5.2.4 &mdash; Component Details: DAO and Repository Layer.</i></p>
 *
 * @see com.jspider.spring_boot_simple_crud_with_mysql.entity.Product
 * @see com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao
 * @see org.springframework.data.jpa.repository.JpaRepository
 * @since 1.0.0
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {

	/**
	 * Retrieves all {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} rows whose
	 * {@code name} field matches the supplied value exactly.
	 *
	 * <p>This method is a Spring Data JPA derived query: the framework parses the method name
	 * {@code findByName} at proxy-generation time and emits the equivalent JPQL query
	 * ({@code SELECT p FROM Product p WHERE p.name = :name}). No {@code @Query} annotation is required
	 * because the method name itself encodes the query intent.</p>
	 *
	 * <p>The comparison is case-sensitive and uses exact-match semantics (no wildcards). The generated
	 * JPQL is executed by Hibernate and may yield zero, one, or many results depending on the dataset.</p>
	 *
	 * @param name the exact value to match against the {@code Product.name} column; must not be
	 *             {@code null}; case-sensitive equality is applied
	 * @return a {@link java.util.List} of {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         entities whose {@code name} field equals the supplied value; an empty list when no rows match
	 *         (never {@code null})
	 * @since 1.0.0
	 */
	List<Product> findByName(String name);
	
	/**
	 * Retrieves all {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} rows whose
	 * {@code price} column equals the supplied value, executed as a native SQL query.
	 *
	 * <p>The {@code @Query(value = "select * from product where price=?", nativeQuery = true)} annotation
	 * declares this method as a native SQL query, NOT a JPQL query. The SQL string is passed verbatim to
	 * the underlying JDBC driver after positional parameter substitution ({@code ?} is bound to the
	 * {@code price} argument). The result-set rows are mapped back to
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} instances by Hibernate using
	 * the entity's column mapping. This contrasts with the derived-query approach used by
	 * {@link #findByName(String)} &mdash; native queries bypass JPQL parsing entirely.</p>
	 *
	 * <p>Because the SQL is native, it is bound to the underlying database dialect (the project targets
	 * MySQL per the project name; the SQL {@code select * from product} happens to be portable to H2 in the
	 * default configuration too). A future migration to a different database may require revising the SQL.</p>
	 *
	 * @param price the exact price value to match against the {@code price} column; floating-point equality
	 *              semantics apply (no tolerance window)
	 * @return a {@link java.util.List} of {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product}
	 *         entities whose {@code price} column equals the supplied value; an empty list when no rows
	 *         match (never {@code null})
	 * @since 1.0.0
	 */
	@Query(value = "select * from product where price=?",nativeQuery = true)
	List<Product> getProductByPrice(double price);
	
	/**
	 * Deletes all {@link com.jspider.spring_boot_simple_crud_with_mysql.entity.Product} rows whose
	 * {@code price} column equals the supplied value, executed as a native SQL DELETE within a
	 * transactional boundary.
	 *
	 * <p>This method carries three coordinating annotations:</p>
	 * <ul>
	 *   <li>{@code @Query(value = "delete from product where price=?", nativeQuery = true)} &mdash;
	 *       declares the native SQL DELETE statement.</li>
	 *   <li>{@code @Modifying} &mdash; required by Spring Data JPA for any {@code @Query} whose SQL
	 *       mutates state (DELETE, UPDATE, INSERT); the absence of {@code @Modifying} would cause Spring
	 *       Data to attempt a {@code SELECT}-style execution and fail at runtime.</li>
	 *   <li>{@link org.springframework.transaction.annotation.Transactional @Transactional} &mdash; opens a
	 *       Spring-managed transaction around the delete so that the change is committed (or rolled back
	 *       on exception) atomically.</li>
	 * </ul>
	 *
	 * <p><b>Architectural note:</b> This method is the <i>only</i>
	 * {@link org.springframework.transaction.annotation.Transactional} boundary declared anywhere in the
	 * codebase. In particular, the {@code updateProductDao} method on
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao} performs a {@code findById}-then-
	 * {@code save} sequence WITHOUT {@code @Transactional}, which splits the read and write into two
	 * distinct transactions and opens a lost-update race window between detached-state mutation and
	 * write-back. The deliberate placement of {@code @Transactional} on this single delete method, and its
	 * absence elsewhere, is documented in technical specification Section 5.2.4.4. See
	 * {@link com.jspider.spring_boot_simple_crud_with_mysql.dao.ProductDao#updateProductDao(com.jspider.spring_boot_simple_crud_with_mysql.entity.Product, java.lang.Integer)}.</p>
	 *
	 * @param price the exact price value to match against the {@code price} column for deletion;
	 *              floating-point equality semantics apply
	 * @since 1.0.0
	 */
	@Query(value = "delete from product where price=?",nativeQuery = true)
	@Modifying
	@Transactional
	void deleteProductByPrice(double price);
}
