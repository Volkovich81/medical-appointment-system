import { useState, useEffect } from 'react';
import api from '../../api/axios';
import toast from 'react-hot-toast';
import AppointmentModal from './AppointmentModal';
import './DoctorList.css';

const DoctorList = ({ patientId }) => {
  const [doctors, setDoctors] = useState([]);
  const [specializations, setSpecializations] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterSpecId, setFilterSpecId] = useState('');
  const [selectedDoctor, setSelectedDoctor] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const itemsPerPage = 8;

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const [docRes, specRes] = await Promise.all([
        api.get('/doctors'),
        api.get('/specializations')
      ]);
      setDoctors(docRes.data);
      setSpecializations(specRes.data);
    } catch (err) {
      toast.error('Не удалось загрузить данные');
    } finally {
      setLoading(false);
    }
  };

  const filteredDoctors = doctors.filter(d => {
    const fullName = `${d.firstName} ${d.lastName}`.toLowerCase();
    const matchesSearch = fullName.includes(searchTerm.toLowerCase());
    const matchesSpec = !filterSpecId || d.specializationIds?.includes(Number(filterSpecId));
    return matchesSearch && matchesSpec;
  });

  const totalPages = Math.ceil(filteredDoctors.length / itemsPerPage);
  const paginatedDoctors = filteredDoctors.slice(
    (currentPage - 1) * itemsPerPage,
    currentPage * itemsPerPage
  );

  useEffect(() => setCurrentPage(1), [searchTerm, filterSpecId]);

  if (loading) return <div className="loader">Загрузка...</div>;

  return (
    <div className="doctor-list-section">
      <div className="filter-bar">
        <input
          type="text"
          placeholder="Поиск по имени врача"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
        <select
          value={filterSpecId}
          onChange={(e) => setFilterSpecId(e.target.value)}
          className="filter-select"
        >
          <option value="">Все специализации</option>
          {specializations.map(s => (
            <option key={s.id} value={s.id}>{s.name}</option>
          ))}
        </select>
      </div>

      {paginatedDoctors.length === 0 ? (
        <div className="empty-state">Врачи не найдены</div>
      ) : (
        <div className="doctor-grid">
          {paginatedDoctors.map(doctor => {
            const docSpecs = specializations.filter(s =>
              doctor.specializationIds?.includes(s.id)
            );
            return (
              <div key={doctor.id} className="doctor-card">
                <div className="doctor-avatar">
                  {doctor.firstName[0]}{doctor.lastName[0]}
                </div>
                <div className="doctor-info">
                  <h3>{doctor.firstName} {doctor.lastName}</h3>
                  <div className="doctor-specs">
                    {docSpecs.map(s => (
                      <span key={s.id} className="spec-tag">{s.name}</span>
                    ))}
                  </div>
                  {doctor.phone && <p className="doctor-contact">{doctor.phone}</p>}
                </div>
                <button
                  className="btn-primary"
                  onClick={() => setSelectedDoctor(doctor)}
                >
                  Записаться
                </button>
              </div>
            );
          })}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setCurrentPage(p => p - 1)}
            disabled={currentPage === 1}
          >
            ←
          </button>
          <span>{currentPage} / {totalPages}</span>
          <button
            onClick={() => setCurrentPage(p => p + 1)}
            disabled={currentPage === totalPages}
          >
            →
          </button>
        </div>
      )}

      {selectedDoctor && (
        <AppointmentModal
          doctor={selectedDoctor}
          onClose={() => setSelectedDoctor(null)}
          onSuccess={() => {
            setSelectedDoctor(null);
            toast.success('Вы успешно записаны на приём');
          }}
        />
      )}
    </div>
  );
};

export default DoctorList;