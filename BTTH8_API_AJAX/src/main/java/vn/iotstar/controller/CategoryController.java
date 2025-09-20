package vn.iotstar.controller;

import java.util.Optional;
import java.util.UUID;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import vn.iotstar.entity.Category;
import vn.iotstar.model.CategoryModel;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IStorageService;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService service;
    private final IStorageService storageService;

    @GetMapping({"", "/"})
    public String list(ModelMap model) {
        model.addAttribute("categories", service.findAll(Sort.by("categoryName").ascending()));
        return "admin/categories/list";
    }

    @GetMapping("add")
    public String add(ModelMap model) {
        CategoryModel cateModel = new CategoryModel();
        cateModel.setIsEdit(false);
        model.addAttribute("category", cateModel);
        return "admin/categories/addOrEdit";
    }

    @GetMapping("edit/{categoryId}")
    public ModelAndView edit(ModelMap model, @PathVariable Long categoryId) {
        Category entity = service.findById(categoryId).orElse(null);
        if (entity == null) {
            model.addAttribute("message", "Category is not exist!");
            return new ModelAndView("forward:/admin/categories/searchpaginated", model);
        }
        CategoryModel catModel = new CategoryModel();
        BeanUtils.copyProperties(entity, catModel); // copy categoryId, categoryName
        catModel.setIsEdit(true);
        model.addAttribute("category", catModel);
        return new ModelAndView("admin/categories/addOrEdit", model);
    }

    // Cho phép cả multipart và urlencoded (tránh 415 nếu quên enctype)
    @PostMapping(
        value = "/saveOrUpdate",
        consumes = { MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_FORM_URLENCODED_VALUE }
    )
    public ModelAndView saveOrUpdate(
        @Valid @ModelAttribute("category") CategoryModel catModel,
        BindingResult result,
        ModelMap model) {

        if (result.hasErrors()) {
            return new ModelAndView("admin/categories/addOrEdit");
        }

        // Nếu có id -> load để giữ icon cũ nếu không upload mới
        Category entity = Optional.ofNullable(catModel.getCategoryId())
            .flatMap(service::findById)
            .orElse(new Category());

        entity.setCategoryName(catModel.getCategoryName());

        // xử lý upload icon (nếu có)
        if (catModel.getIconFile() != null && !catModel.getIconFile().isEmpty()) {
            String stored = storageService.getStorageFilename(catModel.getIconFile(), UUID.randomUUID().toString());
            storageService.store(catModel.getIconFile(), stored);
            entity.setIcon(stored);
        }
        // nếu không upload mới và đang edit -> giữ icon cũ (entity đang mang sẵn)

        service.save(entity);

        String message = Boolean.TRUE.equals(catModel.getIsEdit())
                ? "Category is Edited!"
                : "Category is saved!";
        model.addAttribute("message", message);

        return new ModelAndView("forward:/admin/categories/searchpaginated", model);
    }

    @GetMapping("delete/{categoryId}")
    public ModelAndView delete(ModelMap model, @PathVariable Long categoryId) {
        service.deleteById(categoryId);
        model.addAttribute("message", "Category is deleted!");
        return new ModelAndView("forward:/admin/categories/searchpaginated", model);
    }

    @RequestMapping(value = "searchpaginated", method = {RequestMethod.GET, RequestMethod.POST})
    public String search(@RequestParam(required = false) String name,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         ModelMap model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("categoryName").ascending());
        Page<Category> resultPage = (name == null || name.isBlank())
                ? service.findAll(pageable)
                : service.findByCategoryNameContaining(name, pageable);
        model.addAttribute("categoryPage", resultPage);
        model.addAttribute("name", name);
        return "admin/categories/searchpaging";
    }
    @GetMapping("ajax")
    public String ajaxPage() {
        return "admin/categories/ajax";
    }
}
