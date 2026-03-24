import React, { useState } from 'react';
import { listingsAPI } from '../api/authAPI';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function AddProductPage() {
  const { logout } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [images, setImages] = useState([]);
  const [imagePreviews, setImagePreviews] = useState([]);

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'TRACTOR',
    type: 'RENT',
    salePrice: '',
    rentPricePerDay: '',
    location: '',
  });

  const categories = ['TRACTOR', 'HARVESTER', 'PLOUGH', 'SEEDER', 'SPRAYER', 'THRESHER', 'OTHER'];

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleImageChange = (e) => {
    const files = Array.from(e.target.files || []);
    setImages(files);
    
    // Create preview URLs
    const previews = files.map(file => URL.createObjectURL(file));
    setImagePreviews(previews);
    console.log(`Selected ${files.length} images for upload`);
  };

  const removeImage = (index) => {
    const newImages = images.filter((_, i) => i !== index);
    const newPreviews = imagePreviews.filter((_, i) => i !== index);
    setImages(newImages);
    setImagePreviews(newPreviews);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    // Validation
    if (!formData.title.trim()) {
      setError('Title is required');
      return;
    }
    if (!formData.description.trim()) {
      setError('Description is required');
      return;
    }
    if (!formData.location.trim()) {
      setError('Location is required');
      return;
    }
    if (formData.type === 'SALE' && (!formData.salePrice || formData.salePrice <= 0)) {
      setError('Valid sale price is required');
      return;
    }
    if (formData.type === 'RENT' && (!formData.rentPricePerDay || formData.rentPricePerDay <= 0)) {
      setError('Valid rent price per day is required');
      return;
    }

    try {
      setLoading(true);
      const payload = new FormData();
      payload.append('title', formData.title);
      payload.append('description', formData.description);
      payload.append('category', formData.category);
      payload.append('type', formData.type);
      payload.append('location', formData.location);
      if (formData.type === 'SALE') {
        payload.append('salePrice', formData.salePrice);
      } else {
        payload.append('rentPricePerDay', formData.rentPricePerDay);
      }
      images.forEach((img) => payload.append('images', img));

      console.log('Submitting listing with payload:', {
        title: formData.title,
        category: formData.category,
        type: formData.type,
        location: formData.location
      });

      const response = await listingsAPI.create(payload);
      console.log('Listing created response:', response);
      alert('Listing created successfully! It will be visible after admin approval.');
      navigate('/farmer/listings');
    } catch (err) {
      console.error('Full error object:', err);
      console.error('Error response:', err.response);
      const errorMessage = err.response?.data?.error || err.message || 'Failed to create listing';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation */}
      <nav className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            <div className="flex items-center space-x-8">
              <h1 className="text-2xl font-bold text-green-600">FarmFlex</h1>
              <a href="/farmer/dashboard" className="text-gray-700 hover:text-gray-900">
                Dashboard
              </a>
              <a href="/farmer/listings" className="text-gray-700 hover:text-gray-900">
                My Listings
              </a>
              <a href="/listings/create" className="text-gray-700 hover:text-gray-900 font-bold">
                Create Listing
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

      {/* Main Content */}
      <div className="max-w-2xl mx-auto py-12 px-4 sm:px-6 lg:px-8">
        <div className="bg-white rounded-lg shadow p-8">
          <h2 className="text-3xl font-bold text-gray-800 mb-2">Create a New Listing</h2>
          <p className="text-gray-600 mb-8">
            Fill in the details below. Your listing will be reviewed by our admin team before it appears on the marketplace.
          </p>

          {error && (
            <div className="mb-6 p-4 bg-red-100 text-red-700 rounded-lg">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Product Name */}
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">
                Listing Title *
              </label>
              <input
                type="text"
                name="title"
                value={formData.title}
                onChange={handleChange}
                placeholder="e.g., John Deere Tractor"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>

            {/* Description */}
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">
                Description *
              </label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                placeholder="Describe your product in detail"
                rows="4"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>

            {/* Category and Type */}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">
                  Category *
                </label>
                <select
                  name="category"
                  value={formData.category}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  {categories.map(cat => (
                    <option key={cat} value={cat}>{cat}</option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">
                  Listing Type *
                </label>
                <select
                  name="type"
                  value={formData.type}
                  onChange={handleChange}
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  <option value="RENT">RENT</option>
                  <option value="SALE">SALE</option>
                </select>
              </div>
            </div>

            {/* Price */}
            {formData.type === 'SALE' && (
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">
                  Sale Price (₹) *
                </label>
                <input
                  type="number"
                  name="salePrice"
                  value={formData.salePrice}
                  onChange={handleChange}
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>
            )}

            {formData.type === 'RENT' && (
              <div>
                <label className="block text-sm font-bold text-gray-700 mb-2">
                  Rent Price Per Day (₹) *
                </label>
                <input
                  type="number"
                  name="rentPricePerDay"
                  value={formData.rentPricePerDay}
                  onChange={handleChange}
                  placeholder="0.00"
                  step="0.01"
                  min="0"
                  className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
                />
              </div>
            )}

            {/* Location */}
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">
                Location *
              </label>
              <input
                type="text"
                name="location"
                value={formData.location}
                onChange={handleChange}
                placeholder="City, State"
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>

            {/* Images */}
            <div>
              <label className="block text-sm font-bold text-gray-700 mb-2">
                Equipment Images (Optional) 📸
              </label>
              <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:border-green-500 transition cursor-pointer">
                <input
                  type="file"
                  multiple
                  accept="image/*"
                  onChange={handleImageChange}
                  className="hidden"
                  id="image-input"
                />
                <label htmlFor="image-input" className="cursor-pointer">
                  <div className="text-4xl mb-2">📷</div>
                  <p className="text-sm text-gray-600 font-medium">Click to upload or drag images here</p>
                  <p className="text-xs text-gray-500 mt-1">PNG, JPG, GIF up to 5MB each</p>
                </label>
              </div>
              
              {images.length > 0 && (
                <div className="mt-4">
                  <p className="text-sm font-bold text-gray-700 mb-3">{images.length} image{images.length !== 1 ? 's' : ''} selected:</p>
                  <div className="grid grid-cols-3 gap-4">
                    {imagePreviews.map((preview, index) => (
                      <div key={index} className="relative group">
                        <img
                          src={preview}
                          alt={`Preview ${index + 1}`}
                          className="w-full h-24 object-cover rounded-lg border border-gray-300"
                        />
                        <button
                          type="button"
                          onClick={() => removeImage(index)}
                          className="absolute top-1 right-1 bg-red-600 text-white rounded-full w-6 h-6 flex items-center justify-center opacity-0 group-hover:opacity-100 transition text-sm font-bold"
                        >
                          ✕
                        </button>
                        <span className="absolute bottom-1 left-1 bg-gray-800 text-white text-xs px-2 py-1 rounded">
                          {index + 1}
                        </span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            {/* Submit Button */}
            <div className="flex gap-4 pt-6">
              <button
                type="submit"
                disabled={loading}
                className="flex-1 px-6 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 font-bold disabled:opacity-50"
              >
                {loading ? 'Listing...' : 'Create Listing'}
              </button>
              <button
                type="button"
                onClick={() => navigate('/farmer/listings')}
                className="flex-1 px-6 py-3 bg-gray-600 text-white rounded-lg hover:bg-gray-700 font-bold"
              >
                Cancel
              </button>
            </div>
          </form>

          <div className="mt-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <p className="text-sm text-blue-900">
              <span className="font-bold">Note:</span> Your listing will be reviewed by our admin team. Once approved, it will be visible on the marketplace.
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
