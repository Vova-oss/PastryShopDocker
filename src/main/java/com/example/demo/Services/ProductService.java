package com.example.demo.Services;

import com.example.demo.Entity.Basket;
import com.example.demo.Entity.Product;
import com.example.demo.Repository.ProductRepository;
import org.apache.tomcat.jni.Library;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductService {

    @Value("${upload.path.without.images}")
    String uploadPathWithoutImages;

    @Autowired
    ProductRepository productRepository;
    @Autowired
    BasketService basketService;

    @Transactional
    public List<Product> findAll(){
        return (List<Product>) productRepository.findAll();
    }

    @Transactional
    public List<Product> findAllSorted(){
        List<Product> product = findAll();
        List<Product> newList = new ArrayList<>();
        Stream<Product> stream = product.stream();
        stream.sorted((o1, o2) -> o1.getTypeProduct().compareTo(o2.getTypeProduct()))
                .forEach(newList::add);
        return newList;
    }

    public List<Product> findAllByUserIdWithNull(long id){
        List<Basket> basketList= basketService.getAllByUserId(id);

        List<Product> productList = findAllSorted();
        for(Product product: productList){
            for(Basket basket: basketList){
                if(basket.getNameOfProduct().equals(product.getTypeProduct())){
                    product.setAmount(product.getAmount() - basket.getAmount());
                }
            }
        }

        return productList;
    }

    @Transactional
    public Product findById(String id){
        return productRepository.findById(Long.parseLong(id));
    }

    @Transactional
    public boolean deleteOneAmountOfProduct(Product product){
        if(productRepository.findById(product.getId()).getAmount()>0) {
            product.setAmount(product.getAmount() - 1);
            productRepository.save(product);
            return true;
        }else return false;
    }

    @Transactional
    public Product findByTypeProduct(String typeProduct){
        return productRepository.findByTypeProduct(typeProduct);
    }

    @Transactional
    public void addOneAmountOfProduct(Product product) {
        product.setAmount(product.getAmount()+1);
        productRepository.save(product);
    }

    @Transactional
    public void addSomeAmountOfProductByName(String nameOfProduct, String amount) {
        Product product = findByTypeProduct(nameOfProduct);
        product.setAmount(product.getAmount()+ Integer.parseInt(amount));
        productRepository.save(product);
    }

    @Transactional
    public void deleteSomeAmountOfProductByName(String nameOfProduct, String amount){
        Product product = findByTypeProduct(nameOfProduct);
        int a = Integer.parseInt(amount);
        product.setAmount(product.getAmount()-Integer.parseInt(amount));
        if(product.getAmount()<0){
            product.setAmount(0);
        }
        productRepository.save(product);
    }

    public void eraseProductByName(String nameOfProduct) {
        Product product = findByTypeProduct(nameOfProduct);
        File file = new File(System.getProperty("user.dir").replace('\\','/') + "static/" + product.getPathOfPicture());
        file.delete();
        productRepository.delete(product);
    }

    public void addNewProduct(String name, String price, String amount, String pathOfPicture) {

        Product product = new Product();
        product.setTypeProduct(name);
        product.setPrice(Long.parseLong(price));
        product.setAmount(Integer.parseInt(amount));
        product.setPathOfPicture("/images/"+pathOfPicture);
        productRepository.save(product);

    }

    public void changePriceOfProduct(String nameOfProduct, String newPrice) {
        Product product = productRepository.findByTypeProduct(nameOfProduct);
        product.setPrice(Long.parseLong(newPrice));
        productRepository.save(product);

        basketService.changePriceOfProduct(nameOfProduct, newPrice);

    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(Product product) {
        productRepository.delete(product);
    }
}
