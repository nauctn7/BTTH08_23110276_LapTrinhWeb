package vn.iotstar.controller.api;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.model.Response;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IProductService;
import vn.iotstar.service.IStorageService;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductAPIController {

    private final IProductService productService;
    private final ICategoryService categoryService;
    private final IStorageService storageService;

    /** --------- VIEW MODEL (không sửa entity) --------- */
    private Map<String, Object> vm(Product p) {
        Map<String, Object> m = new HashMap<>();
        m.put("productId",   p.getProductId());
        m.put("productName", p.getProductName());
        m.put("quantity",    p.getQuantity());
        m.put("unitPrice",   p.getUnitPrice());
        m.put("images",      p.getImages()); // chỉ tên file; front-end ghép /uploads/<tên file>
        m.put("description", p.getDescription());
        m.put("discount",    p.getDiscount());
        m.put("createDate",  p.getCreateDate());
        m.put("status",      p.getStatus());
        m.put("categoryId",  p.getCategory() != null ? p.getCategory().getCategoryId()   : null);
        m.put("categoryName",p.getCategory() != null ? p.getCategory().getCategoryName() : null);
        return m;
    }

    /** --------- LIST: trả List<Map> trong body --------- */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> list() {
        List<Map<String,Object>> result = productService.findAll().stream().map(p -> {
            Map<String,Object> m = new HashMap<>();
            m.put("productId", p.getProductId());
            m.put("productName", p.getProductName());
            m.put("unitPrice", p.getUnitPrice());
            m.put("quantity", p.getQuantity());
            m.put("discount", p.getDiscount());
            m.put("description", p.getDescription());
            m.put("images", p.getImages());
            m.put("status", p.getStatus());
            m.put("createDate", p.getCreateDate());
            // thêm tên category
            m.put("categoryName", p.getCategory()!=null ? p.getCategory().getCategoryName() : null);
            return m;
        }).toList();
        return ResponseEntity.ok(new Response(true,"OK",result));
    }


    /** --------- GET ONE: trả Map trong body --------- */
    @GetMapping(value="/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response> get(@RequestParam Long id) {
        return productService.findById(id)
                .map(p -> ResponseEntity.ok(new Response(true, "OK", vm(p))))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new Response(false, "Không tìm thấy", null)));
    }

    /** --------- ADD --------- */
    @PostMapping(value="/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> add(@RequestParam String productName,
                                        @RequestParam double unitPrice,
                                        @RequestParam int quantity,
                                        @RequestParam double discount,
                                        @RequestParam String description,
                                        @RequestParam short status,
                                        @RequestParam Long categoryId,
                                        @RequestParam(required=false) MultipartFile imageFile) {

        if (productService.findByProductName(productName).isPresent()) {
            return ResponseEntity.badRequest().body(new Response(false, "Product đã tồn tại", null));
        }

        Product p = new Product();
        p.setProductName(productName);
        p.setUnitPrice(unitPrice);
        p.setQuantity(quantity);
        p.setDiscount(discount);
        p.setDescription(description);
        p.setStatus(status);
        p.setCreateDate(new Timestamp(System.currentTimeMillis()));

        // Category
        Optional<Category> cate = categoryService.findById(categoryId);
        if (cate.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Category không hợp lệ", null));
        }
        p.setCategory(cate.get());

        // Image upload -> lưu tên file vào p.images
        if (imageFile != null && !imageFile.isEmpty()) {
            String stored = storageService.getStorageFilename(imageFile, UUID.randomUUID().toString());
            storageService.store(imageFile, stored);
            p.setImages(stored);
        }

        productService.save(p);
        return ResponseEntity.ok(new Response(true, "Thêm thành công", vm(p)));
    }

    /** --------- UPDATE --------- */
    @PutMapping(value="/updateProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response> update(@RequestParam Long productId,
                                           @RequestParam String productName,
                                           @RequestParam double unitPrice,
                                           @RequestParam int quantity,
                                           @RequestParam double discount,
                                           @RequestParam String description,
                                           @RequestParam short status,
                                           @RequestParam Long categoryId,
                                           @RequestParam(required=false) MultipartFile imageFile) {

        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Không tìm thấy Product", null));
        }

        Product p = opt.get();
        p.setProductName(productName);
        p.setUnitPrice(unitPrice);
        p.setQuantity(quantity);
        p.setDiscount(discount);
        p.setDescription(description);
        p.setStatus(status);

        // Category
        categoryService.findById(categoryId).ifPresent(p::setCategory);

        // Image (nếu có file mới thì thay)
        if (imageFile != null && !imageFile.isEmpty()) {
            String stored = storageService.getStorageFilename(imageFile, UUID.randomUUID().toString());
            storageService.store(imageFile, stored);
            p.setImages(stored);
        }

        productService.save(p);
        return ResponseEntity.ok(new Response(true, "Cập nhật thành công", vm(p)));
    }

    /** --------- DELETE --------- */
    @DeleteMapping("/deleteProduct")
    public ResponseEntity<Response> delete(@RequestParam Long productId) {
        Optional<Product> opt = productService.findById(productId);
        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body(new Response(false, "Không tìm thấy Product", null));
        }
        productService.delete(opt.get());
        return ResponseEntity.ok(new Response(true, "Xoá thành công", vm(opt.get())));
    }
}
