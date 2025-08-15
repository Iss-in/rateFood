package com.ratefood.app.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponseDTO<T> {
   private T data;
   private int totalPages;
   private int totalElements;
   private int currentPage;
}
