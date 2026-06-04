import React, { useState } from 'react';
import bankLogo from '../assets/bank.png';
import cardLogo from '../assets/card.png';

export default function Login({ onLoginSuccess, onNavigateToSignup }) {
  const [cardNumber, setCardNumber] = useState('');
  const [pin, setPin] = useState('');
  const [error, setError] = useState('');

  const handleSignIn = async (e) => {
    e.preventDefault();
    setError('');

    if (!cardNumber || !pin) {
      setError('Please fill in both Card Number and PIN');
      return;
    }

    try {
      const cleanCardNumber = cardNumber.replace(/\s|-/g, '');
      const response = await fetch('http://localhost:8080/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ cardNumber: cleanCardNumber, pin }),
      });

      const data = await response.json();

      if (response.ok && data.success) {
        onLoginSuccess(data.pin, data.cardNumber);
      } else {
        setError(data.message || 'Incorrect Card Number or PIN');
      }
    } catch (err) {
      setError('Server connection failed. Make sure the Spring Boot backend is running.');
    }
  };

  const handleClear = () => {
    setCardNumber('');
    setPin('');
    setError('');
  };

  return (
    <div className="app-container">
      <div className="title-header">
        <img src={bankLogo} alt="Bank Logo" className="logo-img" />
        <div className="header-text">
          <h1>WELCOME TO ATM</h1>
          <p>Secure Bank Management Portal</p>
        </div>
      </div>

      {error && (
        <div className="alert-box alert-box-error">
          <div>
            <div className="alert-title">Authentication Alert</div>
            <div>{error}</div>
          </div>
        </div>
      )}

      <div className="welcome-grid">
        <form onSubmit={handleSignIn}>
          <div className="form-group">
            <label htmlFor="cardNumber">Card Number:</label>
            <input
              id="cardNumber"
              type="text"
              className="form-control"
              placeholder="XXXX-XXXX-XXXX-XXXX"
              value={cardNumber}
              onChange={(e) => setCardNumber(e.target.value)}
            />
          </div>

          <div className="form-group">
            <label htmlFor="pin">PIN:</label>
            <input
              id="pin"
              type="password"
              className="form-control"
              placeholder="XXXX"
              maxLength={4}
              value={pin}
              onChange={(e) => setPin(e.target.value)}
            />
          </div>

          <div className="btn-group" style={{ justifyContent: 'flex-start' }}>
            <button type="submit" className="btn btn-primary">
              SIGN IN
            </button>
            <button type="button" className="btn btn-secondary" onClick={handleClear}>
              CLEAR
            </button>
          </div>

          <div style={{ marginTop: '20px' }}>
            <button
              type="button"
              className="btn btn-accent"
              style={{ width: '100%' }}
              onClick={onNavigateToSignup}
            >
              CREATE NEW ACCOUNT (SIGN UP)
            </button>
          </div>
        </form>

        <div style={{ display: 'flex', justifyContent: 'center' }}>
          <div className="card-preview">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div className="card-chip"></div>
              <img src={cardLogo} alt="Card Provider" className="logo-img-small" style={{ opacity: 0.8 }} />
            </div>
            <div className="card-number">
              {cardNumber ? cardNumber.replace(/(\d{4})/g, '$1 ').trim() : 'XXXX XXXX XXXX XXXX'}
            </div>
            <div className="card-footer">
              <div>CARDHOLDER</div>
              <div>VALID THRU 12/29</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
