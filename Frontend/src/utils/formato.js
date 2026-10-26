export const TIPO_INSPECCION = {
  VIERNES_ENTREGA: 'Viernes · entrega',
  LUNES_RECEPCION: 'Lunes · recepción',
};

export const TIPO_COMPONENTE = {
  TORRE: 'Torre', PANTALLA: 'Pantalla', TECLADO: 'Teclado', MOUSE: 'Mouse',
  CABLE_PODER: 'Cable de poder', CABLE_VIDEO: 'Cable de video', CABLE_USB: 'Cable USB', OTRO: 'Otro',
};

export const GRAVEDAD = { LEVE: 'Leve', MODERADO: 'Moderado', GRAVE: 'Grave' };

export const CATEGORIA = {
  LIMPIEZA: 'Limpieza', ORDEN: 'Orden', PERIFERICO: 'Periféricos', DANO_FISICO: 'Daño físico',
};

// fecha local en formato YYYY-MM-DD
export const hoy = () => new Date().toLocaleDateString('en-CA');

export const fechaCorta = (iso) =>
  new Date(`${iso}T00:00:00`).toLocaleDateString('es-CO', { weekday: 'short', day: 'numeric', month: 'short' });

export const fechaLarga = (iso) =>
  new Date(`${iso}T00:00:00`).toLocaleDateString('es-CO',
    { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });

export const claveHallazgo = (tipoId, componenteId) => `${tipoId}:${componenteId ?? 0}`;

// Reduce la foto a máx. 1600 px por lado y la convierte a JPEG
export async function reducirImagen(archivo, maxLado = 1600, calidad = 0.82) {
  try {
    const bmp = await createImageBitmap(archivo, { imageOrientation: 'from-image' });
    const escala = Math.min(1, maxLado / Math.max(bmp.width, bmp.height));
    const canvas = document.createElement('canvas');
    canvas.width = Math.round(bmp.width * escala);
    canvas.height = Math.round(bmp.height * escala);
    canvas.getContext('2d').drawImage(bmp, 0, 0, canvas.width, canvas.height);
    bmp.close?.();
    const blob = await new Promise((ok) => canvas.toBlob(ok, 'image/jpeg', calidad));
    return blob ? new File([blob], 'foto.jpg', { type: 'image/jpeg' }) : archivo;
  } catch {
    return archivo;
  }
}

export const RESULTADO = { OK: 'OK', CON_NOVEDADES: 'Con novedades' };

// 'orden' define qué puestos aparecen primero: lo más urgente arriba
export const ESTADO_COMPARACION = {
  NUEVA_NOVEDAD: { texto: 'Nueva novedad', clase: 'insignia-peligro', orden: 0 },
  PERSISTE: { texto: 'Persiste', clase: 'insignia-alerta', orden: 1 },
  SIN_REGISTRO: { texto: 'Sin registro', clase: 'insignia-gris', orden: 2 },
  RESUELTO: { texto: 'Resuelto', clase: 'insignia-ok', orden: 3 },
  SIN_CAMBIOS: { texto: 'Sin cambios', clase: 'insignia-gris', orden: 4 },
};

export const textoHallazgo = (h) =>
  [h.tipoNombre, h.componenteTipo && TIPO_COMPONENTE[h.componenteTipo], h.gravedad && GRAVEDAD[h.gravedad]]
    .filter(Boolean).join(' · ');