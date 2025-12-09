# Permission Logic Documentation

## Overview

This document outlines the permission system for the HR Management System, defining what actions each user type can perform.

## User Roles

- **Employee**: Regular employee with limited access
- **Employer**: Regular manager/employer with team management capabilities
- **Admin**: Employer with administrative privileges (can edit all employers and company info)
- **Owner**: Company owner/CEO with full system access (can manage admins)

## Permission Matrix

### Employee Management

| User Type | View Employees | Edit Own Profile | Edit Other Employees | Create Employees | Delete Employees |
|-----------|---------------|------------------|---------------------|------------------|------------------|
| **Employee** | ❌ No | ✅ Yes (limited: name, email, password) | ❌ No | ❌ No | ❌ No |
| **Employer** | ✅ Yes (team members) | ✅ Yes (limited: name, email, password) | ✅ Yes (team members only) | ✅ Yes | ❌ No (use status) |
| **Admin** | ✅ Yes (all) | ✅ Yes (limited: name, email, password) | ✅ Yes (all) | ✅ Yes | ❌ No (use status) |
| **Owner** | ✅ Yes (all) | ✅ Yes (limited: name, email, password) | ✅ Yes (all) | ✅ Yes | ❌ No (use status) |

### Employer Management

| User Type | View Employers | Edit Own Profile | Edit Other Employers | Create Employers | Delete Employers | Manage Admin Roles |
|-----------|---------------|------------------|---------------------|------------------|------------------|-------------------|
| **Employee** | ❌ No | ❌ No | ❌ No | ❌ No | ❌ No | ❌ No |
| **Employer** | ❌ No | ✅ Yes (limited: name, email, password) | ❌ No | ❌ No | ❌ No | ❌ No |
| **Admin** | ✅ Yes (all) | ✅ Yes (limited: name, email, password) | ✅ Yes (all except Owner) | ✅ Yes | ❌ No (use status) | ❌ No |
| **Owner** | ✅ Yes (all) | ✅ Yes (limited: name, email, password) | ✅ Yes (all) | ✅ Yes | ❌ No (use status) | ✅ Yes |

### Company Information

| User Type | View Company Info | Edit Company Info | View Org Chart | Edit Org Structure |
|-----------|------------------|-------------------|---------------|-------------------|
| **Employee** | ✅ Yes (read-only) | ❌ No | ✅ Yes (read-only) | ❌ No |
| **Employer** | ✅ Yes (read-only) | ❌ No | ✅ Yes (read-only) | ❌ No |
| **Admin** | ✅ Yes | ✅ Yes | ✅ Yes | ✅ Yes |
| **Owner** | ✅ Yes | ✅ Yes | ✅ Yes | ✅ Yes |

### Payroll Management

| User Type | View Own Payroll | View Team Payroll | Generate Paychecks | Edit Payroll Settings |
|-----------|-----------------|------------------|-------------------|---------------------|
| **Employee** | ✅ Yes | ❌ No | ❌ No | ❌ No |
| **Employer** | ✅ Yes | ✅ Yes (team members) | ✅ Yes (team members) | ❌ No |
| **Admin** | ✅ Yes | ✅ Yes (all) | ✅ Yes (all) | ✅ Yes |
| **Owner** | ✅ Yes | ✅ Yes (all) | ✅ Yes (all) | ✅ Yes |

### Training Management

| User Type | View Own Training | View Team Training | Assign Training | Create Training |
|-----------|-----------------|-------------------|----------------|----------------|
| **Employee** | ✅ Yes | ❌ No | ❌ No | ❌ No |
| **Employer** | ✅ Yes | ✅ Yes (team members) | ✅ Yes (team members) | ✅ Yes |
| **Admin** | ✅ Yes | ✅ Yes (all) | ✅ Yes (all) | ✅ Yes |
| **Owner** | ✅ Yes | ✅ Yes (all) | ✅ Yes (all) | ✅ Yes |

### Admin Role Management

