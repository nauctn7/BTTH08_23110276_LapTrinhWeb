package vn.iotstar.service.impl;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.service.ICategoryService;

@Service
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // SỬA DỨT ĐIỂM: không gọi findById(null)
    @Override
    public <S extends Category> S save(S entity) {
        // Thêm mới
        if (entity.getCategoryId() == null) {
            return categoryRepository.save(entity);
        }
        // Cập nhật: giữ icon cũ nếu icon mới rỗng
        Optional<Category> opt = findById(entity.getCategoryId());
        if (opt.isPresent()) {
            if (StringUtils.isBlank(entity.getIcon())) {
                entity.setIcon(opt.get().getIcon());
            }
        }
        return categoryRepository.save(entity);
    }

    @Override
    public Optional<Category> findByCategoryName(String name) {
        return categoryRepository.findByCategoryName(name);
    }

    @Override
    public List<Category> findByCategoryNameContaining(String name) {
        return categoryRepository.findByCategoryNameContaining(name);
    }

    @Override
    public Page<Category> findByCategoryNameContaining(String name, Pageable pageable) {
        return categoryRepository.findByCategoryNameContaining(name, pageable);
    }

    // Giữ tương thích với code cũ: map sang field mới
    @Override
    public Page<Category> findByNameContaining(String name, Pageable pageable) {
        if (StringUtils.isBlank(name)) {
            return categoryRepository.findAll(pageable);
        }
        return categoryRepository.findByCategoryNameContaining(name, pageable);
    }

    @Override
    public <S extends Category> Optional<S> findOne(Example<S> example) {
        return categoryRepository.findOne(example);
    }

    @Override
    public List<Category> findAll(Sort sort) {
        return categoryRepository.findAll(sort);
    }

    @Override
    public Page<Category> findAll(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Category> findAllById(Iterable<Long> ids) {
        return categoryRepository.findAllById(ids);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    @Override
    public long count() {
        return categoryRepository.count();
    }

    @Override
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public void delete(Category entity) {
        categoryRepository.delete(entity);
    }
}
