import React from 'react';
import { useAuth } from '../context/AuthContext';
import { Camera, LogOut, Shield, User, Image, ExternalLink } from 'lucide-react';
import { useNavigate, Link } from 'react-router-dom';

const Navbar = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand-logo">
          <div className="brand-icon">
            <Camera size={22} />
          </div>
          <span>TrizenPhoto</span>
        </Link>

        {user ? (
          <div className="nav-user">
            <span className={`role-badge ${user.role === 'ROLE_ADMIN' ? 'admin' : 'team'}`}>
              {user.role === 'ROLE_ADMIN' ? 'Admin / Lead' : 'Team Member'}
            </span>
            <div style={{ display: 'flex', flexDirection: 'column', textAlign: 'right' }}>
              <span style={{ fontWeight: 600, fontSize: '0.9rem' }}>{user.fullName}</span>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user.email}</span>
            </div>
            <button className="btn btn-secondary" onClick={handleLogout} title="Log Out">
              <LogOut size={16} />
              <span>Logout</span>
            </button>
          </div>
        ) : (
          <div style={{ display: 'flex', gap: '0.75rem' }}>
            <Link to="/gallery/abc123" className="btn btn-secondary" style={{ fontSize: '0.85rem' }}>
              <ExternalLink size={15} /> Demo Customer Gallery
            </Link>
            <Link to="/login" className="btn btn-primary" style={{ fontSize: '0.85rem' }}>
              <User size={15} /> Sign In
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
