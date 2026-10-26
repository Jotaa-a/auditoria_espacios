import { useEffect, useRef, useState } from 'react';
import { api } from '../api/client';
import { CATEGORIA, GRAVEDAD, TIPO_COMPONENTE, claveHallazgo, reducirImagen } from '../utils/formato';
import FotosHallazgo from './FotosHallazgo';

const VACIO = { tipoId: '', componenteId: '', gravedad: '', descripcion: '' };

function Previa({ archivo, onQuitar }) {
  const [url] = useState(() => URL.createObjectURL(archivo));
  useEffect(() => () => URL.revokeObjectURL(url), [url]);
  return (
    <span className="previa">
      <img className="foto" src={url} alt="Foto nueva, aún sin subir" />
      <button type="button" className="btn btn-secundario" onClick={onQuitar}>Quitar</button>
    </span>
  );
}

export default function EditorPuesto({
  puesto, inspeccionId, detalle, tipos, componentes, onCerrar, onGuardado,
}) {
  const dialogo = useRef(null);
  const [lista, setLista] = useState(() =>
    (detalle?.hallazgos ?? []).map((h) => ({
      clave: claveHallazgo(h.tipoHallazgoId, h.componenteId),
      tipoId: h.tipoHallazgoId,
      componenteId: h.componenteId,
      gravedad: h.gravedad,
      descripcion: h.descripcion ?? '',
      hallazgoId: h.id,
      fotos: [],
    })));
  const [form, setForm] = useState(VACIO);
  const [errorForm, setErrorForm] = useState('');
  const [error, setError] = useState('');
  const [guardando, setGuardando] = useState(false);

  useEffect(() => {
    const d = dialogo.current;
    if (d && !d.open) d.showModal();
  }, []);

  const tipo = tipos.find((t) => String(t.id) === String(form.tipoId));
  const esDano = tipo?.categoria === 'DANO_FISICO';

  const porCategoria = tipos.reduce((acc, t) => {
    (acc[t.categoria] ??= []).push(t);
    return acc;
  }, {});

  const nombreTipo = (id) => tipos.find((t) => t.id === id)?.nombre ?? 'Hallazgo';
  const nombreComp = (id) => {
    const c = componentes.find((x) => x.id === id);
    return c ? `${TIPO_COMPONENTE[c.tipo]}${c.serial ? ` · ${c.serial}` : ''}` : null;
  };

  function agregar() {
    setErrorForm('');
    if (!tipo) return setErrorForm('Elige el tipo de hallazgo');
    if (esDano && !form.componenteId) return setErrorForm('Indica el componente dañado');
    if (esDano && !form.gravedad) return setErrorForm('Indica la gravedad del daño');
    if (tipo.codigo === 'OTRO' && !form.descripcion.trim()) return setErrorForm('Describe el hallazgo');

    const componenteId = form.componenteId ? Number(form.componenteId) : null;
    const clave = claveHallazgo(tipo.id, componenteId);
    if (lista.some((h) => h.clave === clave)) return setErrorForm('Ese hallazgo ya está en la lista');

    setLista([...lista, {
      clave, tipoId: tipo.id, componenteId,
      gravedad: esDano ? form.gravedad : null,
      descripcion: form.descripcion.trim(),
      hallazgoId: null, fotos: [],
    }]);
    setForm(VACIO);
  }

  const quitar = (clave) => setLista(lista.filter((h) => h.clave !== clave));

  const agregarFoto = (clave, archivo) => {
    if (!archivo) return;
    setLista((l) => l.map((h) => (h.clave === clave ? { ...h, fotos: [...h.fotos, archivo] } : h)));
  };

  const quitarFoto = (clave, indice) =>
    setLista((l) => l.map((h) =>
      (h.clave === clave ? { ...h, fotos: h.fotos.filter((_, i) => i !== indice) } : h)));

  async function guardar() {
    if (!lista.length) return setError('Agrega al menos un hallazgo, o cierra y marca el puesto como OK');
    setError('');
    setGuardando(true);
    try {
      const guardado = await api.put(`/api/inspecciones/${inspeccionId}/puestos/${puesto.id}`, {
        resultado: 'CON_NOVEDADES',
        hallazgos: lista.map((h) => ({
          tipoHallazgoId: h.tipoId,
          componenteId: h.componenteId,
          descripcion: h.descripcion || null,
          gravedad: h.gravedad,
        })),
      });

      const porClave = new Map(
        guardado.hallazgos.map((h) => [claveHallazgo(h.tipoHallazgoId, h.componenteId), h]));

      let fallidas = 0;
      for (const h of lista) {
        const destino = porClave.get(h.clave);
        for (const foto of h.fotos) {
          try {
            const datos = new FormData();
            datos.append('archivo', await reducirImagen(foto));
            await api.subir(`/api/hallazgos/${destino.id}/evidencias`, datos);
          } catch {
            fallidas += 1;
          }
        }
      }
      onGuardado(fallidas
        ? `El puesto ${puesto.codigo} se guardó, pero ${fallidas} foto(s) no se pudieron subir. Ábrelo de nuevo para reintentar.`
        : '');
    } catch (e) {
      setError(e.message);
      setGuardando(false);
    }
  }

  return (
    <dialog ref={dialogo} className="modal" aria-labelledby="titulo-editor"
            onCancel={(e) => { e.preventDefault(); if (!guardando) onCerrar(); }}>
      <h2 id="titulo-editor">Puesto {puesto.codigo}: novedades</h2>

      {lista.length === 0 && <p className="suave">Aún no hay hallazgos. Agrega el primero abajo.</p>}

      {lista.map((h) => (
        <div className="item-hallazgo" key={h.clave}>
          <strong>{nombreTipo(h.tipoId)}</strong>
          <p className="suave">
            {[nombreComp(h.componenteId), h.gravedad && `Gravedad: ${GRAVEDAD[h.gravedad]}`, h.descripcion]
              .filter(Boolean).join(' · ') || 'Todo el puesto'}
          </p>

          {h.hallazgoId && <FotosHallazgo hallazgoId={h.hallazgoId} />}
          <div className="fotos">
            {h.fotos.map((f, i) => (
              <Previa key={`${f.name}-${f.size}-${i}`} archivo={f} onQuitar={() => quitarFoto(h.clave, i)} />
            ))}
          </div>

          <div className="fila">
            <label className="btn btn-secundario btn-foto">
              Agregar foto
              <input type="file" accept="image/*" capture="environment" hidden
                     onChange={(e) => { agregarFoto(h.clave, e.target.files[0]); e.target.value = ''; }} />
            </label>
            <button type="button" className="btn btn-secundario" onClick={() => quitar(h.clave)}>
              Quitar hallazgo
            </button>
          </div>
        </div>
      ))}

      <fieldset className="nuevo-hallazgo">
        <legend>Agregar hallazgo</legend>

        <label htmlFor="tipo">Qué pasó</label>
        <select id="tipo" value={form.tipoId} onChange={(e) => setForm({ ...form, tipoId: e.target.value })}>
          <option value="">Elige…</option>
          {Object.entries(porCategoria).map(([cat, items]) => (
            <optgroup key={cat} label={CATEGORIA[cat] ?? cat}>
              {items.map((t) => <option key={t.id} value={t.id}>{t.nombre}</option>)}
            </optgroup>
          ))}
        </select>

        <label htmlFor="comp">Componente {esDano ? '(obligatorio)' : '(opcional)'}</label>
        <select id="comp" value={form.componenteId}
                onChange={(e) => setForm({ ...form, componenteId: e.target.value })}>
          <option value="">Todo el puesto / ninguno</option>
          {componentes.map((c) => (
            <option key={c.id} value={c.id}>
              {TIPO_COMPONENTE[c.tipo]}{c.serial ? ` · ${c.serial}` : ''}
            </option>
          ))}
        </select>

        {esDano && (
          <>
            <label htmlFor="grav">Gravedad</label>
            <select id="grav" value={form.gravedad}
                    onChange={(e) => setForm({ ...form, gravedad: e.target.value })}>
              <option value="">Elige…</option>
              {Object.entries(GRAVEDAD).map(([v, t]) => <option key={v} value={v}>{t}</option>)}
            </select>
          </>
        )}

        <label htmlFor="desc">Descripción {tipo?.codigo === 'OTRO' ? '(obligatoria)' : '(opcional)'}</label>
        <textarea id="desc" rows={2} maxLength={500} value={form.descripcion}
                  onChange={(e) => setForm({ ...form, descripcion: e.target.value })} />

        {errorForm && <p className="error" role="alert">{errorForm}</p>}
        <button type="button" className="btn btn-secundario agregar" onClick={agregar}>
          Agregar a la lista
        </button>
      </fieldset>

      {error && <p className="error" role="alert">{error}</p>}

      <div className="fila pie-modal">
        <button type="button" className="btn btn-secundario" onClick={onCerrar} disabled={guardando}>
          Cancelar
        </button>
        <button type="button" className="btn btn-primario" onClick={guardar} disabled={guardando}>
          {guardando ? 'Guardando…' : 'Guardar puesto'}
        </button>
      </div>
    </dialog>
  );
}