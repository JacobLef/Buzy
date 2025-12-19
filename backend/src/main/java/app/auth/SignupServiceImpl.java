package app.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.auth.dto.AuthDTO;
import app.common.exception.EmailNotFoundException;
import app.employee.Employee;
import app.employee.EmployeeService;
import app.employee.dto.CreateEmployeeRequest;
import app.employer.Employer;
import app.employer.EmployerService;
import app.employer.dto.CreateEmployerRequest;
import app.user.User;
import app.user.UserRepository;
import app.user.UserRole;

@Service
public class SignupServiceImpl implements SignupService {

  private final EmployeeService employeeService;
  private final EmployerService employerService;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  @Autowired
  public SignupServiceImpl(EmployeeService employeeService, EmployerService employerService,
      UserRepository userRepository, PasswordEncoder passwordEncoder,
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
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailNotFoundException("Email already exists");
    }

    String encryptedPassword = passwordEncoder.encode(request.password());

    CreateEmployeeRequest requestWithEncryptedPassword = new CreateEmployeeRequest(request.name(),
        request.email(), encryptedPassword, request.salary(), request.position(),
        request.companyId(), request.managerId(), request.hireDate());

    Employee employee = employeeService.createEmployee(requestWithEncryptedPassword);

    User user = new User();
    user.setEmail(request.email());
    user.setPassword(encryptedPassword);
    user.setRole(UserRole.EMPLOYEE);
    user.setBusinessPerson(employee);
    user.setEnabled(true);

    user = userRepository.save(user);
    String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

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
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailNotFoundException("Email already exists");
    }
    String encryptedPassword = passwordEncoder.encode(request.password());
    CreateEmployerRequest requestWithEncryptedPassword = new CreateEmployerRequest(request.name(),
        request.email(), encryptedPassword, request.salary(), request.department(), request.title(),
        request.companyId(), request.hireDate());

    Employer employer = employerService.createEmployer(requestWithEncryptedPassword);

    User user = new User();
    user.setEmail(request.email());
    user.setPassword(encryptedPassword);
    user.setRole(UserRole.EMPLOYER);
    user.setBusinessPerson(employer);
    user.setEnabled(true);

    user = userRepository.save(user);
    String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole().name());

    AuthDTO response = new AuthDTO();
    response.setToken(token);
    response.setRole(user.getRole().name());
    response.setEmail(user.getEmail());
    response.setUserId(user.getId());
    response.setBusinessPersonId(employer.getId());

    return response;
  }
}
