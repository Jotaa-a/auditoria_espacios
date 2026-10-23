import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import RutaProtegida from './auth/RutaProtegida';
import Layout from './components/Layout';
import Pendiente from './components/Pendiente';
import Login from './pages/Login';

function Inicio() {
  const { rol } = useAuth();
  return <Navigate to={rol === 'REPRESENTANTE' ? '/reportes' : '/inspecciones'} replace />;
}

const TODOS = ['ADMIN', 'TECNICO', 'REPRESENTANTE'];

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />

      <Route element={<RutaProtegida><Layout /></RutaProtegida>}>
        <Route path="/" element={<Inicio />} />
        <Route path="/inspecciones" element={
          <RutaProtegida roles={['ADMIN', 'TECNICO']}><Pendiente titulo="Inspecciones" /></RutaProtegida>} />
        <Route path="/comparacion" element={
          <RutaProtegida roles={TODOS}><Pendiente titulo="Comparación viernes vs. lunes" /></RutaProtegida>} />
        <Route path="/reportes" element={
          <RutaProtegida roles={TODOS}><Pendiente titulo="Reportes de daño" /></RutaProtegida>} />
        <Route path="/admin" element={
          <RutaProtegida roles={['ADMIN']}><Pendiente titulo="Administración" /></RutaProtegida>} />
        <Route path="/auditoria" element={
          <RutaProtegida roles={['ADMIN']}><Pendiente titulo="Auditoría" /></RutaProtegida>} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}