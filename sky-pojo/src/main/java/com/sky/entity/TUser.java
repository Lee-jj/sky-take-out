package com.sky.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TUser implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private Long id;

    private String userName;

    private String password;

    private String phone;

    private Integer status;
}
