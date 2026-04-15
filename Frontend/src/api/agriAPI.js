import apiClient from './apiClient';

export const listingsAPI = {
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
  
  getById: (id) => 
    apiClient.get(`/listings/${id}`),
  
  getDetails: (id) => 
    apiClient.get(`/listings/${id}/details`),
  
  create: (formData) => 
    apiClient.post('/listings', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  
  getMyListings: () => 
    apiClient.get('/listings/my'),
  
  update: (id, formData) => 
    apiClient.put(`/listings/${id}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
  
  delete: (id) => 
    apiClient.delete(`/listings/${id}`),
};

export const ordersAPI = {
  getMyOrders: () => 
    apiClient.get('/orders/my'),
  
  getOrderDetails: (orderId) => 
    apiClient.get(`/orders/${orderId}`),
  
  checkAvailability: (listingId) => 
    apiClient.get(`/orders/listing/${listingId}/available`),
};

export const paymentsAPI = {
  createOrder: (listingId, rentStartDate = null, rentEndDate = null) => 
    apiClient.post('/payments/create-order', {
      listingId,
      rentStartDate,
      rentEndDate,
    }),
  
  confirmPayment: (paymentData) => 
    apiClient.post('/payments/confirm', paymentData),
};

export const adminAPI = {
  getDashboard: () => 
    apiClient.get('/admin/dashboard'),
  
  getPendingListings: () => 
    apiClient.get('/admin/listings/pending'),
  
  approveListing: (listingId) => 
    apiClient.post(`/admin/listings/${listingId}/approve`),
  
  rejectListing: (listingId, reason) => 
    apiClient.post(`/admin/listings/${listingId}/reject`, { reason }),
  
  getAllListings: () => 
    apiClient.get('/admin/listings'),
  
  getAllFarmers: () => 
    apiClient.get('/admin/farmers'),
  
  getFarmerListings: (farmerId) => 
    apiClient.get(`/admin/farmers/${farmerId}/listings`),
  
  getAllUsers: () => 
    apiClient.get('/admin/users'),
  
  getUserDetails: (userId) => 
    apiClient.get(`/admin/users/${userId}`),
};

export const authAPI = {
  register: (userData) => 
    apiClient.post('/auth/register', userData),
  
  login: (email, password) => 
    apiClient.post('/auth/login', { email, password }),
  
  verifyEmail: (token) => 
    apiClient.post('/auth/verify-email', { token }),
  
  resendVerification: (email) => 
    apiClient.post('/auth/resend-token', { email }),
  
  forgotPassword: (email) => 
    apiClient.post('/auth/forgot-password', { email }),
  
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
