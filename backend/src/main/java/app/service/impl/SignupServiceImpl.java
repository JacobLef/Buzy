package app.service.impl;

import app.dto.AuthDTO;
import app.dto.request.CreateEmployeeRequest;
import app.dto.request.CreateEmployerRequest;
import app.model.domain.*;
import app.repository.UserRepository;
import app.security.JwtTokenProvider;
import app.service.interfaces.EmployeeService;
import app.service.interfaces.EmployerService;
import app.service.interfaces.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SignupServiceImpl implements SignupService {

    private final EmployeeService employeeService;
    private final EmployerService employerService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SignupServiceImpl(
            EmployeeService employeeService,
            EmployerService employerService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.employeeService = employeeService;
        this.employerService = employerService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    @Transactional
    public AuthDTO signupEmployee(CreateEmployeeRequest request) {
        //Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        //Encrypt the plain password here in SignupService
        String encryptedPassword = passwordEncoder.encode(request.password());

        //Create a new request object with encrypted password

        CreateEmployeeRequest requestWithEncryptedPassword = new CreateEmployeeRequest(
                request.name(),
                request.email(),
                encryptedPassword, //already encrypted, for employeeService use
                request.salary(),
                request.position(),
                request.companyId(),
                request.managerId(),
                request.hireDate()
        );

        Employee employee = employeeService.createEmployee(requestWithEncryptedPassword);

        //Create User account for authentication
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(encryptedPassword); // Same encrypted password
        user.setRole(UserRole.EMPLOYEE);
        user.setBusinessPerson(employee);
        user.setEnabled(true);

        //Save User
        user = userRepository.save(user);

        //Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        //Build response
        AuthDTO response = new AuthDTO();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setEmail(user.getEmail());
        response.setUserId(user.getId());
        response.setBusinessPersonId(employee.getId());

        return response;
    }

    @Override
    @Transactional
    public AuthDTO signupEmployer(CreateEmployerRequest request) {
        //Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }

        //Encrypt the plain password here in SignupService
        String encryptedPassword = passwordEncoder.encode(request.password());

        //Create request with encrypted password for existing EmployerService
        CreateEmployerRequest requestWithEncryptedPassword = new CreateEmployerRequest(
                request.name(),
                request.email(),
                encryptedPassword, // Already encrypted, EmployerService will use it directly
                request.salary(),
                request.department(),
                request.title(),
                request.companyId(),
                request.hireDate()
        );

        Employer employer = employerService.createEmployer(requestWithEncryptedPassword);

        //Create User account for authentication
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(encryptedPassword); // Same encrypted password
        user.setRole(UserRole.EMPLOYER);
        user.setBusinessPerson(employer);
        user.setEnabled(true);

        user = userRepository.save(user);

        //enerate JWT token
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

        //Build response
        AuthDTO response = new AuthDTO();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setEmail(user.getEmail());
        response.setUserId(user.getId());
        response.setBusinessPersonId(employer.getId());

        return response;
    }
}