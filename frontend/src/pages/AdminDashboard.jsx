import React, { useState, useEffect } from 'react';
import API from '../services/api';
import { 
  Plus, Users, Image as ImageIcon, Share2, Key, CheckCircle, XCircle, 
  Calendar, Check, Copy, Globe, RefreshCw, Eye, Sparkles, Filter 
} from 'lucide-react';

const AdminDashboard = () => {
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [allUsers, setAllUsers] = useState([]);
  const [photos, setPhotos] = useState([]);
  const [filterMode, setFilterMode] = useState('ALL'); // 'ALL' or 'SELECTED'
  const [loading, setLoading] = useState(true);

  // Modals
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showPublishModal, setShowPublishModal] = useState(false);
  const [showTeamModal, setShowTeamModal] = useState(false);

  // Form states
  const [newTitle, setNewTitle] = useState('');
  const [newDesc, setNewDesc] = useState('');
  const [newDate, setNewDate] = useState(new Date().toISOString().split('T')[0]);
  const [selectedTeamIds, setSelectedTeamIds] = useState([]);

  // Gallery publish state
  const [pin, setPin] = useState('482917');
  const [publishedGallery, setPublishedGallery] = useState(null);
  const [copiedLink, setCopiedLink] = useState(false);
  const [copiedPin, setCopiedPin] = useState(false);

  useEffect(() => {
    fetchEvents();
    fetchUsers();
  }, []);

  const fetchEvents = async () => {
    try {
      setLoading(true);
      const res = await API.get('/events');
      if (res.data.success) {
        setEvents(res.data.data);
        if (res.data.data.length > 0 && !selectedEvent) {
          selectEvent(res.data.data[0]);
        } else if (selectedEvent) {
          const updated = res.data.data.find(e => e.id === selectedEvent.id);
          if (updated) setSelectedEvent(updated);
        }
      }
    } catch (err) {
      console.error("Failed to fetch events", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchUsers = async () => {
    try {
      const res = await API.get('/admin/users');
      if (res.data.success) {
        setAllUsers(res.data.data);
      }
    } catch (err) {
      console.error("Failed to fetch users", err);
    }
  };

  const selectEvent = async (event) => {
    setSelectedEvent(event);
    if (event.gallery) {
      setPublishedGallery(event.gallery);
      setPin(event.gallery.pin || '482917');
    } else {
      setPublishedGallery(null);
    }
    fetchPhotos(event.id);
  };

  const fetchPhotos = async (eventId) => {
    try {
      const res = await API.get(`/admin/events/${eventId}/photos`);
      if (res.data.success) {
        setPhotos(res.data.data);
      }
    } catch (err) {
      console.error("Failed to fetch photos", err);
    }
  };

  const handleCreateEvent = async (e) => {
    e.preventDefault();
    try {
      const res = await API.post('/events', {
        title: newTitle,
        description: newDesc,
        eventDate: newDate,
        assignedUserIds: selectedTeamIds
      });
      if (res.data.success) {
        setShowCreateModal(false);
        setNewTitle('');
        setNewDesc('');
        setSelectedTeamIds([]);
        fetchEvents();
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create event');
    }
  };

  const handleToggleSelectPhoto = async (photoId, currentSelected) => {
    try {
      const res = await API.patch(`/photos/${photoId}/select`, { isSelected: !currentSelected });
      if (res.data.success) {
        setPhotos(photos.map(p => p.id === photoId ? { ...p, isSelected: !currentSelected } : p));
        fetchEvents(); // Update counters in operational state
      }
    } catch (err) {
      alert("Failed to update photo selection");
    }
  };

  const handlePublishGallery = async (e) => {
    e.preventDefault();
    if (!selectedEvent) return;
    try {
      const selectedPhotoIds = photos.filter(p => p.isSelected).map(p => p.id);
      const res = await API.post(`/events/${selectedEvent.id}/gallery/publish`, {
        pin: pin,
        title: selectedEvent.title + " Gallery",
        selectedPhotoIds: selectedPhotoIds
      });
      if (res.data.success) {
        setPublishedGallery(res.data.data);
        fetchEvents();
        setShowPublishModal(false);
      }
    } catch (err) {
      alert(err.response?.data?.message || "Failed to publish gallery");
    }
  };

  const copyGalleryLink = () => {
    if (!publishedGallery) return;
    const url = `${window.location.origin}/gallery/${publishedGallery.galleryToken}`;
    navigator.clipboard.writeText(url);
    setCopiedLink(true);
    setTimeout(() => setCopiedLink(false), 2500);
  };

  const copyGalleryPin = () => {
    if (!publishedGallery) return;
    navigator.clipboard.writeText(publishedGallery.pin);
    setCopiedPin(true);
    setTimeout(() => setCopiedPin(false), 2500);
  };

  const filteredPhotos = filterMode === 'SELECTED' ? photos.filter(p => p.isSelected) : photos;

  return (
    <div className="main-content">
      {/* Header Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '2rem' }}>Admin Control Center</h1>
          <p style={{ color: 'var(--text-muted)' }}>Manage photography events, assign team members, curate photos & publish PIN galleries</p>
        </div>
        <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
          <Plus size={18} /> Create New Event
        </button>
      </div>

      {/* Events Selector Strip */}
      <div style={{ display: 'flex', gap: '1rem', overflowX: 'auto', paddingBottom: '1rem', marginBottom: '2rem' }}>
        {events.map((evt) => (
          <div
            key={evt.id}
            onClick={() => selectEvent(evt)}
            className={`glass-card ${selectedEvent?.id === evt.id ? 'active' : ''}`}
            style={{
              padding: '1rem 1.25rem',
              minWidth: '240px',
              cursor: 'pointer',
              borderColor: selectedEvent?.id === evt.id ? 'var(--primary)' : 'var(--border-color)',
              background: selectedEvent?.id === evt.id ? 'rgba(99, 102, 241, 0.15)' : 'var(--bg-card)'
            }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
              <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Calendar size={14} /> {evt.eventDate}
              </span>
              {evt.gallery?.isPublished && (
                <span className="role-badge customer" style={{ fontSize: '0.65rem' }}>Published</span>
              )}
            </div>
            <h4 style={{ fontSize: '1.1rem', marginBottom: '0.25rem' }}>{evt.title}</h4>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-dim)' }}>
              {evt.assignedTeam?.length || 0} Team Members
            </span>
          </div>
        ))}
      </div>

      {selectedEvent && (
        <>
          {/* OPERATIONAL STATE BANNER (Matching PDF Section 5!) */}
          <div className="op-state-card">
            <div className="op-stat-item">
              <div className="op-icon blue">
                <ImageIcon size={26} />
              </div>
              <div>
                <div className="op-val">{selectedEvent.title}</div>
                <div className="op-lbl">Active Event Operational State</div>
              </div>
            </div>

            <div className="op-stat-item">
              <div className="op-icon pink">
                <RefreshCw size={26} />
              </div>
              <div>
                <div className="op-val">{photos.length}</div>
                <div className="op-lbl">Total Uploaded Photos</div>
              </div>
            </div>

            <div className="op-stat-item">
              <div className="op-icon green">
                <CheckCircle size={26} />
              </div>
              <div>
                <div className="op-val">{photos.filter(p => p.isSelected).length}</div>
                <div className="op-lbl">Selected for Publishing</div>
              </div>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: '0.75rem' }}>
              <button className="btn btn-accent" onClick={() => setShowPublishModal(true)}>
                <Share2 size={18} /> Publish / Manage Gallery
              </button>
            </div>
          </div>

          {/* Published Credentials Banner (if published) */}
          {publishedGallery && publishedGallery.isPublished && (
            <div className="glass-panel" style={{ padding: '1.25rem 1.75rem', marginBottom: '2rem', background: 'linear-gradient(135deg, rgba(16, 185, 129, 0.1) 0%, rgba(15, 23, 42, 0.8) 100%)', borderColor: 'rgba(16, 185, 129, 0.3)' }}>
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                  <div style={{ width: '42px', height: '42px', borderRadius: '50%', background: 'rgba(16, 185, 129, 0.2)', color: 'var(--success)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Globe size={22} />
                  </div>
                  <div>
                    <h4 style={{ fontSize: '1rem', color: 'var(--success)' }}>Gallery Live & Protected by PIN</h4>
                    <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                      Shareable URL: <code style={{ color: '#fff', background: 'rgba(0,0,0,0.3)', padding: '0.15rem 0.5rem', borderRadius: '4px' }}>{`${window.location.origin}/gallery/${publishedGallery.galleryToken}`}</code>
                    </p>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: '0.75rem' }}>
                  <button className="btn btn-secondary" onClick={copyGalleryLink} style={{ fontSize: '0.85rem' }}>
                    {copiedLink ? <Check size={16} color="var(--success)" /> : <Copy size={16} />} {copiedLink ? 'URL Copied!' : 'Copy Link'}
                  </button>
                  <button className="btn btn-secondary" onClick={copyGalleryPin} style={{ fontSize: '0.85rem' }}>
                    <Key size={16} color="var(--warning)" /> PIN: <strong>{publishedGallery.pin}</strong> {copiedPin ? ' (Copied!)' : ''}
                  </button>
                  <a href={`/gallery/${publishedGallery.galleryToken}`} target="_blank" rel="noopener noreferrer" className="btn btn-primary" style={{ fontSize: '0.85rem' }}>
                    <Eye size={16} /> Open Gallery
                  </a>
                </div>
              </div>
            </div>
          )}

          {/* Curation Controls & Filters */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', margin: '1.5rem 0 1rem 0' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <h3 style={{ fontSize: '1.3rem' }}>Team Uploaded Photographs ({photos.length})</h3>
              <div style={{ display: 'flex', background: 'rgba(15,23,42,0.6)', padding: '0.25rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-color)' }}>
                <button
                  className={`btn ${filterMode === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
                  style={{ padding: '0.35rem 0.85rem', fontSize: '0.8rem' }}
                  onClick={() => setFilterMode('ALL')}
                >
                  All ({photos.length})
                </button>
                <button
                  className={`btn ${filterMode === 'SELECTED' ? 'btn-accent' : 'btn-secondary'}`}
                  style={{ padding: '0.35rem 0.85rem', fontSize: '0.8rem' }}
                  onClick={() => setFilterMode('SELECTED')}
                >
                  Selected ({photos.filter(p => p.isSelected).length})
                </button>
              </div>
            </div>

            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Click the checkmark badge on any photo to toggle selection for customer gallery publishing.
            </p>
          </div>

          {/* Photo Gallery Grid */}
          {filteredPhotos.length === 0 ? (
            <div className="glass-panel" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <ImageIcon size={48} style={{ opacity: 0.4, marginBottom: '1rem' }} />
              <p>No photos found in this view. Team members can upload photos using their login.</p>
            </div>
          ) : (
            <div className="photo-grid">
              {filteredPhotos.map((photo) => (
                <div key={photo.id} className="photo-card">
                  <div className="photo-img-wrapper">
                    <img src={photo.storageLocation} alt={photo.filename} className="photo-img" />
                    <div
                      className={`photo-badge-select ${photo.isSelected ? 'selected' : 'unselected'}`}
                      onClick={() => handleToggleSelectPhoto(photo.id, photo.isSelected)}
                      title={photo.isSelected ? "Click to deselect from gallery" : "Click to select for gallery"}
                    >
                      <Check size={18} />
                    </div>
                  </div>
                  <div className="photo-info">
                    <div>
                      <div className="photo-name">{photo.filename}</div>
                      <div className="photo-meta">By {photo.uploadedByName || 'Team Member'}</div>
                    </div>
                    <span style={{ fontSize: '0.75rem', padding: '0.15rem 0.5rem', borderRadius: '4px', background: photo.isSelected ? 'rgba(236,72,153,0.2)' : 'rgba(255,255,255,0.05)', color: photo.isSelected ? 'var(--accent)' : 'var(--text-dim)' }}>
                      {photo.isSelected ? 'Selected' : 'Draft'}
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}

      {/* CREATE EVENT MODAL */}
      {showCreateModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '1.4rem', marginBottom: '1rem' }}>Create New Event</h3>
            <form onSubmit={handleCreateEvent}>
              <div className="form-group">
                <label>Event Name</label>
                <input type="text" className="form-control" placeholder="e.g. Arjun & Priya Wedding" value={newTitle} onChange={e => setNewTitle(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea className="form-control" rows="3" placeholder="Event details, venue..." value={newDesc} onChange={e => setNewDesc(e.target.value)} />
              </div>
              <div className="form-group">
                <label>Event Date</label>
                <input type="date" className="form-control" value={newDate} onChange={e => setNewDate(e.target.value)} required />
              </div>
              <div className="form-group">
                <label>Assign Team Members</label>
                <div style={{ maxHeight: '140px', overflowY: 'auto', border: '1px solid var(--border-color)', borderRadius: 'var(--radius-sm)', padding: '0.5rem' }}>
                  {allUsers.filter(u => u.role === 'ROLE_TEAM_MEMBER').map(u => (
                    <label key={u.id} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', padding: '0.35rem 0.5rem', cursor: 'pointer' }}>
                      <input
                        type="checkbox"
                        checked={selectedTeamIds.includes(u.id)}
                        onChange={(e) => {
                          if (e.target.checked) setSelectedTeamIds([...selectedTeamIds, u.id]);
                          else setSelectedTeamIds(selectedTeamIds.filter(id => id !== u.id));
                        }}
                      />
                      <span>{u.fullName} ({u.email})</span>
                    </label>
                  ))}
                </div>
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Create Event</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* PUBLISH GALLERY MODAL */}
      {showPublishModal && selectedEvent && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 style={{ fontSize: '1.4rem', marginBottom: '0.5rem' }}>Publish Customer Gallery</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '1.25rem' }}>
              Set access PIN and publish selected photos for <strong>{selectedEvent.title}</strong>
            </p>

            <form onSubmit={handlePublishGallery}>
              <div className="form-group">
                <label>Gallery Access PIN (4-8 digits)</label>
                <input type="text" className="form-control" value={pin} onChange={e => setPin(e.target.value)} maxLength="8" pattern="[0-9]{4,8}" required style={{ fontFamily: 'monospace', fontSize: '1.2rem', letterSpacing: '0.2em', textAlign: 'center' }} />
              </div>

              <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '1rem', borderRadius: 'var(--radius-sm)', border: '1px solid rgba(99, 102, 241, 0.25)', marginBottom: '1.5rem' }}>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-main)', marginBottom: '0.25rem' }}>
                  <strong>Selected Photos Counter:</strong> {photos.filter(p => p.isSelected).length} of {photos.length} photos ready for publication.
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Only selected photos will be visible to customers entering the PIN.
                </div>
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowPublishModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-accent">
                  <Share2 size={16} /> Publish & Generate Link
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminDashboard;
