import { useEffect, useMemo, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { api } from '../api/client';
import { VerFotos } from '../components/FotosHallazgo';
import { ESTADO_COMPARACION, RESULTADO, fechaCorta, fechaLarga, textoHallazgo } from '../utils/formato';

function Grupo({ titulo, nota, hallazgos }) {
  if (!hallazgos.length) return null;
  return (
    <div className="grupo">
      <h3>{titulo} ({hallazgos.length})</h3>
      {nota && <p className="suave">{nota}</p>}
      <ul className="chips">
        {hallazgos.map((h) => <li key={h.id}>{textoHallazgo(h)}</li>)}
      </ul>
      <VerFotos hallazgos={hallazgos} />
    </div>
  );
}

export default function Comparacion() {
  const [params, setParams] = useSearchParams();
  const lunesId = params.get('lunes') ?? '';

  const [espacios, setEspacios] = useState([]);
  const [filtro, setFiltro] = useState('');
  const [opciones, setOpciones] = useState(null);
  const [respuesta, setRespuesta] = useState(null); // { id, datos } o { id, error }
  const [errorLista, setErrorLista] = useState('');
  const [soloCambios, setSoloCambios] = useState(true);

  useEffect(() => {
    api.get('/api/espacios').then(setEspacios).catch((e) => setErrorLista(e.message));
  }, []);

  // inspecciones de lunes ya cerradas (las únicas comparables)
  useEffect(() => {
    api.get('/api/inspecciones', { espacioId: filtro })
      .then((lista) => setOpciones(
        lista.filter((i) => i.tipo === 'LUNES_RECEPCION' && i.estado === 'CERRADA')))
      .catch((e) => setErrorLista(e.message));
  }, [filtro]);

  useEffect(() => {
    if (!lunesId) return;
    let activo = true;
    api.get(`/api/inspecciones/${lunesId}/comparacion`)
      .then((datos) => { if (activo) setRespuesta({ id: lunesId, datos }); })
      .catch((e) => { if (activo) setRespuesta({ id: lunesId, error: e.message }); });
    return () => { activo = false; };
  }, [lunesId]);

  const actual = respuesta?.id === lunesId ? respuesta : null;
  const cargando = !!lunesId && !actual;
  const datos = actual?.datos;

  const puestos = useMemo(() => {
    if (!datos) return [];
    return datos.puestos
      .filter((p) => !soloCambios || p.estado !== 'SIN_CAMBIOS')
      .sort((a, b) =>
        ESTADO_COMPARACION[a.estado].orden - ESTADO_COMPARACION[b.estado].orden
        || a.puestoCodigo.localeCompare(b.puestoCodigo, 'es', { numeric: true }));
  }, [datos, soloCambios]);

  const elegir = (valor) => setParams(valor ? { lunes: valor } : {});

  return (
    <section className="pila">
      <div className="tarjeta">
        <h1>Comparación viernes vs. lunes</h1>

        <label htmlFor="espacio">Espacio</label>
        <select id="espacio" value={filtro} onChange={(e) => setFiltro(e.target.value)}>
          <option value="">Todos</option>
          {espacios.map((x) => <option key={x.id} value={x.id}>{x.nombre}</option>)}
        </select>

        <label htmlFor="lunes">Inspección de lunes</label>
        <select id="lunes" value={lunesId} onChange={(e) => elegir(e.target.value)}>
          <option value="">Elige una…</option>
          {opciones?.map((i) => (
            <option key={i.id} value={i.id}>{i.espacioNombre} · {fechaCorta(i.fecha)}</option>
          ))}
        </select>

        {opciones?.length === 0 && (
          <p className="suave">Todavía no hay inspecciones de lunes cerradas para comparar.</p>
        )}
        {errorLista && <p className="error" role="alert">{errorLista}</p>}
      </div>

      {cargando && <p className="suave">Comparando…</p>}
      {actual?.error && <p className="error" role="alert">{actual.error}</p>}

      {datos && (
        <>
          <div className="tarjeta">
            <h2>{datos.espacioNombre}</h2>
            <p className="suave">
              Viernes: {fechaLarga(datos.viernes.fecha)} · {datos.viernes.inspectorNombre}
            </p>
            <p className="suave">
              Lunes: {fechaLarga(datos.lunes.fecha)} · {datos.lunes.inspectorNombre}
            </p>

            <div className="resumen">
              <div className="dato"><strong>{datos.resumen.nuevasNovedades}</strong>Nuevas novedades</div>
              <div className="dato"><strong>{datos.resumen.persisten}</strong>Persisten</div>
              <div className="dato"><strong>{datos.resumen.resueltos}</strong>Resueltos</div>
              <div className="dato"><strong>{datos.resumen.sinCambios}</strong>Sin cambios</div>
              {datos.resumen.sinRegistro > 0 && (
                <div className="dato"><strong>{datos.resumen.sinRegistro}</strong>Sin registro</div>
              )}
            </div>

            <label className="check">
              <input type="checkbox" checked={soloCambios}
                     onChange={(e) => setSoloCambios(e.target.checked)} />
              Ocultar los puestos sin cambios
            </label>
          </div>

          {puestos.length === 0 && (
            <p className="suave">
              No hay diferencias: todos los puestos quedaron igual que el viernes.
            </p>
          )}

          <ul className="lista">
            {puestos.map((p) => {
              const meta = ESTADO_COMPARACION[p.estado];
              return (
                <li key={p.puestoId} className="tarjeta">
                  <div className="puesto-cab">
                    <strong>{p.puestoCodigo}</strong>
                    <span className={`insignia ${meta.clase}`}>{meta.texto}</span>
                  </div>
                  <p className="suave">
                    Viernes: {RESULTADO[p.resultadoViernes] ?? 'sin registro'} →
                    {' '}Lunes: {RESULTADO[p.resultadoLunes] ?? 'sin registro'}
                  </p>

                  <Grupo titulo="Nuevos desde el viernes" hallazgos={p.hallazgosNuevos} />
                  <Grupo titulo="Siguen igual que el viernes" hallazgos={p.hallazgosPersistentes} />
                  <Grupo titulo="Estaban el viernes y ya no aparecen"
                         nota="Las fotos corresponden a la inspección del viernes."
                         hallazgos={p.hallazgosResueltos} />
                </li>
              );
            })}
          </ul>
        </>
      )}
    </section>
  );
}