import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';

const PatientModal = ({ patient, onClose, onSave }) => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [birthDate, setBirthDate] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (patient) {
      setFirstName(patient.firstName || '');
      setLastName(patient.lastName || '');
      setBirthDate(patient.birthDate || '');
      setPhone(patient.phone || '');
      setEmail(patient.email || '');
    }
  }, [patient]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!firstName.trim() || !lastName.trim()) {
      toast.error('Заполните имя и фамилию');
      return;
    }
    setLoading(true);
    try {
      const data = { firstName, lastName, birthDate: birthDate || null, phone, email };
      if (patient) {
        await api.put(`/patients/${patient.id}`, data);
        toast.success('Пациент обновлён');
      } else {
        await api.post('/patients', data);
        toast.success('Пациент добавлен');
      }
      onSave();
    } catch (err) {
      toast.error('Ошибка сохранения');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h2>{patient ? 'Редактировать' : 'Добавить'} пациента</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-row">
              <div className="form-group">
                <label>Имя</label>
                <input type="text" value={firstName} onChange={e => setFirstName(e.target.value)} placeholder="Имя" />
              </div>
              <div className="form-group">
                <label>Фамилия</label>
                <input type="text" value={lastName} onChange={e => setLastName(e.target.value)} placeholder="Фамилия" />
              </div>
            </div>
            <div className="form-group">
              <label>Дата рождения</label>
              <input type="date" value={birthDate} onChange={e => setBirthDate(e.target.value)} />
            </div>
            <div className="form-row">
              <div className="form-group">
                <label>Телефон</label>
                <input type="text" value={phone} onChange={e => setPhone(e.target.value)} placeholder="+7 (999) 123-45-67" />
              </div>
              <div className="form-group">
                <label>Email</label>
                <input type="email" value={email} onChange={e => setEmail(e.target.value)} placeholder="email@example.com" />
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button type="button" className="btn-secondary" onClick={onClose}>Отмена</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Сохранение...' : 'Сохранить'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default PatientModal;