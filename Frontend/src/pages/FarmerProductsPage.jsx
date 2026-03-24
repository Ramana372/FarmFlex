import React, { useEffect, useState } from 'react';
import { listingsAPI } from '../api/authAPI';
import { useAuth } from '../context/AuthContext';

export default function FarmerProductsPage() {
  const { logout } = useAuth();
  const [grouped, setGrouped] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchMyListings();
  }, []);

  const fetchMyListings = async () => {
    try {
      setLoading(true);
      const response = await listingsAPI.getMyGrouped();
      setGrouped(response.data);
    } catch (err) {
      setError('Failed to load your listings');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const statusOrder = ['PENDING', 'LIVE', 'REJECTED', 'SOLD', 'BOOKED'];
  const statusLabel = {
    PENDING: 'Pending',
    LIVE: 'Live',
    REJECTED: 'Rejected',
    SOLD: 'Sold',
    BOOKED: 'Booked'
  };

  const badgeStyle = (status) => {
    const styles = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      LIVE: 'bg-green-100 text-green-800',
      REJECTED: 'bg-red-100 text-red-800',
      SOLD: 'bg-gray-200 text-gray-700',
      BOOKED: 'bg-blue-100 text-blue-700'
    };
    return styles[status] || 'bg-gray-100 text-gray-800';
  };

  if (loading) return <div className="flex items-center justify-center min-h-screen">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <a href="/" className="text-2xl font-bold text-green-600">FarmFlex</a>
              <a href="/farmer/dashboard" className="text-gray-700 hover:text-gray-900">Dashboard</a>
              <a href="/farmer/listings" className="text-gray-700 hover:text-gray-900 font-bold">My Listings</a>
              <a href="/listings/create" className="text-gray-700 hover:text-gray-900">Create Listing</a>
              <a href="/marketplace" className="text-gray-700 hover:text-gray-900">Marketplace</a>
            </div>
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
      </nav>

      <div className="max-w-7xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center mb-8">
          <h2 className="text-3xl font-bold text-gray-800">My Listings</h2>
          <a
            href="/listings/create"
            className="px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold"
          >
            + Create Listing
          </a>
        </div>

        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        )}

        {statusOrder.map((status) => {
          const listings = grouped[status] || [];
          return (
            <div key={status} className="mb-10">
              <div className="flex items-center justify-between mb-4">
                <h3 className="text-xl font-bold text-gray-800">
                  {statusLabel[status]} ({listings.length})
                </h3>
              </div>

              {listings.length === 0 ? (
                <div className="bg-white rounded-lg shadow p-6 text-gray-500">No listings in this status.</div>
              ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                  {listings.map((listing) => (
                    <div key={listing.id} className="bg-white rounded-lg shadow overflow-hidden hover:shadow-lg transition">
                      <div className="h-40 bg-gray-100">
                        {listing.imageUrls?.length ? (
                          <img src={listing.imageUrls[0]} alt={listing.title} className="w-full h-full object-cover" />
                        ) : (
                          <div className="w-full h-full flex items-center justify-center text-4xl">🚜</div>
                        )}
                      </div>
                      <div className="p-5">
                        <div className="flex justify-between items-start mb-3">
                          <h4 className="text-lg font-bold text-gray-800 flex-1">{listing.title}</h4>
                          <span className={`px-3 py-1 rounded-full text-sm font-bold whitespace-nowrap ml-2 ${badgeStyle(listing.status)}`}>
                            {listing.status}
                          </span>
                        </div>
                        <p className="text-gray-600 text-sm mb-3 line-clamp-2">{listing.description}</p>
                        <div className="text-sm text-gray-700 space-y-1">
                          <p><span className="font-bold">Category:</span> {listing.category}</p>
                          <p><span className="font-bold">Type:</span> {listing.type}</p>
                          <p><span className="font-bold">Price:</span> ₹{listing.type === 'RENT' ? listing.rentPricePerDay : listing.salePrice}{listing.type === 'RENT' && '/day'}</p>
                          <p><span className="font-bold">Location:</span> {listing.location}</p>
                        </div>
                        {listing.status === 'REJECTED' && listing.rejectionReason && (
                          <div className="mt-3 p-2 bg-red-50 border border-red-200 rounded text-sm">
                            <p className="text-red-800">
                              <span className="font-bold">Rejection Reason:</span> {listing.rejectionReason}
                            </p>
                          </div>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
