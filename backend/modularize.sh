#!/bin/bash
set -e

cd src/main/java/app

echo "=== Step 1: Creating directory structure ==="
mkdir -p auth/dto
mkdir -p employee/dto
mkdir -p employer/dto
mkdir -p business/dto
mkdir -p payroll/dto
mkdir -p payroll/strategy
mkdir -p training/dto
mkdir -p dashboard/dto
mkdir -p user
mkdir -p common/config
mkdir -p common/exception
mkdir -p common/factory
mkdir -p common/util
mkdir -p common/dto
echo "Done."

echo "=== Step 2: Moving files ==="

# AUTH
mv controller/AuthController.java auth/ 2>/dev/null || true
mv service/impl/AuthServiceImpl.java auth/ 2>/dev/null || true
mv service/interfaces/AuthService.java auth/ 2>/dev/null || true
mv service/impl/SignupServiceImpl.java auth/ 2>/dev/null || true
mv service/interfaces/SignupService.java auth/ 2>/dev/null || true
mv security/JwtAuthenticationFilter.java auth/ 2>/dev/null || true
mv security/JwtTokenProvider.java auth/ 2>/dev/null || true
mv dto/AuthDTO.java auth/dto/ 2>/dev/null || true
mv dto/request/AuthRequest.java auth/dto/ 2>/dev/null || true
mv exception/InvalidCredentialsException.java auth/ 2>/dev/null || true
mv exception/InvalidTokenException.java auth/ 2>/dev/null || true
echo "Auth done."

# EMPLOYEE
mv model/domain/Employee.java employee/ 2>/dev/null || true
mv controller/EmployeeController.java employee/ 2>/dev/null || true
mv service/interfaces/EmployeeService.java employee/ 2>/dev/null || true
mv service/impl/EmployeeServiceImpl.java employee/ 2>/dev/null || true
mv repository/EmployeeRepository.java employee/ 2>/dev/null || true
mv exception/EmployeeNotFoundException.java employee/ 2>/dev/null || true
mv dto/EmployeeDTO.java employee/dto/ 2>/dev/null || true
mv dto/request/CreateEmployeeRequest.java employee/dto/ 2>/dev/null || true
mv dto/request/UpdateEmployeeRequest.java employee/dto/ 2>/dev/null || true
echo "Employee done."

# EMPLOYER
mv model/domain/Employer.java employer/ 2>/dev/null || true
mv controller/EmployerController.java employer/ 2>/dev/null || true
mv service/interfaces/EmployerService.java employer/ 2>/dev/null || true
mv service/impl/EmployerServiceImpl.java employer/ 2>/dev/null || true
mv repository/EmployerRepository.java employer/ 2>/dev/null || true
mv exception/EmployerNotFoundException.java employer/ 2>/dev/null || true
mv dto/EmployerDTO.java employer/dto/ 2>/dev/null || true
mv dto/request/CreateEmployerRequest.java employer/dto/ 2>/dev/null || true
mv dto/request/UpdateEmployerRequest.java employer/dto/ 2>/dev/null || true
echo "Employer done."

# BUSINESS
mv model/domain/Business.java business/ 2>/dev/null || true
mv model/domain/BusinessPerson.java business/ 2>/dev/null || true
mv model/domain/Company.java business/ 2>/dev/null || true
mv controller/BusinessController.java business/ 2>/dev/null || true
mv service/interfaces/BusinessService.java business/ 2>/dev/null || true
mv service/impl/BusinessServiceImpl.java business/ 2>/dev/null || true
mv repository/BusinessRepository.java business/ 2>/dev/null || true
mv repository/BusinessPersonRepository.java business/ 2>/dev/null || true
mv factory/BusinessPersonFactory.java business/ 2>/dev/null || true
mv exception/BusinessNotFoundException.java business/ 2>/dev/null || true
mv exception/BusinessValidationException.java business/ 2>/dev/null || true
mv exception/InvalidBusinessException.java business/ 2>/dev/null || true
mv dto/BusinessDTO.java business/dto/ 2>/dev/null || true
mv dto/BusinessPersonDTO.java business/dto/ 2>/dev/null || true
mv dto/CompanyDTO.java business/dto/ 2>/dev/null || true
mv dto/request/CreateBusinessRequest.java business/dto/ 2>/dev/null || true
mv dto/request/UpdateBusinessRequest.java business/dto/ 2>/dev/null || true
echo "Business done."

