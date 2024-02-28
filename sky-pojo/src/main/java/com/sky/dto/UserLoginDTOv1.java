package com.sky.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户登录时传递的数据模型")
public class UserLoginDTOv1 implements Serializable{
    
    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("密码")
    private String password;
}
