import { useEffect, useState } from 'react';
import { api } from '../api/client';

export default function Dashboard() {
  const [estadisticas, setEstadisticas] = useState(null);
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(true);

  useEffect(() => {
    cargarEstadisticas();
  }, []);

  async function cargarEstadisticas() {
    try {
      // Opcional: Puedes crear un endpoint en el backend que agrupe esto, 
      // o calcularlo consumiendo los datos generales que ya tienes:
      const [espacios, puestos, inspecciones, tipos] = await Promise.all([
        api.get('/api/espacios'),
        api.get('/api/puestos'),
        api.get('/api/inspecciones'),
        api.get('/api/tipos-hallazgo'),
      ]);

      // Ejemplo de cálculo rápido en frontend con los datos obtenidos
      const totalEspacios = espacios?.length ?? 0;
      const totalPuestos = puestos?.length ?? 0;
      const totalInspecciones = inspecciones?.length ?? 0;
      const inspeccionesBorrador = inspecciones?.filter(i => i.estado === 'BORRADOR').length ?? 0;

      setEstadisticas({
        totalEspacios,
        totalPuestos,
        totalInspecciones,
        inspeccionesBorrador,
        puestosActivos: puestos?.filter(p => p.activo).length ?? 0,
      });
    } catch (e) {
      setError('No se pudieron cargar las estadísticas del sistema.');
    } finally {
      setCargando(false);
    }
  }

  if (cargando) return <p className="suave">Cargando panel de estadísticas…</p>;
  if (error) return <p className="error" role="alert">{error}</p>;

  return (
    <section className="pila">
      <div className="tarjeta">
        <h2>Panel General y Estadísticas</h2>
        <p className="suave">Métricas clave sobre el estado de los espacios y las inspecciones.</p>

        {/* Cuadrícula de KPIs */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginTop: '1.5rem' }}>
          
          <div className="tarjeta" style={{ textAlign: 'center', background: '#f9fafb' }}>
            <span className="suave">Espacios Registrados</span>
            <h3 style={{ fontSize: '2rem', margin: '0.5rem 0' }}>{estadisticas?.totalEspacios}</h3>
          </div>

          <div className="tarjeta" style={{ textAlign: 'center', background: '#f9fafb' }}>
            <span className="suave">Puestos Activos</span>
            <h3 style={{ fontSize: '2rem', margin: '0.5rem 0', color: '#10b981' }}>{estadisticas?.puestosActivos}</h3>
            <span className="suave" style={{ fontSize: '0.85rem' }}>De {estadisticas?.totalPuestos} totales</span>
          </div>

          <div className="tarjeta" style={{ textAlign: 'center', background: '#f9fafb' }}>
            <span className="suave">Inspecciones Totales</span>
            <h3 style={{ fontSize: '2rem', margin: '0.5rem 0' }}>{estadisticas?.totalInspecciones}</h3>
          </div>

          <div className="tarjeta" style={{ textAlign: 'center', background: '#f9fafb' }}>
            <span className="suave">En Borrador (Pendientes)</span>
            <h3 style={{ fontSize: '2rem', margin: '0.5rem 0', color: '#f59e0b' }}>{estadisticas?.inspeccionesBorrador}</h3>
          </div>

        </div>
      </div>
    </section>
  );
}