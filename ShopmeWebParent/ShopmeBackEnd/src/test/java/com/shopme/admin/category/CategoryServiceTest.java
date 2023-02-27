package com.shopme.admin.category;

import com.shopme.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTest {
    @MockBean
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService service;

    @Test
    public void testCheckUniqueDuplicateName(){
        Integer id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);

        String result = service.isCategoryUnique(id, name, alias);
        assertThat(result).isEqualTo("DuplicateName");
    }

   @Test
   public void testCheckUniqueDuplicateAlias(){
        Integer id = null;
        String name = "NameABC";
        String alias = "computers";

        Category category = new Category(id, name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = service.isCategoryUnique(id, name, alias);
        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInEditModeReturn(){
       Integer id = 1;
       String name = "Computers";
       String alias = "abc";

       Category category = new Category(2, name, alias);

       Mockito.when(categoryRepository.findByName(name)).thenReturn(category);
       Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(null);

       String result = service.isCategoryUnique(id, name, alias);
       assertThat(result).isEqualTo("DuplicateName");
    }

    @Test
    public void testCheckUniqueInEditModeReturnAlias(){
        Integer id = 1;
        String name = "ABC";
        String alias = "computers";

        Category category = new Category(2, name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = service.isCategoryUnique(id, name, alias);
        assertThat(result).isEqualTo("DuplicateAlias");
    }

    @Test
    public void testCheckUniqueReturnOk(){
        Integer id = 1;
        String name = "NameABC";
        String alias = "computers";

        Category category = new Category(1, name, alias);

        Mockito.when(categoryRepository.findByName(name)).thenReturn(null);
        Mockito.when(categoryRepository.findByAlias(alias)).thenReturn(category);

        String result = service.isCategoryUnique(id, name, alias);
        assertThat(result).isEqualTo("OK");
    }
}
