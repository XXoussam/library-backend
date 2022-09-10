package com.gkemayo.library.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest/category/api")
@Api(value = "Book Category Rest Controller")
public class CategoryRestController {

    @Autowired
    private CategoryServiceImpl categoryService;

    @GetMapping("/allCategories")
    @ApiOperation(value = "List all book categories of the Library", response = List.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Ok: successfully listed"),
            @ApiResponse(code = 204, message = "No Content: no result founded"),
    })
    public ResponseEntity<List<CategoryDTO>> getAllBookCategories(){
        System.out.println("fetch categories");
        List<Category> categories = categoryService.getAllCategories();
        if (!CollectionUtils.isEmpty(categories)){
            categories.removeAll(Collections.singleton(null));
            List<CategoryDTO> categoryDTOs = categories.stream().map(category -> {
                return mapCategoryToCategoryDTO(category);
            }).collect(Collectors.toList());
            ResponseEntity response = new ResponseEntity<List<CategoryDTO>>(categoryDTOs, HttpStatus.OK);
            System.out.println(response);
            return response;
        }
        return new ResponseEntity<List<CategoryDTO>>(HttpStatus.NO_CONTENT);
    }

    private CategoryDTO mapCategoryToCategoryDTO(Category category) {
        ModelMapper mapper = new ModelMapper();
        CategoryDTO categoryDTO = mapper.map(category, CategoryDTO.class);
        return categoryDTO;
    }
}
