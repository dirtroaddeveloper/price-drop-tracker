import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import AddProductModal from '../components/AddProductModal';
import styles from './Dashboard.module.css';

export default function Dashboard() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [checking, setChecking] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchProducts();
  }, []);

  async function fetchProducts() {
    try {
      const { data } = await api.get('/api/products');
      setProducts(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  }

  async function handleCheckNow(e, id) {
    e.stopPropagation();
    setChecking(id);
    try {
      await api.post(`/api/products/${id}/check`);
    } catch (err) {
      console.error(err);
    } finally {
      setChecking(null);
    }
  }

  async function handleDelete(e, id) {
    e.stopPropagation();
    if (!confirm('Stop tracking this product?')) return;
    try {
      await api.delete(`/api/products/${id}`);
      setProducts((prev) => prev.filter((p) => p.id !== id));
    } catch (err) {
      console.error(err);
    }
  }

  function handleAdded(product) {
    setProducts((prev) => [product, ...prev]);
  }

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div>
          <h1 className={styles.title}>Dashboard</h1>
          <p className={styles.sub}>
            {products.length} product{products.length !== 1 ? 's' : ''} tracked
          </p>
        </div>
        <button className="btn-primary" onClick={() => setShowModal(true)}>
          + Track a Product
        </button>
      </div>

      {loading ? (
        <div className={styles.empty}>
          <p>Loading...</p>
        </div>
      ) : products.length === 0 ? (
        <div className={styles.empty}>
          <div className={styles.emptyIcon}>📦</div>
          <h2>Nothing tracked yet</h2>
          <p>Paste a product URL and set your target price — we'll watch it for you.</p>
          <button className="btn-primary" onClick={() => setShowModal(true)}>
            Track your first product
          </button>
        </div>
      ) : (
        <div className={styles.grid}>
          {products.map((p) => (
            <div
              key={p.id}
              className={styles.card}
              onClick={() => navigate(`/product/${p.id}`)}
            >
              <div className={styles.cardTop}>
                <span
                  className="tag"
                  style={{
                    background: 'var(--orange-dim)',
                    color: 'var(--orange)',
                  }}
                >
                  {p.retailer || 'Other'}
                </span>
                <span className={styles.dot}>↗</span>
              </div>

              <p className={styles.productName}>
                {p.name || truncateUrl(p.url)}
              </p>

              <div className={styles.priceRow}>
                <div>
                  <p className={styles.priceLabel}>Target Price</p>
                  <p className={styles.price}>
                    {p.targetPrice ? `$${parseFloat(p.targetPrice).toFixed(2)}` : '—'}
                  </p>
                </div>
              </div>

              <div className={styles.actions}>
                <button
                  className="btn-ghost"
                  style={{ fontSize: '12px', padding: '6px 12px' }}
                  onClick={(e) => handleCheckNow(e, p.id)}
                  disabled={checking === p.id}
                >
                  {checking === p.id ? 'Checking...' : '⟳ Check Now'}
                </button>
                <button className="btn-danger" onClick={(e) => handleDelete(e, p.id)}>
                  Remove
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {showModal && (
        <AddProductModal onClose={() => setShowModal(false)} onAdded={handleAdded} />
      )}
    </div>
  );
}

function truncateUrl(url) {
  try {
    const u = new URL(url);
    return u.hostname + u.pathname.slice(0, 30) + (u.pathname.length > 30 ? '...' : '');
  } catch {
    return url.slice(0, 50);
  }
}
