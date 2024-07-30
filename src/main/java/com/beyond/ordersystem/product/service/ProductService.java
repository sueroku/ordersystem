package com.beyond.ordersystem.product.service;

import com.beyond.ordersystem.product.domain.Product;
import com.beyond.ordersystem.product.dto.ProductListResDto;
import com.beyond.ordersystem.product.dto.ProductSaveReqDto;
import com.beyond.ordersystem.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product productCreate(ProductSaveReqDto dto){
        MultipartFile image = dto.getProductImage();
        Product product;
        try{
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp",
                    product.getId()+ "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());
        }catch (IOException e){ // 트라이-캐치 때문에 트랜잭션 처리때문에
            throw new RuntimeException("이미지 저장 실패"); // 여기서 예외를 던져용
        }
        return product;
    }

    @Transactional
    public Product productAwsCreate(ProductSaveReqDto dto){
        MultipartFile image = dto.getProductImage();
        Product product;
        try{
            product = productRepository.save(dto.toEntity());
            byte[] bytes = image.getBytes();
            Path path = Paths.get("C:/Users/Playdata/Desktop/tmp",
                    product.getId()+ "_" + image.getOriginalFilename());
            Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            product.updateImagePath(path.toString());
        }catch (IOException e){ // 트라이-캐치 때문에 트랜잭션 처리때문에
            throw new RuntimeException("이미지 저장 실패"); // 여기서 예외를 던져용
        }
        return product;
    }



    public Page<ProductListResDto> productList(Pageable pageable){
        Page<Product> products = productRepository.findAll(pageable);
        return products.map(a->a.listFromEntity());
    }



}





//
//@Transactional
//public Product productCreate(ProductSaveReqDto dto){
//    MultipartFile image = dto.getProductImage();
//    Product product;
//    try{
//        byte[] bytes = image.getBytes();
//        Path path = Paths.get("C:/Users/Playdata/Desktop/tmp",
//                UUID.randomUUID()+ "_" + image.getOriginalFilename()); // 같은이름의 파일은 어째.. 그래서 유효값 붙이기 UUID말고 상품 id 붙이려 하면..?
//        Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
//        product = productRepository.save(dto.toEntity(path.toString()));
//    }catch (IOException e){ // 트라이-캐치 때문에 트랜잭션 처리때문에
//        throw new RuntimeException("이미지 저장 실패"); // 여기서 예외를 던져용
//    }
//    String imagePath = dto.getProductImage().getName(); // 내가한게 남았네
//    return product;
//}
