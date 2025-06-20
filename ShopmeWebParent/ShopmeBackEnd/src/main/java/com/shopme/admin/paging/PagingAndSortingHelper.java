package com.shopme.admin.paging;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

public class PagingAndSortingHelper {
    private final ModelAndViewContainer model;
    private final String listName;

    @Getter
    private final String sortField;
    @Getter
    private final String keyword;
    @Getter
    private final String sortDir;

    public PagingAndSortingHelper(ModelAndViewContainer model,
                                  String listName,
                                  String sortField, String sortDir,
                                  String keyword) {
        this.model = model;
        this.listName = listName;
        this.sortField = sortField;
        this.sortDir = sortDir;
        this.keyword = keyword;
    }

    public void udpateModelAttributes(int pageNum, Page<?> page) {
        List<?> listItems = page.getContent();
        int pageSize = page.getSize();
        pageNum = Math.max(pageNum, 0);

        long startCount = (long) (pageNum - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;

        // Getting to the last page with uneven elements
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute(listName, listItems);
    }


    public void listEntities(int pageNum, int pageSize, SearchRepository<?, Integer> repo) {
        System.out.println(sortField);

        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, sort);

        Page<?> page = null;
        if (keyword != null) {
            page = repo.findAll(keyword, pageable);
        }else {
            page = repo.findAll(pageable);
        }
        udpateModelAttributes(pageNum, page);
    }

    public Pageable createPageable(int pagesize, int pageNum) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        return PageRequest.of(pageNum - 1, pagesize, sort);
    }
}
