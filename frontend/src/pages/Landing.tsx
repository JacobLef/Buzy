import { Link } from "react-router-dom";

export default function Landing() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-indigo-50 to-purple-50 flex items-center justify-center p-4 relative overflow-hidden">
      {/* Subtle background decoration */}
      <div className="absolute inset-0 overflow-hidden pointer-events-none">
        <div className="absolute -top-40 -right-40 w-80 h-80 bg-indigo-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-blob"></div>
        <div className="absolute -bottom-40 -left-40 w-80 h-80 bg-purple-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-blob-delayed"></div>
        <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 w-80 h-80 bg-blue-200 rounded-full mix-blend-multiply filter blur-xl opacity-30 animate-blob-delayed-more"></div>
      </div>

      <div className="max-w-4xl w-full text-center relative z-10">
        {/* Logo/Title */}
        <div className="mb-8">
          <h1 className="text-7xl font-extrabold text-transparent bg-clip-text bg-gradient-to-r from-indigo-600 to-purple-600 mb-2 tracking-tight">
            Buzy
          </h1>
          <div className="h-1 w-24 bg-gradient-to-r from-indigo-600 to-purple-600 mx-auto rounded-full"></div>
        </div>

        {/* Main description */}
        <p className="text-2xl text-gray-700 mb-4 leading-relaxed font-light max-w-3xl mx-auto">
          Are you too <span className="font-semibold text-indigo-700">buzy</span> to manage your employees' information
        </p>
        <p className="text-2xl text-gray-700 mb-12 leading-relaxed font-light max-w-3xl mx-auto">
          yet too small to hire an entire HR team?
        </p>

        {/* Feature highlight box */}
        <div className="bg-white/60 backdrop-blur-sm rounded-2xl p-8 mb-12 shadow-xl border border-white/20 max-w-2xl mx-auto">
          <p className="text-lg text-gray-600 leading-relaxed">
            Buzy lets you manage your <span className="font-semibold text-indigo-600">employees</span>, <span className="font-semibold text-indigo-600">payroll</span>, and <span className="font-semibold text-indigo-600">training</span> all in one place.
          </p>
        </div>

        {/* CTA Button */}
        <div className="flex gap-4 justify-center">
          <Link
            to="/login"
            className="group relative bg-gradient-to-r from-indigo-600 to-purple-600 text-white px-10 py-4 rounded-xl font-semibold text-lg hover:from-indigo-700 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-2xl hover:scale-105"
          >
            Get Started
          </Link>
        </div>

        <p className="mt-8 text-sm text-gray-500 font-light">
          Simple HR management for small businesses
        </p>
      </div>
    </div>
  );
}
