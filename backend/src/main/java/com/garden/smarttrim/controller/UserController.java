package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.UserDTO;
import com.garden.smarttrim.service.UserService;
import com.garden.smarttrim.vo.ClientVO;
import com.garden.smarttrim.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 账号管理模块 /users（管理员）
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 账号分页查询 */
    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(@RequestParam(required = false) Integer pageNum,
                                           @RequestParam(required = false) Integer pageSize,
                                           @RequestParam(required = false) String username,
                                           @RequestParam(required = false) String realName,
                                           @RequestParam(required = false) String role,
                                           @RequestParam(required = false) Long deptId,
                                           @RequestParam(required = false) Integer status) {
        return Result.ok(userService.page(username, realName, role, deptId, status, pageNum, pageSize));
    }

    /** 客户下拉列表 */
    @GetMapping("/clients")
    public Result<List<ClientVO>> clients() {
        return Result.ok(userService.clientOptions());
    }

    /** 账号详情 */
    @GetMapping("/{id}")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.ok(userService.detail(id));
    }

    /** 新增账号 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody UserDTO dto) {
        userService.create(dto);
        return Result.ok();
    }

    /** 修改账号 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserDTO dto) {
        userService.update(id, dto);
        return Result.ok();
    }

    /** 删除账号 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    /** 启用/停用 */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        userService.updateStatus(id, status);
        return Result.ok();
    }
}
