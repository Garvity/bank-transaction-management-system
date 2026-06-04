import React, { useState, useEffect } from 'react';
import bankLogo from '../assets/bank.png';

export default function SignupStep1({ formNo, onNextStep, onCancel }) {
  const [name, setName] = useState('');
  const [fname, setFname] = useState('');
  const [dob, setDob] = useState('');
  const [gender, setGender] = useState('Male');
  const [email, setEmail] = useState('');
  const [marital, setMarital] = useState('Married');
  const [address, setAddress] = useState('');
  const [city, setCity] = useState('');
  const [pincode, setPincode] = useState('');
  const [state, setState] = useState('');
  const [error, setError] = useState('');

  const handleNext = async (e) => {
    e.preventDefault();
    setError('');

    // Verification: name check (matching JOptionPane.showMessageDialog(null, "Fill all the fields"))
    if (!name.trim()) {
      setError('Please fill in the Name field');
      return;
    }
    if (!fname.trim() || !dob || !email.trim() || !address.trim() || !city.trim() || !pincode.trim() || !state.trim()) {
      setError('Please fill in all the fields');
      return;
    }

    try {
      const payload = {
        formno: formNo,
        name,
        fname,
        dob,
        gender,
        email,
        marital,
        address,
        city,
        pincode,
        state
      };

      const response = await fetch('http://localhost:8080/api/auth/signup/step1', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });

      const data = await response.json();

      if (response.ok && data.success) {
        onNextStep();
      } else {
        setError(data.message || 'Signup Step 1 failed.');
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
          <p>Page 1: Personal Details</p>
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
            <label htmlFor="name">Name *</label>
            <input
              id="name"
              type="text"
              className="form-control"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="fname">Father's Name *</label>
            <input
              id="fname"
              type="text"
              className="form-control"
              value={fname}
              onChange={(e) => setFname(e.target.value)}
              required
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="dob">Date of Birth *</label>
            <input
              id="dob"
              type="date"
              className="form-control date-picker-input"
              value={dob}
              onChange={(e) => setDob(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Gender *</label>
            <div className="radio-group">
              <label className={`radio-option ${gender === 'Male' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="gender"
                  value="Male"
                  checked={gender === 'Male'}
                  onChange={() => setGender('Male')}
                />
                Male
              </label>
              <label className={`radio-option ${gender === 'Female' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="gender"
                  value="Female"
                  checked={gender === 'Female'}
                  onChange={() => setGender('Female')}
                />
                Female
              </label>
            </div>
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="email">Email Address *</label>
            <input
              id="email"
              type="email"
              className="form-control"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label>Marital Status *</label>
            <div className="radio-group">
              <label className={`radio-option ${marital === 'Married' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="marital"
                  value="Married"
                  checked={marital === 'Married'}
                  onChange={() => setMarital('Married')}
                />
                Married
              </label>
              <label className={`radio-option ${marital === 'Unmarried' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="marital"
                  value="Unmarried"
                  checked={marital === 'Unmarried'}
                  onChange={() => setMarital('Unmarried')}
                />
                Unmarried
              </label>
              <label className={`radio-option ${marital === 'Other' ? 'selected' : ''}`}>
                <input
                  type="radio"
                  name="marital"
                  value="Other"
                  checked={marital === 'Other'}
                  onChange={() => setMarital('Other')}
                />
                Other
              </label>
            </div>
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="address">Address *</label>
          <input
            id="address"
            type="text"
            className="form-control"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            required
          />
        </div>

        <div className="form-row">
          <div className="form-group">
            <label htmlFor="city">City *</label>
            <input
              id="city"
              type="text"
              className="form-control"
              value={city}
              onChange={(e) => setCity(e.target.value)}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="pincode">Pin Code *</label>
            <input
              id="pincode"
              type="text"
              className="form-control"
              value={pincode}
              onChange={(e) => setPincode(e.target.value)}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label htmlFor="state">State *</label>
          <input
            id="state"
            type="text"
            className="form-control"
            value={state}
            onChange={(e) => setState(e.target.value)}
            required
          />
        </div>

        <div className="btn-group">
          <button type="button" className="btn btn-secondary" onClick={onCancel}>
            Back to Login
          </button>
          <button type="submit" className="btn btn-primary">
            Next
          </button>
        </div>
      </form>
    </div>
  );
}
