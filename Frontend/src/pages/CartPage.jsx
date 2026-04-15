import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { IMAGE_BASE_URL } from '../api/apiClient';
import Navigation from '../components/Navigation';
import Footer from '../components/Footer';

export default function CartPage() {
  const { cartItems, removeFromCart, updateQuantity, clearCart, getCartTotal } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();
  const [showCheckout, setShowCheckout] = useState(false);

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50">
        <Navigation />
        <div className="max-w-7xl mx-auto py-20 px-4 text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">Your Shopping Cart</h1>
          <p className="text-gray-600 mb-6">Please log in to view your cart</p>
          <Link
            to="/login"
            className="inline-block px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 font-semibold"
          >
            Go to Login
          </Link>
        </div>
        <Footer />
      </div>
    );
  }

  const handleCheckout = () => {
    if (cartItems.length === 0) {
      alert('Your cart is empty!');
      return;
    }
    setShowCheckout(true);
  };

  const handleConfirmOrder = () => {
    // Create order
    const orderData = {
      items: cartItems.map(item => ({
        listingId: item.id,
        quantity: item.quantity || 1,
        price: item.type === 'RENT' ? item.rentPricePerDay : item.salePrice,
        type: item.type
      })),
      totalAmount: getCartTotal(),
      status: 'CONFIRMED',
      createdAt: new Date().toISOString()
    };

    // Save order to localStorage
    const orders = JSON.parse(localStorage.getItem('orders') || '[]');
    orders.push({
      ...orderData,
      orderId: 'ORD-' + Date.now(),
      userId: user.id
    });
    localStorage.setItem('orders', JSON.stringify(orders));

    // Clear cart and show success
    clearCart();
    setShowCheckout(false);
    alert('✅ Order Confirmed!\n\nOrder ID: ' + orders[orders.length - 1].orderId + '\nTotal: ₹' + getCartTotal() + '\n\nThank you for shopping with FarmFlex!');
    navigate('/my-orders');
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col">
      <Navigation />

      <div className="flex-1 max-w-7xl mx-auto w-full py-12 px-4 md:px-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-8">🛒 Shopping Cart</h1>

        {cartItems.length === 0 ? (
          <div className="bg-white rounded-lg shadow p-12 text-center">
            <div className="text-6xl mb-4">🛒</div>
            <h2 className="text-2xl font-bold text-gray-900 mb-4">Your cart is empty</h2>
            <p className="text-gray-600 mb-6">Start adding equipment to your cart!</p>
            <Link
              to="/marketplace"
              className="inline-block px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-semibold"
            >
              Browse Equipment
            </Link>
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div className="lg:col-span-2 space-y-4">
              {cartItems.map(item => (
                <div key={item.id} className="bg-white rounded-lg shadow p-6 border border-gray-100 hover:shadow-lg transition">
                  <div className="flex gap-6">
                    <div className="w-24 h-24 bg-gray-200 rounded-lg flex-shrink-0 overflow-hidden">
                      {item.imageUrls?.length ? (
                        <img
                          src={`${IMAGE_BASE_URL}${item.imageUrls[0]}`}
                          alt={item.title}
                          className="w-full h-full object-cover"
                          onError={(e) => (e.target.style.display = 'none')}
                        />
                      ) : (
                        <div className="w-full h-full flex items-center justify-center text-3xl">🚜</div>
                      )}
                    </div>

                    <div className="flex-1">
                      <Link to={`/listings/${item.id}`} className="text-xl font-bold text-gray-900 hover:text-blue-600">
                        {item.title}
                      </Link>
                      <p className="text-gray-600 text-sm mt-1">{item.category} • {item.location}</p>
                      <div className="flex gap-4 mt-3">
                        <span className="inline-block px-3 py-1 bg-gray-100 text-sm font-semibold rounded-full text-gray-700">
                          {item.type}
                        </span>
                        <span className="inline-block px-3 py-1 bg-green-100 text-sm font-semibold rounded-full text-green-700">
                          ₹{item.type === 'RENT' ? item.rentPricePerDay : item.salePrice}
                          {item.type === 'RENT' && <span className="text-xs"> /day</span>}
                        </span>
                      </div>

                      <div className="flex items-center gap-4 mt-4">
                        <div className="flex items-center border border-gray-300 rounded-lg">
                          <button
                            onClick={() => updateQuantity(item.id, (item.quantity || 1) - 1)}
                            className="px-3 py-2 text-gray-600 hover:bg-gray-100"
                          >
                            −
                          </button>
                          <span className="px-4 py-2 font-semibold text-gray-900">
                            {item.quantity || 1}
                          </span>
                          <button
                            onClick={() => updateQuantity(item.id, (item.quantity || 1) + 1)}
                            className="px-3 py-2 text-gray-600 hover:bg-gray-100"
                          >
                            +
                          </button>
                        </div>
                        <span className="font-bold text-gray-900">
                          ₹{((item.type === 'RENT' ? item.rentPricePerDay : item.salePrice) * (item.quantity || 1)).toLocaleString()}
                        </span>
                        <button
                          onClick={() => removeFromCart(item.id)}
                          className="ml-auto px-4 py-2 text-red-600 hover:bg-red-50 rounded-lg transition font-semibold"
                        >
                          Remove
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>

            <div className="lg:col-span-1">
              <div className="bg-white rounded-lg shadow p-6 border border-gray-100 sticky top-24">
                <h2 className="text-2xl font-bold text-gray-900 mb-6">Order Summary</h2>

                <div className="space-y-4 pb-6 border-b border-gray-200">
                  <div className="flex justify-between text-gray-600">
                    <span>Subtotal ({cartItems.length} items)</span>
                    <span>₹{getCartTotal().toLocaleString()}</span>
                  </div>
                  <div className="flex justify-between text-gray-600">
                    <span>Shipping</span>
                    <span className="text-green-600 font-semibold">FREE</span>
                  </div>
                  <div className="flex justify-between text-gray-600">
                    <span>Tax</span>
                    <span>₹0</span>
                  </div>
                </div>

                <div className="py-4 flex justify-between items-center">
                  <span className="text-lg font-bold text-gray-900">Total:</span>
                  <span className="text-3xl font-bold text-green-600">₹{getCartTotal().toLocaleString()}</span>
                </div>

                <button
                  onClick={handleCheckout}
                  className="w-full mt-6 px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold text-lg transition"
                >
                  Proceed to Checkout
                </button>

                <Link
                  to="/marketplace"
                  className="block w-full mt-3 px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-semibold text-center transition"
                >
                  Continue Shopping
                </Link>

                <button
                  onClick={clearCart}
                  className="w-full mt-3 px-6 py-2 text-red-600 border border-red-200 rounded-lg hover:bg-red-50 transition font-semibold"
                >
                  Clear Cart
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {showCheckout && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-2xl shadow-2xl max-w-md w-full p-8 space-y-6">
            <div className="text-center">
              <h3 className="text-2xl font-bold text-gray-900">Confirm Order</h3>
              <p className="text-gray-600 mt-2">Review your order before confirming</p>
            </div>

            <div className="bg-gray-50 rounded-lg p-4 space-y-3">
              <div className="flex justify-between">
                <span className="text-gray-600">Items:</span>
                <span className="font-semibold text-gray-900">{cartItems.length}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-600">Total Qty:</span>
                <span className="font-semibold text-gray-900">
                  {cartItems.reduce((sum, item) => sum + (item.quantity || 1), 0)}
                </span>
              </div>
              <div className="border-t border-gray-200 pt-3 flex justify-between">
                <span className="font-bold text-gray-900">Total Amount:</span>
                <span className="text-2xl font-bold text-green-600">₹{getCartTotal().toLocaleString()}</span>
              </div>
            </div>

            <div className="grid gap-3">
              <button
                onClick={handleConfirmOrder}
                className="w-full px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold transition"
              >
                ✓ Confirm Order
              </button>
              <button
                onClick={() => setShowCheckout(false)}
                className="w-full px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 font-semibold transition"
              >
                Continue Shopping
              </button>
            </div>
          </div>
        </div>
      )}

      <Footer />
    </div>
  );
}
