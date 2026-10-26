import { useEffect, useState } from 'react';
import { api } from '../api/client';

export function Foto({ ruta, alt }) {
  const [src, setSrc] = useState(null);
  const [fallo, setFallo] = useState(false);

  useEffect(() => {
    let activo = true;
    let url = null;
    api.imagen(ruta)
      .then((u) => { url = u; if (activo) setSrc(u); else URL.revokeObjectURL(u); })
      .catch(() => { if (activo) setFallo(true); });
    return () => { activo = false; if (url) URL.revokeObjectURL(url); };
  }, [ruta]);

  if (fallo) return <span className="foto foto-vacia">No se pudo cargar</span>;
  return src
    ? <img className="foto" src={src} alt={alt} />
    : <span className="foto foto-vacia">Cargando…</span>;
}

export default function FotosHallazgo({ hallazgoId }) {
  const [fotos, setFotos] = useState(null);

  useEffect(() => {
    api.get(`/api/hallazgos/${hallazgoId}/evidencias`).then(setFotos).catch(() => setFotos([]));
  }, [hallazgoId]);

  if (!fotos) return <p className="suave">Cargando fotos…</p>;
  if (!fotos.length) return <p className="suave">Sin fotos.</p>;
  return (
    <div className="fotos">
      {fotos.map((f, i) => <Foto key={f.id} ruta={f.url} alt={`Foto ${i + 1} del hallazgo`} />)}
    </div>
  );
}

// Botón que carga las fotos solo cuando se piden
export function VerFotos({ hallazgos }) {
  const [abierto, setAbierto] = useState(false);
  return (
    <div>
      <button type="button" className="btn btn-enlace" aria-expanded={abierto}
              onClick={() => setAbierto(!abierto)}>
        {abierto ? 'Ocultar fotos' : 'Ver fotos'}
      </button>
      {abierto && hallazgos.map((h) => (
        <div key={h.id}>
          <p className="suave">{h.tipoNombre}</p>
          <FotosHallazgo hallazgoId={h.id} />
        </div>
      ))}
    </div>
  );
}