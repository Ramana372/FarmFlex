/**
 * Utility functions for AgriBuy frontend
 */

export const priceUtils = {
  formatPrice: (price) => {
    if (!price) return '₹0';
    return `₹${Number(price).toLocaleString('en-IN', { 
      minimumFractionDigits: 0,
      maximumFractionDigits: 2 
    })}`;
  },

  // Calculate total rent amount
  calculateRentAmount: (pricePerDay, startDate, endDate) => {
    if (!pricePerDay || !startDate || !endDate) return 0;
    const start = new Date(startDate);
    const end = new Date(endDate);
    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;
    return pricePerDay * days;
  },

  // Calculate number of days
  calculateDays: (startDate, endDate) => {
    const start = new Date(startDate);
    const end = new Date(endDate);
    return Math.ceil((end - start) / (1000 * 60 * 60 * 24)) + 1;
  },
};

// Date utilities
export const dateUtils = {
  // Format date to readable format
  formatDate: (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-IN', { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric' 
    });
  },

  // Get minimum date for rental (today)
  getMinDate: () => {
    const today = new Date();
    return today.toISOString().split('T')[0];
  },

  // Get maximum date for rental (365 days from now)
  getMaxDate: () => {
    const maxDate = new Date();
    maxDate.setDate(maxDate.getDate() + 365);
    return maxDate.toISOString().split('T')[0];
  },

  // Check if date is valid and not in past
  isValidDate: (dateString) => {
    const date = new Date(dateString);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return date >= today;
  },
};

// Validation utilities
export const validationUtils = {
  // Validate email
  isValidEmail: (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  },

  // Validate phone number (10 digits for India)
  isValidPhone: (phone) => {
    const phoneRegex = /^[0-9]{10}$/;
    return phoneRegex.test(phone);
  },

  // Validate password strength
  isStrongPassword: (password) => {
    // At least 8 chars, 1 uppercase, 1 number, 1 special char
    const strongRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    return strongRegex.test(password);
  },

  // Validate listing title
  isValidTitle: (title) => {
    return title && title.length >= 5 && title.length <= 200;
  },

  // Validate listing description
  isValidDescription: (description) => {
    return description && description.length >= 10 && description.length <= 5000;
  },

  // Validate price
  isValidPrice: (price) => {
    return price && price > 0;
  },
};

// Error handling utilities
export const errorUtils = {
  // Get user-friendly error message
  getErrorMessage: (error) => {
    if (error.response?.data?.error) {
      return error.response.data.error;
    }
    if (error.response?.status === 401) {
      return 'Unauthorized. Please login again.';
    }
    if (error.response?.status === 403) {
      return 'You don\'t have permission to perform this action.';
    }
    if (error.response?.status === 404) {
      return 'Resource not found.';
    }
    if (error.response?.status === 500) {
      return 'Server error. Please try again later.';
    }
    return error.message || 'An error occurred. Please try again.';
  },

  // Log error for debugging
  logError: (context, error) => {
    console.error(`[${context}]`, {
      message: error.message,
      status: error.response?.status,
      data: error.response?.data,
      stack: error.stack,
    });
  },
};

// Storage utilities
export const storageUtils = {
  // Save auth token
  saveToken: (token) => {
    localStorage.setItem('authToken', token);
  },

  // Get auth token
  getToken: () => {
    return localStorage.getItem('authToken');
  },

  // Clear auth token
  clearToken: () => {
    localStorage.removeItem('authToken');
  },

  // Save user data
  saveUser: (user) => {
    localStorage.setItem('user', JSON.stringify(user));
  },

  // Get user data
  getUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },

  // Clear user data
  clearUser: () => {
    localStorage.removeItem('user');
  },

  // Logout (clear all auth data)
  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('user');
  },

  // Save filters
  saveFilters: (filters) => {
    localStorage.setItem('listingFilters', JSON.stringify(filters));
  },

  // Get filters
  getFilters: () => {
    const filters = localStorage.getItem('listingFilters');
    return filters ? JSON.parse(filters) : null;
  },

  // Clear filters
  clearFilters: () => {
    localStorage.removeItem('listingFilters');
  },
};

