import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';

const MedicalRecordModal = ({ record, patients, onClose, onSave }) => {
  const [patientId, setPatientId] = useState('');
  const [recordDate, setRecordDate] = useState('');
  const [diagnosis, setDiagnosis] = useState('');
  const [treatment, setTreatment] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (record) {
      setPatientId(record.patientId || '');
      setRecordDate(record.recordDate || '');
      setDiagnosis(record.diagnosis || '');
      setTreatment(record.treatment || '');
    }
  }, [record]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!patientId) {
      toast.error('Выберите пациента');
      return;
    }
    setLoading(true);
    try {
      const data = { patientId: Number(patientId), recordDate: recordDate || null, diagnosis, treatment };
      if (record) {
        await api.put(`/medical-records/${record.id}`, data);
        toast.success('Карта обновлена');
      } else {
        await api.post('/medical-records', data);
        toast.success('Карта создана');
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
          <h2>{record ? 'Редактировать' : 'Создать'} медкарту</h2>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="modal-body">
            <div className="form-group">
              <label>Пациент</label>
              <select value={patientId} onChange={e => setPatientId(e.target.value)} disabled={!!record}>
                <option value="">Выберите пациента</option>
                {patients.map(p => (
                  <option key={p.id} value={p.id}>{p.firstName} {p.lastName}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Дата записи</label>
              <input type="date" value={recordDate} onChange={e => setRecordDate(e.target.value)} />
            </div>
            <div className="form-group">
              <label>Диагноз</label>
              <textarea value={diagnosis} onChange={e => setDiagnosis(e.target.value)} placeholder="Диагноз" rows={3} />
            </div>
            <div className="form-group">
              <label>Лечение</label>
              <textarea value={treatment} onChange={e => setTreatment(e.target.value)} placeholder="Назначенное лечение" rows={3} />
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

export default MedicalRecordModal;