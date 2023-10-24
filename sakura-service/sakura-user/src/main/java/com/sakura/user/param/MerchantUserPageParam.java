package com.sakura.user.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.sakura.common.pagination.BasePageOrderParam;

/**
 * <pre>
 * 商户用户表 分页参数对象
 * </pre>
 *
 * @author Sakura
 * @date 2023-09-26
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "商户用户表分页参数")
public class MerchantUserPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("姓名")
    private String name;

    @ApiModelProperty("性别：1男 2女")
    private Integer sex;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("角色id")
    private Integer roleId;

    @ApiModelProperty("状态：0注销 1正常 2冻结 3临时冻结")
    private Integer status;
}
