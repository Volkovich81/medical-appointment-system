import { AuthProvider, useAuth } from './context/AuthContext';
import { Toaster } from 'react-hot-toast';
import LoginForm from './components/auth/LoginForm';
import PatientDashboard from './components/patient/PatientDashboard';
import AdminDashboard from './components/admin/AdminDashboard';
import './index.css';

const AppContent = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return <div className="loader">Загрузка...</div>;
  }

  if (!user) {
    return <LoginForm />;
  }

  if (user.role === 'ADMIN') {
    return <AdminDashboard />;
  }

  return <PatientDashboard />;
};

function App() {
  return (
    <AuthProvider>
      <AppContent />
      <Toaster position="top-center" />
    </AuthProvider>
  );
}

export default App;