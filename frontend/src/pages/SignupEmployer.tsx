import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { signupEmployer } from "../api/auth";
import type { AuthResponse } from "../types/auth";
import type { CreateEmployerRequest } from "../types/employer";
import { getAllBusinesses } from "../api/businesses";
import type { Company } from "../types/business";

export default function SignupEmployer() {
    const [formData, setFormData] = useState<CreateEmployerRequest>({
        name: "",
        email: "",
        password: "",
        salary: 0,
        department: "",
        title: "",
        companyId: 0,
        hireDate: "",
    });
    const [error, setError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [companies, setCompanies] = useState<Company[]>([]);
    const [isLoadingCompanies, setIsLoadingCompanies] = useState(false);
    const [companyError, setCompanyError] = useState<string | null>(null);

    const handleChange = (
        e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
    ) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]:
                name === "salary" || name === "companyId"
                    ? value === ""
                        ? 0
                        : Number(value)
                    : value,
        }));
    };

    // Load companies
    useEffect(() => {
        const loadCompanies = async () => {
            try {
                setIsLoadingCompanies(true);
                setCompanyError(null);
                const response = await getAllBusinesses();
                setCompanies(response.data);
            } catch (err: any) {
                console.error("Failed to load companies:", err);
                setCompanyError(
                    err.response?.data?.message ||
                    err.response?.data?.error ||
                    "Failed to load companies. Please refresh the page."
                );
            } finally {
                setIsLoadingCompanies(false);
            }
        };

        loadCompanies();
    }, []);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);
        setIsLoading(true);

        try {
            // Validate required fields
            if (
                !formData.name ||
                !formData.email ||
                !formData.password ||
                !formData.salary ||
                !formData.department ||
                !formData.title ||
                !formData.companyId ||
                !formData.hireDate
            ) {
                setError("Please fill in all required fields");
                setIsLoading(false);
                return;
            }

            const response = await signupEmployer(formData);
            const authData: AuthResponse = response.data;

            // Store token and user data
            localStorage.setItem("token", authData.token);
            localStorage.setItem(
                "user",
                JSON.stringify({
                    role: authData.role,
                    email: authData.email,
                    userId: authData.userId,
                    businessPersonId: authData.businessPersonId,
                })
            );

            setSuccess(true);
        } catch (err: any) {
            const errorMessage =
                err.response?.data?.message ||
                err.response?.data?.error ||
                err.message ||
                "Registration failed. Please try again.";
            setError(errorMessage);
        } finally {
            setIsLoading(false);
        }
    };

    if (success) {
        return (
            <div className="min-h-screen flex items-center justify-center bg-gray-50">
                <div className="max-w-md w-full space-y-6 p-8 bg-white rounded-lg shadow">
                    <div className="text-center">
                        <div className="mx-auto flex items-center justify-center h-12 w-12 rounded-full bg-green-100 mb-4">
                            <svg
                                className="h-6 w-6 text-green-600"
                                fill="none"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                                strokeWidth="2"
                                viewBox="0 0 24 24"
                                stroke="currentColor"
                            >
                                <path d="M5 13l4 4L19 7"></path>
                            </svg>
                        </div>
                        <h2 className="text-2xl font-bold text-gray-900 mb-2">
                            Successfully Sign Up!
                        </h2>
                        <p className="text-gray-600 mb-6">
                            Your employer account has been created successfully.
                        </p>
                        <Link
                            to="/login"
                            className="inline-block text-sm text-blue-600 hover:text-blue-800 hover:underline"
                        >
                            Back to Login
                        </Link>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50">
            <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow">
                <div className="flex items-center justify-between">
                    <h2 className="text-3xl font-bold text-gray-900">
                        Employer Sign Up
                    </h2>
                    <Link
                        to="/login"
                        className="text-sm text-gray-600 hover:text-gray-800 hover:underline"
                    >
                        Back to Login
                    </Link>
                </div>

                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    {error && (
                        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
                            {error}
                        </div>
                    )}

                    <div className="space-y-4">
                        <div>
                            <label
                                htmlFor="name"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Name <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="name"
                                name="name"
                                type="text"
                                required
                                value={formData.name}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                disabled={isLoading}
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="email"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Email <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="email"
                                name="email"
                                type="email"
                                required
                                value={formData.email}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                disabled={isLoading}
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="password"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Password <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                required
                                value={formData.password}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Enter password"
                                disabled={isLoading}
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="salary"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Salary <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="salary"
                                name="salary"
                                type="number"
                                step="0.01"
                                required
                                value={formData.salary || ""}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                placeholder="80000"
                                disabled={isLoading}
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="department"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Department <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="department"
                                name="department"
                                type="text"
                                required
                                value={formData.department}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Engineering"
                                disabled={isLoading}
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="title"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Title <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="title"
                                name="title"
                                type="text"
                                required
                                value={formData.title}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                placeholder="Senior Manager"
                                disabled={isLoading}
                            />
                        </div>

                        <div>
                            <label
                                htmlFor="companyId"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Company <span className="text-red-500">*</span>
                            </label>
                            <select
                                id="companyId"
                                name="companyId"
                                value={formData.companyId || ""}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                required
                                disabled={isLoading || isLoadingCompanies}
                            >
                                <option value="">Select a Company</option>
                                {companies.map((company) => (
                                    <option key={company.id} value={company.id}>
                                        {company.name} ({company.industry})
                                    </option>
                                ))}
                            </select>
                            {isLoadingCompanies && (
                                <p className="mt-1 text-sm text-gray-500">Loading companies...</p>
                            )}
                            {companyError && (
                                <p className="mt-1 text-sm text-red-500">{companyError}</p>
                            )}
                            {!isLoadingCompanies && !companyError && companies.length === 0 && (
                                <p className="mt-1 text-sm text-gray-500">
                                    No companies available. Please create a company first.
                                </p>
                            )}
                        </div>

                        <div>
                            <label
                                htmlFor="hireDate"
                                className="block text-sm font-medium text-gray-700"
                            >
                                Hire Date <span className="text-red-500">*</span>
                            </label>
                            <input
                                id="hireDate"
                                name="hireDate"
                                type="date"
                                required
                                value={formData.hireDate}
                                onChange={handleChange}
                                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                                disabled={isLoading}
                            />
                        </div>
                    </div>

                    <button
                        type="submit"
                        disabled={isLoading}
                        className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isLoading ? "Signing up..." : "Sign Up"}
                    </button>
                </form>
            </div>
        </div>
    );
}
