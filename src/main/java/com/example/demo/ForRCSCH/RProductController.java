package com.example.demo.ForRCSCH;

import com.example.demo.Entity.Product;
import com.example.demo.Entity.User;
import com.example.demo.ForRCSCH.AuxiliaryClasses.StaticMethods;
import com.example.demo.ForRCSCH.DTO.ProductDTO;
import com.example.demo.ForRCSCH.DTO.UserDTO;
import com.example.demo.Services.ProductService;
import com.example.demo.Services.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/RP")
@Api(tags = "Product")
public class RProductController {

    @Autowired
    ProductService productService;


    @ApiOperation(value = "Получение информации обо всех продуктах")
    @GetMapping("/getProducts")
    public List<ProductDTO> getProducts(){
        return ProductDTO.createListProductDTO(productService.findAll());
    }

    @ApiOperation(value = "Получение информации об одной продукции по id")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Product with this id doesn't exist")
    })
    @GetMapping("/getProduct/{id}")
    public ProductDTO getProductById(
            @ApiParam(type = "String",
                    value = "id",
                    example = "5",
                    required = true)
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response){
        ProductDTO productDTO = ProductDTO.createProductDTO(productService.findById(id));
        if(productDTO == null)
            StaticMethods.createResponse(request, response, 400, "Product with this id doesn't exist");
        return productDTO;
    }


    @ApiOperation(value = "Добавление продукции")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Created product with id = :id"),
            @ApiResponse(code = 400, message = "Incorrect JSON\n")
    })
    @PostMapping("/addProduct")
    public void addProduct(
            @ApiParam(type = "String",
                    value = "Цена, тип продука, кол-во продукта",
                    example = "{\n\"price\":\"5000\", " +
                            "\n\"typeProduct\":\"Шоколадка\", " +
                            "\n\"amount\": \"30\"}",
                    required = true)
            @RequestBody String body,
            HttpServletRequest request,
            HttpServletResponse response){

        String price = StaticMethods.parsingJson(body, "price", request, response);
        String typeProduct = StaticMethods.parsingJson(body, "typeProduct", request, response);
        String amount = StaticMethods.parsingJson(body, "amount", request, response);
        if(price == null || typeProduct == null || amount == null )
            return;
        Product product = new Product();
        product.setAmount(Integer.parseInt(amount));
        product.setPrice(Long.parseLong(price));
        product.setTypeProduct(typeProduct);

        product = productService.save(product);

        StaticMethods.createResponse(request, response, 201, "Created product with id = " + product.getId());
    }

    @ApiOperation(value = "Редактирование данных продукции")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 201, message = "Updated product with id = :id"),
            @ApiResponse(code = 400, message = "Incorrect JSON\n" +
                    "Product with this id doesn't exist")
    })
    @PutMapping("/editProduct/{id}")
    public void editProduct(
            @ApiParam(type = "String",
                    value = "Цена, тип продука, кол-во продукта",
                    example = "{\n\"price\":\"5000\", " +
                            "\n\"typeProduct\":\"Шоколадка\", " +
                            "\n\"amount\": \"30\"}",
                    required = true)
            @RequestBody String body,
            @ApiParam(type = "String",
                    value = "id",
                    example = "5",
                    required = true)
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response){
        Product product = productService.findById(id);
        if(product == null) {
            StaticMethods.createResponse(request, response, 400, "Product with this id doesn't exist");
            return;
        }
        String price = StaticMethods.parsingJson(body, "price", request, response);
        String typeProduct = StaticMethods.parsingJson(body, "typeProduct", request, response);
        String amount = StaticMethods.parsingJson(body, "amount", request, response);
        if(price == null || typeProduct == null || amount == null )
            return;

        product.setAmount(Integer.parseInt(amount));
        product.setPrice(Long.parseLong(price));
        product.setTypeProduct(typeProduct);

        product = productService.save(product);

        StaticMethods.createResponse(request, response, 201, "Updated product with id = " + product.getId());
    }


    @ApiOperation(value = "Удаление продукции")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "---"),
            @ApiResponse(code = 204, message = "Product have deleted"),
            @ApiResponse(code = 400, message = "Product with this id doesn't exist")
    })
    @DeleteMapping("/deleteProduct/{id}")
    public void deleteUser(
            @ApiParam(type = "String",
                    value = "id",
                    example = "5",
                    required = true)
            @PathVariable("id") String id,
            HttpServletRequest request,
            HttpServletResponse response){
        Product product = productService.findById(id);
        if(product == null) {
            StaticMethods.createResponse(request, response, 400, "product with this id doesn't exist");
            return;
        }
        productService.deleteOneAmountOfProduct(product);
        StaticMethods.createResponse(request, response, 204, "product have deleted");
    }

}
