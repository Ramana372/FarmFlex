import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';

export default function Navigation() {
  const { user, logout } = useAuth();
  const { getCartItemsCount } = useCart();
  const navigate = useNavigate();
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const cartCount = getCartItemsCount();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  const isActive = (path) => {
    return location.pathname === path ? 'text-blue-600 font-semibold border-b-2 border-blue-600' : 'text-gray-700 hover:text-blue-600';
  };

  return (
    <nav className="bg-white border-b border-gray-200 sticky top-0 z-50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 md:px-8 py-3 flex justify-between items-center">
        <Link to="/" className="flex items-center gap-3 hover:opacity-80 transition">
          <div className="w-10 h-10 bg-gradient-to-br from-green-500 to-green-600 rounded-lg flex items-center justify-center shadow-md">
            <span className="text-lg font-bold text-white">⚡</span>
          </div>
          <div>
            <h1 className="text-xl font-bold bg-gradient-to-r from-green-600 to-blue-600 bg-clip-text text-transparent leading-none">FarmFlex</h1>
            <p className="text-xs text-gray-500 leading-none">Flex Your Farm</p>
          </div>
        </Link>

        <div className="hidden md:flex gap-8 items-center">
          <Link to="/" className={`py-2 transition ${isActive('/')}`}>Home</Link>
          <Link to="/marketplace" className={`py-2 transition ${isActive('/marketplace')}`}>Marketplace</Link>
          
          {user && user.role === 'FARMER' && (
            <>
              {/* <Link to="/farmer/listings" className={`py-2 transition ${isActive('/farmer/listings')}`}>My Listings</Link> */}
              <Link to="/listings/create" className={`py-2 transition ${isActive('/listings/create')}`}>Add Equipment</Link>
            </>
          )}

          {user && user.role === 'ADMIN' && (
            <>
              <Link to="/admin/dashboard" className={`py-2 transition ${isActive('/admin/dashboard')}`}>Admin Panel</Link>
              <Link to="/admin/listings/pending" className={`py-2 transition ${isActive('/admin/listings/pending')}`}>Approvals</Link>
            </>
          )}

          {user && (
            <>
              <Link to="/my-orders" className={`py-2 transition ${isActive('/my-orders')}`}>My Orders</Link>
              <Link to="/cart" className={`py-2 transition relative ${isActive('/cart')}`}>
                🛒 Cart
                {cartCount > 0 && (
                  <span className="absolute -top-2 -right-3 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                    {cartCount}
                  </span>
                )}
              </Link>
            </>
          )}
        </div>

        <div className="flex gap-3 items-center">
          {user ? (
            <div className="hidden md:flex items-center gap-3">
              <div className="px-4 py-2 bg-blue-50 rounded-lg">
                <p className="text-sm font-semibold text-gray-900">{user.name}</p>
                <p className="text-xs text-gray-500">{user.role}</p>
              </div>
              <Link 
                to="/dashboard" 
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-medium text-sm"
              >
                Dashboard
              </Link>
              <button
                onClick={handleLogout}
                className="px-4 py-2 text-gray-700 border border-gray-300 rounded-lg hover:bg-gray-50 transition font-medium text-sm"
              >
                Logout
              </button>
            </div>
          ) : (
            <div className="hidden md:flex gap-3">
              <Link 
                to="/login" 
                className="px-4 py-2 text-blue-600 border border-blue-600 rounded-lg hover:bg-blue-50 transition font-medium text-sm"
              >
                Login
              </Link>
              <Link 
                to="/register" 
                className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition font-medium text-sm"
              >
                Sign Up
              </Link>
            </div>
          )}

          {/* Mobile Cart Button */}
          {user && cartCount > 0 && (
            <Link to="/cart" className="md:hidden relative p-2 text-gray-700 hover:bg-gray-100 rounded transition">
              <span className="text-2xl">🛒</span>
              <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs font-bold rounded-full w-5 h-5 flex items-center justify-center">
                {cartCount}
              </span>
            </Link>
          )}

          {/* Mobile Menu Button */}
          <button
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className="md:hidden p-2 text-gray-700 hover:bg-gray-100 rounded transition"
          >
            <svg className="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 6h16M4 12h16M4 18h16" />
            </svg>
          </button>
        </div>
      </div>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden border-t border-gray-200 bg-gray-50">
          <div className="px-4 py-3 space-y-2">
            <Link to="/" className="block py-2 text-gray-700 hover:text-blue-600 font-medium">Home</Link>
            <Link to="/marketplace" className="block py-2 text-gray-700 hover:text-blue-600 font-medium">Marketplace</Link>
            
            {user && user.role === 'FARMER' && (
              <>
                {/* <Link to="/farmer/listings" className="block py-2 text-gray-700 hover:text-blue-600 font-medium">My Listings</Link> */}
                <Link to="/listings/create" className="block py-2 text-gray-700 hover:text-blue-600 font-medium">Add Equipment</Link>
              </>
            )}

            {user && user.role === 'ADMIN' && (
              <>
                <Link to="/admin/dashboard" className="block py-2 text-gray-700 hover:text-blue-600 font-medium">Admin Panel</Link>
                <Link to="/admin/listings/pending" className="block py-2 text-gray-700 hover:text-blue-600 font-medium">Approvals</Link>
              </>
            )}

            <hr className="my-2" />

            {user ? (
              <>
                <Link to="/dashboard" className="block py-2 text-blue-600 font-semibold">Dashboard</Link>
                <button onClick={handleLogout} className="block w-full text-left py-2 text-red-600 hover:text-red-700 font-medium">
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link to="/login" className="block py-2 text-blue-600 font-semibold">Login</Link>
                <Link to="/register" className="block py-2 text-blue-600 font-semibold">Sign Up</Link>
              </>
            )}
          </div>
        </div>
      )}
    </nav>
  );
}
