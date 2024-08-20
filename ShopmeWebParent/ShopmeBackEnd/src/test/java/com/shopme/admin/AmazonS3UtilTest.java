package com.shopme.admin;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AmazonS3UtilTest {
    private static final Logger log = LoggerFactory.getLogger(AmazonS3UtilTest.class);

    @Test
    public void testListFolder() {
        String folderName = "product-images/18";
        List<String> listKeys = AmazonS3Util.listFolder(folderName);
        listKeys.forEach(System.out::println);
    }

    @Test
    public void testUploadFile() throws FileNotFoundException {
        String folderName = "test-upload";
        String fileName = "JAX-WS-Tomcat.zip";
        String filePath = "E:\\Test\\" + fileName;

        InputStream inputStream = new FileInputStream(filePath);
        AmazonS3Util.uploadFile(folderName, fileName, inputStream);
    }

    @Test
    public void testDeleteFile() {
        String fileName = "test-upload/JAX-WS-Tomcat.zip";
        AmazonS3Util.deleteFile(fileName);
    }

    @Test
    public void testDeleteFolder() {
        String fileName = "test-upload/JAX-WS-Tomcat.zip";
        AmazonS3Util.removeFolder(fileName);
    }


}
