package vn.iotstar.controller.api;

import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryAPIController {

  private final ICategoryService categoryService;
  private final IStorageService storageService;

  // === ĐÚNG THEO TÀI LIỆU: trả về MẢNG JSON (không bọc Response) ===
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Category> listRaw() {
    return categoryService.findAll();
  }

  // (giữ lại nếu bạn cần search / get by id bằng API kiểu bọc Response)
  @GetMapping(value="/get", produces = MediaType.APPLICATION_JSON_VALUE)
  public Response get(@RequestParam Long id){
    return new Response(true, "OK", categoryService.findById(id).orElse(null));
  }

  // === ADD (multipart) ===
  @PostMapping(value="/addCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Response> add(@RequestParam String categoryName,
                                      @RequestParam(required=false) MultipartFile icon){
    if (categoryService.findByCategoryName(categoryName).isPresent()){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new Response(false,"Category đã tồn tại", null));
    }
    Category c = new Category();
    c.setCategoryName(categoryName);

    if (icon != null && !icon.isEmpty()){
      String stored = storageService.getStorageFilename(icon, UUID.randomUUID().toString());
      storageService.store(icon, stored);
      c.setIcon(stored);
    }
    categoryService.save(c);
    return ResponseEntity.ok(new Response(true,"Thành công", c));
  }

  // === UPDATE (multipart) ===
  @PutMapping(value="/updateCategory", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Response> update(@RequestParam Long categoryId,
                                         @RequestParam String categoryName,
                                         @RequestParam(required=false) MultipartFile icon){
    var opt = categoryService.findById(categoryId);
    if (opt.isEmpty()){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new Response(false,"Không tìm thấy Category", null));
    }
    Category c = opt.get();
    c.setCategoryName(categoryName);

    if (icon != null && !icon.isEmpty()){
      String stored = storageService.getStorageFilename(icon, UUID.randomUUID().toString());
      storageService.store(icon, stored);
      c.setIcon(stored);
    }
    categoryService.save(c);
    return ResponseEntity.ok(new Response(true,"Cập nhật thành công", c));
  }

  // === DELETE ===
  @DeleteMapping("/deleteCategory")
  public ResponseEntity<Response> delete(@RequestParam Long categoryId){
    var opt = categoryService.findById(categoryId);
    if (opt.isEmpty()){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body(new Response(false,"Không tìm thấy Category", null));
    }
    categoryService.delete(opt.get());
    return ResponseEntity.ok(new Response(true,"Xoá thành công", opt.get()));
  }
}
