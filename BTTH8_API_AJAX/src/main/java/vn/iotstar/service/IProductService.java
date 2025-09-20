package vn.iotstar.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Product;

public interface IProductService {

	void delete(Product entity);

	Optional<Product> findById(Long id);

	List<Product> findAll();

	Optional<Product> findByProductName(String name);

	Page<Product> findByProductNameContaining(String name, Pageable pageable);

	<S extends Product> S save(S entity);

}
