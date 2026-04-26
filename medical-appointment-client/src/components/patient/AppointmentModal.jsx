import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import './AppointmentModal.css';

const timeSlots = [
  '09:00', '09:30', '10:00', '10:30', '11:00', '11:30',
  '12:00', '14:00', '14:30', '15:00', '15:30', '16:00', '16:30', '17:00'
];

const AppointmentModal = ({ doctor, onClose, onSuccess }) => {
  const [selectedDate, setSelectedDate] = useState('');
  const [selectedTime, setSelectedTime] = useState('');
  const [availableTimes, setAvailableTimes] = useState(timeSlots);
  const [loading, setLoading] = useState(false);
  const [patientId, setPatientId] = useState(null);

  // Получаем ID первого пациента при открытии модалки
  useEffect(() => {
    const fetchPatientId = async () => {
      try {
        const res = await api.get('/patients');
        if (res.data.length > 0) {
          setPatientId(res.data[0].id);
        } else {
          toast.error('Нет доступных пациентов. Обратитесь к администратору.');
          onClose();
        }
      } catch (err) {
        console.error('Ошибка получения пациентов:', err);
        toast.error('Ошибка загрузки данных пациента');
        onClose();
      }
    };
    fetchPatientId();
  }, [onClose]);

  // Фильтрация доступных слотов при выборе даты
  useEffect(() => {
    if (!selectedDate || !doctor?.id) return;

    const fetchBookedTimes = async () => {
      try {
        const res = await api.get('/appointments');
        const doctorAppointments = res.data.filter(
          a => a.doctorId === doctor.id &&
               a.appointmentDate?.startsWith(selectedDate) &&
               a.status !== 'CANCELLED'
        );
        const booked = doctorAppointments.map(a => {
          const date = new Date(a.appointmentDate);
          const hh = String(date.getHours()).padStart(2, '0');
          const mm = String(date.getMinutes()).padStart(2, '0');
          return `${hh}:${mm}`;
        });
        setAvailableTimes(timeSlots.filter(t => !booked.includes(t)));
        if (selectedTime && booked.includes(selectedTime)) {
          setSelectedTime('');
        }
      } catch (err) {
        console.error(err);
      }
    };

    fetchBookedTimes();
  }, [selectedDate, doctor]);

  const handleSubmit = async () => {
    if (!patientId) {
      toast.error('Данные пациента не загружены. Попробуйте снова.');
      return;
    }
    if (!selectedDate || !selectedTime) {
      toast.error('Выберите дату и время');
      return;
    }
    setLoading(true);
    try {
      const appointmentDate = `${selectedDate}T${selectedTime}:00`;
      await api.post('/appointments', {
        patientId,
        doctorId: doctor.id,
        appointmentDate,
        status: 'SCHEDULED'
      });
      toast.success('Запись успешно создана!');
      onSuccess?.();
      onClose();
    } catch (err) {
      console.error(err);
      const msg = err.response?.data?.message || 'Не удалось создать запись';
      toast.error(msg);
    } finally {
      setLoading(false);
    }
  };

  const today = new Date().toISOString().split('T')[0];

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>Запись к врачу</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">
          <div className="doctor-preview">
            <div className="doctor-avatar-small">
              {doctor.firstName?.[0]}{doctor.lastName?.[0]}
            </div>
            <div>
              <strong>{doctor.firstName} {doctor.lastName}</strong>
            </div>
          </div>

          <div className="form-group">
            <label>Выберите дату</label>
            <input
              type="date"
              value={selectedDate}
              onChange={e => { setSelectedDate(e.target.value); setSelectedTime(''); }}
              min={today}
            />
          </div>

          {selectedDate && (
            <div className="form-group">
              <label>Выберите время</label>
              {availableTimes.length === 0 ? (
                <p className="no-times">Нет свободных слотов на эту дату</p>
              ) : (
                <div className="time-grid">
                  {availableTimes.map(time => (
                    <button
                      key={time}
                      type="button"
                      className={`time-slot ${selectedTime === time ? 'selected' : ''}`}
                      onClick={() => setSelectedTime(time)}
                    >
                      {time}
                    </button>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
        <div className="modal-footer">
          <button className="btn-secondary" onClick={onClose}>Отмена</button>
          <button
            className="btn-primary"
            onClick={handleSubmit}
            disabled={loading || !selectedDate || !selectedTime}
          >
            {loading ? 'Запись...' : 'Записаться'}
          </button>
        </div>
      </div>
    </div>
  );
};

export default AppointmentModal;