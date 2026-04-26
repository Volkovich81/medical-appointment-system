import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import './MyAppointments.css';

const MyAppointments = ({ patientId }) => {
  const [appointments, setAppointments] = useState([]);
  const [doctors, setDoctors] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState('all');
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 5;

  useEffect(() => {
    if (!patientId) return;
    fetchData();
  }, [patientId]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [appRes, docRes] = await Promise.all([
        api.get('/appointments'),
        api.get('/doctors')
      ]);
      // Фильтруем по полученному patientId
      const myAppointments = appRes.data.filter(a => a.patientId === patientId);
      setAppointments(myAppointments);
      setDoctors(docRes.data);
    } catch (err) {
      toast.error('Не удалось загрузить записи');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm('Отменить запись?')) return;
    try {
      await api.patch(`/appointments/${id}/status?status=CANCELLED`);
      setAppointments(prev => prev.map(a =>
        a.id === id ? { ...a, status: 'CANCELLED' } : a
      ));
      toast.success('Запись отменена');
    } catch (err) {
      toast.error('Ошибка отмены');
    }
  };

  const getStatusText = (status) => {
    const map = {
      SCHEDULED: 'Запланирован',
      COMPLETED: 'Завершён',
      CANCELLED: 'Отменён'
    };
    return map[status] || status;
  };

  const filteredAppointments = appointments.filter(a => {
    if (filter === 'all') return true;
    return a.status === filter.toUpperCase();
  });

  const sorted = [...filteredAppointments].sort((a, b) =>
    new Date(b.appointmentDate) - new Date(a.appointmentDate)
  );

  const totalPages = Math.ceil(sorted.length / itemsPerPage);
  const paginated = sorted.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  useEffect(() => setCurrentPage(1), [filter]);

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="my-appointments-section">
      <div className="filter-tabs">
        <button className={filter === 'all' ? 'active' : ''} onClick={() => setFilter('all')}>Все</button>
        <button className={filter === 'scheduled' ? 'active' : ''} onClick={() => setFilter('scheduled')}>Запланированные</button>
        <button className={filter === 'completed' ? 'active' : ''} onClick={() => setFilter('completed')}>Завершённые</button>
        <button className={filter === 'cancelled' ? 'active' : ''} onClick={() => setFilter('cancelled')}>Отменённые</button>
      </div>

      {paginated.length === 0 ? (
        <div className="empty-state">Записи не найдены</div>
      ) : (
        <div className="appointments-list">
          {paginated.map(appointment => {
            const doctor = doctors.find(d => d.id === appointment.doctorId);
            const date = new Date(appointment.appointmentDate);
            const isPast = date < new Date();
            const canCancel = appointment.status === 'SCHEDULED' && !isPast;

            return (
              <div key={appointment.id} className={`appointment-card status-${appointment.status.toLowerCase()}`}>
                <div className="appointment-date">
                  <div className="date-day">{date.getDate()}</div>
                  <div className="date-month">{date.toLocaleDateString('ru-RU', { month: 'short' })}</div>
                  <div className="date-time">{date.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' })}</div>
                </div>
                <div className="appointment-info">
                  <h4>{doctor ? `${doctor.firstName} ${doctor.lastName}` : 'Врач не найден'}</h4>
                  <span className={`status-badge status-${appointment.status.toLowerCase()}`}>
                    {getStatusText(appointment.status)}
                  </span>
                </div>
                <div className="appointment-actions">
                  {canCancel && (
                    <button className="btn-cancel" onClick={() => handleCancel(appointment.id)}>
                      Отменить
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

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

export default MyAppointments;