import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Navigation from '../components/Navigation';
import Footer from '../components/Footer';

export default function MyOrdersPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }

    // Load orders from localStorage
    const savedOrders = JSON.parse(localStorage.getItem('orders') || '[]');
    const userOrders = savedOrders.filter(order => order.userId === user.id);
    setOrders(userOrders);
    setLoading(false);
  }, [user, navigate]);

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <div className="flex items-center justify-center py-20">
          <div className="text-gray-600">Loading orders...</div>
        </div>
        <Footer />
      </div>
    );
  }

  if (!user) {
    return null;
  }

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navigation />

      <div className="flex-1 max-w-7xl mx-auto w-full py-12 px-4 md:px-8">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-gray-900 mb-2">📦 My Orders</h1>
          <p className="text-gray-600">Track and manage your equipment orders</p>
        </div>

        {orders.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <div className="text-6xl mb-4">📦</div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">No orders yet</h2>
            <p className="text-gray-600 mb-6">Start exploring our marketplace and place your first order!</p>
            <Link
              to="/marketplace"
              className="inline-block px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-semibold"
            >
              Browse Equipment
            </Link>
          </div>
        ) : (
          <div className="space-y-6">
            {orders.map((order, idx) => (
              <div key={idx} className="bg-white rounded-lg shadow border border-gray-100 overflow-hidden hover:shadow-lg transition">
                {/* Order Header */}
                <div className="bg-gradient-to-r from-green-50 to-blue-50 p-6 border-b border-gray-100">
                  <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div>
                      <p className="text-gray-600 text-sm font-semibold">ORDER ID</p>
                      <p className="text-xl font-bold text-gray-900 mt-1">{order.orderId}</p>
                    </div>
                    <div>
                      <p className="text-gray-600 text-sm font-semibold">ORDER DATE</p>
                      <p className="text-lg font-semibold text-gray-900 mt-1">
                        {new Date(order.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                    <div>
                      <p className="text-gray-600 text-sm font-semibold">TOTAL AMOUNT</p>
                      <p className="text-2xl font-bold text-green-600 mt-1">₹{order.totalAmount.toLocaleString()}</p>
                    </div>
                    <div>
                      <p className="text-gray-600 text-sm font-semibold">STATUS</p>
                      <div className="flex gap-2 mt-1">
                        <span className="inline-block px-3 py-1 bg-green-100 text-green-700 rounded-full text-sm font-bold">
                          ✓ {order.status}
                        </span>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Order Items */}
                <div className="p-6">
                  <h3 className="text-lg font-bold text-gray-900 mb-4">Order Items ({order.items.length})</h3>
                  <div className="space-y-3">
                    {order.items.map((item, itemIdx) => (
                      <div key={itemIdx} className="flex items-center justify-between p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition">
                        <div className="flex-1">
                          <p className="font-semibold text-gray-900">Listing #{item.listingId}</p>
                          <p className="text-sm text-gray-600 mt-1">
                            {item.type === 'RENT' ? `Rental - ₹${item.price}/day` : `Sale - ₹${item.price}`} × {item.quantity}
                          </p>
                        </div>
                        <span className="text-lg font-bold text-gray-900">
                          ₹{(item.price * item.quantity).toLocaleString()}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Order Footer */}
                <div className="bg-gray-50 p-6 border-t border-gray-100 flex gap-3 justify-end">
                  <Link
                    to="/marketplace"
                    className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold transition"
                  >
                    Continue Shopping
                  </Link>
                  <button
                    onClick={() => {
                      const orderDetails = `Order #${order.orderId}\nDate: ${new Date(order.createdAt).toLocaleDateString()}\nAmount: ₹${order.totalAmount}\n\nItems:\n${order.items.map(i => `- Listing #${i.listingId}: ₹${i.price} × ${i.quantity}`).join('\n')}`;
                      alert(orderDetails);
                    }}
                    className="px-6 py-2 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-semibold transition"
                  >
                    View Details
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Order Statistics */}
        {orders.length > 0 && (
          <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
            <div className="bg-white rounded-lg shadow p-6 border border-gray-100">
              <p className="text-gray-600 font-semibold">Total Orders</p>
              <p className="text-4xl font-bold text-gray-900 mt-2">{orders.length}</p>
            </div>
            <div className="bg-white rounded-lg shadow p-6 border border-gray-100">
              <p className="text-gray-600 font-semibold">Total Spent</p>
              <p className="text-4xl font-bold text-green-600 mt-2">
                ₹{orders.reduce((sum, order) => sum + order.totalAmount, 0).toLocaleString()}
              </p>
            </div>
            <div className="bg-white rounded-lg shadow p-6 border border-gray-100">
              <p className="text-gray-600 font-semibold">Total Items</p>
              <p className="text-4xl font-bold text-blue-600 mt-2">
                {orders.reduce((sum, order) => sum + order.items.reduce((itemSum, item) => itemSum + item.quantity, 0), 0)}
              </p>
            </div>
          </div>
        )}
      </div>

      <Footer />
    </div>
  );
}
