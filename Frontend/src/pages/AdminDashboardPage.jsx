import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminAPI } from '../api/authAPI';
import { useAuth } from '../context/AuthContext';

export default function AdminDashboardPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const response = await adminAPI.getDashboard();
        setStats(response.data);
      } catch (err) {
        setError('Failed to load dashboard');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, []);

  if (loading) return <div className="flex items-center justify-center min-h-screen">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation */}
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center gap-4">
              <button
                onClick={() => navigate('/')}
                className="text-gray-600 hover:text-gray-900 font-semibold flex items-center gap-2"
              >
                ← Back to Home
              </button>
              <h1 className="text-2xl font-bold text-green-600">FarmFlex</h1>
              <span className="text-gray-600">Admin Panel</span>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-gray-700">{user?.name}</span>
              <button
                onClick={() => {
                  logout();
                  window.location.href = '/login';
                }}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Logout
              </button>
            </div>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <h2 className="text-3xl font-bold text-gray-800 mb-8">Dashboard</h2>

        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        )}

        {/* Stats Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-500 text-sm font-medium">Pending Approvals</h3>
            <p className="text-4xl font-bold text-blue-600 mt-2">
              {stats?.pendingApprovals || 0}
            </p>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-500 text-sm font-medium">Total Farmers</h3>
            <p className="text-4xl font-bold text-green-600 mt-2">
              {stats?.totalFarmers || 0}
            </p>
          </div>

          <div className="bg-white rounded-lg shadow p-6">
            <h3 className="text-gray-500 text-sm font-medium">Live Listings</h3>
            <p className="text-4xl font-bold text-purple-600 mt-2">
              {stats?.totalLiveListings || 0}
            </p>
          </div>
        </div>

        {/* Recent Pending Listings */}
        <div className="bg-white rounded-lg shadow p-6">
          <h3 className="text-xl font-bold text-gray-800 mb-4">Recent Pending Approvals</h3>
          
          {stats?.recentPendingListings && stats.recentPendingListings.length > 0 ? (
            <div className="space-y-4">
              {stats.recentPendingListings.map((listing) => (
                <div key={listing.id} className="border rounded p-4 hover:bg-gray-50">
                  <div className="flex justify-between items-start">
                    <div>
                      <h4 className="font-bold text-gray-800">{listing.title}</h4>
                      <p className="text-gray-600 text-sm">Category: {listing.category}</p>
                      <p className="text-gray-600 text-sm">Type: {listing.type}</p>
                      <p className="text-gray-600 text-sm">Price: ₹{listing.type === 'RENT' ? listing.rentPricePerDay : listing.salePrice}</p>
                      <p className="text-gray-600 text-sm">Owner: {listing.owner?.name}</p>
                    </div>
                    <button
                      onClick={() => window.location.href = '/admin/listings/pending'}
                      className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                      Review
                    </button>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-gray-600">No pending approvals</p>
          )}
        </div>

        {/* Quick Actions */}
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-8">
          <a
            href="/admin/listings/pending"
            className="bg-blue-50 p-6 rounded-lg border border-blue-200 hover:shadow-lg transition"
          >
            <h3 className="text-xl font-bold text-blue-900">Review Listings</h3>
            <p className="text-blue-700 mt-2">Approve or reject equipment listings</p>
          </a>

          <div className="bg-green-50 p-6 rounded-lg border border-green-200">
            <h3 className="text-xl font-bold text-green-900">Manage Users</h3>
            <p className="text-green-700 mt-2">View and manage farmer accounts</p>
          </div>
        </div>
      </div>
    </div>
  );
}
