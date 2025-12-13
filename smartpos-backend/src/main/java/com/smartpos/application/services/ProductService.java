package com.smartpos.application.services;

import com.smartpos.application.dtos.*;
import com.smartpos.domain.entities.Product;
import com.smartpos.domain.entities.Supplier;
import com.smartpos.domain.entities.StockCurrent;
import com.smartpos.domain.repositories.ProductRepository;
import com.smartpos.domain.repositories.StockCurrentRepository;
import com.smartpos.domain.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final StockCurrentRepository stockCurrentRepository;
    
    public ProductDTO createProduct(CreateProductRequest request) {
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        Product product = Product.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .purchasePrice(request.getPurchasePrice())
                .salePrice(request.getSalePrice())
                .taxPercentage(request.getTaxPercentage())
                .supplier(supplier)
                .active(true)
                .build();
        
        product = productRepository.save(product);
        
        // Initialize stock current
        StockCurrent stockCurrent = StockCurrent.builder()
                .product(product)
                .quantity(0)
                .build();
        stockCurrentRepository.save(stockCurrent);
        
        return toDTO(product);
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDTO(product);
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductByCode(String code) {
        Product product = productRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return toDTO(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllActiveProducts() {
        return productRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String searchTerm) {
        return productRepository.searchActiveProducts(searchTerm).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public ProductDTO updateProduct(Long id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        if (!request.getCode().equals(product.getCode())) {
            if (productRepository.findByCode(request.getCode()).isPresent()) {
                throw new RuntimeException("Product code already exists");
            }
            product.setCode(request.getCode());
        }
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPurchasePrice(request.getPurchasePrice());
        product.setSalePrice(request.getSalePrice());
        product.setTaxPercentage(request.getTaxPercentage());
        
        if (!request.getSupplierId().equals(product.getSupplier().getId())) {
            Supplier supplier = supplierRepository.findById(request.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            product.setSupplier(supplier);
        }
        
        product = productRepository.save(product);
        return toDTO(product);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }
    
    private ProductDTO toDTO(Product product) {
        StockCurrent stock = stockCurrentRepository.findByProductId(product.getId()).orElse(null);
        return ProductDTO.builder()
                .id(product.getId())
                .code(product.getCode())
                .name(product.getName())
                .description(product.getDescription())
                .purchasePrice(product.getPurchasePrice())
                .salePrice(product.getSalePrice())
                .active(product.getActive())
                .taxPercentage(product.getTaxPercentage())
                .supplierId(product.getSupplier().getId())
                .supplierName(product.getSupplier().getName())
                .currentStock(stock != null ? stock.getQuantity() : 0)
                .build();
    }
}
