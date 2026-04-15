import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { listingsAPI } from '../api/agriAPI';
import { IMAGE_BASE_URL } from '../api/apiClient';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

export default function MarketplacePage() {
  const { user, logout } = useAuth();
  const { addToCart } = useCart();
  const navigate = useNavigate();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [cartMessage, setCartMessage] = useState(null);

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

  const handleAddToCart = (e, listing) => {
    e.preventDefault();
    e.stopPropagation();

    if (!user) {
      navigate('/login');
      return;
    }

    addToCart({
      ...listing,
      quantity: 1
    });

    setCartMessage(listing.id);
    setTimeout(() => setCartMessage(null), 2000);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-green-600 mx-auto mb-4"></div>
          <p className="text-gray-600 font-medium">Loading farm equipment...</p>
        </div>
      </div>
    );
  }

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

      <div className="max-w-7xl mx-auto py-8 px-4 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl md:text-4xl font-bold bg-gradient-to-r from-green-600 to-blue-600 bg-clip-text text-transparent mb-2">
            FarmFlex Marketplace
          </h1>
          <p className="text-gray-600 text-lg">Discover premium agricultural equipment for rent or sale</p>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-xl flex items-center gap-3">
            <div className="flex-shrink-0">
              <svg className="w-5 h-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clipRule="evenodd" />
              </svg>
            </div>
            <p className="text-red-800 font-medium">{error}</p>
          </div>
        )}

        <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-6 mb-8">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-6 gap-4">
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-2">
                <span className="w-2 h-2 bg-blue-500 rounded-full"></span>
                Type
              </label>
              <select
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:ring-2 focus:ring-blue-500 focus:border-transparent transition"
                value={filters.type}
                onChange={(e) => updateFilter('type', e.target.value)}
              >
                <option value="ALL">All Types</option>
                <option value="RENT">For Rent</option>
                <option value="SALE">For Sale</option>
              </select>
            </div>
            
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-2">
                <span className="w-2 h-2 bg-green-500 rounded-full"></span>
                Category
              </label>
              <select
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:ring-2 focus:ring-green-500 focus:border-transparent transition"
                value={filters.category}
                onChange={(e) => updateFilter('category', e.target.value)}
              >
                {categories.map(cat => (
                  <option key={cat} value={cat}>
                    {cat === 'ALL' ? 'All Categories' : cat}
                  </option>
                ))}
              </select>
            </div>
            
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-2">
                <span className="w-2 h-2 bg-purple-500 rounded-full"></span>
                Location
              </label>
              <input
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:ring-2 focus:ring-purple-500 focus:border-transparent transition"
                value={filters.location}
                onChange={(e) => updateFilter('location', e.target.value)}
                placeholder="City or region"
              />
            </div>
            
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-2">
                <span className="w-2 h-2 bg-yellow-500 rounded-full"></span>
                Min Price
              </label>
              <input
                type="number"
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:ring-2 focus:ring-yellow-500 focus:border-transparent transition"
                value={filters.minPrice}
                onChange={(e) => updateFilter('minPrice', e.target.value)}
                placeholder="0"
              />
            </div>
            
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-2">
                <span className="w-2 h-2 bg-orange-500 rounded-full"></span>
                Max Price
              </label>
              <input
                type="number"
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:ring-2 focus:ring-orange-500 focus:border-transparent transition"
                value={filters.maxPrice}
                onChange={(e) => updateFilter('maxPrice', e.target.value)}
                placeholder="100000"
              />
            </div>
            
            <div className="space-y-2">
              <label className="text-sm font-semibold text-gray-700 flex items-center gap-2">
                <span className="w-2 h-2 bg-red-500 rounded-full"></span>
                Search
              </label>
              <input
                className="w-full border border-gray-300 rounded-xl px-4 py-3 focus:ring-2 focus:ring-red-500 focus:border-transparent transition"
                value={filters.search}
                onChange={(e) => updateFilter('search', e.target.value)}
                placeholder="Search equipment..."
              />
            </div>
          </div>
        </div>

        <div className="flex items-center justify-between mb-6">
          <p className="text-gray-600">
            <span className="font-semibold text-gray-900">{listings.length}</span> equipment{listings.length !== 1 ? 's' : ''} found
          </p>
          <button
            onClick={() => setFilters({
              type: 'ALL',
              category: 'ALL',
              location: '',
              minPrice: '',
              maxPrice: '',
              search: ''
            })}
            className="px-4 py-2 text-sm text-gray-600 hover:text-gray-900 border border-gray-300 rounded-lg hover:bg-gray-50 transition"
          >
            Clear Filters
          </button>
        </div>

        {listings.length === 0 ? (
          <div className="bg-white rounded-2xl shadow-lg border border-gray-100 p-12 text-center">
            <div className="mb-6">
              <div className="w-24 h-24 bg-gradient-to-br from-gray-200 to-gray-300 rounded-full flex items-center justify-center mx-auto mb-4">
                <span className="text-4xl">🚜</span>
              </div>
              <h3 className="text-xl font-semibold text-gray-900 mb-2">No equipment found</h3>
              <p className="text-gray-600">Try adjusting your filters or search terms to find what you're looking for.</p>
            </div>
            <button
              onClick={() => setFilters({
                type: 'ALL',
                category: 'ALL',
                location: '',
                minPrice: '',
                maxPrice: '',
                search: ''
              })}
              className="px-6 py-3 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl hover:shadow-lg transition font-medium"
            >
              Clear All Filters
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
            {listings.map((listing) => (
              <div
                key={listing.id}
                className="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden hover:shadow-2xl transition-all duration-300 transform hover:scale-[1.02] hover:-translate-y-1 flex flex-col group"
              >
                <Link
                  to={`/listings/${listing.id}`}
                  className="block flex-1"
                >
                  <div className="h-48 bg-gradient-to-br from-gray-100 to-gray-200 relative overflow-hidden">
                    {listing.imageUrls?.length ? (
                      <img
                        src={`${IMAGE_BASE_URL}${listing.imageUrls[0]}`}
                        alt={listing.title}
                        className="w-full h-full object-cover group-hover:scale-110 transition duration-500"
                        onError={(e) => { e.target.style.display = 'none'; }}
                      />
                    ) : (
                      <div className="w-full h-full flex items-center justify-center text-5xl bg-gradient-to-br from-green-100 to-blue-100">
                        🚜
                      </div>
                    )}
                    <div className="absolute top-3 right-3">
                      <span className={`px-3 py-1 text-xs font-bold rounded-full shadow-lg backdrop-blur-sm ${
                        listing.type === 'RENT' 
                          ? 'bg-blue-500/90 text-white' 
                          : 'bg-green-500/90 text-white'
                      }`}>
                        {listing.type}
                      </span>
                    </div>
                    <div className="absolute top-3 left-3">
                      <span className="px-2 py-1 text-xs font-semibold bg-white/90 text-gray-700 rounded-full shadow-sm">
                        {listing.category}
                      </span>
                    </div>
                  </div>
                  <div className="p-6 space-y-4 flex-1">
                    <div className="flex items-center justify-between">
                      <div className="flex items-center gap-2 text-gray-500">
                        <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clipRule="evenodd" />
                        </svg>
                        <span className="text-sm">{listing.location.split(',')[0]}</span>
                      </div>
                    </div>
                    
                    <h3 className="text-lg font-bold text-gray-900 line-clamp-2 group-hover:text-green-600 transition">
                      {listing.title}
                    </h3>
                    
                    <p className="text-gray-600 text-sm line-clamp-2 leading-relaxed">
                      {listing.description}
                    </p>
                    
                    <div className="flex justify-between items-center pt-3 border-t border-gray-100">
                      <div className="flex flex-col">
                        <span className="text-2xl font-bold bg-gradient-to-r from-green-500 to-green-600 bg-clip-text text-transparent">
                          ₹{listing.type === 'RENT' ? listing.rentPricePerDay : listing.salePrice}
                        </span>
                        <span className="text-xs text-gray-500">
                          {listing.type === 'RENT' ? 'per day' : 'total price'}
                        </span>
                      </div>
                    </div>
                  </div>
                </Link>

                {cartMessage === listing.id && (
                  <div className="px-6 py-3 bg-green-50 border-b border-green-200 flex items-center gap-3 animate-fade-in">
                    <div className="flex-shrink-0">
                      <svg className="w-5 h-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
                        <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                      </svg>
                    </div>
                    <p className="text-green-800 font-medium text-sm">Added to cart successfully!</p>
                  </div>
                )}

                <div className="px-6 pb-6 flex gap-3">
                  <button
                    onClick={(e) => handleAddToCart(e, listing)}
                    className="flex-1 px-4 py-3 bg-gradient-to-r from-blue-500 to-blue-600 text-white text-sm rounded-xl hover:shadow-lg transition-all duration-200 font-semibold hover:scale-105 flex items-center justify-center gap-2"
                  >
                    <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 3H6.28l-.31-1.243A1 1 0 005 1H3zM16 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0zM6.5 16.5a1.5 1.5 0 11-3 0 1.5 1.5 0 013 0z" />
                    </svg>
                    Add to Cart
                  </button>
                  <Link
                    to={`/listings/${listing.id}`}
                    className="flex-1 px-4 py-3 bg-gray-100 text-gray-700 text-sm rounded-xl hover:bg-gray-200 transition-all duration-200 font-semibold text-center hover:scale-105 flex items-center justify-center gap-2"
                  >
                    <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                      <path d="M10 12a2 2 0 100-4 2 2 0 000 4z" />
                      <path fillRule="evenodd" d="M.458 10C1.732 5.943 5.522 3 10 3s8.268 2.943 9.542 7c-1.274 4.057-5.064 7-9.542 7S1.732 14.057.458 10zM14 10a4 4 0 11-8 0 4 4 0 018 0z" clipRule="evenodd" />
                    </svg>
                    View Details
                  </Link>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
