package app.auth;

import app.auth.dto.AuthDTO;
import app.employee.dto.CreateEmployeeRequest;
import app.employer.dto.CreateEmployerRequest;

public interface SignupService {
  AuthDTO signupEmployee(CreateEmployeeRequest request);

  AuthDTO signupEmployer(CreateEmployerRequest request);
}
