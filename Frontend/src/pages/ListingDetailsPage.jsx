import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { listingsAPI, paymentsAPI } from '../api/authAPI';
import { useAuth } from '../context/AuthContext';
import { IMAGE_BASE_URL } from '../api/apiClient';

const loadRazorpay = () =>
  new Promise((resolve) => {
    if (document.getElementById('razorpay-script')) {
      resolve(true);
      return;
    }
    const script = document.createElement('script');
    script.id = 'razorpay-script';
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.onload = () => resolve(true);
    script.onerror = () => resolve(false);
    document.body.appendChild(script);
  });

export default function ListingDetailsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [listing, setListing] = useState(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const [activeImage, setActiveImage] = useState(0);
  const [rentStartDate, setRentStartDate] = useState('');
  const [rentEndDate, setRentEndDate] = useState('');

  useEffect(() => {
    fetchListing();
  }, [id]);

  const fetchListing = async () => {
    try {
      setLoading(true);
      const response = await listingsAPI.getById(id);
      setListing(response.data);
    } catch (err) {
      setError('Failed to load listing');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const [showPaymentModal, setShowPaymentModal] = useState(false);

  const handlePayment = () => {
    setError('');
    if (!user) {
      navigate('/login');
      return;
    }

    if (listing.type === 'RENT' && (!rentStartDate || !rentEndDate)) {
      setError('Please select rental dates');
      return;
    }

    setShowPaymentModal(true);
  };

  const handleUPIPayment = async () => {
    try {
      // Create order first
      const orderResponse = await paymentsAPI.createOrder({
        listingId: listing.id,
        rentStartDate: listing.type === 'RENT' ? rentStartDate : null,
        rentEndDate: listing.type === 'RENT' ? rentEndDate : null
      });

      const { orderId } = orderResponse.data;

      // Confirm payment (simulate UPI payment)
      await paymentsAPI.confirmPayment({
        listingId: listing.id,
        orderId: orderId,
        paymentId: 'UPI_' + Date.now(),
        signature: 'upi_verified',
        rentStartDate: listing.type === 'RENT' ? rentStartDate : null,
        rentEndDate: listing.type === 'RENT' ? rentEndDate : null
      });

      setShowPaymentModal(false);
      alert('✅ Payment successful! Your order has been placed.');
      navigate('/marketplace');
    } catch (err) {
      setError('Payment failed. Please try again.');
      console.error(err);
    }
  };

  if (loading) return <div className="flex items-center justify-center min-h-screen">Loading...</div>;
  if (!listing) return <div className="flex items-center justify-center min-h-screen">Listing not found</div>;

  const price = listing.type === 'RENT' ? listing.rentPricePerDay : listing.salePrice;

  return (
    <div className="min-h-screen bg-gray-50">
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16 items-center">
            <Link to="/" className="text-2xl font-bold text-green-600">AgriBuy</Link>
            <Link to="/marketplace" className="text-gray-700 hover:text-gray-900">Marketplace</Link>
          </div>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        {error && (
          <div className="mb-4 p-3 bg-red-100 text-red-700 rounded">{error}</div>
        )}

        <div className="bg-white rounded-2xl shadow-xl p-8 grid grid-cols-1 lg:grid-cols-2 gap-8 border border-gray-100">
          <div>
            <div className="bg-gradient-to-br from-gray-200 to-gray-300 rounded-xl overflow-hidden h-80 flex items-center justify-center shadow-lg">
              {listing.imageUrls?.length ? (
                <img
                  src={`${IMAGE_BASE_URL}${listing.imageUrls[activeImage]}`}
                  alt={listing.title}
                  className="w-full h-full object-cover hover:scale-105 transition duration-300"
                  onError={(e) => { e.target.style.display = 'none'; }}
                />
              ) : (
                <div className="text-7xl">🚜</div>
              )}
            </div>
            {listing.imageUrls?.length > 1 && (
              <div className="flex gap-2 mt-6 overflow-x-auto pb-2">
                {listing.imageUrls.map((url, index) => (
                  <button
                    key={url}
                    onClick={() => setActiveImage(index)}
                    className={`w-20 h-20 rounded-lg border-2 overflow-hidden flex-shrink-0 transition-all ${activeImage === index ? 'border-green-500 shadow-lg scale-105' : 'border-gray-300 hover:border-gray-400'}`}
                  >
                    <img src={`${IMAGE_BASE_URL}${url}`} alt="thumb" className="w-full h-full object-cover" />
                  </button>
                ))}
              </div>
            )}
          </div>

          <div className="space-y-6">
            <div className="flex items-center gap-3">
              <span className={`px-4 py-2 rounded-full text-sm font-bold shadow-md ${listing.type === 'RENT' ? 'bg-blue-500 text-white' : 'bg-green-500 text-white'}`}>
                {listing.type}
              </span>
              <span className="px-4 py-2 bg-gray-100 text-gray-700 rounded-full text-sm font-semibold">Status: {listing.status}</span>
            </div>
            <h1 className="text-4xl font-bold text-gray-900">{listing.title}</h1>
            <p className="text-gray-600 text-lg leading-relaxed">{listing.description}</p>

            <div className="grid grid-cols-2 gap-6 bg-gray-50 rounded-xl p-6">
              <div>
                <p className="text-gray-500 text-sm font-semibold uppercase">Category</p>
                <p className="font-bold text-lg text-gray-900 mt-1">{listing.category}</p>
              </div>
              <div>
                <p className="text-gray-500 text-sm font-semibold uppercase">Location</p>
                <p className="font-bold text-lg text-gray-900 mt-1">📍 {listing.location}</p>
              </div>
              <div>
                <p className="text-gray-500">Price</p>
                <p className="font-bold text-green-600">
                  ₹{price}
                  {listing.type === 'RENT' && <span className="text-xs text-gray-500"> / day</span>}
                </p>
              </div>
            </div>

            {listing.type === 'RENT' && (
              <div className="grid grid-cols-2 gap-4 mb-6">
                <div>
                  <label className="text-sm font-semibold text-gray-600">Start Date</label>
                  <input
                    type="date"
                    value={rentStartDate}
                    onChange={(e) => setRentStartDate(e.target.value)}
                    className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
                <div>
                  <label className="text-sm font-semibold text-gray-600">End Date</label>
                  <input
                    type="date"
                    value={rentEndDate}
                    onChange={(e) => setRentEndDate(e.target.value)}
                    className="w-full mt-1 border border-gray-300 rounded-lg px-3 py-2"
                  />
                </div>
              </div>
            )}

            <button
              onClick={handlePayment}
              className="w-full px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold"
            >
              {listing.type === 'RENT' ? 'Book & Pay' : 'Buy Now'}
            </button>
          </div>
        </div>
      </div>

      {/* Payment Modal */}
      {showPaymentModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-8 space-y-6">
            <div className="text-center">
              <h3 className="text-2xl font-bold text-gray-900">Choose Payment Method</h3>
              <p className="text-gray-600 mt-2">₹{price} {listing.type === 'RENT' ? '/ day' : ''}</p>
            </div>

            <div className="grid gap-4">
              {/* UPI Payment Option */}
              <button
                onClick={handleUPIPayment}
                className="p-6 border-2 border-green-500 rounded-xl hover:bg-green-50 transition text-center space-y-2"
              >
                <div className="text-4xl">📱</div>
                <h4 className="font-bold text-lg text-gray-900">UPI Payment</h4>
                <p className="text-sm text-gray-600">Google Pay, PhonePe, Paytm</p>
              </button>

              {/* QR Code Payment Option */}
              <button
                onClick={() => {
                  alert('📲 Scan the QR code from your payment app:\n\nUPI: agribuy@bank\n\nOr use UPI Payment option above');
                  handleUPIPayment();
                }}
                className="p-6 border-2 border-blue-500 rounded-xl hover:bg-blue-50 transition text-center space-y-2"
              >
                <div className="text-4xl">📲</div>
                <h4 className="font-bold text-lg text-gray-900">QR Code</h4>
                <p className="text-sm text-gray-600">Scan with any UPI app</p>
              </button>

              {/* Bank Transfer Option */}
              <button
                onClick={() => {
                  alert('💳 Bank Account Details:\n\nAccount: AgriBuy Marketplace\nUPI: agribuy@bank\n\nPlease use reference: #' + listing.id);
                  handleUPIPayment();
                }}
                className="p-6 border-2 border-purple-500 rounded-xl hover:bg-purple-50 transition text-center space-y-2"
              >
                <div className="text-4xl">💳</div>
                <h4 className="font-bold text-lg text-gray-900">Bank Transfer</h4>
                <p className="text-sm text-gray-600">Direct bank account</p>
              </button>
            </div>

            <button
              onClick={() => setShowPaymentModal(false)}
              className="w-full px-4 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-semibold transition"
            >
              Cancel
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
