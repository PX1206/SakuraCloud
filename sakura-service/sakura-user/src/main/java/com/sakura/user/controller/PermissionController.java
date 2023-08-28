package com.sakura.user.controller;

import com.sakura.user.param.PermissionParam;
import com.sakura.user.service.PermissionService;
import com.sakura.user.vo.PermissionTreeVo;
import com.sakura.user.vo.PermissionVo;
import lombok.extern.slf4j.Slf4j;
import com.sakura.common.base.BaseController;
import com.sakura.common.api.ApiResult;
import com.sakura.common.log.Module;
import com.sakura.common.log.OperationLog;
import com.sakura.common.enums.OperationLogType;
import com.sakura.common.api.Add;
import com.sakura.common.api.Update;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限表 控制器
 *
 * @author Sakura
 * @since 2023-08-17
 */
@Slf4j
@RestController
@RequestMapping("/permission")
@Module("user")
@Api(value = "权限管理", tags = {"权限管理"})
public class PermissionController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 添加权限表
     */
    @PostMapping("/add")
    @OperationLog(name = "添加权限", type = OperationLogType.ADD)
    @ApiOperation(value = "添加权限", response = ApiResult.class)
    public ApiResult<Boolean> addPermission(@Validated(Add.class) @RequestBody PermissionParam permissionParam) throws Exception {
        boolean flag = permissionService.savePermission(permissionParam);
        return ApiResult.result(flag);
    }

    /**
     * 修改权限表
     */
    @PostMapping("/update")
    @OperationLog(name = "修改权限", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改权限", response = ApiResult.class)
    public ApiResult<Boolean> updatePermission(@Validated(Update.class) @RequestBody PermissionParam permissionParam) throws Exception {
        boolean flag = permissionService.updatePermission(permissionParam);
        return ApiResult.result(flag);
    }

    /**
     * 删除权限表
     */
    @PostMapping("/delete/{id}")
    @OperationLog(name = "删除权限", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除权限", response = ApiResult.class)
    public ApiResult<Boolean> deletePermission(@PathVariable("id") Long id) throws Exception {
        boolean flag = permissionService.deletePermission(id);
        return ApiResult.result(flag);
    }

    /**
     * 获取权限表详情
     */
    @GetMapping("/info/{id}")
    @OperationLog(name = "权限详情", type = OperationLogType.INFO)
    @ApiOperation(value = "权限详情", response = PermissionVo.class)
    public ApiResult<PermissionVo> getPermission(@PathVariable("id") Long id) throws Exception {
        PermissionVo permissionVo = permissionService.getPermission(id);
        return ApiResult.ok(permissionVo);
    }

    /**
     * 权限表分页列表
     */
    @GetMapping("/tree/{parentId}")
    @OperationLog(name = "权限树", type = OperationLogType.QUERY)
    @ApiOperation(value = "权限树 顶层parentId为0", response = PermissionTreeVo.class)
    public ApiResult<List<PermissionTreeVo>> getPermissionTree(@PathVariable("parentId") Integer parentId) throws Exception {
        List<PermissionTreeVo> permissionTreeVos = permissionService.getPermissionTree(parentId);
        return ApiResult.ok(permissionTreeVos);
    }

}