// Image utilities
export const imageUtils = {
  // Check if file is valid image
  isValidImage: (file) => {
    const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
    return validTypes.includes(file.type);
  },

  // Check file size (max 5MB)
  isValidSize: (file, maxMB = 5) => {
    return file.size <= maxMB * 1024 * 1024;
  },

  // Get file preview URL
  getPreviewURL: (file) => {
    return URL.createObjectURL(file);
  },

  // Revoke preview URL (cleanup)
  revokePreviewURL: (url) => {
    URL.revokeObjectURL(url);
  },
};

// Listing utilities
export const listingUtils = {
  // Get category display name
  getCategoryLabel: (category) => {
    const labels = {
      TRACTOR: 'Tractor',
      HARVESTER: 'Harvester',
      PLOUGH: 'Plough',
      SEEDER: 'Seeder',
      SPRAYER: 'Sprayer',
      THRESHER: 'Thresher',
      OTHER: 'Other Equipment'
    };
    return labels[category] || category;
  },

  // Get type display name
  getTypeLabel: (type) => {
    return type === 'RENT' ? '📅 For Rent' : '🛒 For Sale';
  },

  // Get status color
  getStatusColor: (status) => {
    const colors = {
      PENDING: 'bg-yellow-100 text-yellow-800',
      LIVE: 'bg-green-100 text-green-800',
      REJECTED: 'bg-red-100 text-red-800',
      SOLD: 'bg-blue-100 text-blue-800',
      BOOKED: 'bg-purple-100 text-purple-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  },

  // Get status badge
  getStatusBadge: (status) => {
    const badges = {
      PENDING: '⏳ Pending',
      LIVE: '✅ Live',
      REJECTED: '❌ Rejected',
      SOLD: '🎉 Sold',
      BOOKED: '📅 Booked'
    };
    return badges[status] || status;
  },

  // Format listing for display
  formatListing: (listing) => {
    return {
      ...listing,
      categoryLabel: listingUtils.getCategoryLabel(listing.category),
      typeLabel: listingUtils.getTypeLabel(listing.type),
      statusBadge: listingUtils.getStatusBadge(listing.status),
      priceDisplay: listing.type === 'RENT' 
        ? `${priceUtils.formatPrice(listing.rentPricePerDay)}/day`
        : priceUtils.formatPrice(listing.salePrice),
    };
  },
};

// Form utilities
export const formUtils = {
  // Convert form data to multipart for file upload
  createFormData: (data, fileFields = ['images']) => {
    const formData = new FormData();
    
    Object.keys(data).forEach(key => {
      if (fileFields.includes(key) && Array.isArray(data[key])) {
        data[key].forEach(file => {
          formData.append(key, file);
        });
      } else if (fileFields.includes(key) && data[key]) {
        formData.append(key, data[key]);
      } else if (data[key] !== null && data[key] !== undefined && data[key] !== '') {
        formData.append(key, data[key]);
      }
    });
    
    return formData;
  },

  // Reset form
  resetForm: (formElement) => {
    if (formElement) {
      formElement.reset();
    }
  },
};

// Razorpay utilities
export const razorpayUtils = {
  // Initialize Razorpay payment
  initiatePayment: (options) => {
    return new Promise((resolve, reject) => {
      const rzp = new window.Razorpay({
        ...options,
        handler: function(response) {
          resolve(response);
        },
        modal: {
          ondismiss: function() {
            reject(new Error('Payment cancelled by user'));
          }
        }
      });
      
      rzp.on('payment.failed', function(response) {
        reject(new Error(response.error.description));
      });
      
      rzp.open();
    });
  },

  // Verify payment data
  isValidPaymentResponse: (response) => {
    return response.razorpay_order_id && 
           response.razorpay_payment_id && 
           response.razorpay_signature;
  },
};

export default {
  priceUtils,
  dateUtils,
  validationUtils,
  errorUtils,
  storageUtils,
  imageUtils,
  listingUtils,
  formUtils,
  razorpayUtils,
};
