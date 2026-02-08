import apiClient from './apiClient';

/**
 * Listings API - All operations related to agricultural equipment listings
 */
export const listingsAPI = {
  // Get all live listings with optional filters
  getLive: (filters = {}) => 
    apiClient.get('/listings', { 
      params: {
        type: filters.type,
        category: filters.category,
        location: filters.location,
        minPrice: filters.minPrice,
        maxPrice: filters.maxPrice,
        search: filters.search,
      }
    }),
  
  // Get listing by ID (public, LIVE only)
  getById: (id) => 
    apiClient.get(`/listings/${id}`),
  
  // Get listing with full details (authenticated)
  getDetails: (id) => 
    apiClient.get(`/listings/${id}/details`),
  
  // Create new listing (multipart form data with images)
  create: (formData) => 
    apiClient.post('/listings', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  
  // Get farmer's listings grouped by status
  getMyListings: () => 
    apiClient.get('/listings/my'),
  
  // Update listing
  update: (id, formData) => 
    apiClient.put(`/listings/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  
  // Delete listing
  delete: (id) => 
    apiClient.delete(`/listings/${id}`),
};

/**
 * Orders API - Order and booking management
 */
export const ordersAPI = {
  // Get buyer's orders
  getMyOrders: () => 
    apiClient.get('/orders/my'),
  
  // Get order details
  getOrderDetails: (orderId) => 
    apiClient.get(`/orders/${orderId}`),
  
  // Check if listing is available
  checkAvailability: (listingId) => 
    apiClient.get(`/orders/listing/${listingId}/available`),
};

/**
 * Payments API - Razorpay integration for RENT and SALE
 */
export const paymentsAPI = {
  // Create Razorpay order
  createOrder: (listingId, rentStartDate = null, rentEndDate = null) => 
    apiClient.post('/payments/create-order', {
      listingId,
      rentStartDate,
      rentEndDate,
    }),
  
  // Confirm payment after successful transaction
  confirmPayment: (paymentData) => 
    apiClient.post('/payments/confirm', paymentData),
};

/**
 * Admin API - Listing approval and user management
 */
export const adminAPI = {
  // Get admin dashboard statistics
  getDashboard: () => 
    apiClient.get('/admin/dashboard'),
  
  // Get pending listings awaiting approval
  getPendingListings: () => 
    apiClient.get('/admin/listings/pending'),
  
  // Approve a pending listing
  approveListing: (listingId) => 
    apiClient.post(`/admin/listings/${listingId}/approve`),
  
  // Reject a listing with reason
  rejectListing: (listingId, reason) => 
    apiClient.post(`/admin/listings/${listingId}/reject`, { reason }),
  
  // Get all live listings
  getAllListings: () => 
    apiClient.get('/admin/listings'),
  
  // Get all farmers
  getAllFarmers: () => 
    apiClient.get('/admin/farmers'),
  
  // Get specific farmer's listings
  getFarmerListings: (farmerId) => 
    apiClient.get(`/admin/farmers/${farmerId}/listings`),
  
  // Get all users
  getAllUsers: () => 
    apiClient.get('/admin/users'),
  
  // Get user details
  getUserDetails: (userId) => 
    apiClient.get(`/admin/users/${userId}`),
};

/**
 * Authentication API - User registration, login, email verification
 */
export const authAPI = {
  // Register new user
  register: (userData) => 
    apiClient.post('/auth/register', userData),
  
  // Login user
  login: (email, password) => 
    apiClient.post('/auth/login', { email, password }),
  
  // Verify email with token
  verifyEmail: (token) => 
    apiClient.post('/auth/verify-email', { token }),
  
  // Resend verification email
  resendVerification: (email) => 
    apiClient.post('/auth/resend-token', { email }),
  
  // Request password reset
  forgotPassword: (email) => 
    apiClient.post('/auth/forgot-password', { email }),
  
  // Reset password with token
  resetPassword: (token, newPassword) => 
    apiClient.post('/auth/reset-password', { token, newPassword }),
};

export default {
  listingsAPI,
  ordersAPI,
  paymentsAPI,
  adminAPI,
  authAPI,
};
