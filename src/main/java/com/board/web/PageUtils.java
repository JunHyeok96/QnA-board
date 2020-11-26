package com.board.web;

import java.util.ArrayList;
import java.util.List;
import org.springframework.ui.Model;

/**
start page 1 not 0
*/
public class PageUtils {

  private static final int PAGE_SIZE = 10;

  private PageUtils() {
  }

  public static int previousPage(int cur, int size) {
    int start = cur % size == 0 ? cur - size : cur - cur % size;
    int prevPage = start - size + 1;
    return prevPage < 0 ? 1 : prevPage;
  }

  public static int nextPage(int totalPage, int currentPage, int size) {
    int totalPageStartPage = totalPage - totalPage % size;
    int currentStartPage = currentPage - currentPage % size;
    totalPageStartPage = totalPageStartPage == totalPage ? totalPageStartPage - size : totalPageStartPage;
    currentStartPage = currentStartPage == currentPage ? currentStartPage - size : currentStartPage;
    if (totalPageStartPage > currentStartPage) {
      return currentStartPage + size + 1;
    } else {
      return totalPage;
    }
  }

  public static List<Integer> getPageList(int totalPage, int cur, int size) {
    int start = previousPage(cur, size);
    start = cur > size ? start + size : start;
    int end = nextPage(totalPage, cur, size);
    end = end - start >= size ? end - 1 : end;
    List<Integer> pageList = new ArrayList<>();
    for (int i = start; i <= end; i++) {
      pageList.add(i);
    }
    return pageList;
  }

  public static Model pagingComponent(Model model, int page, int maxPage) {
    int previousPage = previousPage(page, PAGE_SIZE);
    int nextPage = nextPage(maxPage, page, PAGE_SIZE);
    model.addAttribute("currentPage", page);
    model.addAttribute("page", getPageList(maxPage, page, PAGE_SIZE));
    if (page > PAGE_SIZE) {
      model.addAttribute("previous", previousPage);
    }
    if (!(page == maxPage) && maxPage > PAGE_SIZE && nextPage <= maxPage) {
      model.addAttribute("next", nextPage);
    }
    return model;
  }
}
