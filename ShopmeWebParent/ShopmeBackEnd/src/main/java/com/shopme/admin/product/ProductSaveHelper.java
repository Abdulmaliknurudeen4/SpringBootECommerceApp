package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.entity.Product;
import com.shopme.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    public static void deleteExtraImagesWereRemovedOnForm(Product product) {
        String extraImage = "../product-images/" + product.getId() + "/extras";
        Path dirpaths = Paths.get(extraImage);

        try {
            Files.list(dirpaths).forEach(file -> {
                String filename = file.toFile().getName();
                if (!product.containsImageName(filename)) {
                    try {
                        Files.delete(file);
                        LOGGER.info("Deleted extra images " + filename);
                    } catch (IOException e) {
                        LOGGER.error("could not delete extra image" + filename);
                    }
                }
            });
        } catch (IOException ignored) {
            LOGGER.error("Could not list directory " + dirpaths);
        }
    }

    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;
        if (imageNames == null || imageNames.length == 0) return;

        Set<ProductImage> images = new HashSet<>();
        for (int count = 0; count < imageNames.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];
            images.add(new ProductImage(id, name, product));
        }
        product.setImages(images);
    }

    public static void setProductDetails(String[] detailIDs, String[] detialsNames, String[] detailValues, Product product) {
        if (detialsNames == null || detialsNames.length == 0) return;
        if (detailValues == null || detailValues.length == 0) return;

        for (int i = 0; i < detialsNames.length; i++) {
            String name = detialsNames[i];
            String value = detailValues[i];

            Integer id = Integer.valueOf(detailIDs[i]);
            if (id != 0) {
                product.addDetails(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetails(name, value);
            }
        }

    }

    public static void setMainImageName(MultipartFile mainImageMultipart, Product product) {

        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }

    public static void setNewExtraImageNames(List<MultipartFile> extraImageMultipart, Product product) {
        extraImageMultipart.forEach(multipartFile -> {
            String name = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            if (!product.containsImageName(name)) {
                product.addExtraImage(name);
            }
        });
    }

    public static void saveUploadedImages(MultipartFile main, List<MultipartFile> extras, Product product) throws IOException {
        if (!main.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(main.getOriginalFilename()));
            String uploadDir = "../product-images/" + product.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, main);
        }
        if (!extras.isEmpty()) {
            String uploadDir = "../product-images/" + product.getId() + "/extras";
            for (MultipartFile extra : extras) {
                if (extra.isEmpty()) continue;
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(extra.getOriginalFilename()));
                FileUploadUtil.saveFile(uploadDir, fileName, extra);

            }
        }
    }
}
