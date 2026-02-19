import { useNavigate } from 'react-router-dom';
import styles from './Landing.module.css';

const FEATURES = [
  {
    icon: '🔍',
    title: 'Smart Scraping Engine',
    desc: 'Checks Amazon, Best Buy, Walmart and more every 30 minutes using a retailer-specific strategy pattern.',
  },
  {
    icon: '📉',
    title: 'Instant Price Alerts',
    desc: 'The moment a price drops to your target we fire an email alert. No polling, no delays.',
  },
  {
    icon: '📊',
    title: 'Price History Charts',
    desc: 'See every price point over 7, 30, or 90 days. Lowest ever, highest ever, average — all at a glance.',
  },
  {
    icon: '🔐',
    title: 'JWT Auth & Stateless API',
    desc: 'Access + refresh tokens, Spring Security filter chain, BCrypt password hashing. Production-grade auth.',
  },
  {
    icon: '⚡',
    title: 'Background Scheduling',
    desc: 'Spring @Scheduled jobs run in the background without blocking the API. Batched and staggered requests.',
  },
  {
    icon: '🗄️',
    title: 'Time-Series Data',
    desc: 'Price history is append-only in PostgreSQL — the correct way to model time-series data.',
  },
];

const STEPS = [
  { number: '01', title: 'Paste a URL', desc: 'Any Amazon, Best Buy, or Walmart product link.' },
  { number: '02', title: 'Set Your Price', desc: 'Tell us what you\'re willing to pay.' },
  { number: '03', title: 'Get Alerted', desc: 'We email you the moment it drops.' },
];

