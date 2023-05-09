package com.shopme.admin.customer.controller;

import com.shopme.admin.customer.CustomerNotFoundExcpetion;
import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.customer.export.CustomerCSVExporter;
import com.shopme.admin.setting.country.CountryService;
import com.shopme.entity.Customer;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CountryService countryService;


    @GetMapping("/customers")
    public String listByFirstPage(Model model) {
        return listByPage(1, model, "firstName", "asc", null);
    }

    @GetMapping("/customers/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
                             @Param("sortField") String sortField,
                             @Param("sortDir") String sortDir,
                             @Param("keyword") String keyword) {


        Page<Customer> page = customerService.listByPage(pageNum, sortField, sortDir, keyword);
        List<Customer> listCustomers = page.getContent();
        pageNum = (pageNum <= 0) ? 0 : pageNum;

        long startCount = (long) (pageNum - 1) * CustomerService.CUSTOMERS_PER_PAGE + 1;
        long endCount = startCount + CustomerService.CUSTOMERS_PER_PAGE - 1;

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
        model.addAttribute("listCustomers", listCustomers);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("keyword", keyword);
        return "customers/customers";
    }

    @PostMapping("/customers/update")
    public String updateCustomer(Customer customer, RedirectAttributes redirectAttributes) throws IOException, CustomerNotFoundExcpetion {
        Customer updatedCustomer = null;
        if (customer != null) {
            updatedCustomer = customerService.updateCustomer(customer);
        }
        // Review
        redirectAttributes.addFlashAttribute("message", "The customer has been updated successfully. !");
        return getRedirectURLToAffectedCustomer(updatedCustomer);
    }

    @GetMapping("/customers/edit/{id}")
    public String editCustomer(@PathVariable(name = "id") Integer id, Model model,
                               RedirectAttributes redirectAttributes) {
        model.addAttribute("pageTitle", "Edit Customer (ID: " + id + " )");
        try {
            Customer customer = customerService.getCustomer(id);
            model.addAttribute("customer", customer);
            model.addAttribute("listCountries", countryService.countryList());
            return "customers/customer_form";
        } catch (CustomerNotFoundExcpetion e) {
            // Review
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }

    }

    @GetMapping("/customers/delete/{id}")
    public String deleteCustomer(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("message",
                    "The Customer with the ID : " + id + " has been deleted Successfully!");
        } catch (CustomerNotFoundExcpetion e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return "redirect:/customers";
    }

    @GetMapping("/customers/setCustomerStatus/{id}/{status}")
    public String changeCustomerEnableStatus(@PathVariable(name = "id") Integer id,
                                             @PathVariable(name = "status") boolean status, RedirectAttributes redirectAttributes) {
        String enabled = (status) ? "enabled" : "disabled";
        try {
            customerService.setEnableCustomer(status, id);
            redirectAttributes.addFlashAttribute("message",
                    "The Customer with the ID : " + id + " has been " + enabled + " Successfully!");
        } catch (CustomerNotFoundExcpetion e) {
            redirectAttributes.addFlashAttribute("message",
                    "The Customer with the ID : " + id + " has been " + enabled + " Successfully!");
        }
        return "redirect:/customers";
    }


    private static String getRedirectURLToAffectedCustomer(Customer customer) {
        if (customer != null) {
            String keyPart = customer.getEmail().split("@")[0];
            return "redirect:/customers/page/1?sortField=id&sortDir=asc&keyword=" + keyPart;
        }
        return "redirect:/customers";
    }

    @GetMapping("/customers/export/csv")
    public String exportToCSV(HttpServletResponse response) throws IOException {
        List<Customer> customerList = customerService.listAll();
        CustomerCSVExporter exporter = new CustomerCSVExporter();
        exporter.export(customerList, response);
        return "redirect:/customers";
    }

    @GetMapping("/customers/detail/{id}")
    public String viewCustomerDetials(@PathVariable("id") Integer id,
                                      Model model, RedirectAttributes ra) {
        try {
            Customer customer = customerService.getCustomer(id);
            model.addAttribute("customer", customer);

            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundExcpetion e) {
            e.printStackTrace();
            ra.addFlashAttribute("message", e.getMessage());
            return "redirect:/customers";
        }

    }


}
