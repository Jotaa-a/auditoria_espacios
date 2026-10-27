import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './auth/AuthContext';
import RutaProtegida from './auth/RutaProtegida';
import Layout from './components/Layout';
import Pendiente from './components/Pendiente';
import Login from './pages/Login';
import Inspecciones from './pages/Inspecciones';
import InspeccionDetalle from './pages/InspeccionDetalle';
import Comparacion from './pages/Comparacion';
import ReporteDanos from './pages/ReporteDanos';
import Admin from './pages/Admin';
import Auditoria from './pages/Auditoria';


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
          <RutaProtegida roles={['ADMIN', 'TECNICO']}><Inspecciones /></RutaProtegida>} />

        <Route path="/inspecciones/:id" element={
          <RutaProtegida roles={['ADMIN', 'TECNICO']}><InspeccionDetalle /></RutaProtegida>} />

        <Route path="/comparacion" element={
          <RutaProtegida roles={TODOS}><Comparacion /></RutaProtegida>} />

        <Route path="/reportes" element={
          <RutaProtegida roles={TODOS}> <ReporteDanos /></RutaProtegida>} />

        <Route path="/admin" element={
          <RutaProtegida roles={['ADMIN']}><Admin /></RutaProtegida>} />

        <Route path="/auditoria" element={
          <RutaProtegida roles={['ADMIN']}><Auditoria /></RutaProtegida>} />
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}