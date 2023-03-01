package com.shopme.admin.brand;

import com.shopme.admin.brand.controller.BrandNotFoundException;
import com.shopme.admin.user.UserNotFoundExcpetion;
import com.shopme.entity.Brand;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class BrandService {
    public static final int BRANDS_PER_PAGE = 4;

    @Autowired
    private BrandRepository brandRepository;

    public List<Brand> listAll() {
        return (List<Brand>) brandRepository.findAll();
    }

    public Brand save(Brand brand){
        return brandRepository.save(brand);
    }

    public Brand getBrand(Integer id ) throws BrandNotFoundException {
        try {
            return brandRepository.findById(id).get();
        }catch (NoSuchElementException e){
            throw new BrandNotFoundException("Counld not find any Brand with the id " + id);
        }
    }

    public boolean isNameUnique(String name, Integer id) {
        boolean isCreatingNew = (id == null);
        Brand brandByName = brandRepository.getBrandByName(name);
        if (brandByName == null)
            return true;
        if (isCreatingNew) {
            return brandByName == null;
        } else {
            return brandByName.getId() == id;
        }
    }

    public void deleteBrand(Integer id) throws BrandNotFoundException {
        Long brandCount = brandRepository.countById(id);
        if (brandCount == null || brandCount == 0) {
            throw new BrandNotFoundException("User not Present !!");
        }
        brandRepository.deleteById(id);
    }
}
