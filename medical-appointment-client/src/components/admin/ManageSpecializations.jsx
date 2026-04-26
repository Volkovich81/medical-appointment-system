import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import SpecializationModal from './SpecializationModal';

const ManageSpecializations = () => {
  const [specializations, setSpecializations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [modalOpen, setModalOpen] = useState(false);
  const [editingSpec, setEditingSpec] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 10;

  useEffect(() => {
    fetchSpecializations();
  }, []);

  const fetchSpecializations = async () => {
    setLoading(true);
    try {
      const res = await api.get('/specializations');
      setSpecializations(res.data);
    } catch (err) {
      toast.error('Не удалось загрузить специализации');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Удалить специализацию?')) return;
    try {
      await api.delete(`/specializations/${id}`);
      toast.success('Специализация удалена');
      fetchSpecializations();
    } catch (err) {
      toast.error('Ошибка при удалении');
    }
  };

  const filteredSpecs = specializations.filter(s =>
    s.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const totalPages = Math.ceil(filteredSpecs.length / itemsPerPage);
  const paginatedSpecs = filteredSpecs.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  useEffect(() => setCurrentPage(1), [searchTerm]);

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="manage-section">
      <div className="section-header">
        <input
          type="text"
          placeholder="Поиск по названию"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <button className="btn-primary" onClick={() => setModalOpen(true)}>
          + Добавить
        </button>
      </div>

      <div className="table-wrapper">
        <table className="data-table">
          <tbody>
            {paginatedSpecs.length === 0 ? (
              <tr><td colSpan="3" className="empty-row">Специализации не найдены</td></tr>
            ) : (
              paginatedSpecs.map(spec => (
                <tr key={spec.id}>
                  <td>{spec.name}</td>
                  <td>{spec.description || '—'}</td>
                  <td className="actions-cell">
                    <button className="btn-icon" onClick={() => { setEditingSpec(spec); setModalOpen(true); }}>Изменить</button>
                    <button className="btn-icon btn-danger" onClick={() => handleDelete(spec.id)}>Удалить</button>
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
        <SpecializationModal
          specialization={editingSpec}
          onClose={() => { setModalOpen(false); setEditingSpec(null); }}
          onSave={() => { setModalOpen(false); setEditingSpec(null); fetchSpecializations(); }}
        />
      )}
    </div>
  );
};

export default ManageSpecializations;