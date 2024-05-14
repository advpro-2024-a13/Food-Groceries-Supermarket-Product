package heymart.backend.controller;

import heymart.backend.models.Product;
import heymart.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<Product>> createProduct(@RequestBody Product product) {
        return productService.createProduct(product)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/delete/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteProduct(@PathVariable UUID id) {
        return productService.findByProductId(id)
                .thenCompose(existingProduct -> {
                    if (existingProduct != null) {
                        return productService.deleteProduct(id)
                                .thenApply(deleted -> ResponseEntity.ok("Product with ID " + id + " deleted."));
                    } else {
                        return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Product not found with ID " + id));
                    }
                });
    }


    @PutMapping("/edit")
    public CompletableFuture<ResponseEntity<String>> editProduct(@RequestBody Product product) {
        UUID productId = product.getProductId();
        return productService.findByProductId(productId)
                .thenCompose(existingProduct -> {
                    if (existingProduct != null) {
                        return productService.editProduct(product)
                                .thenApply(updated -> ResponseEntity.ok("Product edited successfully."));
                    } else {
                        return CompletableFuture.completedFuture(ResponseEntity.badRequest().body("Product not found with ID " + productId));
                    }
                });
    }


    @GetMapping("/findById/{id}")
    public CompletableFuture<ResponseEntity<Product>> findByProductId(@PathVariable UUID id) {
        return productService.findByProductId(id)
                .thenApply(product -> {
                    if (product != null) {
                        return ResponseEntity.ok(product);
                    } else {
                        return ResponseEntity.notFound().build();
                    }
                });
    }

    @GetMapping("/findByOwnerId/{ownerId}")
    public CompletableFuture<ResponseEntity<List<Product>>> findBySupermarketOwnerId(@PathVariable Long ownerId) {
        return productService.findBySupermarketOwnerId(ownerId)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/findAll")
    public CompletableFuture<ResponseEntity<List<Product>>> findAllProducts() {
        return productService.findAllProducts()
                .thenApply(ResponseEntity::ok);
    }
}
