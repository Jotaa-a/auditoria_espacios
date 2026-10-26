import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { api } from '../api/client';
import { TIPO_INSPECCION, fechaCorta, hoy } from '../utils/formato';

export default function Inspecciones() {
  const navegar = useNavigate();
  const [espacios, setEspacios] = useState([]);
  const [lista, setLista] = useState(null);
  const [filtro, setFiltro] = useState('');
  const [form, setForm] = useState({
    espacioId: '',
    tipo: new Date().getDay() === 1 ? 'LUNES_RECEPCION' : 'VIERNES_ENTREGA',
    fecha: hoy(),
  });
  const [error, setError] = useState('');
  const [creando, setCreando] = useState(false);

  useEffect(() => {
    api.get('/api/espacios').then(setEspacios).catch((e) => setError(e.message));
  }, []);

  useEffect(() => {
    api.get('/api/inspecciones', { espacioId: filtro }).then(setLista).catch((e) => setError(e.message));
  }, [filtro]);

  async function crear(e) {
    e.preventDefault();
    setError('');
    setCreando(true);
    try {
      const nueva = await api.post('/api/inspecciones', {
        espacioId: Number(form.espacioId), tipo: form.tipo, fecha: form.fecha,
      });
      navegar(`/inspecciones/${nueva.id}`);
    } catch (err) {
      setError(err.message);
      setCreando(false);
    }
  }

  return (
    <section className="pila">
      <form className="tarjeta" onSubmit={crear}>
        <h1>Nueva inspección</h1>

        <label htmlFor="espacio">Espacio</label>
        <select id="espacio" required value={form.espacioId}
                onChange={(e) => setForm({ ...form, espacioId: e.target.value })}>
          <option value="">Elige un espacio…</option>
          {espacios.map((x) => <option key={x.id} value={x.id}>{x.nombre}</option>)}
        </select>

        <label htmlFor="tipo">Tipo</label>
        <select id="tipo" value={form.tipo} onChange={(e) => setForm({ ...form, tipo: e.target.value })}>
          {Object.entries(TIPO_INSPECCION).map(([v, t]) => <option key={v} value={v}>{t}</option>)}
        </select>

        <label htmlFor="fecha">Fecha</label>
        <input id="fecha" type="date" required value={form.fecha}
               onChange={(e) => setForm({ ...form, fecha: e.target.value })} />

        {error && <p className="error" role="alert">{error}</p>}
        <button className="btn btn-primario" disabled={creando}>
          {creando ? 'Creando…' : 'Empezar inspección'}
        </button>
      </form>

      <div className="tarjeta">
        <h2>Inspecciones</h2>
        <label htmlFor="filtro">Filtrar por espacio</label>
        <select id="filtro" value={filtro} onChange={(e) => setFiltro(e.target.value)}>
          <option value="">Todos</option>
          {espacios.map((x) => <option key={x.id} value={x.id}>{x.nombre}</option>)}
        </select>
      </div>

      {lista === null && <p className="suave">Cargando…</p>}
      {lista?.length === 0 && <p className="suave">No hay inspecciones todavía.</p>}

      <ul className="lista">
        {lista?.map((i) => (
          <li key={i.id}>
            <Link to={`/inspecciones/${i.id}`} className="tarjeta enlace">
              <div className="puesto-cab">
                <strong>{i.espacioNombre} · {TIPO_INSPECCION[i.tipo]}</strong>
                <span className={`insignia ${i.estado === 'CERRADA' ? 'insignia-ok' : 'insignia-alerta'}`}>
                  {i.estado === 'CERRADA' ? 'Cerrada' : 'En borrador'}
                </span>
              </div>
              <p className="suave">
                {fechaCorta(i.fecha)} · {i.puestosRegistrados}/{i.puestosTotales} puestos · {i.inspectorNombre}
              </p>
            </Link>
          </li>
        ))}
      </ul>
    </section>
  );
}