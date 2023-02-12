package com.example.majorproject;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String userName;
    private int age;
    private String mobile;
    private String email;
    private String name;
}
