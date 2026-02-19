import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Filler,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import api from '../api/axios';
import styles from './ProductDetail.module.css';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Tooltip, Filler);

const RANGE_OPTIONS = [
  { label: '7 days', days: 7 },
  { label: '30 days', days: 30 },
  { label: '90 days', days: 90 },
  { label: 'All time', days: null },
];

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [history, setHistory] = useState([]);
  const [stats, setStats] = useState(null);
  const [range, setRange] = useState(30);
  const [checking, setChecking] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchAll();
  }, [id]);

  useEffect(() => {
    fetchHistory();
  }, [range]);

  async function fetchAll() {
    try {
      const [prodRes, statsRes] = await Promise.all([
        api.get(`/api/products/${id}`),
        api.get(`/api/products/${id}/stats`),
      ]);
      setProduct(prodRes.data);
      setStats(statsRes.data);
      await fetchHistory();
    } catch {
      navigate('/');
    } finally {
      setLoading(false);
    }
  }

  async function fetchHistory() {
    const params = range ? `?days=${range}` : '';
    const { data } = await api.get(`/api/products/${id}/history${params}`);
    setHistory(data);
  }

  async function handleCheckNow() {
    setChecking(true);
    try {
      await api.post(`/api/products/${id}/check`);
      await fetchHistory();
      const statsRes = await api.get(`/api/products/${id}/stats`);
      setStats(statsRes.data);
    } finally {
      setChecking(false);
    }
  }

  if (loading) return <div className={styles.loading}>Loading...</div>;
  if (!product) return null;

  const labels = history.map((h) =>
    new Date(h.scrapedAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric' })
  );
  const prices = history.map((h) => parseFloat(h.price));
  const currentPrice = prices[prices.length - 1];

  const chartData = {
    labels,
    datasets: [
      {
        data: prices,
        borderColor: '#ff6b2b',
        backgroundColor: 'rgba(255, 107, 43, 0.08)',
        fill: true,
        tension: 0.4,
        pointRadius: prices.length > 30 ? 0 : 4,
        pointBackgroundColor: '#ff6b2b',
        borderWidth: 2,
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      x: {
        grid: { color: '#1e1e1e' },
        ticks: { color: '#888', font: { size: 11 } },
      },
      y: {
        grid: { color: '#1e1e1e' },
        ticks: {
          color: '#888',
          font: { size: 11 },
          callback: (v) => `$${v.toFixed(2)}`,
        },
      },
    },
  };

  return (
    <div className={styles.page}>
      <button className={styles.back} onClick={() => navigate('/')}>
        ← Back to Dashboard
      </button>

      <div className={styles.topRow}>
        <div className={styles.productInfo}>
          <span
            className="tag"
            style={{ background: 'var(--orange-dim)', color: 'var(--orange)', marginBottom: 10 }}
          >
            {product.retailer || 'Other'}
          </span>
          <h1 className={styles.name}>{product.name || 'Tracked Product'}</h1>
          <a href={product.url} target="_blank" rel="noreferrer" className={styles.url}>
            View on site ↗
          </a>
        </div>

        <button
          className="btn-ghost"
          onClick={handleCheckNow}
          disabled={checking}
          style={{ alignSelf: 'flex-start' }}
        >
          {checking ? 'Checking...' : '⟳ Check Now'}
        </button>
      </div>

      {/* Stats row */}
      <div className={styles.statsRow}>
        <StatCard
          label="Current Price"
          value={currentPrice ? `$${currentPrice.toFixed(2)}` : '—'}
          highlight
        />
        <StatCard
          label="Target Price"
          value={product.targetPrice ? `$${parseFloat(product.targetPrice).toFixed(2)}` : '—'}
        />
        <StatCard
          label="Lowest Ever"
          value={stats?.lowestPrice !== 'N/A' ? `$${parseFloat(stats.lowestPrice).toFixed(2)}` : '—'}
          good
        />
        <StatCard
          label="Highest Ever"
          value={stats?.highestPrice !== 'N/A' ? `$${parseFloat(stats.highestPrice).toFixed(2)}` : '—'}
        />
        <StatCard
          label="Average Price"
          value={stats?.averagePrice !== 'N/A' ? `$${parseFloat(stats.averagePrice).toFixed(2)}` : '—'}
        />
      </div>

      {/* Chart */}
      <div className={styles.chartCard}>
        <div className={styles.chartHeader}>
          <h2>Price History</h2>
          <div className={styles.rangeToggle}>
            {RANGE_OPTIONS.map((opt) => (
              <button
                key={opt.label}
                className={range === opt.days ? styles.activeRange : styles.rangeBtn}
                onClick={() => setRange(opt.days)}
              >
                {opt.label}
              </button>
            ))}
          </div>
        </div>

        {history.length === 0 ? (
          <div className={styles.noData}>
            No price data yet. Click "Check Now" to scrape the current price.
          </div>
        ) : (
          <div className={styles.chartWrap}>
            <Line data={chartData} options={chartOptions} />
          </div>
        )}
      </div>
    </div>
  );
}

function StatCard({ label, value, highlight, good }) {
  return (
    <div className={styles.statCard}>
      <p className={styles.statLabel}>{label}</p>
      <p
        className={styles.statValue}
        style={{
          color: highlight ? 'var(--text)' : good ? 'var(--green)' : 'var(--text)',
          fontSize: highlight ? '24px' : '20px',
        }}
      >
        {value}
      </p>
    </div>
  );
}
