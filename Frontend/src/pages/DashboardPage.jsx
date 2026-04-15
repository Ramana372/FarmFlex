import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function DashboardPage() {
  const { user, logout, loading } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navigateTo = (path) => {
    navigate(path);
  };

  if (loading) {
    return <div className="flex items-center justify-center min-h-screen"><p>Loading...</p></div>;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-gradient-to-r from-green-600 to-green-700 shadow-lg">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/')}
                className="text-white hover:bg-green-700 px-3 py-2 rounded-lg font-semibold flex items-center gap-2 transition"
              >
                ← Back to Home
              </button>
              <h1 className="text-2xl font-bold text-white">FarmFlex</h1>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-white font-semibold">{user?.name}</span>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 font-semibold transition"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      <div className="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 py-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="bg-white rounded-2xl shadow-xl p-8 border border-gray-100 mb-8">
            <h2 className="text-4xl font-bold text-gray-900 mb-2">Welcome back, <span className="text-green-600">{user?.name}</span>! 👋</h2>
            <p className="text-gray-600">Manage your agricultural equipment marketplace</p>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {user?.role === 'FARMER' && (
              <>
                <div className="bg-white rounded-xl shadow-md p-6 border-t-4 border-blue-500 hover:shadow-xl transition transform hover:scale-105 duration-300">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-2xl font-bold text-gray-900">My Listings</h3>
                    <span className="text-3xl">📋</span>
                  </div>
                  <p className="text-gray-600 mb-6">Manage your equipment listings</p>
                  <button 
                    onClick={() => navigateTo('/farmer/listings')}
                    className="w-full px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg hover:shadow-lg font-semibold transition"
                  >
                    View Listings →
                  </button>
                </div>

                <div className="bg-white rounded-xl shadow-md p-6 border-t-4 border-green-500 hover:shadow-xl transition transform hover:scale-105 duration-300">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-2xl font-bold text-gray-900">Create Listing</h3>
                    <span className="text-3xl">➕</span>
                  </div>
                  <p className="text-gray-600 mb-6">Post equipment for rent or sale</p>
                  <button 
                    onClick={() => navigateTo('/listings/create')}
                    className="w-full px-4 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-lg hover:shadow-lg font-semibold transition"
                  >
                    Create Listing →
                  </button>
                </div>

                <div className="bg-white rounded-xl shadow-md p-6 border-t-4 border-orange-500 hover:shadow-xl transition transform hover:scale-105 duration-300">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-2xl font-bold text-gray-900">Marketplace</h3>
                    <span className="text-3xl">🏪</span>
                  </div>
                  <p className="text-gray-600 mb-6">Browse live equipment listings</p>
                  <button 
                    onClick={() => navigateTo('/marketplace')}
                    className="w-full px-4 py-3 bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-lg hover:shadow-lg font-semibold transition"
                  >
                    Browse Listings →
                  </button>
                </div>
              </>
            )}

            {user?.role === 'ADMIN' && (
              <>
                <div className="bg-white rounded-xl shadow-md p-6 border-t-4 border-indigo-500 hover:shadow-xl transition transform hover:scale-105 duration-300">
                  <div className="flex items-center justify-between mb-4">
                    <h3 className="text-2xl font-bold text-gray-900">Admin Dashboard</h3>
                    <span className="text-3xl">⚙️</span>
                  </div>
                  <p className="text-gray-600 mb-6">View platform statistics</p>
                  <button 
                    onClick={() => navigateTo('/admin/dashboard')}
                    className="w-full px-4 py-3 bg-gradient-to-r from-indigo-500 to-indigo-600 text-white rounded-lg hover:shadow-lg font-semibold transition"
                  >
                    View Dashboard
                  </button>
                </div>

                <div className="bg-pink-50 p-6 rounded-lg border border-pink-200 hover:shadow-lg transition">
                  <h3 className="text-xl font-bold text-pink-900">Approve Listings</h3>
                  <p className="text-gray-600 mt-2">Review pending equipment listings</p>
                  <button 
                    onClick={() => navigateTo('/admin/listings/pending')}
                    className="mt-4 px-4 py-2 bg-pink-600 text-white rounded hover:bg-pink-700"
                  >
                    View Approvals
                  </button>
                </div>

                <div className="bg-purple-50 p-6 rounded-lg border border-purple-200 hover:shadow-lg transition">
                  <h3 className="text-xl font-bold text-purple-900">Manage Farmers</h3>
                  <p className="text-gray-600 mt-2">View farmer accounts and activity</p>
                  <button 
                    onClick={() => alert('Coming soon!')}
                    className="mt-4 px-4 py-2 bg-purple-600 text-white rounded hover:bg-purple-700"
                  >
                    Manage Farmers
                  </button>
                </div>
              </>
            )}
          </div>

          <div className="mt-12 p-6 bg-gray-100 rounded-lg">
            <h3 className="text-lg font-bold text-gray-800 mb-4">Profile Information</h3>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-gray-600 font-medium">Name:</p>
                <p className="text-gray-800">{user?.name}</p>
              </div>
              <div>
                <p className="text-gray-600 font-medium">Email:</p>
                <p className="text-gray-800">{user?.email}</p>
              </div>
              <div>
                <p className="text-gray-600 font-medium">Phone:</p>
                <p className="text-gray-800">{user?.phone || '—'}</p>
              </div>
              <div>
                <p className="text-gray-600 font-medium">Location:</p>
                <p className="text-gray-800">{user?.location || '—'}</p>
              </div>
              <div>
                <p className="text-gray-600 font-medium">Role:</p>
                <p className="text-gray-800">{user?.role}</p>
              </div>
              <div>
                <p className="text-gray-600 font-medium">User ID:</p>
                <p className="text-gray-800">{user?.userId}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
