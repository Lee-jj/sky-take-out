package com.sky.vo;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "用户登录返回的数据格式")
public class UserLoginVOv1 implements Serializable{
    
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("jwt令牌")
    private String token;
}
