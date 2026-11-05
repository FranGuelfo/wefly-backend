package com.wefly.wefly.service;

import com.wefly.wefly.model.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO findById(Long id);

    UserDTO update(Long id, UserDTO dto);

    List<UserDTO> findAll();
}
