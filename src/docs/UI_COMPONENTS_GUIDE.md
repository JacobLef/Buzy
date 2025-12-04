# Reusable UI Components Guide

This guide explains how to use the reusable UI components to create consistent, well-styled pages across the application.

## Table of Contents

1. [Overview](#overview)
2. [Design System](#design-system)
3. [Component Library](#component-library)
   - [Card](#card)
   - [CardHeader](#cardheader)
   - [Button](#button)
   - [Badge](#badge)
4. [Usage Examples](#usage-examples)
5. [Best Practices](#best-practices)
6. [Page Structure Template](#page-structure-template)

---

## Overview

The UI component library provides a consistent set of reusable components that follow the **Modern Soft-Neo** design system. These components ensure:

- **Consistent styling** across all pages
- **Maintainable code** with reusable components
- **Responsive design** that works on all screen sizes
- **Accessibility** built-in

All components are located in `src/components/ui/` and can be imported from `src/components/ui/index.ts`.

---

## Design System

### Color Palette

- **Primary Dark (Navy)**: `text-slate-900` - Used for headings and important text
- **Secondary Text**: `text-gray-500` - Used for subtitles and less important text
- **Primary CTA**: `bg-blue-600` - Used for primary actions
- **Background**: `bg-white` - Card backgrounds
- **Soft Background**: `bg-gray-50` or `bg-[#f8fafc]` - Page backgrounds

### Spacing

- Use Tailwind spacing utilities: `space-y-8`, `gap-4`, `p-6`, etc.
- Consistent padding sizes: `p-4` (sm), `p-6` (md), `p-8` (lg)

### Shadows

- Soft shadow applied to cards: `boxShadow: '0 4px 20px -2px rgba(0, 0, 0, 0.05)'`

---

## Component Library

### Card

A container component for grouping related content with consistent styling.

#### Import

```typescript
import { Card } from '../components/ui';
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `children` | `React.ReactNode` | required | Content to display inside the card |
| `className` | `string` | `''` | Additional CSS classes |
| `padding` | `'none' \| 'sm' \| 'md' \| 'lg'` | `'md'` | Padding size |

#### Usage

```tsx
// Basic card
<Card>
  <p>Card content goes here</p>
</Card>

// Card with custom padding
<Card padding="lg">
  <h2>Large Padding</h2>
</Card>

// Card with no padding (useful for tables)
<Card padding="none">
  <table>...</table>
</Card>

// Card with additional styling
<Card className="mt-4">
  <p>Custom margin</p>
</Card>
```

---

### CardHeader

A header component for cards that provides consistent title, subtitle, icon, and action button styling.

#### Import

```typescript
import { CardHeader } from '../components/ui';
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `title` | `string` | required | Main title text |
| `subtitle` | `string` | optional | Subtitle text displayed below title |
| `icon` | `React.ReactNode` | optional | Icon component (from lucide-react) |
| `action` | `React.ReactNode` | optional | Action button or element on the right |

#### Usage

```tsx
import { Card, CardHeader } from '../components/ui';
import { Users, Settings } from 'lucide-react';

// Basic header
<Card>
  <CardHeader title="Users" />
  <p>Content here</p>
</Card>

// With subtitle and icon
<Card>
  <CardHeader 
    title="User Management" 
    subtitle="Manage all users in the system"
    icon={<Users size={20} />}
  />
  <p>Content here</p>
</Card>

// With action button
<Card>
  <CardHeader 
    title="Settings"
    subtitle="Configure application settings"
    icon={<Settings size={20} />}
    action={<Button variant="secondary">Edit</Button>}
  />
  <p>Content here</p>
</Card>
```

---

### Button

A button component with multiple variants and built-in loading states.

#### Import

```typescript
import { Button } from '../components/ui';
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `variant` | `'primary' \| 'secondary' \| 'ghost' \| 'danger'` | `'primary'` | Button style variant |
| `isLoading` | `boolean` | `false` | Shows loading spinner when true |
| `icon` | `React.ReactNode` | optional | Icon component (from lucide-react) |
| `children` | `React.ReactNode` | required | Button text |
| `className` | `string` | `''` | Additional CSS classes |
| All standard button props | - | - | onClick, disabled, type, etc. |

#### Variants

- **primary**: Blue background, white text - Use for main actions
- **secondary**: White background, dark text, border - Use for secondary actions
- **ghost**: Transparent background - Use for subtle actions
- **danger**: Red background - Use for destructive actions

#### Usage

```tsx
import { Button } from '../components/ui';
import { Save, Trash2, Edit } from 'lucide-react';

// Primary button
<Button onClick={handleSave}>Save</Button>

// Button with icon
<Button icon={<Save size={18} />}>Save Changes</Button>

// Loading state
<Button isLoading={isSaving}>Saving...</Button>

// Secondary button
<Button variant="secondary" onClick={handleCancel}>Cancel</Button>

// Ghost button
<Button variant="ghost" icon={<Edit size={18} />}>Edit</Button>

// Danger button
<Button variant="danger" icon={<Trash2 size={18} />}>Delete</Button>

// Full width
<Button className="w-full">Submit</Button>

// Disabled
<Button disabled={!isValid}>Submit</Button>
```

---

### Badge

A badge component for displaying status, labels, or tags.

#### Import

```typescript
import { Badge } from '../components/ui';
```

#### Props

| Prop | Type | Default | Description |
|------|------|---------|-------------|
| `children` | `React.ReactNode` | required | Badge text |
| `variant` | `'success' \| 'warning' \| 'error' \| 'neutral' \| 'blue'` | `'neutral'` | Color variant |
| `className` | `string` | `''` | Additional CSS classes |

#### Variants

- **success**: Green background - Use for success states
- **warning**: Yellow background - Use for warnings
- **error**: Red background - Use for errors
- **neutral**: Gray background - Use for default/inactive states
- **blue**: Blue background - Use for info/active states

#### Usage

```tsx
import { Badge } from '../components/ui';

// Status badges
<Badge variant="success">Active</Badge>
<Badge variant="warning">Pending</Badge>
<Badge variant="error">Failed</Badge>
<Badge variant="neutral">Inactive</Badge>
<Badge variant="blue">New</Badge>

// In tables
<td>
  <Badge variant={status === 'paid' ? 'success' : 'warning'}>
    {status}
  </Badge>
</td>
```

---

## Usage Examples

### Example 1: Simple Page with Card

```tsx
import { Card, CardHeader, Button } from '../components/ui';
import { Users } from 'lucide-react';

export default function UsersPage() {
  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
      <Card>
        <CardHeader 
          title="Users" 
          subtitle="Manage system users"
          icon={<Users size={20} />}
          action={<Button>Add User</Button>}
        />
        <div className="space-y-4">
          {/* Your content here */}
        </div>
      </Card>
    </div>
  );
}
```

### Example 2: Page with Multiple Cards

```tsx
import { Card, CardHeader, Badge } from '../components/ui';
import { FileText, CheckCircle } from 'lucide-react';

export default function DashboardPage() {
  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
      {/* Page Header */}
      <div>
        <h1 className="text-3xl font-bold text-slate-900">Dashboard</h1>
        <p className="text-gray-500 mt-2">Overview of your system</p>
      </div>

      {/* Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <Card>
          <CardHeader 
            title="Documents" 
            icon={<FileText size={20} />}
            action={<Badge variant="blue">12</Badge>}
          />
          <p>Content here</p>
        </Card>

        <Card>
          <CardHeader 
            title="Completed Tasks" 
            icon={<CheckCircle size={20} />}
            action={<Badge variant="success">24</Badge>}
          />
          <p>Content here</p>
        </Card>
      </div>
    </div>
  );
}
```

### Example 3: Form Page

```tsx
import { Card, CardHeader, Button } from '../components/ui';
import { Save, X } from 'lucide-react';
import { useState } from 'react';

export default function CreateUserPage() {
  const [isLoading, setIsLoading] = useState(false);

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
      <Card>
        <CardHeader title="Create New User" />
        
        <form className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-slate-900 mb-1.5">
              Name
            </label>
            <input 
              type="text"
              className="w-full px-4 py-2.5 rounded-lg border border-gray-300 focus:ring-2 focus:ring-blue-100 focus:border-blue-500"
            />
          </div>

          <div className="flex gap-4 justify-end">
            <Button variant="secondary" icon={<X size={18} />}>
              Cancel
            </Button>
            <Button 
              isLoading={isLoading}
              icon={<Save size={18} />}
            >
              Save User
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
}
```

### Example 4: Table Page

```tsx
import { Card, Badge, Button } from '../components/ui';
import { FileText, Edit, Trash2 } from 'lucide-react';

export default function UsersListPage() {
  const users = [
    { id: 1, name: 'John Doe', status: 'active', email: 'john@example.com' },
    { id: 2, name: 'Jane Smith', status: 'inactive', email: 'jane@example.com' },
  ];

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
      <Card padding="none">
        <div className="px-6 py-4 border-b border-gray-100">
          <h2 className="text-lg font-bold text-slate-900">Users</h2>
        </div>
        
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Name
                </th>
                <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Email
                </th>
                <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Status
                </th>
                <th className="px-6 py-4 text-right text-xs font-medium text-gray-500 uppercase">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {users.map((user) => (
                <tr key={user.id} className="hover:bg-gray-50">
                  <td className="px-6 py-4 text-sm font-medium text-slate-900">
                    {user.name}
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">
                    {user.email}
                  </td>
                  <td className="px-6 py-4">
                    <Badge variant={user.status === 'active' ? 'success' : 'neutral'}>
                      {user.status}
                    </Badge>
                  </td>
                  <td className="px-6 py-4 text-right">
                    <div className="flex gap-2 justify-end">
                      <Button variant="ghost" icon={<Edit size={18} />} />
                      <Button variant="danger" icon={<Trash2 size={18} />} />
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
}
```

---

## Best Practices

### 1. Page Structure

Always wrap your page content in a container with consistent spacing:

```tsx
<div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
  {/* Page content */}
</div>
```

### 2. Card Usage

- Use `Card` for grouping related content
- Use `CardHeader` for consistent headers
- Use `padding="none"` for tables or custom layouts
- Use `padding="lg"` for content-heavy cards

### 3. Button Variants

- **Primary**: Main actions (Save, Submit, Create)
- **Secondary**: Secondary actions (Cancel, Back)
- **Ghost**: Subtle actions (Edit, View)
- **Danger**: Destructive actions (Delete, Remove)

### 4. Badge Usage

- Use badges for status indicators
- Match badge color to meaning (green = success, red = error)
- Keep badge text short (1-2 words)

### 5. Icons

- Use icons from `lucide-react` library
- Standard icon size: `18` for buttons, `20` for headers
- Always include icon size prop: `<Icon size={18} />`

### 6. Spacing

- Use `space-y-8` for vertical spacing between major sections
- Use `gap-4` or `gap-6` for grid/flex spacing
- Use `p-6` (md) as default padding for cards
- Use `mb-6` for spacing after headers

### 7. Typography

- Page titles: `text-3xl font-bold text-slate-900`
- Card titles: `text-lg font-bold text-slate-900`
- Subtitles: `text-sm text-gray-500`
- Body text: `text-sm` or default

---

## Page Structure Template

Use this template as a starting point for new pages:

```tsx
import { Card, CardHeader, Button, Badge } from '../components/ui';
import { YourIcon } from 'lucide-react';

export default function YourPageName() {
  // State and hooks here

  return (
    <div className="space-y-8 max-w-7xl mx-auto px-4 py-6">
      {/* Page Header */}
      <div className="flex flex-col md:flex-row justify-between md:items-end gap-4">
        <div>
          <h1 className="text-3xl font-bold text-slate-900">Page Title</h1>
          <p className="text-gray-500 mt-2">Page description</p>
        </div>
        {/* Optional header actions */}
        <Button>Action</Button>
      </div>

      {/* Main Content */}
      <Card>
        <CardHeader 
          title="Section Title"
          subtitle="Section description"
          icon={<YourIcon size={20} />}
          action={<Button variant="secondary">Action</Button>}
        />
        
        {/* Your content here */}
        <div className="space-y-4">
          {/* Content */}
        </div>
      </Card>

      {/* Additional sections */}
      <Card>
        {/* More content */}
      </Card>
    </div>
  );
}
```

---

## Additional Resources

- **Icons**: [Lucide React Icons](https://lucide.dev/icons/)
- **Tailwind CSS**: [Tailwind CSS Documentation](https://tailwindcss.com/docs)
- **Example Implementation**: See `src/pages/employer/Payroll.tsx` for a complete example

---

## Questions?

If you have questions about using these components or need to create a new component, please:
1. Check existing components in `src/components/ui/`
2. Review the Payroll page implementation
