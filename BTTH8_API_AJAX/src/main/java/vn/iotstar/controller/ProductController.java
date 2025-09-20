package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/products")
public class ProductController {

    // Trang AJAX Product
    @GetMapping({"", "/"})
    public String index() {
        // Nếu sau này bạn có trang list SSR, có thể return "admin/products/list"
        // Tạm thời điều hướng thẳng sang trang ajax:
        return "admin/products/ajax";
    }

    // Nếu muốn URL rõ ràng hơn như Category:
    @GetMapping("/ajax")
    public String ajaxPage() {
        return "admin/products/ajax";
    }
}
