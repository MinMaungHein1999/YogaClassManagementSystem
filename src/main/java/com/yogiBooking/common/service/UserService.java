package com.yogiBooking.common.service;

import com.yogiBooking.common.dto.ForgotPasswordRequest;
import com.yogiBooking.common.dto.OtpVerificationRequest;
import com.yogiBooking.common.dto.SearchRequest;
import com.yogiBooking.common.dto.auth.ChangePasswordRequest;
import com.yogiBooking.common.dto.auth.ResetPasswordRequest;
import com.yogiBooking.common.dto.user.UserCreateDTO;
import com.yogiBooking.common.dto.user.UserDTO;
import com.yogiBooking.common.entity.Role;
import com.yogiBooking.common.entity.User;
import com.yogiBooking.common.entity.constants.Status;
import com.yogiBooking.common.exception.OtpInvalidateException;
import com.yogiBooking.common.exception.ResourceAlreadyExistsException;
import com.yogiBooking.common.exception.ResourceNotFoundException;
import com.yogiBooking.common.mapper.GenericMapper;
import com.yogiBooking.common.repository.RoleRepository;
import com.yogiBooking.common.repository.UserRepository;
import com.yogiBooking.common.dto.UserRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GenericMapper mapper;
    private final GenericSearchService genericSearchService;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EntityManager entityManager;

    @Transactional(readOnly=true)
    public List<UserDTO> getAllUser() {
        List<User> users = userRepository.findAll();
        return mapper.convertList(users, UserDTO.class);
    }

    public void activateUser(OtpVerificationRequest request){
        if(otpService.isOtpInvalid(request.getEmail(), request.getOtp())) {
            throw new OtpInvalidateException("Invalid OTP");
        }
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with %s not found."
                        .formatted(request.getEmail())));
        user.setConfirmedAt(LocalDateTime.now());
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID %d not found.".formatted(id)));

        return this.mapper.convert(user, UserDTO.class);
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        Role role = roleRepository.findById(userCreateDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID %d not found."
                        .formatted(userCreateDTO.getRoleId())));

        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new ResourceAlreadyExistsException("User with email %s already exists."
                    .formatted(userCreateDTO.getEmail()));
        }
        User user = this.mapper.convert(userCreateDTO, User.class);
        otpService.sendOtp(user);
        user.setId(null);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        user.setRole(role);
        user.setStatus(Status.ACTIVE);
        user = userRepository.save(user);
        entityManager.refresh(user);
        return mapper.convert(user, UserDTO.class);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID %d not found.".formatted(id)));

        Role role = roleRepository.findById(userRequest.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role with ID %d not found."
                        .formatted(userRequest.getRoleId())));
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setRole(role);
        user.setRoleId(userRequest.getRoleId());
        userRepository.save(user);
        userRepository.flush();
        entityManager.refresh(user);
        return this.mapper.convert(user, UserDTO.class);
    }

    @Transactional
    public boolean deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID %d not found.".formatted(id)));
        userRepository.delete(user);
        return true;
    }

    public Page<UserDTO> findUserByFilter(SearchRequest request, Pageable pageable) {
        Page<User> users = genericSearchService.search(request, userRepository, pageable);
        return users.map(user -> mapper.convert(user, UserDTO.class));
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if(otpService.isOtpInvalid(request.email(), request.otp())) {
            throw new OtpInvalidateException("Invalid OTP");
        }
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User with %s not found".formatted(request.email())));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User with %s not found".formatted(request.email())));

        if(!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Old password does not match");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with %s not found"
                        .formatted(request.getEmail())));
        otpService.sendOtp(user);
        userRepository.save(user);
    }
}
