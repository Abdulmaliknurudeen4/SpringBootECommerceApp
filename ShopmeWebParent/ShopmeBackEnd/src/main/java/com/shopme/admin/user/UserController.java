package com.shopme.admin.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.entity.User;

@Controller
public class UserController {
	@Autowired
	private UserService service;

	@GetMapping("/users")
	public String listAll(Model model) {
		List<User> listUsers = service.listAll();
		model.addAttribute("listUsers", listUsers);
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
	public String saveUser(User user, RedirectAttributes redirectAttributes) {
		System.out.println(user);
		service.save(user);
		// Review
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully. !");
		return "redirect:/users";
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
