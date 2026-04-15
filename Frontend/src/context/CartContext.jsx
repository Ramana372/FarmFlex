import React, { createContext, useContext, useState, useEffect } from 'react';

export const CartContext = createContext();

export function CartProvider({ children }) {
  const [cartItems, setCartItems] = useState([]);

  useEffect(() => {
    const savedCart = localStorage.getItem('cart');
    if (savedCart) {
      try {
        setCartItems(JSON.parse(savedCart));
      } catch (err) {
        console.error('Failed to load cart:', err);
      }
    }
  }, []);

  useEffect(() => {
    localStorage.setItem('cart', JSON.stringify(cartItems));
  }, [cartItems]);

  const addToCart = (listing) => {
    setCartItems(prevItems => {
      const existingItem = prevItems.find(item => item.id === listing.id);
      
      if (existingItem) {
        // Update quantity if already in cart
        return prevItems.map(item =>
          item.id === listing.id
            ? { ...item, quantity: (item.quantity || 1) + 1 }
            : item
        );
        return [...prevItems, { ...listing, quantity: 1 }];
      }
    });
  };

  const removeFromCart = (listingId) => {
    setCartItems(prevItems => prevItems.filter(item => item.id !== listingId));
  };

  const updateQuantity = (listingId, quantity) => {
    if (quantity <= 0) {
      removeFromCart(listingId);
    } else {
      setCartItems(prevItems =>
        prevItems.map(item =>
          item.id === listingId ? { ...item, quantity } : item
        )
      );
    }
  };

  const clearCart = () => {
    setCartItems([]);
  };

  const getCartTotal = () => {
    return cartItems.reduce((total, item) => {
      const price = item.type === 'RENT' ? item.rentPricePerDay : item.salePrice;
      return total + (price * (item.quantity || 1));
    }, 0);
  };

  const getCartItemsCount = () => {
    return cartItems.reduce((count, item) => count + (item.quantity || 1), 0);
  };

  return (
    <CartContext.Provider value={{
      cartItems,
      addToCart,
      removeFromCart,
      updateQuantity,
      clearCart,
      getCartTotal,
      getCartItemsCount
    }}>
      {children}
    </CartContext.Provider>
  );
}

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within CartProvider');
  }
  return context;
}
