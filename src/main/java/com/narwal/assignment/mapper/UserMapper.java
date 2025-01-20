package com.narwal.assignment.mapper;

import com.narwal.assignment.dto.*;
import com.narwal.assignment.entity.Address;
import com.narwal.assignment.entity.Coordinates;
import com.narwal.assignment.entity.User;
import com.narwal.assignment.service.encryption.PasswordEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private static final Logger logger = LoggerFactory.getLogger(UserMapper.class);

    private final PasswordEncryptionService encoderService;

    public UserMapper(PasswordEncryptionService encoderService) {
        this.encoderService = encoderService;
    }

    public User toEntity(DummyUserDto dto) {
        if (dto == null) {
            logger.error("User DTO cannot be null.");
            throw new IllegalArgumentException("User DTO cannot be null");
        }

        logger.info("Mapping DummyUserDto to User entity for username: {}", dto.getUsername());
        logger.debug("Mapping address for user: {}", dto.getUsername());
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAge(dto.getAge());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone());
        user.setUsername(dto.getUsername());
        user.setImage(dto.getImage());
        user.setSsn(dto.getSsn());

        logger.debug("Encrypting password for username: {}", dto.getUsername());
        user.setHashedPassword(encoderService.hashPassword(dto.getPassword()));

        user.setAddress(toEntity(dto.getAddress()));
        logger.info("Successfully mapped DummyUserDto to User entity for username: {}", dto.getUsername());
        return user;
    }

    private Address toEntity(DummyUserAddressDto dummyUserAddressDto) {
        if (dummyUserAddressDto == null) {
            logger.warn("DummyUserAddressDto is null. Returning null address entity.");
            return null;
        }

        logger.debug("Mapping DummyUserAddressDto to Address entity.");
        Address address = new Address();
        address.setAddress(dummyUserAddressDto.getAddress());
        address.setCity(dummyUserAddressDto.getCity());
        address.setState(dummyUserAddressDto.getState());
        address.setPostalCode(dummyUserAddressDto.getPostalCode());
        address.setCountry(dummyUserAddressDto.getCountry());
        address.setCoordinates(toEntity(dummyUserAddressDto.getCoordinates()));
        return address;
    }

    private Coordinates toEntity(DummyAddressCoordinatesDto coordinates) {
        if (coordinates == null) {
            logger.warn("DummyAddressCoordinatesDto is null. Returning null coordinates entity.");
            return null;
        }

        logger.debug("Mapping DummyAddressCoordinatesDto to Coordinates entity.");
        Coordinates coordinatesEntity = new Coordinates();
        coordinatesEntity.setLat(coordinates.getLat());
        coordinatesEntity.setLng(coordinates.getLng());
        return coordinatesEntity;
    }

    public UserDto toDto(User user) {
        if (user == null) {
            logger.error("User entity cannot be null.");
            throw new IllegalArgumentException("User entity cannot be null");
        }

        logger.info("Mapping User entity to UserDto for user ID: {}", user.getId());
        logger.debug("Mapping address for user ID: {}", user.getId());
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setAge(user.getAge());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setGender(user.getGender());
        userDto.setPhone(user.getPhone());
        userDto.setUsername(user.getUsername());
        userDto.setImage(user.getImage());
        userDto.setSsn(user.getSsn());
        userDto.setAddress(toDto(user.getAddress()));

        logger.info("Successfully mapped User entity to UserDto for user ID: {}", user.getId());
        return userDto;
    }

    private AddressDto toDto(Address address) {
        if (address == null) {
            logger.warn("Address entity is null. Returning null AddressDto.");
            return null;
        }

        logger.debug("Mapping Address entity to AddressDto.");
        AddressDto addressDto = new AddressDto();
        addressDto.setAddress(address.getAddress());
        addressDto.setCity(address.getCity());
        addressDto.setState(address.getState());
        addressDto.setCountry(address.getCountry());
        addressDto.setPostalCode(address.getPostalCode());
        addressDto.setCoordinates(toDto(address.getCoordinates()));
        return addressDto;
    }

    private CoordinatesDto toDto(Coordinates coordinates) {
        if (coordinates == null) {
            logger.warn("Coordinates entity is null. Returning null CoordinatesDto.");
            return null;
        }

        logger.debug("Mapping Coordinates entity to CoordinatesDto.");
        CoordinatesDto coordinatesDto = new CoordinatesDto();
        coordinatesDto.setLat(coordinates.getLat());
        coordinatesDto.setLng(coordinates.getLng());
        return coordinatesDto;
    }
}