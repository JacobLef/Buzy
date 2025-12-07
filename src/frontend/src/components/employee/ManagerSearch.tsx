import { useState, useEffect, useRef } from 'react';
import { Search, X, User, Briefcase } from 'lucide-react';
import { getEmployeesByBusiness } from '../../api/employees';
import { getEmployersByBusiness } from '../../api/employers';
import type { Employee } from '../../types/employee';
import type { Employer } from '../../types/employer';

interface ManagerOption {
  id: number;
  name: string;
  type: 'EMPLOYEE' | 'EMPLOYER';
  position: string;
}

interface ManagerSearchProps {
  value: number | null;
  onChange: (managerId: number | null) => void;
  companyId: number;
  excludeId?: number; // Exclude self from results
}

export const ManagerSearch = ({
  value,
  onChange,
  companyId,
  excludeId
}: ManagerSearchProps) => {
  const [searchQuery, setSearchQuery] = useState('');
  const [options, setOptions] = useState<ManagerOption[]>([]);
  const [filteredOptions, setFilteredOptions] = useState<ManagerOption[]>([]);
  const [isOpen, setIsOpen] = useState(false);
  const [selectedManager, setSelectedManager] = useState<ManagerOption | null>(null);
  const searchRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  // Load all company members (employees + employers)
  useEffect(() => {
    const loadMembers = async () => {
      try {
        const [employeesRes, employersRes] = await Promise.all([
          getEmployeesByBusiness(companyId),
          getEmployersByBusiness(companyId),
        ]);

        const employeeOptions: ManagerOption[] = employeesRes.data
          .filter((emp: Employee) => emp.id !== excludeId)
          .map((emp: Employee) => ({
            id: emp.id,
            name: emp.name,
            type: 'EMPLOYEE' as const,
            position: emp.position,
          }));

        const employerOptions: ManagerOption[] = employersRes.data
          .filter((emp: Employer) => emp.id !== excludeId)
          .map((emp: Employer) => ({
            id: emp.id,
            name: emp.name,
            type: 'EMPLOYER' as const,
            position: emp.title,
          }));

        setOptions([...employerOptions, ...employeeOptions]);
      } catch (error) {
        console.error('Failed to load members:', error);
      }
    };

    if (companyId) {
      loadMembers();
    }
  }, [companyId, excludeId]);

  // Find selected manager
  useEffect(() => {
    if (value) {
      const found = options.find(opt => opt.id === value);
      setSelectedManager(found || null);
      if (found) {
        setSearchQuery(found.name);
      }
    } else {
      setSelectedManager(null);
      setSearchQuery('');
    }
  }, [value, options]);

  // Filter options based on search query
  useEffect(() => {
    if (!searchQuery.trim()) {
      setFilteredOptions([]);
      return;
    }

    const query = searchQuery.toLowerCase();
    const filtered = options.filter(
      opt =>
        opt.name.toLowerCase().includes(query) ||
        opt.position.toLowerCase().includes(query)
    );
    setFilteredOptions(filtered);
  }, [searchQuery, options]);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (searchRef.current && !searchRef.current.contains(event.target as Node)) {
        setIsOpen(false);
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const handleSelect = (option: ManagerOption) => {
    setSelectedManager(option);
    setSearchQuery(option.name);
    onChange(option.id);
    setIsOpen(false);
  };

  const handleClear = () => {
    setSelectedManager(null);
    setSearchQuery('');
    onChange(null);
    setIsOpen(false);
    inputRef.current?.focus();
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setSearchQuery(e.target.value);
    setIsOpen(true);
    if (!e.target.value) {
      onChange(null);
      setSelectedManager(null);
    }
  };

  const handleInputFocus = () => {
    setIsOpen(true);
  };

  return (
    <div className="relative" ref={searchRef}>
      <div className="relative">
        <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400" size={18} />
        <input
          ref={inputRef}
          type="text"
          value={searchQuery}
          onChange={handleInputChange}
          onFocus={handleInputFocus}
          placeholder="Search for manager by name..."
          className="w-full pl-10 pr-10 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
        />
        {selectedManager && (
          <button
            type="button"
            onClick={handleClear}
            className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 hover:text-gray-600"
          >
            <X size={18} />
          </button>
        )}
      </div>

      {isOpen && filteredOptions.length > 0 && (
        <div className="absolute z-50 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg max-h-60 overflow-auto">
          {filteredOptions.map(option => (
            <button
              key={`${option.type}-${option.id}`}
              type="button"
              onClick={() => handleSelect(option)}
              className="w-full text-left px-4 py-2 hover:bg-gray-50 flex items-center gap-3 transition-colors"
            >
              {option.type === 'EMPLOYER' ? (
                <Briefcase size={16} className="text-blue-600" />
              ) : (
                <User size={16} className="text-gray-600" />
              )}
              <div className="flex-1">
                <div className="font-medium text-slate-900">{option.name}</div>
                <div className="text-xs text-gray-500">{option.position}</div>
              </div>
              {option.type === 'EMPLOYER' && (
                <span className="text-xs px-2 py-0.5 bg-blue-50 text-blue-700 rounded">
                  Employer
                </span>
              )}
            </button>
          ))}
        </div>
      )}

      {isOpen && searchQuery && filteredOptions.length === 0 && (
        <div className="absolute z-50 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg p-4 text-center text-gray-500 text-sm">
          No managers found matching "{searchQuery}"
        </div>
      )}
    </div>
  );
};

