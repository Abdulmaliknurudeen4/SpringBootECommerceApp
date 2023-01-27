package com.shopme.admin.user.controller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserService;
import com.shopme.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService service;

    @GetMapping("/account")
    public String viewDetials(@AuthenticationPrincipal
                                  ShopmeUserDetails loggedUser, Model model){
        String userMail = loggedUser.getUsername();
        User loggedInUser = service.getUser(userMail);
        model.addAttribute("user", loggedInUser);
        return "users/account_form";
    }
    @PostMapping("/account/update")
    public String saveUser(User user, RedirectAttributes redirectAttributes, Model model,
                           @AuthenticationPrincipal
                           ShopmeUserDetails loggedUser,
                           @RequestParam(name = "image") MultipartFile photoMultipart) throws IOException {
        if (!photoMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(photoMultipart.getOriginalFilename());
            user.setPhotos(fileName);
            System.out.println(user.getPhotos());
            User savedUser = service.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, photoMultipart);

        } else {
            if (user.getPhotos().isEmpty())
                user.setPhotos(null);
            service.save(user);
        }

        loggedUser.setFirstName(user.getFirstName());
        loggedUser.setlastName(user.getLastName());

        // Review
        redirectAttributes.addFlashAttribute("message", "Your account Details has been update successfully.");
        return getRedirectURLToAffectedUser(user);
    }
    private static String getRedirectURLToAffectedUser(User user) {
        String keyPart = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + keyPart;
    }
}
