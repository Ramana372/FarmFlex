import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { listingsAPI } from '../api/authAPI';
import { IMAGE_BASE_URL } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';

export default function MarketplacePage() {
  const { user, logout } = useAuth();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [filters, setFilters] = useState({
    type: 'ALL',
    category: 'ALL',
    location: '',
    minPrice: '',
    maxPrice: '',
    search: ''
  });

  const categories = ['ALL', 'TRACTOR', 'HARVESTER', 'PLOUGH', 'SEEDER', 'SPRAYER', 'THRESHER', 'OTHER'];

  useEffect(() => {
    fetchListings();
  }, [filters]);

  const fetchListings = async () => {
    try {
      setLoading(true);
      const params = {
        type: filters.type === 'ALL' ? undefined : filters.type,
        category: filters.category === 'ALL' ? undefined : filters.category,
        location: filters.location || undefined,
        minPrice: filters.minPrice || undefined,
        maxPrice: filters.maxPrice || undefined,
        search: filters.search || undefined
      };
      const response = await listingsAPI.getLive(params);
      setListings(response.data);
    } catch (err) {
      setError('Failed to load listings');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const updateFilter = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  if (loading) return <div className="flex items-center justify-center min-h-screen">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <Link to="/" className="text-2xl font-bold text-green-600">FarmFlex</Link>
              <Link to="/marketplace" className="text-gray-700 hover:text-gray-900 font-bold">Flex Your Farm</Link>
              {user?.role === 'FARMER' && (
                <Link to="/farmer/dashboard" className="text-gray-700 hover:text-gray-900">
                  Farmer Dashboard
                </Link>
              )}
            </div>
            {user ? (
              <button
                onClick={() => {
                  logout();
                  window.location.href = '/login';
                }}
                className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
              >
                Logout
              </button>
            ) : (
              <Link to="/login" className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700">
                Sign In
              </Link>
            )}
          </div>
        </div>
      </nav>

      <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <h2 className="text-3xl font-bold text-gray-800 mb-8">Explore FarmFlex - Flex Your Farm your Way</h2>

        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        )}

        <div className="bg-white rounded-lg shadow p-6 mb-8 grid grid-cols-1 md:grid-cols-6 gap-4">
          <div>
            <label className="text-sm font-semibold text-gray-600">Type</label>
            <select
              className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
              value={filters.type}
              onChange={(e) => updateFilter('type', e.target.value)}
            >
              <option value="ALL">All</option>
              <option value="RENT">Rent</option>
              <option value="SALE">Sale</option>
            </select>
          </div>
          <div>
            <label className="text-sm font-semibold text-gray-600">Category</label>
            <select
              className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
              value={filters.category}
              onChange={(e) => updateFilter('category', e.target.value)}
            >
              {categories.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-sm font-semibold text-gray-600">Location</label>
            <input
              className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
              value={filters.location}
              onChange={(e) => updateFilter('location', e.target.value)}
              placeholder="City or region"
            />
          </div>
          <div>
            <label className="text-sm font-semibold text-gray-600">Min Price</label>
            <input
              type="number"
              className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
              value={filters.minPrice}
              onChange={(e) => updateFilter('minPrice', e.target.value)}
              placeholder="0"
            />
          </div>
          <div>
            <label className="text-sm font-semibold text-gray-600">Max Price</label>
            <input
              type="number"
              className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
              value={filters.maxPrice}
              onChange={(e) => updateFilter('maxPrice', e.target.value)}
              placeholder="100000"
            />
          </div>
          <div>
            <label className="text-sm font-semibold text-gray-600">Search</label>
            <input
              className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
              value={filters.search}
              onChange={(e) => updateFilter('search', e.target.value)}
              placeholder="Search title"
            />
          </div>
        </div>

        <p className="text-gray-600 mb-6">Showing {listings.length} listing{listings.length !== 1 ? 's' : ''}</p>

        {listings.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <p className="text-gray-600 text-lg">No listings available yet.</p>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {listings.map((listing) => (
              <Link
                key={listing.id}
                to={`/listings/${listing.id}`}
                className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-2xl transition transform hover:scale-105 duration-300 border border-gray-100"
              >
                <div className="h-48 bg-gradient-to-br from-gray-200 to-gray-300 relative overflow-hidden">
                  {listing.imageUrls?.length ? (
                    <img
                      src={`${IMAGE_BASE_URL}${listing.imageUrls[0]}`}
                      alt={listing.title}
                      className="w-full h-full object-cover hover:scale-110 transition duration-300"
                      onError={(e) => { e.target.style.display = 'none'; }}
                    />
                  ) : (
                    <div className="w-full h-full flex items-center justify-center text-5xl">🚜</div>
                  )}
                  <div className="absolute top-3 right-3">
                    <span className={`px-3 py-1 text-xs font-bold rounded-full shadow-md ${listing.type === 'RENT' ? 'bg-blue-500 text-white' : 'bg-green-500 text-white'}`}>
                      {listing.type}
                    </span>
                  </div>
                </div>
                <div className="p-5 space-y-3">
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-semibold text-gray-500 uppercase tracking-wide">{listing.category}</span>
                    <span className="text-xs text-gray-400">📍 {listing.location.split(',')[0]}</span>
                  </div>
                  <h3 className="text-lg font-bold text-gray-800 line-clamp-2 hover:text-green-600">
                    {listing.title}
                  </h3>
                  <p className="text-gray-600 text-sm line-clamp-2">{listing.description}</p>
                  <div className="flex justify-between items-center pt-2 border-t border-gray-100">
                    <span className="text-xl font-bold bg-gradient-to-r from-green-500 to-green-600 bg-clip-text text-transparent">
                      ₹{listing.type === 'RENT' ? listing.rentPricePerDay : listing.salePrice}</span>
                    <span className="text-xs text-gray-500">{listing.type === 'RENT' ? '/day' : ''}</span>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
