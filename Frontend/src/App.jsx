import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import ProtectedRoute from './components/ProtectedRoute';

// Pages
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import DashboardPage from './pages/DashboardPage';
import VerifyEmailPage from './pages/VerifyEmailPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import AdminApprovalsPage from './pages/AdminApprovalsPage';
import FarmerProductsPage from './pages/FarmerProductsPage';
import AddProductPage from './pages/AddProductPage';
import MarketplacePage from './pages/BrowseProductsPage';
import ListingDetailsPage from './pages/ListingDetailsPage';
import CartPage from './pages/CartPage';
import MyOrdersPage from './pages/MyOrdersPage';

// Styles
import './index.css';

function App() {
  return (
    <Router>
      <AuthProvider>
        <CartProvider>
          <Routes>
            {/* Public Routes */}
            <Route path="/" element={<HomePage />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/verify-email" element={<VerifyEmailPage />} />
            <Route path="/marketplace" element={<MarketplacePage />} />
            <Route path="/browse" element={<MarketplacePage />} />
            <Route path="/listings/:id" element={<ListingDetailsPage />} />
            <Route path="/cart" element={<CartPage />} />

            {/* Protected Routes - All Users */}
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <DashboardPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/my-orders"
              element={
                <ProtectedRoute>
                  <MyOrdersPage />
                </ProtectedRoute>
              }
            />

            {/* Admin Routes */}
            <Route
              path="/admin/dashboard"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminDashboardPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/admin/listings/pending"
              element={
                <ProtectedRoute requiredRole="ADMIN">
                  <AdminApprovalsPage />
                </ProtectedRoute>
              }
            />

            {/* Farmer Routes */}
            <Route
              path="/farmer/dashboard"
              element={
                <ProtectedRoute requiredRole="FARMER">
                  <DashboardPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/farmer/listings"
              element={
                <ProtectedRoute requiredRole="FARMER">
                  <FarmerProductsPage />
                </ProtectedRoute>
              }
            />

            <Route
              path="/listings/create"
              element={
                <ProtectedRoute requiredRole="FARMER">
                  <AddProductPage />
                </ProtectedRoute>
              }
            />

            {/* Default Route */}
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            } />

            {/* 404 */}
            <Route
              path="*"
              element={
                <div className="flex items-center justify-center min-h-screen">
                  <h1 className="text-2xl font-bold">Page Not Found</h1>
                </div>
              }
            />
          </Routes>
        </CartProvider>
      </AuthProvider>
    </Router>
  );
}

export default App;
