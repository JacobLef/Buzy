package edu.neu.csye6200.service.interfaces;

import edu.neu.csye6200.dto.AuthDTO;
import edu.neu.csye6200.dto.request.CreateEmployeeRequest;
import edu.neu.csye6200.dto.request.CreateEmployerRequest;


public interface SignupService {

    AuthDTO signupEmployee(CreateEmployeeRequest request);

    AuthDTO signupEmployer(CreateEmployerRequest request);
}