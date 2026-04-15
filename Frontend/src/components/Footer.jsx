import React from 'react';
import { Link } from 'react-router-dom';

export default function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-300 border-t border-gray-800">
      <div className="max-w-7xl mx-auto px-4 md:px-8 py-12">
        <div className="grid grid-cols-1 md:grid-cols-4 gap-8 mb-8">
          <div>
            <div className="flex items-center gap-3 mb-4">
              <div className="w-10 h-10 bg-gradient-to-br from-green-500 to-green-600 rounded-lg flex items-center justify-center">
                <span className="text-lg font-bold text-white">⚡</span>
              </div>
              <div>
                <h3 className="text-white font-bold">FarmFlex</h3>
                <p className="text-xs text-gray-500">Flex Your Farm</p>
              </div>
            </div>
            <p className="text-sm text-gray-400 leading-relaxed">
              Connect with verified sellers and buyers. Farm without limits with FarmFlex - your flexible agricultural equipment marketplace.
            </p>
          </div>

          <div>
            <h4 className="text-white font-semibold mb-4">Quick Links</h4>
            <ul className="space-y-2 text-sm">
              <li><Link to="/" className="text-gray-400 hover:text-white transition">Home</Link></li>
              <li><Link to="/marketplace" className="text-gray-400 hover:text-white transition">Marketplace</Link></li>
              <li><Link to="/about" className="text-gray-400 hover:text-white transition">About Us</Link></li>
              <li><Link to="/contact" className="text-gray-400 hover:text-white transition">Contact</Link></li>
            </ul>
          </div>

          <div>
            <h4 className="text-white font-semibold mb-4">For Farmers</h4>
            <ul className="space-y-2 text-sm">
              <li><Link to="/register" className="text-gray-400 hover:text-white transition">Become a Seller</Link></li>
              <li><Link to="/marketplace" className="text-gray-400 hover:text-white transition">Browse Equipment</Link></li>
              <li><Link to="#faq" className="text-gray-400 hover:text-white transition">FAQ</Link></li>
              <li><Link to="#support" className="text-gray-400 hover:text-white transition">Support</Link></li>
            </ul>
          </div>

          <div>
            <h4 className="text-white font-semibold mb-4">Legal</h4>
            <ul className="space-y-2 text-sm">
              <li><Link to="/terms" className="text-gray-400 hover:text-white transition">Terms of Service</Link></li>
              <li><Link to="/privacy" className="text-gray-400 hover:text-white transition">Privacy Policy</Link></li>
              <li><Link to="/cookies" className="text-gray-400 hover:text-white transition">Cookie Policy</Link></li>
              <li><Link to="/security" className="text-gray-400 hover:text-white transition">Security</Link></li>
            </ul>
          </div>
        </div>

        <div className="border-t border-gray-800 pt-8">
          <div className="flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-gray-400">
            <p>&copy; 2026 FarmFlex. All rights reserved. | Flex Your Farm, Farm Your Way, Farming Without Limits</p>
            <div className="flex gap-6">
              <a href="#facebook" className="hover:text-white transition">Facebook</a>
              <a href="#twitter" className="hover:text-white transition">Twitter</a>
              <a href="#linkedin" className="hover:text-white transition">LinkedIn</a>
              <a href="#instagram" className="hover:text-white transition">Instagram</a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  );
}
