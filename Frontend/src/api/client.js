const BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';
const CLAVE = 'auditoria.sesion';

export class ApiError extends Error {
  constructor(status, mensaje, errores = null) {
    super(mensaje);
    this.status = status;
    this.errores = errores; // errores por campo (validaciones 400)
  }
}

// --- sesión (sessionStorage: se borra al cerrar la pestaña) ---
export function leerSesion() {
  try { return JSON.parse(sessionStorage.getItem(CLAVE)); } catch { return null; }
}
export const guardarSesion = (s) => sessionStorage.setItem(CLAVE, JSON.stringify(s));
export const borrarSesion = () => sessionStorage.removeItem(CLAVE);

let alExpirarSesion = () => {};
export const setAlExpirarSesion = (fn) => { alExpirarSesion = fn; };

// --- petición base ---
async function enviar(ruta, { method = 'GET', body, formData, params } = {}) {
  const sesion = leerSesion();
  const headers = {};
  if (sesion?.token) headers.Authorization = `Bearer ${sesion.token}`;

  let cuerpo;
  if (formData) {
    cuerpo = formData; // el navegador pone el Content-Type con su boundary
  } else if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
    cuerpo = JSON.stringify(body);
  }

  const query = params
    ? '?' + new URLSearchParams(
        Object.entries(params).filter(([, v]) => v !== undefined && v !== null && v !== '')
      )
    : '';

  let resp;
  try {
    resp = await fetch(`${BASE}${ruta}${query}`, { method, headers, body: cuerpo });
  } catch {
    throw new ApiError(0, 'No se pudo conectar con el servidor');
  }

  if (resp.status === 401 && sesion) alExpirarSesion();
  if (!resp.ok) throw await errorDe(resp);
  if (resp.status === 204) return null;
  return resp;
}

async function errorDe(resp) {
  let datos = null;
  try { datos = await resp.json(); } catch { /* respuesta sin cuerpo */ }
  if (datos?.detail) return new ApiError(resp.status, datos.detail, datos.errores ?? null);
  if (resp.status === 401) return new ApiError(401, 'Tu sesión no es válida o expiró');
  if (resp.status === 403) return new ApiError(403, 'No tienes permiso para esta acción');
  return new ApiError(resp.status, 'Ocurrió un error inesperado');
}

const json = async (p) => { const r = await p; return r ? r.json() : null; };

export const api = {
  get: (ruta, params) => json(enviar(ruta, { params })),
  post: (ruta, body) => json(enviar(ruta, { method: 'POST', body })),
  put: (ruta, body) => json(enviar(ruta, { method: 'PUT', body })),
  patch: (ruta, body) => json(enviar(ruta, { method: 'PATCH', body })),
  del: (ruta) => json(enviar(ruta, { method: 'DELETE' })),
  subir: (ruta, formData) => json(enviar(ruta, { method: 'POST', formData })),
  // Las fotos piden token, así que no se pueden usar con <img src>.
  // Esto descarga la imagen y devuelve una URL temporal para mostrarla.
  async imagen(ruta) {
    const r = await enviar(ruta);
    return URL.createObjectURL(await r.blob());
  },
};