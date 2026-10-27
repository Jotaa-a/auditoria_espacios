import { useEffect, useState } from 'react';
import { api } from '../api/client';

export default function Auditoria() {
  const [auditorias, setAuditorias] = useState([]);
  const [usuarios, setUsuarios] = useState([]);
  
  // Filtros y paginación
  const [filtroEntidad, setFiltroEntidad] = useState('');
  const [filtroUsuario, setFiltroUsuario] = useState('');
  const [filtroDesde, setFiltroDesde] = useState('');
  const [filtroHasta, setFiltroHasta] = useState('');
  
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  // Cargar lista de usuarios al montar (para el selector de usuarioId)
  useEffect(() => {
    api.get('/api/usuarios').then(setUsuarios).catch((e) => setError(e.message));
  }, []);

  // Cargar registros de auditoría cuando cambien los filtros o la página
  useEffect(() => {
    cargarAuditoria();
  }, [filtroEntidad, filtroUsuario, filtroDesde, filtroHasta, page]);

  const cargarAuditoria = async () => {
    setLoading(true);
    setError('');
    try {
      const params = { page, size: 10 };
      if (filtroEntidad) params.entidad = filtroEntidad;
      if (filtroUsuario) params.usuarioId = filtroUsuario;
      if (filtroDesde) params.desde = filtroDesde;
      if (filtroHasta) params.hasta = filtroHasta;

      const response = await api.get('/api/auditoria', params);
      
      // Spring Data PagedModel suele devolver los elementos en .content y la info de paginación
      setAuditorias(response.content || []);
      setTotalPages(response.page?.totalPages || 0);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="pila">
      <div className="tarjeta">
        <h1>Registro de Auditoría</h1>
        <p className="suave">Historial de acciones y cambios realizados en el sistema por los usuarios.</p>

        {/* Filtros de búsqueda */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginTop: '1rem' }}>
          <div>
            <label htmlFor="entidad">Entidad</label>
            <input
              id="entidad"
              type="text"
              placeholder="Ej. Espacio, Usuario..."
              value={filtroEntidad}
              onChange={(e) => { setFiltroEntidad(e.target.value); setPage(0); }}
            />
          </div>

          <div>
            <label htmlFor="usuario">Usuario</label>
            <select
              id="usuario"
              value={filtroUsuario}
              onChange={(e) => { setFiltroUsuario(e.target.value); setPage(0); }}
            >
              <option value="">Todos los usuarios</option>
              {usuarios.map((u) => (
                <option key={u.id} value={u.id}>{u.nombre}</option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="desde">Desde</label>
            <input
              id="desde"
              type="date"
              value={filtroDesde}
              onChange={(e) => { setFiltroDesde(e.target.value); setPage(0); }}
            />
          </div>

          <div>
            <label htmlFor="hasta">Hasta</label>
            <input
              id="hasta"
              type="date"
              value={filtroHasta}
              onChange={(e) => { setFiltroHasta(e.target.value); setPage(0); }}
            />
          </div>
        </div>
      </div>

      {error && <p className="error" role="alert">{error}</p>}

      <div className="tarjeta">
        <h2>Historial de eventos</h2>
        
        {loading && <p className="suave">Cargando registros...</p>}

        {!loading && auditorias.length === 0 && (
          <p className="suave" style={{ marginTop: '1rem' }}>No se encontraron registros de auditoría con los filtros seleccionados.</p>
        )}

        {auditorias.length > 0 && (
          <>
            <div style={{ overflowX: 'auto', marginTop: '1rem' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                <thead>
                  <tr style={{ borderBottom: '2px solid #e5e7eb', fontSize: '0.85rem', color: '#6b7280' }}>
                    <th style={{ padding: '0.5rem' }}>Fecha / Hora</th>
                    <th style={{ padding: '0.5rem' }}>Usuario</th>
                    <th style={{ padding: '0.5rem' }}>Acción</th>
                    <th style={{ padding: '0.5rem' }}>Entidad</th>
                    <th style={{ padding: '0.5rem' }}>Detalles</th>
                  </tr>
                </thead>
                <tbody>
                  {auditorias.map((item, index) => (
                    <tr key={item.id || index} style={{ borderBottom: '1px solid #f3f4f6', fontSize: '0.9rem' }}>
                      <td style={{ padding: '0.75rem 0.5rem', whiteSpace: 'nowrap' }}>
                        {item.fecha ? new Date(item.fecha).toLocaleString() : 'N/D'}
                      </td>
                      <td style={{ padding: '0.75rem 0.5rem' }}>{item.usuarioNombre || 'Sistema'}</td>
                      <td style={{ padding: '0.75rem 0.5rem' }}>
                        <span className="insignia insignia-ok">{item.accion || 'ACCION'}</span>
                      </td>
                      <td style={{ padding: '0.75rem 0.5rem' }}><strong>{item.entidad}</strong> (#{item.entidadId})</td>
                      <td style={{ padding: '0.75rem 0.5rem', color: '#4b5563' }}>{item.detalles || 'Sin detalles'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Paginación */}
            <div style={{ display: 'flex', justifyContent: 'between', alignItems: 'center', marginTop: '1.5rem' }}>
              <button
                className="btn btn-secundario"
                disabled={page === 0}
                onClick={() => setPage(page - 1)}
              >
                Anterior
              </button>
              <span className="suave">
                Página {page + 1} de {totalPages || 1}
              </span>
              <button
                className="btn btn-secundario"
                disabled={page + 1 >= totalPages}
                onClick={() => setPage(page + 1)}
              >
                Siguiente
              </button>
            </div>
          </>
        )}
      </div>
    </section>
  );
}