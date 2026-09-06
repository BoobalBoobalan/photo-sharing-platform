import React, { useState, useEffect } from 'react';
import API from '../services/api';
import { UploadCloud, Image as ImageIcon, Calendar, CheckCircle2, Lock, FileText, AlertCircle } from 'lucide-react';

const TeamDashboard = () => {
  const [events, setEvents] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [myPhotos, setMyPhotos] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadSuccessMsg, setUploadSuccessMsg] = useState('');
  const [dragActive, setDragActive] = useState(false);

  useEffect(() => {
    fetchAssignedEvents();
  }, []);

  const fetchAssignedEvents = async () => {
    try {
      const res = await API.get('/events');
      if (res.data.success) {
        setEvents(res.data.data);
        if (res.data.data.length > 0) {
          selectEvent(res.data.data[0]);
        }
      }
    } catch (err) {
      console.error("Failed to fetch assigned events", err);
    }
  };

  const selectEvent = async (event) => {
    setSelectedEvent(event);
    fetchMyUploadedPhotos(event.id);
  };

  const fetchMyUploadedPhotos = async (eventId) => {
    try {
      const res = await API.get(`/events/${eventId}/photos`);
      if (res.data.success) {
        setMyPhotos(res.data.data);
      }
    } catch (err) {
      console.error("Failed to fetch photos", err);
    }
  };

  const handleFileUpload = async (files) => {
    if (!selectedEvent || !files || files.length === 0) return;

    const formData = new FormData();
    for (let i = 0; i < files.length; i++) {
      formData.append('files', files[i]);
    }

    try {
      setUploading(true);
      setUploadSuccessMsg('');
      const res = await API.post(`/events/${selectedEvent.id}/photos/upload`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });

      if (res.data.success) {
        setUploadSuccessMsg(`Successfully uploaded ${res.data.data.length} photo(s)!`);
        fetchMyUploadedPhotos(selectedEvent.id);
        setTimeout(() => setUploadSuccessMsg(''), 4000);
      }
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to upload photos');
    } finally {
      setUploading(false);
    }
  };

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);
    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      handleFileUpload(e.dataTransfer.files);
    }
  };

  return (
    <div className="main-content">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2rem' }}>Team Member Portal</h1>
        <p style={{ color: 'var(--text-muted)' }}>Upload photos for your assigned events. Consolidated curation & gallery publishing are managed by the Lead.</p>
      </div>

      {/* Events Selector Strip */}
      <div style={{ display: 'flex', gap: '1rem', overflowX: 'auto', paddingBottom: '1rem', marginBottom: '2rem' }}>
        {events.length === 0 ? (
          <div className="glass-panel" style={{ padding: '1.5rem', width: '100%', color: 'var(--text-muted)' }}>
            No assigned events yet. Ask an Admin to add you to an event team.
          </div>
        ) : (
          events.map((evt) => (
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
              <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
                <Calendar size={14} /> {evt.eventDate}
              </div>
              <h4 style={{ fontSize: '1.1rem', marginBottom: '0.25rem' }}>{evt.title}</h4>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-dim)' }}>
                Assigned Event
              </span>
            </div>
          ))
        )}
      </div>

      {selectedEvent && (
        <>
          {/* MULTI-FILE UPLOAD ZONE */}
          <div
            className="glass-panel"
            style={{
              padding: '2.5rem',
              textAlign: 'center',
              border: dragActive ? '2px dashed var(--primary)' : '2px dashed var(--border-color)',
              background: dragActive ? 'rgba(99, 102, 241, 0.1)' : 'var(--bg-card)',
              marginBottom: '2rem',
              borderRadius: 'var(--radius-lg)'
            }}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
          >
            <div style={{ width: '64px', height: '64px', margin: '0 auto 1rem auto', borderRadius: '50%', background: 'rgba(99, 102, 241, 0.15)', color: 'var(--primary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <UploadCloud size={34} />
            </div>

            <h3 style={{ fontSize: '1.3rem', marginBottom: '0.5rem' }}>
              Upload Event Photographs for {selectedEvent.title}
            </h3>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '1.25rem' }}>
              Drag & drop image files here, or click to select multiple photos
            </p>

            <input
              type="file"
              id="photo-upload-input"
              multiple
              accept="image/*"
              style={{ display: 'none' }}
              onChange={(e) => handleFileUpload(e.target.files)}
            />

            <label htmlFor="photo-upload-input" className="btn btn-primary" style={{ cursor: 'pointer' }}>
              <UploadCloud size={18} /> Select Files to Upload
            </label>

            {uploading && (
              <div style={{ marginTop: '1.25rem', color: 'var(--primary)', fontWeight: 600, fontSize: '0.9rem' }}>
                Uploading images to storage... Please wait.
              </div>
            )}

            {uploadSuccessMsg && (
              <div style={{ marginTop: '1.25rem', color: 'var(--success)', display: 'inline-flex', alignItems: 'center', gap: '0.5rem', background: 'rgba(16,185,129,0.15)', padding: '0.5rem 1rem', borderRadius: 'var(--radius-sm)' }}>
                <CheckCircle2 size={18} /> {uploadSuccessMsg}
              </div>
            )}
          </div>

          {/* MY UPLOADED PHOTOS GRID */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
            <h3 style={{ fontSize: '1.3rem' }}>My Uploads ({myPhotos.length})</h3>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-dim)', display: 'flex', alignItems: 'center', gap: '0.35rem' }}>
              <Lock size={14} /> Only Admin can curate and publish galleries
            </span>
          </div>

          {myPhotos.length === 0 ? (
            <div className="glass-panel" style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-muted)' }}>
              <ImageIcon size={48} style={{ opacity: 0.4, marginBottom: '1rem' }} />
              <p>You haven't uploaded any photos to this event yet. Use the uploader above to submit photographs.</p>
            </div>
          ) : (
            <div className="photo-grid">
              {myPhotos.map((photo) => (
                <div key={photo.id} className="photo-card">
                  <div className="photo-img-wrapper">
                    <img src={photo.storageLocation} alt={photo.filename} className="photo-img" />
                  </div>
                  <div className="photo-info">
                    <div>
                      <div className="photo-name">{photo.filename}</div>
                      <div className="photo-meta">{(photo.fileSize / 1024).toFixed(1)} KB</div>
                    </div>
                    <span style={{ fontSize: '0.75rem', padding: '0.15rem 0.5rem', borderRadius: '4px', background: 'rgba(99,102,241,0.15)', color: 'var(--primary)' }}>
                      Uploaded
                    </span>
                  </div>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
};

export default TeamDashboard;
