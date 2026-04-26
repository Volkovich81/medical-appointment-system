import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import DoctorModal from './DoctorModal';

const ManageDoctors = () => {
  const [doctors, setDoctors] = useState([]);
  const [specializations, setSpecializations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterSpecId, setFilterSpecId] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingDoctor, setEditingDoctor] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchDoctors();
    fetchSpecializations();
  }, []);

  const fetchDoctors = async () => {
    setLoading(true);
    try {
      const res = await api.get('/doctors');
      setDoctors(res.data);
    } catch (err) {
      toast.error('Не удалось загрузить врачей');
    } finally {
      setLoading(false);
    }
  };

  const fetchSpecializations = async () => {
    try {
      const res = await api.get('/specializations');
      setSpecializations(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить врача?')) return;
    try {
      await api.delete(`/doctors/${id}`);
      toast.success('Врач удалён');
      fetchDoctors();
    } catch (err) {
      toast.error('Ошибка удаления');
    }
  };

  const filteredDoctors = doctors.filter(d => {
    const fullName = `${d.firstName} ${d.lastName}`.toLowerCase();
    const matchesSearch = fullName.includes(searchTerm.toLowerCase());
    const matchesSpec = !filterSpecId || d.specializationIds?.includes(Number(filterSpecId));
    return matchesSearch && matchesSpec;
  });

  const totalPages = Math.ceil(filteredDoctors.length / itemsPerPage);
  const paginatedDoctors = filteredDoctors.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage);

  useEffect(() => setCurrentPage(1), [searchTerm, filterSpecId]);

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="manage-section">
      <div className="section-header">
        <div className="filter-bar">
          <input placeholder="Поиск по имени" value={searchTerm} onChange={e => setSearchTerm(e.target.value)} className="search-input" />
          <select value={filterSpecId} onChange={e => setFilterSpecId(e.target.value)} className="filter-select">
            <option value="">Все специализации</option>
            {specializations.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
          </select>
        </div>
        <button className="btn-primary" onClick={() => setModalOpen(true)}>+ Добавить</button>
      </div>

      <div className="table-wrapper">
        <table className="data-table">
          <tbody>
            {paginatedDoctors.length === 0 ? (
              <tr><td colSpan="5" className="empty-row">Врачи не найдены</td></tr>
            ) : (
              paginatedDoctors.map(d => {
                const docSpecs = specializations.filter(s => d.specializationIds?.includes(s.id));
                return (
                  <tr key={d.id}>
                    <td>{d.firstName} {d.lastName}</td>
                    <td><div className="spec-tags">{docSpecs.map(s => <span key={s.id} className="spec-tag">{s.name}</span>)}</div></td>
                    <td>{d.phone || '—'}</td>
                    <td>{d.email || '—'}</td>
                    <td className="actions-cell">
                      <button className="btn-icon" onClick={() => { setEditingDoctor(d); setModalOpen(true); }}>Изменить</button>
                      <button className="btn-icon btn-danger" onClick={() => handleDelete(d.id)}>Удалить</button>
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
        <DoctorModal
          doctor={editingDoctor}
          specializations={specializations}
          onClose={() => { setModalOpen(false); setEditingDoctor(null); }}
          onSave={() => { setModalOpen(false); setEditingDoctor(null); fetchDoctors(); }}
        />
      )}
    </div>
  );
};

export default ManageDoctors;