import React, { useState } from 'react';

export default function PinChange({ pin, onPinChangeSuccess, onBackToDashboard }) {
  const [newPin, setNewPin] = useState('');
  const [confirmPin, setConfirmPin] = useState('');
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);

  const handleChangePin = async (e) => {
    e.preventDefault();
    setMessage('');
    setIsError(false);

    if (!newPin) {
      setIsError(true);
      setMessage('Enter New PIN');
      return;
    }

    if (!confirmPin) {
      setIsError(true);
      setMessage('Re-Enter New PIN');
      return;
    }

    if (newPin !== confirmPin) {
      setIsError(true);
      setMessage('Entered PIN does not match');
      return;
    }

    if (newPin.length !== 4 || isNaN(newPin)) {
      setIsError(true);
      setMessage('PIN must be a 4-digit number');
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/transactions/change-pin', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ pin, newPin }),
      });

      const data = await response.json();

      if (response.ok && data.success) {
        alert('PIN changed successfully');
        onPinChangeSuccess(newPin); // Update pin in root state so further transactions use new pin
      } else {
        setIsError(true);
        setMessage(data.message || 'Failed to change PIN.');
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
          <h2>Change Your PIN</h2>

          {message && (
            <div className={`alert-box ${isError ? 'alert-box-error' : ''}`} style={{ fontFamily: 'monospace' }}>
              <div>
                <div className="alert-title">PIN Change Alert</div>
                <div>{message}</div>
              </div>
            </div>
          )}

          <form onSubmit={handleChangePin}>
            <div className="atm-input-wrapper" style={{ margin: '20px 0', textAlign: 'left' }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', maxWidth: '350px', margin: '0 auto' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <label htmlFor="newPin" style={{ margin: 0 }}>New PIN:</label>
                  <input
                    id="newPin"
                    type="password"
                    maxLength={4}
                    style={{ background: 'transparent', border: 'none', borderBottom: '1px solid #3fb950', color: '#3fb950', fontSize: '20px', outline: 'none', width: '150px', textAlign: 'center', fontFamily: 'monospace' }}
                    value={newPin}
                    onChange={(e) => setNewPin(e.target.value)}
                    autoFocus
                  />
                </div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <label htmlFor="confirmPin" style={{ margin: 0 }}>Re-Enter PIN:</label>
                  <input
                    id="confirmPin"
                    type="password"
                    maxLength={4}
                    style={{ background: 'transparent', border: 'none', borderBottom: '1px solid #3fb950', color: '#3fb950', fontSize: '20px', outline: 'none', width: '150px', textAlign: 'center', fontFamily: 'monospace' }}
                    value={confirmPin}
                    onChange={(e) => setConfirmPin(e.target.value)}
                  />
                </div>
              </div>
            </div>

            <div className="atm-grid" style={{ gridTemplateColumns: '1fr 1fr', marginTop: '35px' }}>
              <button type="button" className="atm-btn" onClick={onBackToDashboard}>
                BACK
              </button>
              <button type="submit" className="atm-btn">
                CHANGE
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
