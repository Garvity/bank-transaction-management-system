import React, { useState } from 'react';

export default function Deposit({ pin, onBackToDashboard }) {
  const [amount, setAmount] = useState('');
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const handleDeposit = async (e) => {
    e.preventDefault();
    setMessage('');
    setIsError(false);

    if (!amount || isNaN(amount) || parseFloat(amount) <= 0) {
      setIsError(true);
      setMessage('Please enter the Amount you want to Deposit');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/transactions/deposit', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pin, amount }),
      });

      const data = await response.json();

      if (response.ok && data.success) {
        alert(`Rs. ${amount} Deposited Successfully`);
        onBackToDashboard();
      } else {
        setIsError(true);
        setMessage(data.message || 'Deposit transaction failed.');
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
          <h2>Deposit Funds</h2>

          {message && (
            <div className={`alert-box ${isError ? 'alert-box-error' : ''}`} style={{ fontFamily: 'monospace' }}>
              <div>
                <div className="alert-title">{isError ? 'Transaction Alert' : 'Success'}</div>
                <div>{message}</div>
              </div>
            </div>
          )}

          <form onSubmit={handleDeposit}>
            <div className="atm-input-wrapper">
              <label htmlFor="depositAmount">ENTER AMOUNT YOU WANT TO DEPOSIT:</label>
              <input
                id="depositAmount"
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
                DEPOSIT
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
