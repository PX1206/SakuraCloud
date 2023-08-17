package com.sakura.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.sakura.common.base.BaseEntity;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import com.sakura.common.api.Update;

/**
 * 权限表
 *
 * @author Sakura
 * @since 2023-08-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@TableName("t_permissions")
@ApiModel(value = "Permissions对象")
public class Permissions extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "id不能为空", groups = {Update.class})
    @ApiModelProperty("自增ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("父ID，顶层为0")
    private Integer parentId;

    @ApiModelProperty("权限code")
    private String code;

    @ApiModelProperty("权限名称")
    private String name;

    @ApiModelProperty("请求url")
    private String url;

    @ApiModelProperty("类型：菜单menu 按钮button")
    private String type;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("是否是公共权限： 1是 0否")
    private Integer isPublic;

    @ApiModelProperty("创建日期")
    private Date createDt;

    @ApiModelProperty("修改日期")
    private Date updateDt;

    @ApiModelProperty("状态：1正常 0删除 ")
    private Integer status;

}
