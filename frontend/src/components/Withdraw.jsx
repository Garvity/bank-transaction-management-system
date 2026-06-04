import React, { useState } from 'react';

export default function Withdraw({ pin, onBackToDashboard }) {
  const [amount, setAmount] = useState('');
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const handleWithdraw = async (e) => {
    e.preventDefault();
    setMessage('');
    setIsError(false);

    if (!amount || isNaN(amount) || parseFloat(amount) <= 0) {
      setIsError(true);
      setMessage('Please enter the Amount you want to withdraw');
      return;
    }

    if (parseFloat(amount) > 10000) {
      setIsError(true);
      setMessage('Maximum withdrawal limit is Rs. 10,000 per transaction');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/transactions/withdraw', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pin, amount }),
      });

      const data = await response.json();

      if (response.ok && data.success) {
        alert(`Rs. ${amount} Debited Successfully`);
        onBackToDashboard();
      } else {
        setIsError(true);
        setMessage(data.message || 'Transaction failed.');
      }
    } catch (err) {
      setIsError(true);
      setMessage('Connection to backend failed. Please try again.');
    }
  };

  return (
    <div className="app-container" style={{ maxWidth: '750px' }}>
      <div className="atm-screen-frame">
        <div className="atm-screen-inner">
          <h2>Cash Withdrawal</h2>

          <div style={{ textAlign: 'center', color: '#ff7b72', fontSize: '14px', marginBottom: '20px', fontWeight: 'bold' }}>
            MAXIMUM WITHDRAWAL IS RS. 10,000
          </div>

          {message && (
            <div className={`alert-box ${isError ? 'alert-box-error' : ''}`} style={{ fontFamily: 'monospace' }}>
              <div>
                <div className="alert-title">Transaction Alert</div>
                <div>{message}</div>
              </div>
            </div>
          )}

          <form onSubmit={handleWithdraw}>
            <div className="atm-input-wrapper">
              <label htmlFor="withdrawAmount">PLEASE ENTER YOUR AMOUNT:</label>
              <input
                id="withdrawAmount"
                type="text"
                className="atm-input"
                placeholder="Rs."
                value={amount}
                onChange={(e) => setAmount(e.target.value)}
                autoFocus
              />
            </div>

            <div className="atm-grid" style={{ gridTemplateColumns: '1fr 1fr', marginTop: '20px' }}>
              <button type="button" className="atm-btn" onClick={onBackToDashboard}>
                BACK
              </button>
              <button type="submit" className="atm-btn">
                WITHDRAW
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
