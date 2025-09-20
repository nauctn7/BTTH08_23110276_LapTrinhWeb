package vn.iotstar.service.impl;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{
	  private final ProductRepository repo;
	  @Override public <S extends Product> S save(S entity){ return repo.save(entity); }
	  @Override
	public Page<Product> findByProductNameContaining(String name, Pageable pageable) {
		return repo.findByProductNameContaining(name, pageable);
	  }
	  @Override
	public Optional<Product> findByProductName(String name) {
		return repo.findByProductName(name);
	  }
	  @Override
	public List<Product> findAll() {
		return repo.findAll();
	  }
	  @Override
	public Optional<Product> findById(Long id) {
		return repo.findById(id);
	  }
	  @Override
	public void delete(Product entity) {
		repo.delete(entity);
	  }
	  
}
