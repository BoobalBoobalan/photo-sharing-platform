import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import API from '../services/api';
import { 
  KeyRound, Lock, Image as ImageIcon, Calendar, Download, Eye, 
  X, CheckCircle, ShieldAlert, ArrowRight, Camera, Sparkles 
} from 'lucide-react';

const CustomerGalleryPage = () => {
  const { galleryToken } = useParams();

  const [galleryMeta, setGalleryMeta] = useState(null);
  const [photos, setPhotos] = useState([]);
  const [pin, setPin] = useState('');
  const [isPinVerified, setIsPinVerified] = useState(false);
  const [pinError, setPinError] = useState('');
  const [loading, setLoading] = useState(true);
  const [verifying, setVerifying] = useState(false);

  // Lightbox modal state
  const [activeLightboxIndex, setActiveLightboxIndex] = useState(null);

  useEffect(() => {
    fetchGalleryInfo();
  }, [galleryToken]);

  const fetchGalleryInfo = async () => {
    try {
      setLoading(true);
      const res = await API.get(`/public/gallery/${galleryToken}`);
      if (res.data.success) {
        setGalleryMeta(res.data.data);
      }
    } catch (err) {
      setPinError("Gallery not found or link has expired.");
    } finally {
      setLoading(false);
    }
  };

  const handleVerifyPin = async (e) => {
    e.preventDefault();
    setPinError('');
    setVerifying(true);
    try {
      const res = await API.post(`/public/gallery/${galleryToken}/verify-pin`, { pin });
      if (res.data.success) {
        setGalleryMeta(res.data.data);
        setPhotos(res.data.data.photos || []);
        setIsPinVerified(true);
      }
    } catch (err) {
      setPinError(err.response?.data?.message || "Incorrect PIN. Please try again.");
    } finally {
      setVerifying(false);
    }
  };

  const downloadImage = (storageLocation, filename) => {
    const link = document.createElement('a');
    link.href = storageLocation;
    link.download = filename || 'photo.jpg';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '80vh' }}>
        <div style={{ textAlign: 'center', color: 'var(--text-muted)' }}>
          <Sparkles size={36} className="spin" style={{ marginBottom: '1rem', color: 'var(--primary)' }} />
          <p>Loading Private Gallery...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="main-content">
      {/* PIN ENTRY MODAL (If PIN is not yet verified) */}
      {!isPinVerified ? (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 'calc(100vh - 160px)', padding: '1rem' }}>
          <div className="glass-panel" style={{ width: '100%', maxWidth: '440px', padding: '2.5rem', textAlign: 'center' }}>
            <div style={{ width: '64px', height: '64px', margin: '0 auto 1.25rem auto', borderRadius: '50%', background: 'rgba(236, 72, 153, 0.15)', color: 'var(--accent)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1px solid rgba(236, 72, 153, 0.3)' }}>
              <Lock size={32} />
            </div>

            <span className="role-badge customer" style={{ marginBottom: '0.75rem', display: 'inline-block' }}>
              PIN-Protected Gallery
            </span>

            <h2 style={{ fontSize: '1.6rem', marginBottom: '0.5rem' }}>
              {galleryMeta?.eventTitle || 'Protected Gallery'}
            </h2>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', marginBottom: '1.75rem' }}>
              {galleryMeta?.eventDescription || 'Enter the 6-digit access PIN provided by your event host to view the gallery.'}
            </p>

            {pinError && (
              <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: 'var(--danger)', padding: '0.75rem 1rem', borderRadius: 'var(--radius-sm)', marginBottom: '1.25rem', fontSize: '0.85rem', display: 'flex', alignItems: 'center', gap: '0.5rem', justifyContent: 'center' }}>
                <ShieldAlert size={16} />
                <span>{pinError}</span>
              </div>
            )}

            <form onSubmit={handleVerifyPin}>
              <div className="form-group">
                <input
                  type="password"
                  className="form-control"
                  placeholder="Enter Access PIN"
                  value={pin}
                  onChange={(e) => setPin(e.target.value)}
                  maxLength="8"
                  autoFocus
                  required
                  style={{
                    fontFamily: 'monospace',
                    fontSize: '1.5rem',
                    letterSpacing: '0.3em',
                    textAlign: 'center',
                    padding: '0.85rem'
                  }}
                />
              </div>

              <button type="submit" className="btn btn-accent" style={{ width: '100%', padding: '0.85rem', marginTop: '0.5rem' }} disabled={verifying}>
                {verifying ? 'Verifying PIN...' : 'Unlock Gallery'} <ArrowRight size={18} />
              </button>
            </form>

            <div style={{ marginTop: '1.5rem', fontSize: '0.75rem', color: 'var(--text-dim)' }}>
              Demo Test PIN: <code style={{ color: '#fff', background: 'rgba(255,255,255,0.1)', padding: '0.1rem 0.4rem', borderRadius: '4px' }}>482917</code>
            </div>
          </div>
        </div>
      ) : (
        /* UNLOCKED PUBLISHED GALLERY VIEW */
        <>
          {/* Gallery Banner Header */}
          <div className="glass-panel" style={{ padding: '2.5rem', marginBottom: '2.5rem', background: 'linear-gradient(135deg, rgba(30, 41, 59, 0.9) 0%, rgba(15, 23, 42, 0.95) 100%)', border: '1px solid var(--border-color)' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1.5rem' }}>
              <div>
                <span className="role-badge customer" style={{ marginBottom: '0.75rem', display: 'inline-block' }}>
                  <CheckCircle size={12} style={{ marginRight: '4px' }} /> Verified Gallery Access
                </span>
                <h1 style={{ fontSize: '2.4rem', marginBottom: '0.5rem' }}>{galleryMeta.eventTitle}</h1>
                <p style={{ color: 'var(--text-muted)', fontSize: '1rem', maxWidth: '700px' }}>
                  {galleryMeta.eventDescription}
                </p>
              </div>

              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem', background: 'rgba(15, 23, 42, 0.6)', padding: '0.85rem 1.25rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
                <div>
                  <div style={{ fontSize: '1.5rem', fontWeight: '800', color: 'var(--accent)', fontFamily: 'var(--font-heading)' }}>
                    {photos.length}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Published Photos</div>
                </div>
                <div style={{ width: '1px', height: '36px', background: 'var(--border-color)' }} />
                <div>
                  <div style={{ fontSize: '0.9rem', fontWeight: '600' }}>
                    {galleryMeta.eventDate}
                  </div>
                  <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Event Date</div>
                </div>
              </div>
            </div>
          </div>

          {/* Photo Grid */}
          <div className="photo-grid">
            {photos.map((photo, idx) => (
              <div key={photo.id} className="photo-card" style={{ cursor: 'pointer' }}>
                <div className="photo-img-wrapper" onClick={() => setActiveLightboxIndex(idx)}>
                  <img src={photo.storageLocation} alt={photo.filename} className="photo-img" />
                  <div style={{ position: 'absolute', inset: 0, background: 'rgba(0,0,0,0.2)', opacity: 0, transition: '0.2s opacity', display: 'flex', alignItems: 'center', justifyContent: 'center' }} className="hover-overlay">
                    <Eye size={32} color="#fff" />
                  </div>
                </div>
                <div className="photo-info">
                  <div>
                    <div className="photo-name">{photo.filename}</div>
                    <div className="photo-meta">{(photo.fileSize / 1024).toFixed(1)} KB</div>
                  </div>
                  <button
                    className="btn btn-secondary"
                    style={{ padding: '0.35rem 0.6rem', fontSize: '0.75rem' }}
                    onClick={(e) => {
                      e.stopPropagation();
                      downloadImage(photo.storageLocation, photo.filename);
                    }}
                    title="Download High Resolution Photo"
                  >
                    <Download size={14} /> Download
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* LIGHTBOX FULLSCREEN MODAL */}
          {activeLightboxIndex !== null && (
            <div className="lightbox-overlay" onClick={() => setActiveLightboxIndex(null)}>
              <button
                style={{ position: 'absolute', top: '24px', right: '24px', background: 'rgba(255,255,255,0.1)', color: '#fff', padding: '0.5rem', borderRadius: '50%' }}
                onClick={() => setActiveLightboxIndex(null)}
              >
                <X size={24} />
              </button>

              <img
                src={photos[activeLightboxIndex].storageLocation}
                alt={photos[activeLightboxIndex].filename}
                className="lightbox-img"
                onClick={(e) => e.stopPropagation()}
              />

              <div style={{ marginTop: '1rem', display: 'flex', gap: '1rem', alignItems: 'center' }} onClick={(e) => e.stopPropagation()}>
                <span style={{ color: 'white', fontWeight: 600 }}>{photos[activeLightboxIndex].filename}</span>
                <button
                  className="btn btn-primary"
                  onClick={() => downloadImage(photos[activeLightboxIndex].storageLocation, photos[activeLightboxIndex].filename)}
                >
                  <Download size={16} /> Download Photo
                </button>
              </div>
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default CustomerGalleryPage;
