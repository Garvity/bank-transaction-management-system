import React, { useState } from 'react';
import bankLogo from '../assets/bank.png';

export default function SignupStep2({ formNo, onNextStep, onCancel }) {
  const [religion, setReligion] = useState('Hindu');
  const [category, setCategory] = useState('General');
  const [income, setIncome] = useState('Null');
  const [education, setEducation] = useState('Non-Graduate');
  const [occupation, setOccupation] = useState('Salaried');
  const [pan, setPan] = useState('');
  const [aadhar, setAadhar] = useState('');
  const [seniorCitizen, setSeniorCitizen] = useState('No');
  const [existingAccount, setExistingAccount] = useState('No');
  const [error, setError] = useState('');

  const handleNext = async (e) => {
    e.preventDefault();
    setError('');

    // Validations: PAN and Aadhar check (matching Swing's JOptionPane.showMessageDialog(null,"Fill all the fields"))
    if (!pan.trim() || !aadhar.trim()) {
      setError('Please fill in both PAN and Aadhar fields');
      return;
    }

    try {
      const payload = {
        formno: formNo,
        rel: religion,
        cate: category,
        inc: income,
        edu: education,
        occ: occupation,
        pan,
        addhar: aadhar,
        scitizen: seniorCitizen,
        eAccount: existingAccount
      };

      const response = await fetch('http://localhost:8080/api/auth/signup/step2', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (response.ok && data.success) {
        onNextStep();
      } else {
        setError(data.message || 'Signup Step 2 failed.');
      }
    } catch (err) {
      setError('Failed to reach backend server. Please verify connections.');
    }
  };

  return (
    <div className="app-container">
      <div className="title-header">
        <img src={bankLogo} alt="Bank Logo" className="logo-img" />
        <div className="header-text">
          <h1>APPLICATION FORM NO. {formNo}</h1>
          <p>Page 2: Additional Details</p>
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

      <form onSubmit={handleNext}>
        <div className="form-row">
          <div className="form-group">
            <label htmlFor="religion">Religion</label>
            <select
              id="religion"
              className="form-control"
              value={religion}
              onChange={(e) => setReligion(e.target.value)}
            >
              <option value="Hindu">Hindu</option>
              <option value="Muslim">Muslim</option>
              <option value="Sikh">Sikh</option>
              <option value="Christian">Christian</option>
              <option value="Other">Other</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="category">Category</label>
            <select
              id="category"
              className="form-control"
              value={category}
              onChange={(e) => setCategory(e.target.value)}
            >
              <option value="General">General</option>
              <option value="OBC">OBC</option>
              <option value="SC">SC</option>
              <option value="ST">ST</option>
              <option value="Other">Other</option>
            </select>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="income">Income</label>
            <select
              id="income"
              className="form-control"
              value={income}
              onChange={(e) => setIncome(e.target.value)}
            >
              <option value="Null">Null</option>
              <option value="&lt;1,50,000">&lt;1,50,000</option>
              <option value="&lt;2,50,000">&lt;2,50,000</option>
              <option value="5,00,000">5,00,000</option>
              <option value="Uptp 10,00,000">Uptp 10,00,000</option>
              <option value="Above 10,00,000">Above 10,00,000</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="education">Educational Qualification</label>
            <select
              id="education"
              className="form-control"
              value={education}
              onChange={(e) => setEducation(e.target.value)}
            >
              <option value="Non-Graduate">Non-Graduate</option>
              <option value="Graduate">Graduate</option>
              <option value="Post-Graduate">Post-Graduate</option>
              <option value="Doctrate">Doctrate</option>
              <option value="Others">Others</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="occupation">Occupation</label>
          <select
            id="occupation"
            className="form-control"
            value={occupation}
            onChange={(e) => setOccupation(e.target.value)}
          >
            <option value="Salaried">Salaried</option>
            <option value="Self-Employed">Self-Employed</option>
            <option value="Business">Business</option>
            <option value="Student">Student</option>
            <option value="Retired">Retired</option>
            <option value="Other">Other</option>
          </select>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="pan">PAN Number *</label>
            <input
              id="pan"
              type="text"
              className="form-control"
              placeholder="Enter 10-digit PAN"
              value={pan}
              onChange={(e) => setPan(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="aadhar">Aadhar Number *</label>
            <input
              id="aadhar"
              type="text"
              className="form-control"
              placeholder="Enter 12-digit Aadhar"
              value={aadhar}
              onChange={(e) => setAadhar(e.target.value)}
              required
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label>Senior Citizen *</label>
            <div className="radio-group">
              <label className={`radio-option ${seniorCitizen === 'Yes' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="seniorCitizen"
                  value="Yes"
                  checked={seniorCitizen === 'Yes'}
                  onChange={() => setSeniorCitizen('Yes')}
                />
                Yes
              </label>
              <label className={`radio-option ${seniorCitizen === 'No' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="seniorCitizen"
                  value="No"
                  checked={seniorCitizen === 'No'}
                  onChange={() => setSeniorCitizen('No')}
                />
                No
              </label>
            </div>
          </div>

          <div className="form-group">
            <label>Existing Account *</label>
            <div className="radio-group">
              <label className={`radio-option ${existingAccount === 'Yes' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="existingAccount"
                  value="Yes"
                  checked={existingAccount === 'Yes'}
                  onChange={() => setExistingAccount('Yes')}
                />
                Yes
              </label>
              <label className={`radio-option ${existingAccount === 'No' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="existingAccount"
                  value="No"
                  checked={existingAccount === 'No'}
                  onChange={() => setExistingAccount('No')}
                />
                No
              </label>
            </div>
          </div>
        </div>

        <div className="btn-group">
          <button type="button" className="btn btn-secondary" onClick={onCancel}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary">
            Next
          </button>
        </div>
      </form>
    </div>
  );
}
