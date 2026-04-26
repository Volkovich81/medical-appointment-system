import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import MedicalRecordModal from './MedicalRecordModal';

const ManageMedicalRecords = () => {
  const [records, setRecords] = useState([]);
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 12;

  useEffect(() => { fetchData(); }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [recRes, patRes] = await Promise.all([api.get('/medical-records'), api.get('/patients')]);
      setRecords(recRes.data);
      setPatients(patRes.data);
    } catch (err) {
      toast.error('Не удалось загрузить данные');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить медицинскую карту?')) return;
    try {
      await api.delete(`/medical-records/${id}`);
      toast.success('Карта удалена');
      fetchData();
    } catch (err) {
      toast.error('Ошибка удаления');
    }
  };

  const filtered = records.filter(r => {
    const patient = patients.find(p => p.id === r.patientId);
    const name = patient ? `${patient.firstName} ${patient.lastName}`.toLowerCase() : '';
    return name.includes(searchTerm.toLowerCase()) || (r.diagnosis || '').toLowerCase().includes(searchTerm.toLowerCase());
  });

  const totalPages = Math.ceil(filtered.length / itemsPerPage);
  const paginated = filtered.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);
  useEffect(() => setCurrentPage(1), [searchTerm]);

  const patientsWithout = patients.filter(p => !records.some(r => r.patientId === p.id));

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="manage-section">
      <div className="section-header">
        <input placeholder="Поиск по пациенту или диагнозу" value={searchTerm} onChange={e => setSearchTerm(e.target.value)} className="search-input" />
        <button className="btn-primary" onClick={() => setModalOpen(true)} disabled={patientsWithout.length === 0}>+ Создать карту</button>
      </div>

      <div className="table-wrapper">
        <table className="data-table">
          <tbody>
            {paginated.length === 0 ? (
              <tr><td colSpan="5" className="empty-row">Медицинские карты не найдены</td></tr>
            ) : (
              paginated.map(r => {
                const patient = patients.find(p => p.id === r.patientId);
                return (
                  <tr key={r.id}>
                    <td>{patient ? `${patient.firstName} ${patient.lastName}` : '—'}</td>
                    <td>{r.recordDate ? new Date(r.recordDate).toLocaleDateString('ru-RU') : '—'}</td>
                    <td>{r.diagnosis || '—'}</td>
                    <td>{r.treatment || '—'}</td>
                    <td className="actions-cell">
                      <button className="btn-icon" onClick={() => { setEditingRecord(r); setModalOpen(true); }}>Изменить</button>
                      <button className="btn-icon btn-danger" onClick={() => handleDelete(r.id)}>Удалить</button>
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

      {modalOpen && (
        <MedicalRecordModal
          record={editingRecord}
          patients={editingRecord ? patients : patientsWithout}
          onClose={() => { setModalOpen(false); setEditingRecord(null); }}
          onSave={() => { setModalOpen(false); setEditingRecord(null); fetchData(); }}
        />
      )}
    </div>
  );
};

export default ManageMedicalRecords;