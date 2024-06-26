package com.example.demo.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedUsersResponseDto {

    private List<UserResponseDto> users;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isLastOne;

}
