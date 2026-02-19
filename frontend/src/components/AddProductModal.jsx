import { useState } from 'react';
import api from '../api/axios';
import styles from './AddProductModal.module.css';

export default function AddProductModal({ onClose, onAdded }) {
  const [url, setUrl] = useState('');
  const [targetPrice, setTargetPrice] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const { data } = await api.post('/api/products', {
        url,
        targetPrice: parseFloat(targetPrice),
      });
      onAdded(data);
      onClose();
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to add product. Check the URL.');
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className={styles.overlay} onClick={onClose}>
      <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
        <div className={styles.header}>
          <h2>Track a Product</h2>
          <button className={styles.close} onClick={onClose}>✕</button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className={styles.field}>
            <label>Product URL</label>
            <input
              type="url"
              placeholder="https://www.amazon.com/dp/..."
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              required
              autoFocus
            />
          </div>

          <div className={styles.field}>
            <label>Target Price ($)</label>
            <input
              type="number"
              placeholder="e.g. 49.99"
              value={targetPrice}
              onChange={(e) => setTargetPrice(e.target.value)}
              step="0.01"
              min="0.01"
              required
            />
            <p className={styles.hint}>We'll email you when the price drops to this or below.</p>
          </div>

          {error && <p className="error-msg">{error}</p>}

          <div className={styles.actions}>
            <button type="button" className="btn-ghost" onClick={onClose}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Adding...' : 'Start Tracking'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
