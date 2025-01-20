package com.narwal.assignment.service.encryption.impl;

import com.narwal.assignment.service.encryption.PasswordEncryptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class BCryptPasswordEncryptionServiceImpl implements PasswordEncryptionService {

    private static final Logger logger = LoggerFactory.getLogger(BCryptPasswordEncryptionServiceImpl.class);

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public BCryptPasswordEncryptionServiceImpl(BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public String hashPassword(String password) {
        logger.info("Hashing password... (sensitive data not logged)");
        String hashedPassword = bCryptPasswordEncoder.encode(password);
        logger.info("Password successfully hashed.");
        return hashedPassword;
    }
}
