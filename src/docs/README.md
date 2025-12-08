# UML Diagrams - Business Management Platform

## How to run the program

## Configure your MySQL username and password

### Go to the main/resources/ page and do the following
- Create a .env file
- Create a DB_USERNAME and DB_PASSWORD configurations
- Continue with the next steps
- Note: if your username and password are allowed to be root and (no password) then you do not need an environment file


### Start the backend server from the command line
```bash
./mvnw spring-boot:run
```

### If you DO NOT want to do that, just press start on BusinessManagmentApplication class

### Start the the frontend server
```bash
cd src/frontend/src
```

```bash
npm run dev
```

## Use Case Diagram
<img src="uml/images/Use-Case-Diagram-OOD.png" width="700">

## Class Diagram
![Class Diagram](uml/images/ClassDiagramOOD.png)

## Condensed Diagram
<img src="uml/images/CondensedArch.png" width="500">

## ER Diagram
<img src="uml/images/ER-Diagram-OOD.png" width="700">

## Sequence Diagrams

### Workflow 1: Create Employee
<img src="uml/images/Create-Employee-Diagram-OOD.png" width="600">

### Workflow 2: Calculate Payroll
<img src="uml/images/Payroll-Sequence-Diagram.png" width="600">

### Workflow 3: Add Training
<img src="uml/images/Add-Training-Sequence-Diagram-OOD.png" width="600">