package vn.iotstar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Tìm 1 bản ghi theo tên (đúng field trong entity)
    Optional<Category> findByCategoryName(String name);

    // Tìm kiếm danh sách theo tên (contains)
    List<Category> findByCategoryNameContaining(String name);

    // Tìm kiếm + phân trang
    Page<Category> findByCategoryNameContaining(String name, Pageable pageable);
}
