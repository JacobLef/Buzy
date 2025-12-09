package app.service.interfaces;

import app.dto.AuthDTO;
import app.dto.request.CreateEmployeeRequest;
import app.dto.request.CreateEmployerRequest;


public interface SignupService {

    AuthDTO signupEmployee(CreateEmployeeRequest request);

    AuthDTO signupEmployer(CreateEmployerRequest request);
}