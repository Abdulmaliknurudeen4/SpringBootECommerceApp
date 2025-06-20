package com.shopme.admin.user.controller;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.paging.PagingAndSortingParam;
import com.shopme.admin.user.UserNotFoundExcpetion;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCSVExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    private static String getRedirectURLToAffectedUser(User user) {
        String keyPart = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + keyPart;
    }

    @GetMapping("/users")
    public String listFirstPage() {
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=";
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PagingAndSortingParam(listName = "listUsers", moduleURL = "/users", contextDisplay = "user(s)") PagingAndSortingHelper helper,
                             @PathVariable(name = "pageNum") int pageNum) {

        service.listByPage(pageNum, helper);
        return "users/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        User user = new User();
        user.setEnabled(true);
        model.addAttribute("editMode", false);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", service.listRoles());
        model.addAttribute("pageTitle", "Create New User");
        model.addAttribute("moduleURL", "users");
        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes,
                           @RequestParam(name = "image") MultipartFile photoMultipart) throws IOException {
        if (!photoMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(photoMultipart.getOriginalFilename());
            user.setPhotos(fileName);
            System.out.println(user.getPhotos());
            User savedUser = service.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

           /* //Amazon Code
            AmazonS3Util.removeFolder(uploadDir);
            AmazonS3Util.uploadFile(uploadDir, fileName, photoMultipart.getInputStream());*/

        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            service.save(user);
        }

        // Review
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully. !");
        return getRedirectURLToAffectedUser(user);
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("pageTitle", "Edit User (ID: " + id + " )");
        model.addAttribute("listRoles", service.listRoles());
        model.addAttribute("editMode", true);
        try {
            User user = service.getUser(id);
            model.addAttribute("user", user);
            model.addAttribute("moduleURL", "/users");
            return "users/user_form";
        } catch (UserNotFoundExcpetion e) {
            // Review
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/users";
        }

    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            service.deleteUser(id);

           /* //Amazon s3
            String photoDir = "user-photos/"+id;
            AmazonS3Util.removeFolder(photoDir);*/

            String uploadDir = "user-photos/" + ((id != null && id != 0) ? id : 0);
            FileUploadUtil.removeDir(uploadDir);
            redirectAttributes.addFlashAttribute("message",
                    "The User with the ID : " + id + " has been deleted Successfully!");
        } catch (UserNotFoundExcpetion e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/setUserStatus/{id}/{status}")
    public String changeUserEnableStatus(@PathVariable(name = "id") Integer id,
                                         @PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
        String enabled = (status) ? "enabled" : "disabled";
        try {
            service.setEnableUser(status, id);
            redirectAttributes.addFlashAttribute("message",
                    "The User with the ID : " + id + " has been " + enabled + " Successfully!");
        } catch (UserNotFoundExcpetion e) {
            // TODO Auto-generated catch block
            redirectAttributes.addFlashAttribute("message",
                    "The User with the ID : " + id + " has been " + enabled + " Successfully!");
        }
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public String exportToCSV(HttpServletResponse response) throws IOException {
        List<User> userList = service.listAll();
        UserCSVExporter exporter = new UserCSVExporter();
        exporter.export(userList, response);
        return "redirect:/users";
    }

    @GetMapping("/users/export/pdf")
    public String exportToPDF(HttpServletResponse response) throws IOException {
        List<User> userList = service.listAll();
        UserPdfExporter exporter = new UserPdfExporter();
        exporter.export(userList, response);
        return "redirect:/users";
    }

    @GetMapping("/users/export/excel")
    public String exportToExcel(HttpServletResponse response) throws IOException {
        List<User> userList = service.listAll();
        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(userList, response);
        return "redirect:/users";
    }
}