# PAYROLL
mv model/payroll/Paycheck.java payroll/ 2>/dev/null || true
mv model/payroll/PaycheckStatus.java payroll/ 2>/dev/null || true
mv controller/PayrollController.java payroll/ 2>/dev/null || true
mv service/interfaces/PayrollService.java payroll/ 2>/dev/null || true
mv service/impl/PayrollServiceImpl.java payroll/ 2>/dev/null || true
mv repository/PaycheckRepository.java payroll/ 2>/dev/null || true
mv exception/PayrollCalculationException.java payroll/ 2>/dev/null || true
mv strategy/tax/TaxCalculationStrategy.java payroll/strategy/ 2>/dev/null || true
mv strategy/tax/FlatTaxStrategy.java payroll/strategy/ 2>/dev/null || true
mv strategy/tax/ProgressiveTaxStrategy.java payroll/strategy/ 2>/dev/null || true
mv dto/response/PaycheckDTO.java payroll/dto/ 2>/dev/null || true
mv dto/response/PayrollSummaryDTO.java payroll/dto/ 2>/dev/null || true
mv dto/request/DistributeBonusRequest.java payroll/dto/ 2>/dev/null || true
mv dto/response/BonusDistributionResponse.java payroll/dto/ 2>/dev/null || true
mv dto/response/DeletePaycheckResponse.java payroll/dto/ 2>/dev/null || true
mv dto/response/TaxStrategyResponse.java payroll/dto/ 2>/dev/null || true
mv dto/response/TaxStrategiesResponse.java payroll/dto/ 2>/dev/null || true
mv dto/response/TaxStrategySwitchResponse.java payroll/dto/ 2>/dev/null || true
echo "Payroll done."

# TRAINING
mv model/domain/Training.java training/ 2>/dev/null || true
mv controller/TrainingController.java training/ 2>/dev/null || true
mv service/interfaces/TrainingService.java training/ 2>/dev/null || true
mv service/impl/TrainingServiceImpl.java training/ 2>/dev/null || true
mv repository/TrainingRepository.java training/ 2>/dev/null || true
mv dto/TrainingDTO.java training/dto/ 2>/dev/null || true
mv dto/request/CreateTrainingRequest.java training/dto/ 2>/dev/null || true
mv dto/request/UpdateTrainingRequest.java training/dto/ 2>/dev/null || true
echo "Training done."

# DASHBOARD
mv controller/DashboardController.java dashboard/ 2>/dev/null || true
mv service/impl/DashboardServiceImpl.java dashboard/ 2>/dev/null || true
mv dto/response/ActivityDTO.java dashboard/dto/ 2>/dev/null || true
echo "Dashboard done."

# USER
mv model/domain/User.java user/ 2>/dev/null || true
mv model/domain/UserRole.java user/ 2>/dev/null || true
mv model/domain/PersonStatus.java user/ 2>/dev/null || true
mv repository/UserRepository.java user/ 2>/dev/null || true
mv exception/UserNotFoundException.java user/ 2>/dev/null || true
mv exception/UserDisabledException.java user/ 2>/dev/null || true
echo "User done."

