import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Navbar from './components/Navbar';
import LoginPage from './pages/LoginPage';
import AdminDashboard from './pages/AdminDashboard';
import TeamDashboard from './pages/TeamDashboard';
import CustomerGalleryPage from './pages/CustomerGalleryPage';

const ProtectedRoute = ({ children, allowedRole }) => {
  const { user } = useAuth();
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  if (allowedRole && user.role !== allowedRole) {
    return <Navigate to={user.role === 'ROLE_ADMIN' ? '/admin' : '/team'} replace />;
  }
  return children;
};

const DefaultRedirect = () => {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return <Navigate to={user.role === 'ROLE_ADMIN' ? '/admin' : '/team'} replace />;
};

function App() {
  return (
    <AuthProvider>
      <Router>
        <div className="app-container">
          <Navbar />
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/gallery/:galleryToken" element={<CustomerGalleryPage />} />
            
            <Route
              path="/admin"
              element={
                <ProtectedRoute allowedRole="ROLE_ADMIN">
                  <AdminDashboard />
                </ProtectedRoute>
              }
            />

            <Route
              path="/team"
              element={
                <ProtectedRoute allowedRole="ROLE_TEAM_MEMBER">
                  <TeamDashboard />
                </ProtectedRoute>
              }
            />

            <Route path="*" element={<DefaultRedirect />} />
          </Routes>
        </div>
      </Router>
    </AuthProvider>
  );
}

export default App;
