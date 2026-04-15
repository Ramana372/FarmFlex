import apiClient from './apiClient';

export const authAPI = {
  register: (payload) => 
    apiClient.post('/auth/register', payload),
  
  login: (email, password) => 
    apiClient.post('/auth/login', { email, password }),
  
  verifyEmail: (token) => 
    apiClient.get('/auth/verify-email', { params: { token } }),
  
  resendVerification: (email) => 
    apiClient.post('/auth/resend-verification', { email }),
  
  forgotPassword: (email) => 
    apiClient.post('/auth/forgot-password', { email }),
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
  
  getAllUsers: () => 
    apiClient.get('/admin/users'),
  
  getFarmerListings: (farmerId) => 
    apiClient.get(`/admin/farmers/${farmerId}/listings`),
};

export const listingsAPI = {
  getLive: (params) => apiClient.get('/listings', { params }),
  getById: (id) => apiClient.get(`/listings/${id}`),
  create: (formData) => apiClient.post('/listings', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  }),
  getMyGrouped: () => apiClient.get('/listings/my')
};

export const ordersAPI = {
  getMyOrders: () => apiClient.get('/orders/my')
};

export const paymentsAPI = {
  createOrder: (payload) => apiClient.post('/payments/create-order', payload),
  confirmPayment: (payload) => apiClient.post('/payments/confirm', payload)
};