# COMMON
mv config/* common/config/ 2>/dev/null || true
mv exception/GlobalExceptionHandler.java common/exception/ 2>/dev/null || true
mv exception/ResourceNotFoundException.java common/exception/ 2>/dev/null || true
mv factory/DTOFactory.java common/factory/ 2>/dev/null || true
mv util/csv/* common/util/ 2>/dev/null || true
mv dto/ErrorResponse.java common/dto/ 2>/dev/null || true
mv controller/HelloController.java common/ 2>/dev/null || true
mv service/interfaces/HierarchyService.java common/ 2>/dev/null || true
echo "Common done."

echo "=== Step 3: Removing empty directories ==="
rm -rf controller 2>/dev/null || true
rm -rf service 2>/dev/null || true
rm -rf repository 2>/dev/null || true
rm -rf model 2>/dev/null || true
rm -rf dto 2>/dev/null || true
rm -rf exception 2>/dev/null || true
rm -rf factory 2>/dev/null || true
rm -rf security 2>/dev/null || true
rm -rf strategy 2>/dev/null || true
rm -rf util 2>/dev/null || true
rm -rf config 2>/dev/null || true
echo "Done."

echo "=== Step 4: Updating imports (this takes a moment) ==="

# Find all Java files
find . -name "*.java" | while read file; do
  # MODEL DOMAIN -> Module roots
  sed -i '' 's/import app\.model\.domain\.Employee;/import app.employee.Employee;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.Employer;/import app.employer.Employer;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.Company;/import app.business.Company;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.Business;/import app.business.Business;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.BusinessPerson;/import app.business.BusinessPerson;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.Training;/import app.training.Training;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.User;/import app.user.User;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.UserRole;/import app.user.UserRole;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.PersonStatus;/import app.user.PersonStatus;/g' "$file"
  sed -i '' 's/import app\.model\.domain\.\*;/import app.employee.Employee;\nimport app.employer.Employer;\nimport app.business.*;\nimport app.user.*;/g' "$file"
  
  # MODEL PAYROLL -> payroll module
  sed -i '' 's/import app\.model\.payroll\.Paycheck;/import app.payroll.Paycheck;/g' "$file"
  sed -i '' 's/import app\.model\.payroll\.PaycheckStatus;/import app.payroll.PaycheckStatus;/g' "$file"
  
  # REPOSITORIES -> Module roots
  sed -i '' 's/import app\.repository\.EmployeeRepository;/import app.employee.EmployeeRepository;/g' "$file"
  sed -i '' 's/import app\.repository\.EmployerRepository;/import app.employer.EmployerRepository;/g' "$file"
  sed -i '' 's/import app\.repository\.BusinessRepository;/import app.business.BusinessRepository;/g' "$file"
  sed -i '' 's/import app\.repository\.BusinessPersonRepository;/import app.business.BusinessPersonRepository;/g' "$file"
  sed -i '' 's/import app\.repository\.TrainingRepository;/import app.training.TrainingRepository;/g' "$file"
  sed -i '' 's/import app\.repository\.UserRepository;/import app.user.UserRepository;/g' "$file"
  sed -i '' 's/import app\.repository\.PaycheckRepository;/import app.payroll.PaycheckRepository;/g' "$file"
  
  # SERVICE INTERFACES -> Module roots
  sed -i '' 's/import app\.service\.interfaces\.EmployeeService;/import app.employee.EmployeeService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.EmployerService;/import app.employer.EmployerService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.BusinessService;/import app.business.BusinessService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.TrainingService;/import app.training.TrainingService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.PayrollService;/import app.payroll.PayrollService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.AuthService;/import app.auth.AuthService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.SignupService;/import app.auth.SignupService;/g' "$file"
  sed -i '' 's/import app\.service\.interfaces\.HierarchyService;/import app.common.HierarchyService;/g' "$file"
  
  # SERVICE IMPL -> Module roots (rarely imported directly, but just in case)
  sed -i '' 's/import app\.service\.impl\.EmployeeServiceImpl;/import app.employee.EmployeeServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.EmployerServiceImpl;/import app.employer.EmployerServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.BusinessServiceImpl;/import app.business.BusinessServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.TrainingServiceImpl;/import app.training.TrainingServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.PayrollServiceImpl;/import app.payroll.PayrollServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.AuthServiceImpl;/import app.auth.AuthServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.SignupServiceImpl;/import app.auth.SignupServiceImpl;/g' "$file"
  sed -i '' 's/import app\.service\.impl\.DashboardServiceImpl;/import app.dashboard.DashboardServiceImpl;/g' "$file"
  
  # DTO REQUEST -> Module dto packages
  sed -i '' 's/import app\.dto\.request\.CreateEmployeeRequest;/import app.employee.dto.CreateEmployeeRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.UpdateEmployeeRequest;/import app.employee.dto.UpdateEmployeeRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.CreateEmployerRequest;/import app.employer.dto.CreateEmployerRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.UpdateEmployerRequest;/import app.employer.dto.UpdateEmployerRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.CreateBusinessRequest;/import app.business.dto.CreateBusinessRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.UpdateBusinessRequest;/import app.business.dto.UpdateBusinessRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.CreateTrainingRequest;/import app.training.dto.CreateTrainingRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.UpdateTrainingRequest;/import app.training.dto.UpdateTrainingRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.DistributeBonusRequest;/import app.payroll.dto.DistributeBonusRequest;/g' "$file"
  sed -i '' 's/import app\.dto\.request\.AuthRequest;/import app.auth.dto.AuthRequest;/g' "$file"
  
  # DTO RESPONSE -> Module dto packages
  sed -i '' 's/import app\.dto\.response\.PaycheckDTO;/import app.payroll.dto.PaycheckDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.PayrollSummaryDTO;/import app.payroll.dto.PayrollSummaryDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.BonusDistributionResponse;/import app.payroll.dto.BonusDistributionResponse;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.DeletePaycheckResponse;/import app.payroll.dto.DeletePaycheckResponse;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.TaxStrategyResponse;/import app.payroll.dto.TaxStrategyResponse;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.TaxStrategiesResponse;/import app.payroll.dto.TaxStrategiesResponse;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.TaxStrategySwitchResponse;/import app.payroll.dto.TaxStrategySwitchResponse;/g' "$file"
  sed -i '' 's/import app\.dto\.response\.ActivityDTO;/import app.dashboard.dto.ActivityDTO;/g' "$file"
  
  # DTO (root level) -> Module dto packages
  sed -i '' 's/import app\.dto\.EmployeeDTO;/import app.employee.dto.EmployeeDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.EmployerDTO;/import app.employer.dto.EmployerDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.BusinessDTO;/import app.business.dto.BusinessDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.BusinessPersonDTO;/import app.business.dto.BusinessPersonDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.CompanyDTO;/import app.business.dto.CompanyDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.TrainingDTO;/import app.training.dto.TrainingDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.AuthDTO;/import app.auth.dto.AuthDTO;/g' "$file"
  sed -i '' 's/import app\.dto\.ErrorResponse;/import app.common.dto.ErrorResponse;/g' "$file"
  
  # EXCEPTIONS -> Various modules
  sed -i '' 's/import app\.exception\.EmployeeNotFoundException;/import app.employee.EmployeeNotFoundException;/g' "$file"
  sed -i '' 's/import app\.exception\.EmployerNotFoundException;/import app.employer.EmployerNotFoundException;/g' "$file"
  sed -i '' 's/import app\.exception\.BusinessNotFoundException;/import app.business.BusinessNotFoundException;/g' "$file"
  sed -i '' 's/import app\.exception\.BusinessValidationException;/import app.business.BusinessValidationException;/g' "$file"
  sed -i '' 's/import app\.exception\.InvalidBusinessException;/import app.business.InvalidBusinessException;/g' "$file"
  sed -i '' 's/import app\.exception\.PayrollCalculationException;/import app.payroll.PayrollCalculationException;/g' "$file"
  sed -i '' 's/import app\.exception\.ResourceNotFoundException;/import app.common.exception.ResourceNotFoundException;/g' "$file"
  sed -i '' 's/import app\.exception\.GlobalExceptionHandler;/import app.common.exception.GlobalExceptionHandler;/g' "$file"
  sed -i '' 's/import app\.exception\.InvalidCredentialsException;/import app.auth.InvalidCredentialsException;/g' "$file"
  sed -i '' 's/import app\.exception\.InvalidTokenException;/import app.auth.InvalidTokenException;/g' "$file"
  sed -i '' 's/import app\.exception\.UserNotFoundException;/import app.user.UserNotFoundException;/g' "$file"
  sed -i '' 's/import app\.exception\.UserDisabledException;/import app.user.UserDisabledException;/g' "$file"
  
  # SECURITY -> auth module
  sed -i '' 's/import app\.security\.JwtTokenProvider;/import app.auth.JwtTokenProvider;/g' "$file"
  sed -i '' 's/import app\.security\.JwtAuthenticationFilter;/import app.auth.JwtAuthenticationFilter;/g' "$file"
  
  # FACTORY -> various
  sed -i '' 's/import app\.factory\.DTOFactory;/import app.common.factory.DTOFactory;/g' "$file"
  sed -i '' 's/import app\.factory\.BusinessPersonFactory;/import app.business.BusinessPersonFactory;/g' "$file"
  
  # STRATEGY -> payroll.strategy
  sed -i '' 's/import app\.strategy\.tax\.TaxCalculationStrategy;/import app.payroll.strategy.TaxCalculationStrategy;/g' "$file"
  sed -i '' 's/import app\.strategy\.tax\.FlatTaxStrategy;/import app.payroll.strategy.FlatTaxStrategy;/g' "$file"
  sed -i '' 's/import app\.strategy\.tax\.ProgressiveTaxStrategy;/import app.payroll.strategy.ProgressiveTaxStrategy;/g' "$file"
  
  # CONFIG -> common.config
  sed -i '' 's/import app\.config\./import app.common.config./g' "$file"
  
  # UTIL -> common.util
  sed -i '' 's/import app\.util\.csv\./import app.common.util./g' "$file"
done
echo "Imports done."

echo "=== Step 5: Updating package declarations ==="

# AUTH
find auth -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.auth;/' {} \;
find auth -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.auth;/' {} \;
find auth -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.auth;/' {} \;
find auth -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.security;/package app.auth;/' {} \;
find auth -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.auth;/' {} \;
find auth/dto -name "*.java" -exec sed -i '' 's/^package app\.dto;/package app.auth.dto;/' {} \;
find auth/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.request;/package app.auth.dto;/' {} \;

# EMPLOYEE
find employee -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.employee;/' {} \;
find employee -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.employee;/' {} \;
find employee -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.employee;/' {} \;
find employee -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.repository;/package app.employee;/' {} \;
find employee -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.model\.domain;/package app.employee;/' {} \;
find employee -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.employee;/' {} \;
find employee/dto -name "*.java" -exec sed -i '' 's/^package app\.dto;/package app.employee.dto;/' {} \;
find employee/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.request;/package app.employee.dto;/' {} \;

# EMPLOYER
find employer -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.employer;/' {} \;
find employer -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.employer;/' {} \;
find employer -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.employer;/' {} \;
find employer -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.repository;/package app.employer;/' {} \;
find employer -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.model\.domain;/package app.employer;/' {} \;
find employer -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.employer;/' {} \;
find employer/dto -name "*.java" -exec sed -i '' 's/^package app\.dto;/package app.employer.dto;/' {} \;
find employer/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.request;/package app.employer.dto;/' {} \;

# BUSINESS
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.business;/' {} \;
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.business;/' {} \;
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.business;/' {} \;
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.repository;/package app.business;/' {} \;
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.model\.domain;/package app.business;/' {} \;
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.business;/' {} \;
find business -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.factory;/package app.business;/' {} \;
find business/dto -name "*.java" -exec sed -i '' 's/^package app\.dto;/package app.business.dto;/' {} \;
find business/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.request;/package app.business.dto;/' {} \;

# PAYROLL
find payroll -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.payroll;/' {} \;
find payroll -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.payroll;/' {} \;
find payroll -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.payroll;/' {} \;
find payroll -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.repository;/package app.payroll;/' {} \;
find payroll -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.model\.payroll;/package app.payroll;/' {} \;
find payroll -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.payroll;/' {} \;
find payroll/strategy -name "*.java" -exec sed -i '' 's/^package app\.strategy\.tax;/package app.payroll.strategy;/' {} \;
find payroll/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.response;/package app.payroll.dto;/' {} \;
find payroll/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.request;/package app.payroll.dto;/' {} \;

# TRAINING
find training -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.training;/' {} \;
find training -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.training;/' {} \;
find training -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.training;/' {} \;
find training -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.repository;/package app.training;/' {} \;
find training -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.model\.domain;/package app.training;/' {} \;
find training/dto -name "*.java" -exec sed -i '' 's/^package app\.dto;/package app.training.dto;/' {} \;
find training/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.request;/package app.training.dto;/' {} \;

# DASHBOARD
find dashboard -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.dashboard;/' {} \;
find dashboard -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.impl;/package app.dashboard;/' {} \;
find dashboard/dto -name "*.java" -exec sed -i '' 's/^package app\.dto\.response;/package app.dashboard.dto;/' {} \;

# USER
find user -name "*.java" -exec sed -i '' 's/^package app\.model\.domain;/package app.user;/' {} \;
find user -name "*.java" -exec sed -i '' 's/^package app\.repository;/package app.user;/' {} \;
find user -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.user;/' {} \;

# COMMON
find common/config -name "*.java" -exec sed -i '' 's/^package app\.config;/package app.common.config;/' {} \;
find common/exception -name "*.java" -exec sed -i '' 's/^package app\.exception;/package app.common.exception;/' {} \;
find common/factory -name "*.java" -exec sed -i '' 's/^package app\.factory;/package app.common.factory;/' {} \;
find common/util -name "*.java" -exec sed -i '' 's/^package app\.util\.csv;/package app.common.util;/' {} \;
find common/dto -name "*.java" -exec sed -i '' 's/^package app\.dto;/package app.common.dto;/' {} \;
find common -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.controller;/package app.common;/' {} \;
find common -maxdepth 1 -name "*.java" -exec sed -i '' 's/^package app\.service\.interfaces;/package app.common;/' {} \;

echo "Package declarations done."

echo ""
echo "=== COMPLETE ==="
echo "Run 'mvn clean compile' from backend/ to verify."