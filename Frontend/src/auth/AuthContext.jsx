import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { api, borrarSesion, guardarSesion, leerSesion, setAlExpirarSesion } from '../api/client';

const AuthContext = createContext(null);

function sesionVigente() {
  const s = leerSesion();
  if (!s) return null;
  if (new Date(s.expiraEn) <= new Date()) { borrarSesion(); return null; }
  return s;
}

export function AuthProvider({ children }) {
  const [sesion, setSesion] = useState(sesionVigente);

  const logout = useCallback(() => { borrarSesion(); setSesion(null); }, []);

  // si cualquier petición recibe 401, se cierra la sesión
  useEffect(() => { setAlExpirarSesion(logout); }, [logout]);

  // cierre automático cuando el token expira
  useEffect(() => {
    if (!sesion) return;
    const ms = new Date(sesion.expiraEn) - Date.now();
    const t = setTimeout(logout, Math.max(ms, 0));
    return () => clearTimeout(t);
  }, [sesion, logout]);

  const login = useCallback(async (correo, password) => {
    const r = await api.post('/api/auth/login', { correo, password });
    const nueva = { token: r.token, expiraEn: r.expiraEn, usuario: r.usuario };
    guardarSesion(nueva);
    setSesion(nueva);
  }, []);

  const valor = useMemo(() => ({
    usuario: sesion?.usuario ?? null,
    rol: sesion?.usuario?.rol ?? null,
    autenticado: !!sesion,
    login,
    logout,
  }), [sesion, login, logout]);

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);