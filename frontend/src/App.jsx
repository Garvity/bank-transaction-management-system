import React, { useState } from 'react';
import Login from './components/Login';
import SignupStep1 from './components/SignupStep1';
import SignupStep2 from './components/SignupStep2';
import SignupStep3 from './components/SignupStep3';
import Dashboard from './components/Dashboard';
import Deposit from './components/Deposit';
import Withdraw from './components/Withdraw';
import FastCash from './components/FastCash';
import BalanceEnquiry from './components/BalanceEnquiry';
import PinChange from './components/PinChange';
import MiniStatementModal from './components/MiniStatementModal';

export default function App() {
  const [screen, setScreen] = useState('LOGIN'); // Screen states: LOGIN, SIGNUP_1, SIGNUP_2, SIGNUP_3, DASHBOARD, DEPOSIT, WITHDRAW, FAST_CASH, BALANCE, PIN_CHANGE
  const [userPin, setUserPin] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [formNumber, setFormNumber] = useState('');
  const [showMiniStatement, setShowMiniStatement] = useState(false);

  // Triggered on successful login
  const handleLoginSuccess = (pin, cardNum) => {
    setUserPin(pin);
    setCardNumber(cardNum);
    setScreen('DASHBOARD');
  };

  // Generate new Form Number and open Signup Step 1
  const handleNavigateToSignup = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/generate-formno');
      const data = await response.json();
      if (response.ok && data.formno) {
        setFormNumber(data.formno);
        setScreen('SIGNUP_1');
      } else {
        alert('Failed to generate application form number. Check backend.');
      }
    } catch (err) {
      alert('Cannot connect to backend server. Make sure Spring Boot is running on port 8080.');
    }
  };

  // Called when step 3 generates credentials (card and pin) and user acknowledges it
  const handleSignupComplete = (generatedPin, generatedCard) => {
    setUserPin(generatedPin);
    setCardNumber(generatedCard);
    setScreen('DEPOSIT'); // Opens deposit directly, matching Swing's "new Deposit(pin)" step.
  };

  // Exit/Log out
  const handleExit = () => {
    setUserPin('');
    setCardNumber('');
    setFormNumber('');
    setScreen('LOGIN');
  };

  // Pin change synchronization
  const handlePinChangeSuccess = (newPin) => {
    setUserPin(newPin);
    setScreen('DASHBOARD');
  };

  // Router switch rendering
  const renderScreen = () => {
    switch (screen) {
      case 'LOGIN':
        return (
          <Login
            onLoginSuccess={handleLoginSuccess}
            onNavigateToSignup={handleNavigateToSignup}
          />
        );
      case 'SIGNUP_1':
        return (
          <SignupStep1
            formNo={formNumber}
            onNextStep={() => setScreen('SIGNUP_2')}
            onCancel={() => setScreen('LOGIN')}
          />
        );
      case 'SIGNUP_2':
        return (
          <SignupStep2
            formNo={formNumber}
            onNextStep={() => setScreen('SIGNUP_3')}
            onCancel={() => setScreen('SIGNUP_1')}
          />
        );
      case 'SIGNUP_3':
        return (
          <SignupStep3
            formNo={formNumber}
            onSignupComplete={handleSignupComplete}
            onCancel={() => setScreen('SIGNUP_2')}
            onBackToLogin={handleExit}
          />
        );
      case 'DASHBOARD':
        return (
          <Dashboard
            onSelectScreen={setScreen}
            onOpenMiniStatement={() => setShowMiniStatement(true)}
            onExit={handleExit}
          />
        );
      case 'DEPOSIT':
        return (
          <Deposit
            pin={userPin}
            onBackToDashboard={() => setScreen('DASHBOARD')}
          />
        );
      case 'WITHDRAW':
        return (
          <Withdraw
            pin={userPin}
            onBackToDashboard={() => setScreen('DASHBOARD')}
          />
        );
      case 'FAST_CASH':
        return (
          <FastCash
            pin={userPin}
            onBackToDashboard={() => setScreen('DASHBOARD')}
          />
        );
      case 'BALANCE':
        return (
          <BalanceEnquiry
            pin={userPin}
            onBackToDashboard={() => setScreen('DASHBOARD')}
          />
        );
      case 'PIN_CHANGE':
        return (
          <PinChange
            pin={userPin}
            onPinChangeSuccess={handlePinChangeSuccess}
            onBackToDashboard={() => setScreen('DASHBOARD')}
          />
        );
      default:
        return (
          <Login
            onLoginSuccess={handleLoginSuccess}
            onNavigateToSignup={handleNavigateToSignup}
          />
        );
    }
  };

  return (
    <>
      {renderScreen()}

      {/* Mini Statement Popup Overlay */}
      {showMiniStatement && (
        <MiniStatementModal
          pin={userPin}
          onClose={() => setShowMiniStatement(false)}
        />
      )}
    </>
  );
}
