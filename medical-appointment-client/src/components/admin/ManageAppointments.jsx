import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';

const ManageAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchPatient, setSearchPatient] = useState('');
  const [filterDoctorId, setFilterDoctorId] = useState('');
  const [filterStatus, setFilterStatus] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 12;

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [appRes, docRes, patRes] = await Promise.all([
        api.get('/appointments'),
        api.get('/doctors'),
        api.get('/patients')
      ]);
      setAppointments(appRes.data);
      setDoctors(docRes.data);
      setPatients(patRes.data);
    } catch (err) {
      toast.error('Не удалось загрузить данные');
    } finally {
      setLoading(false);
    }
  };

  const handleStatusChange = async (id, newStatus) => {
    try {
      await api.patch(`/appointments/${id}/status?status=${newStatus}`);
      setAppointments(prev => prev.map(a => a.id === id ? { ...a, status: newStatus } : a));
      toast.success('Статус обновлён');
    } catch (err) {
      toast.error('Ошибка изменения статуса');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить запись?')) return;
    try {
      await api.delete(`/appointments/${id}`);
      setAppointments(prev => prev.filter(a => a.id !== id));
      toast.success('Запись удалена');
    } catch (err) {
      toast.error('Ошибка удаления');
    }
  };

  const filteredAppointments = appointments.filter(a => {
    const patient = patients.find(p => p.id === a.patientId);
    const patientName = patient ? `${patient.firstName} ${patient.lastName}`.toLowerCase() : '';
    return (
      (!searchPatient || patientName.includes(searchPatient.toLowerCase())) &&
      (!filterDoctorId || a.doctorId === Number(filterDoctorId)) &&
      (!filterStatus || a.status === filterStatus)
    );
  });

  const sorted = [...filteredAppointments].sort((a, b) => new Date(b.appointmentDate) - new Date(a.appointmentDate));
  const totalPages = Math.ceil(sorted.length / itemsPerPage);
  const paginated = sorted.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  useEffect(() => setCurrentPage(1), [searchPatient, filterDoctorId, filterStatus]);

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="manage-section">
      <div className="filter-bar">
        <input placeholder="Поиск пациента" value={searchPatient} onChange={e => setSearchPatient(e.target.value)} className="search-input" />
        <select value={filterDoctorId} onChange={e => setFilterDoctorId(e.target.value)} className="filter-select">
          <option value="">Все врачи</option>
          {doctors.map(d => <option key={d.id} value={d.id}>{d.firstName} {d.lastName}</option>)}
        </select>
        <select value={filterStatus} onChange={e => setFilterStatus(e.target.value)} className="filter-select">
          <option value="">Все статусы</option>
          <option value="SCHEDULED">Запланирован</option>
          <option value="COMPLETED">Завершён</option>
          <option value="CANCELLED">Отменён</option>
        </select>
      </div>

      <div className="table-wrapper">
        <table className="data-table">
          <tbody>
            {paginated.length === 0 ? (
              <tr><td colSpan="5" className="empty-row">Записи не найдены</td></tr>
            ) : (
              paginated.map(a => {
                const patient = patients.find(p => p.id === a.patientId);
                const doctor = doctors.find(d => d.id === a.doctorId);
                return (
                  <tr key={a.id}>
                    <td>{new Date(a.appointmentDate).toLocaleString('ru-RU')}</td>
                    <td>{patient ? `${patient.firstName} ${patient.lastName}` : '—'}</td>
                    <td>{doctor ? `${doctor.firstName} ${doctor.lastName}` : '—'}</td>
                    <td>
                      <select value={a.status} onChange={e => handleStatusChange(a.id, e.target.value)} className={`status-select status-${a.status.toLowerCase()}`}>
                        <option value="SCHEDULED">Запланирован</option>
                        <option value="COMPLETED">Завершён</option>
                        <option value="CANCELLED">Отменён</option>
                      </select>
                    </td>
                    <td className="actions-cell">
                      <button className="btn-icon btn-danger" onClick={() => handleDelete(a.id)}>Удалить</button>
                    </td>
                  </tr>
                );
              })
            )}
          </tbody>
        </table>
      </div>

      {totalPages > 1 && (
        <div className="pagination">
          <button onClick={() => setCurrentPage(p => p - 1)} disabled={currentPage === 1}>←</button>
          <span>{currentPage} / {totalPages}</span>
          <button onClick={() => setCurrentPage(p => p + 1)} disabled={currentPage === totalPages}>→</button>
        </div>
      )}
    </div>
  );
};

export default ManageAppointments;