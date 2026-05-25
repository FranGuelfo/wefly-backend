package com.wefly.wefly.service.impl;

import com.wefly.wefly.model.dto.UserDTO;
import com.wefly.wefly.mapper.UserMapper;
import com.wefly.wefly.model.User;
import com.wefly.wefly.repository.UserRepository;
import com.wefly.wefly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    double hola;

    @Override
    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado con ID: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO update(Long id, UserDTO dto) {
        // 1. Buscamos el usuario original
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Actualizamos solo los campos permitidos (sociales)
        // No actualizamos el ID ni el password aquí por seguridad
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getBio() != null) user.setBio(dto.getBio());
        if (dto.getProfilePictureUrl() != null) user.setProfilePictureUrl(dto.getProfilePictureUrl());

        // El email suele ser el login, podrías permitir cambiarlo o no según tu lógica
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());

        // 3. Guardamos y mapeamos de vuelta a DTO
        return userMapper.toDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }
}