import React, { useState } from 'react';

export default function FastCash({ pin, onBackToDashboard }) {
  const [error, setError] = useState('');

  const handleFastCash = async (amount) => {
    setError('');
    try {
      const response = await fetch('http://localhost:8080/api/transactions/fast-cash', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pin, amount: String(amount) }),
      });

      const data = await response.json();

      if (response.ok && data.success) {
        alert(`Rs. ${amount} Debited Successfully`);
        onBackToDashboard();
      } else {
        setError(data.message || 'Transaction failed. Please check your balance.');
      }
    } catch (err) {
      setError('Connection to backend failed. Please try again.');
    }
  };

  return (
    <div className="app-container" style={{ maxWidth: '750px' }}>
      <div className="atm-screen-frame">
        <div className="atm-screen-inner">
          <h2>Select Withdrawal Amount</h2>

          {error && (
            <div className="alert-box alert-box-error" style={{ fontFamily: 'monospace' }}>
              <div>
                <div className="alert-title">Transaction Alert</div>
                <div>{error}</div>
              </div>
            </div>
          )}

          <div className="atm-grid">
            <button className="atm-btn" onClick={() => handleFastCash(100)}>
              Rs. 100
            </button>
            <button className="atm-btn" onClick={() => handleFastCash(500)}>
              Rs. 500
            </button>
            <button className="atm-btn" onClick={() => handleFastCash(1000)}>
              Rs. 1000
            </button>
            <button className="atm-btn" onClick={() => handleFastCash(2000)}>
              Rs. 2000
            </button>
            <button className="atm-btn" onClick={() => handleFastCash(5000)}>
              Rs. 5000
            </button>
            <button className="atm-btn" onClick={() => handleFastCash(10000)}>
              Rs. 10000
            </button>
            <button className="atm-btn exit-btn" onClick={onBackToDashboard}>
              BACK
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
