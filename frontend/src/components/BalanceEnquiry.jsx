import React, { useState, useEffect } from 'react';

export default function BalanceEnquiry({ pin, onBackToDashboard }) {
  const [balance, setBalance] = useState('Loading...');
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchBalance = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/transactions/balance?pin=${pin}`);
        const data = await response.json();
        if (response.ok && data.success) {
          setBalance(data.balance);
        } else {
          setError(data.message || 'Failed to retrieve balance.');
        }
      } catch (err) {
        setError('Failed to reach server.');
      }
    };

    fetchBalance();
  }, [pin]);

  return (
    <div className="app-container" style={{ maxWidth: '750px' }}>
      <div className="atm-screen-frame">
        <div className="atm-screen-inner" style={{ textAlign: 'center' }}>
          <h2>Balance Enquiry</h2>

          {error ? (
            <div className="alert-box alert-box-error" style={{ fontFamily: 'monospace', margin: '20px auto', maxWidth: '350px' }}>
              <div>
                <div className="alert-title">System Error</div>
                <div>{error}</div>
              </div>
            </div>
          ) : (
            <div className="atm-input-wrapper" style={{ margin: '40px 0' }}>
              <div style={{ fontSize: '18px', color: '#3fb950', marginBottom: '15px' }}>
                YOUR CURRENT BALANCE IS:
              </div>
              <div style={{ fontSize: '42px', fontWeight: 'bold', textShadow: '0 0 10px rgba(63, 185, 80, 0.6)' }}>
                Rs. {balance}
              </div>
            </div>
          )}

          <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
            <button type="button" className="atm-btn" style={{ width: '200px' }} onClick={onBackToDashboard}>
              BACK
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
