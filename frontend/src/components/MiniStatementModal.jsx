import React, { useState, useEffect } from 'react';

export default function MiniStatementModal({ pin, onClose }) {
  const [statement, setStatement] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchMiniStatement = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/transactions/mini-statement?pin=${pin}`);
        const data = await response.json();
        if (response.ok && data.success) {
          setStatement(data);
        } else {
          setError(data.message || 'Failed to retrieve statement.');
        }
      } catch (err) {
        setError('Server communication failed.');
      }
    };

    fetchMiniStatement();
  }, [pin]);

  return (
    <div className="modal-overlay">
      <div className="modal-content">
        <div className="statement-header">
          <h3>Garvity</h3>
          <p style={{ fontSize: '11px', color: '#666', marginTop: '2px' }}>Mini Statement Receipt</p>
        </div>

        {error ? (
          <div style={{ color: 'var(--danger-color)', padding: '20px', textAlign: 'center', fontFamily: 'monospace' }}>
            {error}
          </div>
        ) : !statement ? (
          <div style={{ padding: '30px', textAlign: 'center', color: '#666', fontFamily: 'monospace' }}>
            Printing statement...
          </div>
        ) : (
          <div className="statement-body">
            <div className="statement-card-info">
              {statement.cardNumber}
            </div>

            <div className="statement-tx-list">
              {statement.transactions && statement.transactions.length > 0 ? (
                statement.transactions.map((tx, idx) => (
                  <div key={idx} className="statement-tx-item">
                    <span className="statement-tx-date">{tx.date}</span>
                    <span className={`statement-tx-type ${tx.type === 'Deposit' ? 'deposit' : 'withdrawal'}`}>
                      {tx.type}
                    </span>
                    <span className="statement-tx-amount">Rs. {tx.amount}</span>
                  </div>
                ))
              ) : (
                <div style={{ textAlign: 'center', color: '#999', margin: '20px 0', fontSize: '12px' }}>
                  No transactions recorded
                </div>
              )}
            </div>

            <div className="statement-balance" style={{ marginTop: '20px' }}>
              <span>Total Balance:</span>
              <span>Rs. {statement.balance}</span>
            </div>
          </div>
        )}

        <div className="statement-actions">
          <button className="btn btn-secondary" style={{ backgroundColor: '#000', color: '#fff', border: 'none', width: '120px' }} onClick={onClose}>
            Exit
          </button>
        </div>
      </div>
    </div>
  );
}
