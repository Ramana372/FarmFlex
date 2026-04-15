import React, { useState, useEffect } from 'react';
import { adminAPI } from '../api/authAPI';
import { useAuth } from '../context/AuthContext';

export default function AdminApprovalsPage() {
  const { logout } = useAuth();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [rejectionModal, setRejectionModal] = useState(null);
  const [rejectionReason, setRejectionReason] = useState('');

  useEffect(() => {
    fetchPendingListings();
  }, []);

  const fetchPendingListings = async () => {
    try {
      setLoading(true);
      setError('');
      console.log('Fetching pending listings...');
      const response = await adminAPI.getPendingListings();
      console.log('Pending listings response:', response);
      
      // Handle both direct array response and wrapped response
      let data = response.data || response;
      let listingsArray = Array.isArray(data) ? data : (data.listings || []);
      
      console.log('Listings array:', listingsArray);
      setListings(listingsArray);
    } catch (err) {
      console.error('Full error:', err);
      const errorMsg = err.response?.data?.error || err.message || 'Failed to load pending listings';
      setError(errorMsg);
      setListings([]);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (listingId) => {
    try {
      await adminAPI.approveListing(listingId);
      setListings(listings.filter(p => p.id !== listingId));
      alert('Listing approved successfully!');
    } catch (err) {
      alert('Failed to approve listing: ' + err.response?.data?.error);
    }
  };

  const handleReject = async (listingId) => {
    try {
      await adminAPI.rejectListing(listingId, rejectionReason);
      setListings(listings.filter(p => p.id !== listingId));
      setRejectionModal(null);
      setRejectionReason('');
      alert('Listing rejected successfully!');
    } catch (err) {
      alert('Failed to reject listing: ' + err.response?.data?.error);
    }
  };

  if (loading) return <div className="flex items-center justify-center min-h-screen">Loading...</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <h1 className="text-2xl font-bold text-green-600">FarmFlex</h1>
              <a href="/admin/dashboard" className="text-gray-700 hover:text-gray-900">
                Dashboard
              </a>
              <a href="/admin/listings/pending" className="text-gray-700 hover:text-gray-900 font-bold">
                Pending Reviews
              </a>
              <a href="/admin/dashboard" className="text-gray-700 hover:text-gray-900">
                Dashboard
              </a>
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
        <h2 className="text-3xl font-bold text-gray-800 mb-8">Pending Listing Approvals</h2>

        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        )}

        {listings.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-6 text-center text-gray-600">
            No pending listings to review
          </div>
        ) : (
          <div className="grid grid-cols-1 gap-6">
            {listings.map((listing) => (
              <div key={listing.id} className="bg-white rounded-lg shadow overflow-hidden hover:shadow-lg transition">
                <div className="p-6">
                  <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {/* Product Details */}
                    <div className="md:col-span-2">
                      <h3 className="text-2xl font-bold text-gray-800 mb-2">
                        {listing.title}
                      </h3>
                      <p className="text-gray-600 mb-4">{listing.description}</p>

                      <div className="grid grid-cols-2 gap-4 text-sm">
                        <div>
                          <span className="text-gray-600">Category:</span>
                          <p className="font-bold">{listing.category}</p>
                        </div>
                        <div>
                          <span className="text-gray-600">Type:</span>
                          <p className="font-bold">{listing.type}</p>
                        </div>
                        <div>
                          <span className="text-gray-600">Price:</span>
                          <p className="font-bold">₹{listing.type === 'RENT' ? listing.rentPricePerDay : listing.salePrice}</p>
                        </div>
                        <div>
                          <span className="text-gray-600">Location:</span>
                          <p className="font-bold">{listing.location}</p>
                        </div>
                      </div>

                      <div className="mt-4 p-3 bg-blue-50 rounded">
                        <p className="text-sm text-gray-700">
                          <span className="font-bold">Owner:</span> {listing.owner?.name}
                        </p>
                        <p className="text-sm text-gray-700">
                          <span className="font-bold">Email:</span> {listing.owner?.email}
                        </p>
                      </div>
                    </div>

                    <div className="flex flex-col justify-between">
                      <button
                        onClick={() => handleApprove(listing.id)}
                        className="w-full px-4 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold mb-3 transition"
                      >
                        ✓ Approve
                      </button>
                      <button
                        onClick={() => setRejectionModal(listing.id)}
                        className="w-full px-4 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 font-bold transition"
                      >
                        ✗ Reject
                      </button>

                      {rejectionModal === listing.id && (
                        <div className="mt-3 border-t pt-3">
                          <textarea
                            placeholder="Rejection reason..."
                            value={rejectionReason}
                            onChange={(e) => setRejectionReason(e.target.value)}
                            className="w-full p-2 border rounded text-sm mb-2"
                            rows="3"
                          />
                          <button
                            onClick={() => handleReject(listing.id)}
                            className="w-full px-3 py-2 bg-red-700 text-white rounded text-sm font-bold hover:bg-red-800"
                          >
                            Confirm Rejection
                          </button>
                          <button
                            onClick={() => {
                              setRejectionModal(null);
                              setRejectionReason('');
                            }}
                            className="w-full px-3 py-2 bg-gray-300 text-gray-700 rounded text-sm mt-1 hover:bg-gray-400"
                          >
                            Cancel
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
