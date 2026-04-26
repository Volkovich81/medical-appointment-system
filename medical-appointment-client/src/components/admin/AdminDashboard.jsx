import { useState } from 'react';
import Header from '../layout/Header';
import ManageSpecializations from './ManageSpecializations';
import ManageDoctors from './ManageDoctors';
import ManagePatients from './ManagePatients';
import ManageAppointments from './ManageAppointments';
import ManageMedicalRecords from './ManageMedicalRecords';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState('specializations');

  const tabs = [
    { id: 'specializations', label: 'Специализации' },
    { id: 'doctors', label: 'Врачи' },
    { id: 'patients', label: 'Пациенты' },
    { id: 'appointments', label: 'Записи' },
    { id: 'records', label: 'Медкарты' },
  ];

  return (
    <div className="admin-dashboard">
      <Header />
      <div className="dashboard-tabs">
        {tabs.map(tab => (
          <button
            key={tab.id}
            className={activeTab === tab.id ? 'active' : ''}
            onClick={() => setActiveTab(tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>
      <main className="dashboard-content">
        {activeTab === 'specializations' && <ManageSpecializations />}
        {activeTab === 'doctors' && <ManageDoctors />}
        {activeTab === 'patients' && <ManagePatients />}
        {activeTab === 'appointments' && <ManageAppointments />}
        {activeTab === 'records' && <ManageMedicalRecords />}
      </main>
    </div>
  );
};

export default AdminDashboard;