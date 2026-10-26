import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { api } from '../api/client';
import EditorPuesto from '../components/EditorPuesto';
import { VerFotos } from '../components/FotosHallazgo';
import { GRAVEDAD, TIPO_COMPONENTE, TIPO_INSPECCION, fechaLarga } from '../utils/formato';

export default function InspeccionDetalle() {
  const { id } = useParams();
  const [insp, setInsp] = useState(null);
  const [puestos, setPuestos] = useState([]);
  const [componentes, setComponentes] = useState([]);
  const [tipos, setTipos] = useState([]);
  const [editando, setEditando] = useState(null);
  const [ocupado, setOcupado] = useState(null);
  const [error, setError] = useState('');
  const [aviso, setAviso] = useState('');

  const refrescar = useCallback(async () => {
    setInsp(await api.get(`/api/inspecciones/${id}`));
  }, [id]);

  useEffect(() => {
    let activo = true;
    (async () => {
      try {
        const i = await api.get(`/api/inspecciones/${id}`);
        const [ps, cs, ts] = await Promise.all([
          api.get('/api/puestos', { espacioId: i.espacioId }),
          api.get('/api/componentes'),
          api.get('/api/tipos-hallazgo'),
        ]);
        if (!activo) return;
        setInsp(i);
        setPuestos(ps.filter((p) => p.activo)
          .sort((a, b) => a.codigo.localeCompare(b.codigo, 'es', { numeric: true })));
        setComponentes(cs);
        setTipos(ts);
      } catch (e) {
        if (activo) setError(e.message);
      }
    })();
    return () => { activo = false; };
  }, [id]);

  const porPuesto = useMemo(
    () => new Map((insp?.detalles ?? []).map((d) => [d.puestoId, d])), [insp]);

  async function marcarOk(puesto) {
    const previo = porPuesto.get(puesto.id);
    if (previo?.hallazgos?.length &&
        !window.confirm('Este puesto tiene hallazgos registrados. ¿Marcarlo como OK y borrarlos?')) return;
    setOcupado(puesto.id);
    setError('');
    try {
      await api.put(`/api/inspecciones/${id}/puestos/${puesto.id}`, { resultado: 'OK' });
      await refrescar();
    } catch (e) {
      setError(e.message);
    } finally {
      setOcupado(null);
    }
  }

  async function cerrar() {
    if (!window.confirm(
      'Al cerrar la inspección ya no podrás modificarla, y los daños físicos se enviarán como reportes. ¿Cerrarla?')) return;
    setOcupado('cerrar');
    setError('');
    try {
      await api.post(`/api/inspecciones/${id}/cerrar`);
      await refrescar();
    } catch (e) {
      setError(e.message);
    } finally {
      setOcupado(null);
    }
  }

  if (!insp) {
    return error
      ? <p className="error" role="alert">{error}</p>
      : <p className="suave">Cargando inspección…</p>;
  }

  const abierta = insp.estado === 'BORRADOR';
  const faltan = insp.puestosTotales - insp.puestosRegistrados;

  return (
    <section className="pila">
      <Link to="/inspecciones">← Inspecciones</Link>

      <div className="tarjeta">
        <h1>{insp.espacioNombre} · {TIPO_INSPECCION[insp.tipo]}</h1>
        <p className="suave">{fechaLarga(insp.fecha)} · Inspector: {insp.inspectorNombre}</p>
        <p>
          <span className={`insignia ${abierta ? 'insignia-alerta' : 'insignia-ok'}`}>
            {abierta ? 'En borrador' : 'Cerrada'}
          </span>
        </p>
        <label htmlFor="avance">Avance: {insp.puestosRegistrados} de {insp.puestosTotales} puestos</label>
        <progress id="avance" max={insp.puestosTotales} value={insp.puestosRegistrados} />
      </div>

      {aviso && <p className="aviso" role="status">{aviso}</p>}
      {error && <p className="error" role="alert">{error}</p>}

      <ul className="lista">
        {puestos.map((p) => {
          const d = porPuesto.get(p.id);
          const conNovedades = d?.resultado === 'CON_NOVEDADES';
          return (
            <li key={p.id} className="tarjeta">
              <div className="puesto-cab">
                <strong>{p.codigo}</strong>
                <span className={`insignia ${!d ? 'insignia-gris' : conNovedades ? 'insignia-alerta' : 'insignia-ok'}`}>
                  {!d ? 'Pendiente' : conNovedades ? `Con novedades (${d.hallazgos.length})` : 'OK'}
                </span>
              </div>

              {conNovedades && (
                <>
                  <ul className="chips">
                    {d.hallazgos.map((h) => (
                      <li key={h.id}>
                        {h.tipoNombre}
                        {h.componenteTipo && ` · ${TIPO_COMPONENTE[h.componenteTipo]}`}
                        {h.gravedad && ` · ${GRAVEDAD[h.gravedad]}`}
                      </li>
                    ))}
                  </ul>
                  <VerFotos hallazgos={d.hallazgos} />
                </>
              )}

              {abierta && (
                <div className="acciones">
                  <button type="button" className="btn btn-ok" disabled={ocupado === p.id}
                          onClick={() => marcarOk(p)}>OK</button>
                  <button type="button" className="btn btn-novedad" disabled={ocupado === p.id}
                          onClick={() => setEditando(p)}>
                    {conNovedades ? 'Editar novedades' : 'Con novedades'}
                  </button>
                </div>
              )}
            </li>
          );
        })}
      </ul>

      {abierta ? (
        <div className="tarjeta">
          {faltan > 0 && <p className="suave">Faltan {faltan} puesto(s) por registrar para poder cerrar.</p>}
          <button type="button" className="btn btn-primario" disabled={faltan > 0 || ocupado === 'cerrar'}
                  onClick={cerrar}>
            {ocupado === 'cerrar' ? 'Cerrando…' : 'Cerrar inspección'}
          </button>
        </div>
        ) : (
        <div className="tarjeta">
          <p className="suave">Esta inspección está cerrada y no se puede modificar.</p>
          {insp.tipo === 'LUNES_RECEPCION' && (
            <Link to={`/comparacion?lunes=${insp.id}`}>Ver comparación con el viernes</Link>
          )}
        </div>
      )}

      {editando && (
        <EditorPuesto
          key={editando.id}
          puesto={editando}
          inspeccionId={id}
          detalle={porPuesto.get(editando.id)}
          tipos={tipos}
          componentes={componentes.filter((c) => c.puestoId === editando.id)}
          onCerrar={() => setEditando(null)}
          onGuardado={async (mensaje) => {
            setEditando(null);
            setAviso(mensaje);
            try { await refrescar(); } catch (e) { setError(e.message); }
          }}
        />
      )}
    </section>
  );
}