package com.controller;

import com.model.Cart;
import com.model.Product;
import com.model.ProductForm;
import com.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@SessionAttributes("cart")
public class ProductController {
    @Value("${file-upload}")
    private String fileUpload;
    @Autowired
    private IProductService productService;

    @ModelAttribute("cart")
    public Cart setupCart() {
        return new Cart();
    }

    @GetMapping("/shop")
    public ModelAndView showShop() {
        ModelAndView modelAndView = new ModelAndView("/shop3");
        modelAndView.addObject("products", productService.findAll());
        return modelAndView;
    }

    @GetMapping("/add/{id}")
    public String addToCart(@PathVariable Long id, @ModelAttribute Cart cart, @RequestParam("action") String action) {
        Optional<Product> productOptional = productService.findById(id);
        if (!productOptional.isPresent()) {
            return "/error.404";
        }
        if (action.equals("show")) {
            cart.addProduct(productOptional.get());
            return "redirect:/shopping-cart";
        }
        cart.addProduct(productOptional.get());
        return "redirect:/shop";
    }
    @GetMapping("/remove/{id}")
    public String removeToCart(@PathVariable Long id, @ModelAttribute Cart cart, @RequestParam("action") String action){
        Optional<Product> product = productService.findById(id);
        if (!product.isPresent()){
            return "/error.404";
        }
        if (action.equals("show")){
            cart.removeProduct(product.get());
            return "redirect:/shopping-cart";
        }
        cart.remove(product.get());
        return "redirect:/shopping-cart";
    }
    @GetMapping("/view/{id}")
    public ModelAndView viewProduct(@PathVariable Long id){
        Optional<Product> product = productService.findById(id);
        ModelAndView modelAndView = new ModelAndView("/product/view");
        modelAndView.addObject("product", product.get());
        return modelAndView;
    }
    @GetMapping("/product/create")
    public ModelAndView formCreate(){
        ModelAndView modelAndView = new ModelAndView("/product/create");
        modelAndView.addObject("productF", new ProductForm());
        return modelAndView;
    }
    @PostMapping("/product/create")
    public String create(@ModelAttribute ProductForm productForm){
        MultipartFile multipartFile = productForm.getImage();
        String file = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(multipartFile.getBytes(), new File(fileUpload + file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Product product = new Product(productForm.getName(), productForm.getPrice(), file, productForm.getDescription());
        productService.save(product);
        return "redirect:/list";
    }
    @GetMapping("/list")
    public ModelAndView getListProduct(){
        ModelAndView modelAndView = new ModelAndView("/product/list");
        modelAndView.addObject("productList", productService.findAll());
        return modelAndView;
    }
}