| User Type | View Admin List | Promote to Admin | Remove Admin | Transfer Ownership |
|-----------|----------------|-----------------|-------------|------------------|
| **Employee** | ❌ No | ❌ No | ❌ No | ❌ No |
| **Employer** | ❌ No | ❌ No | ❌ No | ❌ No |
| **Admin** | ✅ Yes | ❌ No | ❌ No | ❌ No |
| **Owner** | ✅ Yes | ✅ Yes | ✅ Yes | ❌ No (future feature) |

## Detailed Permission Rules

### Edit Permissions

#### Employee Profile Editing
- **Self-edit**: All users can edit their own name, email, and password
- **Employee editing**: Only Admin and Owner can edit other employees
- **Field restrictions**: 
  - Regular employees: Can only edit name, email, password
  - Admin/Owner: Can edit all fields (salary, position, department, manager, status)

#### Employer Profile Editing
- **Self-edit**: Employers can edit their own name, email, and password
- **Other employer editing**: 
  - Admin: Can edit all employers EXCEPT Owner
  - Owner: Can edit all employers (including other admins, but not themselves)
- **Field restrictions**:
  - Regular employer self-edit: Only name, email, password
  - Admin/Owner editing others: All fields (salary, department, title, status)

### Access Control

#### Employer List Page
- **Access**: Only Admin and Owner can access `/employer/employers`
- **Navigation**: "Employers" menu item only visible to Admin/Owner
- **Route protection**: `AdminOnlyRoute` component checks permissions before rendering

#### Company Settings Page
- **View**: All employers can view company information
- **Edit**: Only Admin and Owner can edit company information
- **Admin Management Section**: Only Owner can see and manage admin roles

### UI Visibility Rules

#### Edit Buttons
- **Employee List**: Edit button only visible to Admin/Owner
- **Employer List**: 
  - Edit button visible to Admin/Owner
  - Hidden for Owner when viewing Owner (Admin cannot edit Owner)
- **Company View Modal**: Edit button only visible when user has edit permissions

#### Admin Role Management
- **Promote/Remove Admin buttons**: Only visible to Owner
- **Admin Role column**: Only visible in Employer List when user is Owner

## Permission Check Implementation

### Frontend Checks
1. **Route Protection**: `AdminOnlyRoute` component validates admin/owner status
2. **Component-level**: Conditional rendering based on `isAdmin` and `isOwner` flags
3. **Button Visibility**: Edit buttons only shown when user has appropriate permissions
4. **Form Field Restrictions**: `canEditFullProfile` prop controls field visibility

### Backend Checks (Recommended)
- Validate permissions in service layer before allowing updates
- Check `isAdmin` and `isOwner` flags before processing requests
- Prevent Admin from updating Owner records

## Examples

### Example 1: Regular Employer Editing Employee
- ✅ **Allowed**: Regular employer can edit employees in their team
- ✅ **Fields**: Can edit all fields (salary, position, manager, etc.)
- ❌ **Restricted**: Cannot access Employer List page

### Example 2: Admin Editing Owner
- ❌ **Not Allowed**: Admin cannot edit Owner information
- ❌ **UI**: Edit button hidden for Owner when viewed by Admin
- ❌ **Form**: If somehow accessed, form fields would be disabled

### Example 3: Owner Managing Admins
- ✅ **Allowed**: Owner can promote any employer to Admin
- ✅ **Allowed**: Owner can remove Admin rights (except from themselves)
- ✅ **UI**: Admin Management section visible only to Owner

### Example 4: Employee Self-Edit
- ✅ **Allowed**: Employee can edit their own profile
- ✅ **Fields**: Limited to name, email, and password
- ❌ **Restricted**: Cannot edit salary, position, or other fields

## Notes

- **Status Management**: Instead of deleting, users are set to INACTIVE or ON_LEAVE status
- **Self-Edit Restrictions**: Users cannot edit certain fields of their own profile (e.g., salary, status)
- **Owner Protection**: Owner cannot be demoted or have admin rights removed by Admin
- **Future Enhancement**: Owner transfer functionality may be added in future versions

