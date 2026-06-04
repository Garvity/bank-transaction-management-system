import React, { useState } from 'react';
import bankLogo from '../assets/bank.png';

export default function SignupStep3({ formNo, onSignupComplete, onCancel, onBackToLogin }) {
  const [accountType, setAccountType] = useState('Saving Account');
  const [services, setServices] = useState({
    atmCard: false,
    internetBanking: false,
    mobileBanking: false,
    emailAlerts: false,
    chequeBook: false,
    eStatement: false
  });
  const [declared, setDeclared] = useState(true);
  const [error, setError] = useState('');
  const [credentials, setCredentials] = useState(null); // Holds generated card & pin after submit

  const handleCheckboxChange = (name) => {
    setServices(prev => ({
      ...prev,
      [name]: !prev[name]
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!accountType) {
      setError('Please select an Account Type');
      return;
    }
    if (!declared) {
      setError('You must accept the declaration to proceed');
      return;
    }

    // Build facilities string exactly like original Swing code
    // (We will concatenate all checked items with spaces, fixing the else-if bug so all selected services are registered)
    let fac = '';
    if (services.atmCard) fac += 'ATM CARD ';
    if (services.internetBanking) fac += 'Internet Banking ';
    if (services.mobileBanking) fac += 'Mobile Banking ';
    if (services.emailAlerts) fac += 'EMAIL Alerts ';
    if (services.chequeBook) fac += 'Cheque Book ';
    if (services.eStatement) fac += 'E-Statement ';

    try {
      const payload = {
        formno: formNo,
        atype: accountType,
        fac: fac.trim()
      };

      const response = await fetch('http://localhost:8080/api/auth/signup/step3', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (response.ok && data.success) {
        // Save generated credentials to show the user
        setCredentials({
          cardNumber: data.cardNumber,
          pin: data.pin
        });
      } else {
        setError(data.message || 'Signup Step 3 failed.');
      }
    } catch (err) {
      setError('Failed to reach backend server. Please verify connections.');
    }
  };

  // User acknowledges card and pin details
  const handleAcknowledge = () => {
    onSignupComplete(credentials.pin, credentials.cardNumber);
  };

  return (
    <>
      <div className="app-container">
        <div className="title-header">
          <img src={bankLogo} alt="Bank Logo" className="logo-img" />
          <div className="header-text">
            <h1>APPLICATION FORM NO. {formNo}</h1>
            <p>Page 3: Account Details</p>
          </div>
        </div>

        {error && (
          <div className="alert-box alert-box-error">
            <div>
              <div className="alert-title">Validation Error</div>
              <div>{error}</div>
            </div>
          </div>
        )}

        {credentials ? (
          <div style={{ textAlign: 'center', padding: '40px 0', color: 'var(--text-secondary)' }}>
            Account created successfully. Please review the credentials on the overlay.
          </div>
        ) : (
          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <label>Account Type:</label>
              <div className="radio-group">
                {[
                  'Saving Account',
                  'Fixed Deposit Account',
                  'Current Account',
                  'Recurring Deposit Account'
                ].map(type => (
                  <label key={type} className={`radio-option ${accountType === type ? 'selected' : ''}`}>
                    <input
                      type="radio"
                      name="accountType"
                      value={type}
                      checked={accountType === type}
                      onChange={() => setAccountType(type)}
                    />
                    {type}
                  </label>
                ))}
              </div>
            </div>

            <div className="form-row" style={{ background: 'rgba(255,255,255,0.02)', padding: '15px', borderRadius: '12px', border: '1px solid var(--border-color)', marginBottom: '24px' }}>
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label style={{ color: 'var(--text-secondary)' }}>Card Number:</label>
                <div style={{ fontSize: '18px', fontWeight: 'bold', letterSpacing: '1px', fontFamily: 'monospace' }}>
                  XXXX-XXXX-XXXX-XXXX
                </div>
                <span style={{ fontSize: '11px', color: 'var(--text-secondary)' }}>(Your 16-digit Card Number)</span>
              </div>
              <div className="form-group" style={{ marginBottom: 0 }}>
                <label style={{ color: 'var(--text-secondary)' }}>PIN:</label>
                <div style={{ fontSize: '18px', fontWeight: 'bold', letterSpacing: '1px', fontFamily: 'monospace' }}>
                  XXXX
                </div>
                <span style={{ fontSize: '11px', color: 'var(--text-secondary)' }}>(Your 4-digit Password)</span>
              </div>
            </div>

            <div className="form-group">
              <label>Services Required:</label>
              <div className="checkbox-group">
                <label className={`checkbox-option ${services.atmCard ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={services.atmCard}
                    onChange={() => handleCheckboxChange('atmCard')}
                  />
                  ATM CARD
                </label>
                <label className={`checkbox-option ${services.internetBanking ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={services.internetBanking}
                    onChange={() => handleCheckboxChange('internetBanking')}
                  />
                  Internet Banking
                </label>
                <label className={`checkbox-option ${services.mobileBanking ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={services.mobileBanking}
                    onChange={() => handleCheckboxChange('mobileBanking')}
                  />
                  Mobile Banking
                </label>
                <label className={`checkbox-option ${services.emailAlerts ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={services.emailAlerts}
                    onChange={() => handleCheckboxChange('emailAlerts')}
                  />
                  EMAIL Alerts
                </label>
                <label className={`checkbox-option ${services.chequeBook ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={services.chequeBook}
                    onChange={() => handleCheckboxChange('chequeBook')}
                  />
                  Cheque Book
                </label>
                <label className={`checkbox-option ${services.eStatement ? 'selected' : ''}`}>
                  <input
                    type="checkbox"
                    checked={services.eStatement}
                    onChange={() => handleCheckboxChange('eStatement')}
                  />
                  E-Statement
                </label>
              </div>
            </div>

            <div className="form-group" style={{ marginTop: '30px' }}>
              <label className="checkbox-option" style={{ background: 'transparent', border: 'none', padding: 0 }}>
                <input
                  type="checkbox"
                  checked={declared}
                  onChange={() => setDeclared(!declared)}
                />
                <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                  I hereby declare that the above entered details are correct to the best of my knowledge.
                </span>
              </label>
            </div>

            <div className="btn-group">
              <button type="button" className="btn btn-secondary" onClick={onCancel}>
                Cancel
              </button>
              <button type="submit" className="btn btn-primary">
                Submit
              </button>
            </div>
          </form>
        )}
      </div>

      {credentials && (
        /* Gorgeous Credentials Card Modal */
        <div className="modal-overlay">
          <div className="modal-content" style={{ background: 'linear-gradient(135deg, #0f172a 0%, #1e293b 100%)', color: '#fff', border: '1px solid rgba(255, 255, 255, 0.1)', maxWidth: '450px' }}>
            <div className="statement-header" style={{ borderBottomColor: 'rgba(255, 255, 255, 0.1)' }}>
              <h3 style={{ color: '#fff', fontSize: '20px' }}>CONGRATULATIONS!</h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '13px', marginTop: '4px' }}>Your account has been created successfully</p>
            </div>
            
            <div style={{ margin: '20px 0', display: 'flex', flexDirection: 'column', gap: '20px' }}>
              <p style={{ fontSize: '14px', color: 'var(--text-secondary)', textAlign: 'center' }}>
                Please make a note of your generated credentials. You will need them to sign in.
              </p>

              {/* Graphical Card View */}
              <div className="card-preview" style={{ height: '200px', boxShadow: '0 8px 30px rgba(65, 125, 128, 0.2)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div className="card-chip"></div>
                  <div style={{ fontWeight: 'bold', fontSize: '14px', color: 'var(--primary-hover)', letterSpacing: '1px' }}>ATM CARD</div>
                </div>
                <div className="card-number" style={{ fontSize: '18px', textAlign: 'center', margin: '15px 0' }}>
                  {credentials.cardNumber.replace(/(\d{4})/g, '$1 ').trim()}
                </div>
                <div className="card-footer" style={{ fontSize: '11px' }}>
                  <div>PIN: <span style={{ color: '#ffd04c', fontSize: '14px', fontFamily: 'monospace' }}>{credentials.pin}</span></div>
                  <div>FORM NO: {formNo.trim()}</div>
                </div>
              </div>
            </div>

            <div className="statement-actions" style={{ marginTop: '20px', display: 'flex', flexDirection: 'column', gap: '10px' }}>
              <button className="btn btn-primary" style={{ width: '100%' }} onClick={handleAcknowledge}>
                CONTINUE TO DEPOSIT
              </button>
              <button className="btn btn-secondary" style={{ width: '100%', borderColor: 'rgba(255, 255, 255, 0.15)', color: '#fff' }} onClick={onBackToLogin}>
                BACK TO LOGIN
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
