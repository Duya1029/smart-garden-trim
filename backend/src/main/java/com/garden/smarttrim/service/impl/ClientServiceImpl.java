package com.garden.smarttrim.service.impl;

import com.garden.smarttrim.common.PageResult;
import com.garden.smarttrim.common.ResultCode;
import com.garden.smarttrim.common.exception.BusinessException;
import com.garden.smarttrim.dto.ClientDTO;
import com.garden.smarttrim.entity.SysClient;
import com.garden.smarttrim.mapper.SysClientMapper;
import com.garden.smarttrim.service.ClientService;
import com.garden.smarttrim.util.SecurityUtil;
import com.garden.smarttrim.vo.ClientVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户主体服务实现
 */
@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final SysClientMapper clientMapper;

    @Override
    public PageResult<ClientVO> page(String clientName, Integer pageNum, Integer pageSize) {
        checkAdmin();
        int num = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int size = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (num - 1) * size;
        long total = clientMapper.countClientPage(clientName);
        List<ClientVO> list = clientMapper.selectClientPage(clientName, offset, size);
        return PageResult.of(total, list);
    }

    @Override
    public void create(ClientDTO dto) {
        checkAdmin();
        SysClient client = new SysClient();
        client.setClientName(dto.getClientName());
        client.setContactName(dto.getContactName());
        client.setContactPhone(dto.getContactPhone());
        client.setAddress(dto.getAddress());
        client.setUserId(dto.getUserId());
        client.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        clientMapper.insert(client);
    }

    @Override
    public void update(Long id, ClientDTO dto) {
        checkAdmin();
        if (clientMapper.selectById(id) == null) {
            throw new BusinessException("客户不存在");
        }
        SysClient client = new SysClient();
        client.setId(id);
        client.setClientName(dto.getClientName());
        client.setContactName(dto.getContactName());
        client.setContactPhone(dto.getContactPhone());
        client.setAddress(dto.getAddress());
        client.setUserId(dto.getUserId());
        client.setStatus(dto.getStatus());
        clientMapper.update(client);
    }

    @Override
    public void delete(Long id) {
        checkAdmin();
        if (clientMapper.selectById(id) == null) {
            throw new BusinessException("客户不存在");
        }
        clientMapper.deleteById(id);
    }

    private void checkAdmin() {
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
    }
}
