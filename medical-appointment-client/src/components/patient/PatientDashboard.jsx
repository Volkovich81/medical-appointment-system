import { useState, useEffect } from 'react';
import Header from '../layout/Header';
import DoctorList from './DoctorList';
import MyAppointments from './MyAppointments';
import api from '../../api/axios';
import './PatientDashboard.css';

const PatientDashboard = () => {
  const [activeTab, setActiveTab] = useState('doctors');
  const [patientId, setPatientId] = useState(null);

  useEffect(() => {
    const fetchPatientId = async () => {
      try {
        const res = await api.get('/patients');
        if (res.data.length > 0) {
          // Берём первого реального пациента из базы
          setPatientId(res.data[0].id);
        }
      } catch (err) {
        console.error('Ошибка загрузки пациентов', err);
      }
    };
    fetchPatientId();
  }, []);

  if (!patientId) {
    return <div className="loader">Загрузка профиля...</div>;
  }

  return (
    <div className="patient-dashboard">
      <Header />
      <div className="dashboard-tabs">
        <button
          className={activeTab === 'doctors' ? 'active' : ''}
          onClick={() => setActiveTab('doctors')}
        >
          Специалисты
        </button>
        <button
          className={activeTab === 'appointments' ? 'active' : ''}
          onClick={() => setActiveTab('appointments')}
        >
          Мои записи
        </button>
      </div>
      <main className="dashboard-content">
        {activeTab === 'doctors' && <DoctorList />}
        {activeTab === 'appointments' && <MyAppointments patientId={patientId} />}
      </main>
    </div>
  );
};

export default PatientDashboard;