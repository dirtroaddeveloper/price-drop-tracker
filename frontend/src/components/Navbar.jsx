import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import styles from './Navbar.module.css';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login');
  }

  return (
    <nav className={styles.nav}>
      <div className={styles.brand} onClick={() => navigate('/')}>
        <span className={styles.dot}>▶</span> Price Drop Tracker
      </div>
      <div className={styles.right}>
        <span className={styles.email}>{user?.email}</span>
        <button className="btn-ghost" onClick={handleLogout}>Log out</button>
      </div>
    </nav>
  );
}
