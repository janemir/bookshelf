package com.example.bookshelf.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailsResponse {
    private Long id;
    private String title;
    private String author;
    private String description;
    private OwnerInfo owner;
    private List<UserInfo> accessUsers;
    private List<ShelfInfo> shelves;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OwnerInfo {
        private Long id;
        private String username;
        private String email;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long id;
        private String username;
        private String email;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShelfInfo {
        private Long id;
        private String name;
    }
} 