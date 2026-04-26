import { useAuth } from '../../context/AuthContext';
import './Header.css';

const Header = () => {
  const { user, logout } = useAuth();

  return (
    <header className="header">
      <div className="header-decoration"></div>
      <div className="header-main">
        <div className="header-left">
          <h1 className="header-title">МедЗапись</h1>
          <span className="header-role">
            {user?.role === 'ADMIN' ? 'Администратор' : 'Пациент'}
          </span>
        </div>
        <div className="header-right">
          <span className="header-user">{user?.username}</span>
          <button className="btn-logout" onClick={logout}>
            Выйти
          </button>
        </div>
      </div>
    </header>
  );
};

export default Header;