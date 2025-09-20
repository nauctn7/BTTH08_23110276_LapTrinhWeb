package vn.iotstar.model;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryModel {
    private Long categoryId;

    @NotBlank(message = "Category name is required")
    private String categoryName;

    // để Controller biết đang edit hay add
    private Boolean isEdit = false;

    // file icon tải lên từ form
    private MultipartFile iconFile;
}
