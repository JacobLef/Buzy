import { useState } from "react";
import { Link } from "react-router-dom";
import { Building, ArrowLeft } from "lucide-react";
import { createBusiness } from "../api/businesses";
import type { CreateBusinessRequest } from "../types/business";

export default function CreateCompany() {
  const [formData, setFormData] = useState<CreateBusinessRequest>({
    name: "",
    address: "",
    industry: "",
    foundedDate: "",
  });
  const [customIndustry, setCustomIndustry] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [createdCompanyId, setCreatedCompanyId] = useState<number | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    
    if (name === "industry" && value !== "Other") {
      setCustomIndustry("");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setIsLoading(true);

    try {
      const finalIndustry = formData.industry === "Other" ? customIndustry.trim() : formData.industry;

      if (!formData.name || !formData.address || !finalIndustry || !formData.foundedDate) {
        setError("Please fill in all required fields");
        setIsLoading(false);
        return;
      }

      if (formData.industry === "Other" && !customIndustry.trim()) {
        setError("Please specify your industry");
        setIsLoading(false);
        return;
      }

      const submitData = {
        ...formData,
        industry: finalIndustry,
      };

      const response = await createBusiness(submitData);
      setCreatedCompanyId(response.data.id);
      setSuccess(true);
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message ||
        err.response?.data?.error ||
        err.message ||
        "Failed to create company. Please try again.";
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
              Company Created!
            </h2>
            <p className="text-gray-600 mb-2">
              Your company has been created successfully.
            </p>
            {createdCompanyId && (
              <p className="text-sm text-gray-500 mb-6">
                Company ID: <span className="font-mono font-semibold">{createdCompanyId}</span>
                <br />
                <span className="text-xs">(Use this ID when creating your employer account)</span>
              </p>
            )}
            <div className="space-y-3">
              <Link
                to="/signup/employer"
                className="block w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 text-center"
              >
                Create Employer Account
              </Link>
              <Link
                to="/login"
                className="block text-sm text-gray-600 hover:text-gray-800 hover:underline"
              >
                Back to Login
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 bg-blue-100 rounded-lg">
              <Building className="h-6 w-6 text-blue-600" />
            </div>
            <h2 className="text-2xl font-bold text-gray-900">
              Create Company
            </h2>
          </div>
          <Link
            to="/login"
            className="flex items-center gap-1 text-sm text-gray-600 hover:text-gray-800"
          >
            <ArrowLeft size={16} />
            Back
          </Link>
        </div>

        <p className="text-gray-600 text-sm">
          Create a new company first, then you can create employer and employee accounts for it.
        </p>

        <form className="mt-6 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded text-sm">
              {error}
            </div>
          )}

          <div className="space-y-4">
            <div>
              <label
                htmlFor="name"
                className="block text-sm font-medium text-gray-700"
              >
                Company Name <span className="text-red-500">*</span>
              </label>
              <input
                id="name"
                name="name"
                type="text"
                required
                value={formData.name}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="Acme Corporation"
                disabled={isLoading}
              />
            </div>

            <div>
              <label
                htmlFor="address"
                className="block text-sm font-medium text-gray-700"
              >
                Address <span className="text-red-500">*</span>
              </label>
              <input
                id="address"
                name="address"
                type="text"
                required
                value={formData.address}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                placeholder="123 Main St, Boston, MA 02101"
                disabled={isLoading}
              />
            </div>

            <div>
              <label
                htmlFor="industry"
                className="block text-sm font-medium text-gray-700"
              >
                Industry <span className="text-red-500">*</span>
              </label>
              <select
                id="industry"
                name="industry"
                required
                value={formData.industry}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                disabled={isLoading}
              >
                <option value="">Select an industry</option>
                <option value="Technology">Technology</option>
                <option value="Healthcare">Healthcare</option>
                <option value="Finance">Finance</option>
                <option value="Manufacturing">Manufacturing</option>
                <option value="Retail">Retail</option>
                <option value="Education">Education</option>
                <option value="Construction">Construction</option>
                <option value="Transportation">Transportation</option>
                <option value="Hospitality">Hospitality</option>
                <option value="Other">Other</option>
              </select>
            </div>

            {formData.industry === "Other" && (
              <div>
                <label
                  htmlFor="customIndustry"
                  className="block text-sm font-medium text-gray-700"
                >
                  Specify Industry <span className="text-red-500">*</span>
                </label>
                <input
                  id="customIndustry"
                  name="customIndustry"
                  type="text"
                  required
                  value={customIndustry}
                  onChange={(e) => setCustomIndustry(e.target.value)}
                  className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
                  placeholder="Enter your industry"
                  disabled={isLoading}
                />
              </div>
            )}

            <div>
              <label
                htmlFor="foundedDate"
                className="block text-sm font-medium text-gray-700"
              >
                Founded Date <span className="text-red-500">*</span>
              </label>
              <input
                id="foundedDate"
                name="foundedDate"
                type="date"
                required
                value={formData.foundedDate}
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
            {isLoading ? "Creating..." : "Create Company"}
          </button>
        </form>
      </div>
    </div>
  );
}