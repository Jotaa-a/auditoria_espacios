import { Navigate, useLocation } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function RutaProtegida({ roles, children }) {
  const { autenticado, rol } = useAuth();
  const ubicacion = useLocation();

  if (!autenticado)
    return <Navigate to="/login" replace state={{ desde: ubicacion }} />;
  if (roles && !roles.includes(rol))
    return <Navigate to="/" replace />;
  return children;
}