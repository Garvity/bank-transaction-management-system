import React from 'react';

export default function Dashboard({ onSelectScreen, onOpenMiniStatement, onExit }) {
  return (
    <div className="app-container" style={{ maxWidth: '750px' }}>
      <div className="atm-screen-frame">
        <div className="atm-screen-inner">
          <h2>Please Select Your Transaction</h2>

          <div className="atm-grid">
            <button className="atm-btn" onClick={() => onSelectScreen('DEPOSIT')}>
              DEPOSIT
            </button>
            <button className="atm-btn" onClick={() => onSelectScreen('WITHDRAW')}>
              CASH WITHDRAWAL
            </button>
            <button className="atm-btn" onClick={() => onSelectScreen('FAST_CASH')}>
              FAST CASH
            </button>
            <button className="atm-btn" onClick={onOpenMiniStatement}>
              MINI STATEMENT
            </button>
            <button className="atm-btn" onClick={() => onSelectScreen('PIN_CHANGE')}>
              PIN CHANGE
            </button>
            <button className="atm-btn" onClick={() => onSelectScreen('BALANCE')}>
              BALANCE ENQUIRY
            </button>
            <button className="atm-btn exit-btn" onClick={onExit}>
              EXIT (CARD OUT)
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
