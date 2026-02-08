import React, { useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { authAPI } from '../api/authAPI';

export default function VerifyEmailPage() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const [message, setMessage] = React.useState('Verifying your email...');
  const [error, setError] = React.useState('');

  useEffect(() => {
    const verifyEmail = async () => {
      const token = searchParams.get('token');
      
      if (!token) {
        setError('No verification token provided');
        return;
      }

      try {
        const response = await authAPI.verifyEmail(token);
        setMessage(response.data.message);
        setTimeout(() => navigate('/login'), 3000);
      } catch (err) {
        setError(err.response?.data?.error || 'Email verification failed');
      }
    };

    verifyEmail();
  }, [searchParams, navigate]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-100">
      <div className="max-w-md w-full bg-white rounded-lg shadow-md p-8 text-center">
        <h1 className="text-2xl font-bold text-gray-800 mb-4">Email Verification</h1>
        
        {error ? (
          <div className="p-3 bg-red-100 text-red-700 rounded">
            {error}
          </div>
        ) : (
          <div className="p-3 bg-green-100 text-green-700 rounded">
            {message}
          </div>
        )}

        <p className="text-gray-600 mt-4">Redirecting to login page...</p>
      </div>
    </div>
  );
}