export default function Landing() {
  const navigate = useNavigate();

  return (
    <div className={styles.page}>
      {/* Nav */}
      <nav className={styles.nav}>
        <div className={styles.navBrand}>
          <span className={styles.navDot}>▶</span> Price Drop Tracker
        </div>
        <div className={styles.navLinks}>
          <button className="btn-ghost" onClick={() => navigate('/login')}>Sign In</button>
          <button className="btn-primary" onClick={() => navigate('/register')}>Get Started</button>
        </div>
      </nav>

      {/* Hero */}
      <section className={styles.hero}>
        <div className={styles.heroBadge}>Open Source · Self-Hostable · Free</div>
        <h1 className={styles.heroTitle}>
          Never overpay<br />
          <span className={styles.heroAccent}>for anything online.</span>
        </h1>
        <p className={styles.heroSub}>
          Paste any product URL, set your target price, and get an email the moment the price drops.
          Built on Spring Boot, PostgreSQL, and React.
        </p>
        <div className={styles.heroCtas}>
          <button className={`btn-primary ${styles.heroBtn}`} onClick={() => navigate('/register')}>
            Start Tracking for Free
          </button>
          <a
            href="https://github.com/dirtroaddeveloper/price-drop-tracker"
            target="_blank"
            rel="noreferrer"
            className={styles.heroGithub}
          >
            <svg height="18" viewBox="0 0 16 16" fill="currentColor">
              <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z"/>
            </svg>
            View on GitHub
          </a>
        </div>

        {/* Fake UI preview */}
        <div className={styles.heroPreview}>
          <div className={styles.previewBar}>
            <span className={styles.previewDot} style={{ background: '#ff5f57' }} />
            <span className={styles.previewDot} style={{ background: '#febc2e' }} />
            <span className={styles.previewDot} style={{ background: '#28c840' }} />
            <span className={styles.previewUrl}>price-drop-tracker.vercel.app</span>
          </div>
          <div className={styles.previewBody}>
            <div className={styles.previewCard}>
              <div className={styles.previewCardTop}>
                <span className={styles.previewTag}>Amazon</span>
                <span style={{ color: '#3ecf8e', fontWeight: 800, fontSize: 20 }}>↓ $47.99</span>
              </div>
              <p style={{ fontSize: 13, color: '#888', margin: '8px 0 14px' }}>Sony WH-1000XM5 Wireless Headphones</p>
              <div style={{ display: 'flex', gap: 8, alignItems: 'flex-end' }}>
                {[60, 72, 68, 65, 59, 55, 48].map((h, i) => (
                  <div key={i} style={{
                    flex: 1,
                    height: h,
                    background: i === 6 ? '#ff6b2b' : '#1e1e1e',
                    borderRadius: 4,
                    transition: 'height 0.3s',
                  }} />
                ))}
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 12 }}>
                <span style={{ fontSize: 11, color: '#555' }}>7 days ago</span>
                <span style={{ fontSize: 11, color: '#555' }}>Today</span>
              </div>
            </div>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10, marginTop: 12 }}>
              {[['Target Price', '$49.99'], ['Current', '$47.99'], ['Lowest Ever', '$47.99'], ['Avg Price', '$62.50']].map(([label, val], i) => (
                <div key={i} className={styles.previewStat}>
                  <p style={{ fontSize: 10, color: '#555', marginBottom: 2 }}>{label}</p>
                  <p style={{ fontSize: 15, fontWeight: 800, color: i === 2 ? '#3ecf8e' : '#e8e8e8' }}>{val}</p>
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* How it works */}
      <section className={styles.section}>
        <p className={styles.sectionLabel}>HOW IT WORKS</p>
        <h2 className={styles.sectionTitle}>Three steps to never miss a deal</h2>
        <div className={styles.steps}>
          {STEPS.map((s) => (
            <div key={s.number} className={styles.step}>
              <div className={styles.stepNum}>{s.number}</div>
              <h3 className={styles.stepTitle}>{s.title}</h3>
              <p className={styles.stepDesc}>{s.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Features */}
      <section className={styles.section}>
        <p className={styles.sectionLabel}>BUILT RIGHT</p>
        <h2 className={styles.sectionTitle}>Not a tutorial project</h2>
        <div className={styles.features}>
          {FEATURES.map((f) => (
            <div key={f.title} className={styles.featureCard}>
              <div className={styles.featureIcon}>{f.icon}</div>
              <h3 className={styles.featureTitle}>{f.title}</h3>
              <p className={styles.featureDesc}>{f.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* Tech stack */}
      <section className={styles.section}>
        <p className={styles.sectionLabel}>TECH STACK</p>
        <h2 className={styles.sectionTitle}>What's under the hood</h2>
        <div className={styles.stack}>
          {[
            ['Spring Boot 3.5', 'Backend framework'],
            ['Java 21', 'Virtual threads'],
            ['PostgreSQL', 'Time-series data'],
            ['Spring Security + JWT', 'Stateless auth'],
            ['Jsoup', 'HTML scraping'],
            ['Flyway', 'DB migrations'],
            ['React + Vite', 'Frontend'],
            ['Chart.js', 'Price history'],
            ['Railway', 'Backend hosting'],
            ['Vercel', 'Frontend hosting'],
          ].map(([name, role]) => (
            <div key={name} className={styles.stackItem}>
              <span className={styles.stackName}>{name}</span>
              <span className={styles.stackRole}>{role}</span>
            </div>
          ))}
        </div>
      </section>

      {/* CTA */}
      <section className={styles.cta}>
        <h2 className={styles.ctaTitle}>Start tracking prices now.</h2>
        <p className={styles.ctaSub}>Free. No credit card. Takes 30 seconds.</p>
        <button className={`btn-primary ${styles.ctaBtn}`} onClick={() => navigate('/register')}>
          Create Free Account
        </button>
      </section>

      {/* Footer */}
      <footer className={styles.footer}>
        <span>Made by <strong>syntaxjunkie</strong></span>
        <a href="https://github.com/dirtroaddeveloper/price-drop-tracker" target="_blank" rel="noreferrer">
          GitHub ↗
        </a>
      </footer>
    </div>
  );
}
