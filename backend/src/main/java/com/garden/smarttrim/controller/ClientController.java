package com.garden.smarttrim.controller;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.Result;
import com.garden.smarttrim.dto.ClientDTO;
import com.garden.smarttrim.service.ClientService;
import com.garden.smarttrim.vo.ClientVO;
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

/**
 * 客户主体模块 /clients（管理员）
 */
@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    /** 客户分页查询 */
    @GetMapping("/page")
    public Result<PageResult<ClientVO>> page(@RequestParam(required = false) Integer pageNum,
                                             @RequestParam(required = false) Integer pageSize,
                                             @RequestParam(required = false) String clientName) {
        return Result.ok(clientService.page(clientName, pageNum, pageSize));
    }

    /** 新增客户 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody ClientDTO dto) {
        clientService.create(dto);
        return Result.ok();
    }

    /** 修改客户 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody ClientDTO dto) {
        clientService.update(id, dto);
        return Result.ok();
    }

    /** 删除客户 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return Result.ok();
    }
}
