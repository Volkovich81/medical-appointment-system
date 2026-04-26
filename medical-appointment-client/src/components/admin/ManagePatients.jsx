import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import PatientModal from './PatientModal';

const ManagePatients = () => {
  const [patients, setPatients] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingPatient, setEditingPatient] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchPatients();
  }, []);

  const fetchPatients = async () => {
    setLoading(true);
    try {
      const res = await api.get('/patients');
      setPatients(res.data);
    } catch (err) {
      toast.error('Не удалось загрузить пациентов');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить пациента?')) return;
    try {
      await api.delete(`/patients/${id}`);
      toast.success('Пациент удалён');
      fetchPatients();
    } catch (err) {
      toast.error('Ошибка удаления');
    }
  };

  const filteredPatients = patients.filter(p =>
    `${p.firstName} ${p.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    (p.phone || '').includes(searchTerm)
  );

  const totalPages = Math.ceil(filteredPatients.length / itemsPerPage);
  const paginatedPatients = filteredPatients.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  useEffect(() => setCurrentPage(1), [searchTerm]);

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="manage-section">
      <div className="section-header">
        <input
          placeholder="Поиск по имени или телефону"
          value={searchTerm}
          onChange={e => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <button className="btn-primary" onClick={() => setModalOpen(true)}>+ Добавить</button>
      </div>

      <div className="table-wrapper">
        <table className="data-table">
          <tbody>
            {paginatedPatients.length === 0 ? (
              <tr><td colSpan="5" className="empty-row">Пациенты не найдены</td></tr>
            ) : (
              paginatedPatients.map(p => (
                <tr key={p.id}>
                  <td>{p.lastName} {p.firstName}</td>
                  <td>{p.birthDate ? new Date(p.birthDate).toLocaleDateString('ru-RU') : '—'}</td>
                  <td>{p.phone || '—'}</td>
                  <td>{p.email || '—'}</td>
                  <td className="actions-cell">
                    <button className="btn-icon" onClick={() => { setEditingPatient(p); setModalOpen(true); }}>Изменить</button>
                    <button className="btn-icon btn-danger" onClick={() => handleDelete(p.id)}>Удалить</button>
                  </td>
                </tr>
              ))
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
        <PatientModal
          patient={editingPatient}
          onClose={() => { setModalOpen(false); setEditingPatient(null); }}
          onSave={() => { setModalOpen(false); setEditingPatient(null); fetchPatients(); }}
        />
      )}
    </div>
  );
};

export default ManagePatients;