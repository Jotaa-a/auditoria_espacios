import { useEffect, useState } from 'react';
import { api } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import { fechaCorta } from '../utils/formato';

const ESTADOS_REPORTE = {
  PENDIENTE: 'Pendiente',
  EN_REVISION: 'En revisión',
  RESUELTO: 'Resuelto',
  RECHAZADO: 'Rechazado',
};

export default function Danos() {
  const { rol } = useAuth();
  const [reportes, setReportes] = useState([]);
  const [espacios, setEspacios] = useState([]);
  const [filtroEspacio, setFiltroEspacio] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [error, setError] = useState('');
  const [ocupadoId, setOcupadoId] = useState(null);

  // Cargar lista de espacios para el filtro
  useEffect(() => {
    api.get('/api/espacios').then(setEspacios).catch((e) => setError(e.message));
  }, []);

  // Cargar reportes de daño al cambiar los filtros
  useEffect(() => {
    cargarReportes();
  }, [filtroEspacio, filtroEstado]);

  const cargarReportes = async () => {
    try {
      const params = {};
      if (filtroEspacio) params.espacioId = filtroEspacio;
      if (filtroEstado) params.estado = filtroEstado;
      
      const data = await api.get('/api/reportes-dano', params);
      setReportes(data);
    } catch (e) {
      setError(e.message);
    }
  };

  // Cambiar el estado de un reporte (Solo permitido para ADMIN o TECNICO según backend/roles)
  const cambiarEstado = async (id, nuevoEstado) => {
    setOcupadoId(id);
    setError('');
    try {
      await api.patch(`/api/reportes-dano/${id}/estado`, { estado: nuevoEstado });
      await cargarReportes();
    } catch (e) {
      setError(e.message);
    } finally {
      setOcupadoId(null);
    }
  };

  return (
    <section className="pila">
      <div className="tarjeta">
        <h1>Gestión de Daños y Reportes</h1>
        <p className="suave">Visualiza y da seguimiento a los reportes de daños registrados en los equipos y espacios.</p>

        <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem', flexWrap: 'wrap' }}>
          <div style={{ flex: 1 }}>
            <label htmlFor="filtroEspacio">Filtrar por espacio</label>
            <select
              id="filtroEspacio"
              value={filtroEspacio}
              onChange={(e) => setFiltroEspacio(e.target.value)}
              className="w-full"
            >
              <option value="">Todos los espacios</option>
              {espacios.map((x) => (
                <option key={x.id} value={x.id}>{x.nombre}</option>
              ))}
            </select>
          </div>

          <div style={{ flex: 1 }}>
            <label htmlFor="filtroEstado">Filtrar por estado</label>
            <select
              id="filtroEstado"
              value={filtroEstado}
              onChange={(e) => setFiltroEstado(e.target.value)}
              className="w-full"
            >
              <option value="">Todos los estados</option>
              {Object.entries(ESTADOS_REPORTE).map(([v, t]) => (
                <option key={v} value={v}>{t}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {error && <p className="error" role="alert">{error}</p>}

      {reportes === null && <p className="suave">Cargando reportes...</p>}
      {reportes?.length === 0 && <p className="suave">No hay reportes de daños registrados con los filtros seleccionados.</p>}

      <ul className="lista">
        {reportes?.map((r) => (
          <li key={r.id} className="tarjeta">
            <div className="puesto-cab">
              <strong>{r.espacioNombre || 'Espacio'} · Puesto #{r.puestoCodigo || r.puestoId}</strong>
              <span className={`insignia ${r.estado === 'RESUELTO' ? 'insignia-ok' : 'insignia-alerta'}`}>
                {ESTADOS_REPORTE[r.estado] || r.estado}
              </span>
            </div>
            <p style={{ margin: '0.5rem 0' }}>{r.descripcion}</p>
            <p className="suave">
              Fecha: {r.creadoEn ? new Date(r.creadoEn).toLocaleDateString() : 'N/D'} · Reportado por: {r.reportadoPorNombre || 'Usuario'}
            </p>

            {/* Si es Administrador o Técnico, puede modificar el estado directamente */}
            {(rol === 'ADMIN' || rol === 'TECNICO') && (
              <div style={{ marginTop: '1rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                <span className="suave" style={{ fontSize: '0.85rem' }}>Cambiar estado:</span>
                <select
                  disabled={ocupadoId === r.id}
                  defaultValue={r.estado}
                  onChange={(e) => cambiarEstado(r.id, e.target.value)}
                  style={{ padding: '0.25rem 0.5rem', borderRadius: '4px' }}
                >
                  {Object.entries(ESTADOS_REPORTE).map(([v, t]) => (
                    <option key={v} value={v}>{t}</option>
                  ))}
                </select>
              </div>
            )}
          </li>
        ))}
      </ul>
    </section>
  );
}