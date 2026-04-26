import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';

const DoctorModal = ({ doctor, specializations, onClose, onSave }) => {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [selectedSpecs, setSelectedSpecs] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (doctor) {
      setFirstName(doctor.firstName || '');
      setLastName(doctor.lastName || '');
      setPhone(doctor.phone || '');
      setEmail(doctor.email || '');
      setSelectedSpecs(doctor.specializationIds || []);
    }
  }, [doctor]);

  const toggleSpec = (id) => {
    setSelectedSpecs(prev =>
      prev.includes(id) ? prev.filter(s => s !== id) : [...prev, id]
    );
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!firstName.trim() || !lastName.trim()) {
      toast.error('Заполните имя и фамилию');
      return;
    }
    setLoading(true);
    try {
      const data = { firstName, lastName, phone, email, specializationIds: selectedSpecs };
      if (doctor) {
        await api.put(`/doctors/${doctor.id}`, data);
        toast.success('Врач обновлён');
      } else {
        await api.post('/doctors', data);
        toast.success('Врач добавлен');
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
          <h2>{doctor ? 'Редактировать' : 'Добавить'} врача</h2>
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
            <div className="form-group">
              <label>Специализации</label>
              <div className="checkbox-group">
                {specializations.map(s => (
                  <label key={s.id} className="checkbox-item">
                    <input
                      type="checkbox"
                      checked={selectedSpecs.includes(s.id)}
                      onChange={() => toggleSpec(s.id)}
                    />
                    <span>{s.name}</span>
                  </label>
                ))}
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

export default DoctorModal;