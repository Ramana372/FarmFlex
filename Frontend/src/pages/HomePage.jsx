import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import Navigation from '../components/Navigation';
import Footer from '../components/Footer';

export default function HomePage() {
  const { user } = useAuth();

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Navigation />

      <section className="flex-1 bg-gradient-to-br from-green-50 via-blue-50 to-indigo-100 py-24 px-4 md:px-8">
        <div className="max-w-7xl mx-auto grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
          <div className="space-y-8">
            <div className="space-y-4">
              <div className="inline-block bg-green-100 px-4 py-2 rounded-full">
                <p className="text-green-700 font-semibold text-sm">🌾 Welcome to FarmFlex</p>
              </div>
              <h1 className="text-5xl md:text-7xl font-bold bg-gradient-to-r from-green-600 to-blue-600 bg-clip-text text-transparent leading-tight">
                Flex Your Farm
              </h1>
              <div className="h-1 w-24 bg-gradient-to-r from-green-500 to-blue-500 rounded-full"></div>
              <p className="text-xl text-gray-700 leading-relaxed font-medium">
                Farm Your Way. Connect with verified sellers and buyers. Rent or purchase premium agricultural equipment with confidence. Access real-time market data and transparent pricing.
              </p>
            </div>

            <div className="flex flex-col sm:flex-row gap-4">
              {!user ? (
                <>
                  <Link
                    to="/register"
                    className="px-8 py-4 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl hover:shadow-xl transition font-semibold text-center shadow-lg hover:scale-105 transform duration-200"
                  >
                    Get Started Free
                  </Link>
                  <Link
                    to="/marketplace"
                    className="px-8 py-4 bg-white text-green-600 border-2 border-green-600 rounded-xl hover:bg-green-50 transition font-semibold text-center hover:shadow-lg"
                  >
                    Browse Equipment
                  </Link>
                </>
              ) : (
                <Link
                  to="/dashboard"
                  className="px-8 py-4 bg-gradient-to-r from-green-500 to-green-600 text-white rounded-xl hover:shadow-xl transition font-semibold text-center shadow-lg hover:scale-105 transform duration-200"
                >
                  Go to Dashboard
                </Link>
              )}
            </div>

            <div className="grid grid-cols-3 gap-6 pt-8 border-t border-green-200">
              <div className="bg-green-50 rounded-lg p-4">
                <p className="text-3xl font-bold text-green-600">500+</p>
                <p className="text-sm text-gray-600 mt-1">Equipment</p>
              </div>
              <div className="bg-blue-50 rounded-lg p-4">
                <p className="text-3xl font-bold text-blue-600">3000+</p>
                <p className="text-sm text-gray-600 mt-1">Users</p>
              </div>
              <div className="bg-indigo-50 rounded-lg p-4">
                <p className="text-3xl font-bold text-indigo-600">50K+</p>
                <p className="text-sm text-gray-600 mt-1">Transactions</p>
              </div>
            </div>
          </div>

          <div className="hidden lg:flex items-center justify-center">
            <div className="relative">
              <div className="absolute inset-0 bg-gradient-to-r from-green-200 to-blue-200 rounded-3xl blur-3xl opacity-40"></div>
              <div className="relative bg-white rounded-3xl p-12 shadow-2xl border border-green-100">
                <div className="text-center space-y-6">
                  <div className="inline-flex items-center justify-center w-28 h-28 bg-gradient-to-br from-green-400 to-green-600 rounded-2xl text-6xl shadow-lg">
                    🚜
                  </div>
                  <h3 className="text-3xl font-bold text-gray-900">Farm Equipment</h3>
                  <p className="text-gray-600 text-lg">FarmFlex - Flex Your Farm, Your Way</p>
                  <div className="flex gap-2 justify-center pt-4">
                    <div className="w-3 h-3 bg-green-600 rounded-full animate-pulse"></div>
                    <div className="w-3 h-3 bg-blue-500 rounded-full animate-pulse" style={{animationDelay: '0.2s'}}></div>
                    <div className="w-3 h-3 bg-indigo-500 rounded-full animate-pulse" style={{animationDelay: '0.4s'}}></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="bg-gradient-to-b from-green-50 to-white py-20 px-4 md:px-8 border-b border-green-100">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold bg-gradient-to-r from-green-600 to-green-700 bg-clip-text text-transparent mb-4">Why Choose FarmFlex?</h2>
            <p className="text-xl text-gray-600">Farming Without Limits - Everything you need for agricultural equipment</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              {
                icon: '🔍',
                title: 'Easy Discovery',
                desc: 'Find exactly what you need with advanced filtering by type, category, location, and price range.'
              },
              {
                icon: '✅',
                title: 'Verified Sellers',
                desc: 'All sellers are verified and reviewed. Rent or buy with confidence from trusted agricultural professionals.'
              },
              {
                icon: '💰',
                title: 'Transparent Pricing',
                desc: 'No hidden fees. See the exact rental or purchase price upfront. Secure payment processing included.'
              },
              {
                icon: '📊',
                title: 'Market Data',
                desc: 'Access real-time market trends and pricing data to make informed decisions about equipment rental and purchase.'
              },
              {
                icon: '📱',
                title: 'Mobile Friendly',
                desc: 'Browse, list, and manage your equipment on the go with our fully responsive mobile platform.'
              },
              {
                icon: '🤝',
                title: '24/7 Support',
                desc: 'Dedicated support team ready to help with any questions or issues anytime, anywhere.'
              }
            ].map((feature, idx) => (
              <div key={idx} className="p-8 bg-white rounded-xl border border-green-100 hover:border-green-500 hover:shadow-xl transition transform hover:scale-105 duration-300">
                <div className="text-5xl mb-4 inline-block p-3 bg-green-100 rounded-lg">{feature.icon}</div>
                <h3 className="text-xl font-bold text-gray-900 mb-3">{feature.title}</h3>
                <p className="text-gray-600">{feature.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="bg-blue-50 py-20 px-4 md:px-8">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">How It Works</h2>
            <p className="text-xl text-gray-600">Get started in just a few simple steps</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            {[
              { number: '1', title: 'Create Account', desc: 'Sign up for free and complete your profile' },
              { number: '2', title: 'Browse or List', desc: 'Find equipment or list yours for rent/sale' },
              { number: '3', title: 'Connect', desc: 'Message sellers/buyers directly for details' },
              { number: '4', title: 'Transact', desc: 'Complete rental or purchase securely' }
            ].map((step, idx) => (
              <div key={idx} className="text-center">
                <div className="w-16 h-16 bg-blue-600 text-white rounded-full flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                  {step.number}
                </div>
                <h3 className="text-xl font-bold text-gray-900 mb-2">{step.title}</h3>
                <p className="text-gray-600">{step.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="bg-white py-20 px-4 md:px-8 border-b border-gray-200">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-16">
            <h2 className="text-4xl font-bold text-gray-900 mb-4">Popular Equipment Categories</h2>
            <p className="text-xl text-gray-600">Browse by equipment type or check what's trending</p>
          </div>

          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
            {[
              { emoji: '🚜', name: 'Tractors' },
              { emoji: '🌾', name: 'Seeders' },
              { emoji: '⛓️', name: 'Harvesters' },
              { emoji: '🪜', name: 'Ploughs' },
              { emoji: '💧', name: 'Sprayers' },
              { emoji: '⚙️', name: 'Other' }
            ].map((cat, idx) => (
              <Link
                key={idx}
                to="/marketplace"
                className="p-6 bg-gradient-to-br from-blue-50 to-indigo-50 border border-blue-200 rounded-lg text-center hover:shadow-lg hover:border-blue-400 transition group"
              >
                <div className="text-4xl mb-3 group-hover:scale-110 transition">{cat.emoji}</div>
                <p className="font-semibold text-gray-900">{cat.name}</p>
              </Link>
            ))}
          </div>
        </div>
      </section>



      <section className="relative bg-gradient-to-br from-blue-600 via-green-300 to-emerald-700 py-24 px-4 md:px-8 overflow-hidden">
        <div className="absolute inset-0 opacity-10">
          <div className="absolute top-0 left-0 w-96 h-96 bg-white rounded-full blur-3xl"></div>
          <div className="absolute bottom-0 right-0 w-96 h-96 bg-white rounded-full blur-3xl"></div>
        </div>
        
        <div className="relative max-w-4xl mx-auto text-center space-y-8">
          <div className="inline-block bg-white bg-opacity-20 backdrop-blur-sm px-6 py-2 rounded-full border border-white border-opacity-30">
            <p className="text-white font-semibold">🚀 Start Your Journey</p>
          </div>
          
          <h2 className="text-5xl md:text-6xl font-bold text-white drop-shadow-lg">Flex Your Farm Today</h2>
          <p className="text-xl text-green-50 max-w-2xl mx-auto leading-relaxed">Farm Your Way. Join thousands of farmers on FarmFlex. Connect, trade, and grow without limits.</p>
          
          <div className="flex flex-col sm:flex-row gap-4 justify-center pt-6">
            {!user ? (
              <>
                <Link
                  to="/register"
                  className="px-10 py-4 bg-white text-green-600 rounded-xl hover:bg-green-50 transition font-bold text-lg shadow-lg hover:shadow-xl transform hover:scale-105 duration-200"
                >
                  Create Free Account
                </Link>
                <Link
                  to="/login"
                  className="px-10 py-4 bg-green-500 text-white rounded-xl hover:bg-green-400 transition font-bold text-lg border-2 border-white shadow-lg hover:shadow-xl transform hover:scale-105 duration-200"
                >
                  Sign In
                </Link>
              </>
            ) : (
              <Link
                to="/marketplace"
                className="px-10 py-4 bg-white text-green-600 rounded-xl hover:bg-green-50 transition font-bold text-lg shadow-lg hover:shadow-xl transform hover:scale-105 duration-200"
              >
                Browse Equipment Now
              </Link>
            )}
          </div>
        </div>
      </section>

      <Footer />
    </div>
  );
}
