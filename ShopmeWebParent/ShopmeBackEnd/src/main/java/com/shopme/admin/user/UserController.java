package com.shopme.admin.user;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

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

import com.shopme.admin.FileUploadUtil;
import com.shopme.entity.User;

@Controller
public class UserController {
	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listByPage(1, model, "firstName", "asc", null);
	}

	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir, @Param("keyword") String keyword) {
		
		System.out.println("Sort Field " + sortField);
		System.out.println("Sort Order " + sortDir);
		System.out.println("Keyword " + keyword);
		
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		pageNum = (pageNum <= 0) ? 0 : pageNum;

		long startCount = (long) (pageNum - 1) * UserService.USER_PER_PAGE + 1;
		long endCount = startCount + UserService.USER_PER_PAGE - 1;

		// Getting to the last page with uneven elements
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		return "users";
	}

	@GetMapping("/users/new")
	public String newUser(Model model) {
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("editMode", false);
		model.addAttribute("user", user);
		model.addAttribute("listRoles", service.listRoles());
		model.addAttribute("pageTitle", "Create New User");
		return "user_form";
	}

	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, Model model,
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

		// Review
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully. !");
		return getRedirectURLToAffectedUser(user);
	}

	private static String getRedirectURLToAffectedUser(User user) {
		String keyPart = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + keyPart;
	}

	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		model.addAttribute("pageTitle", "Edit User (ID: " + id + " )");
		model.addAttribute("listRoles", service.listRoles());
		model.addAttribute("editMode", true);
		try {
			User user = service.getUser(id);
			model.addAttribute("user", user);
			return "user_form";
		} catch (UserNotFoundExcpetion e) {
			// Review
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}

	}

	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes,
			Model model) {
		try {
			service.deleteUser(id);
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
			@PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes, Model model) {
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
}
