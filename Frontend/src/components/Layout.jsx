import { NavLink, Outlet } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const MENU = [
  { ruta: '/inspecciones', texto: 'Inspección', roles: ['ADMIN', 'TECNICO'] },
  { ruta: '/comparacion', texto: 'Comparar', roles: ['ADMIN', 'TECNICO', 'REPRESENTANTE'] },
  { ruta: '/reportes', texto: 'Daños', roles: ['ADMIN', 'TECNICO', 'REPRESENTANTE'] },
  { ruta: '/admin', texto: 'Admin', roles: ['ADMIN'] },
  { ruta: '/auditoria', texto: 'Auditoría', roles: ['ADMIN'] },
];

const ETIQUETA_ROL = { ADMIN: 'Administrador', TECNICO: 'Técnico', REPRESENTANTE: 'Representante' };

export default function Layout() {
  const { usuario, rol, logout } = useAuth();

  return (
    <div className="app">
      <header className="encabezado">
        <div className="marca">
          <img src="/logos/prexxa.png" alt="Prexxa" />
          <strong>Auditoría de espacios</strong>
        </div>
        <div className="usuario">
          <span>{usuario.nombre} · {ETIQUETA_ROL[rol]}</span>
          <button className="btn btn-secundario" onClick={logout}>Salir</button>
        </div>
      </header>

      <nav className="menu">
        {MENU.filter((m) => m.roles.includes(rol)).map((m) => (
          <NavLink key={m.ruta} to={m.ruta} className={({ isActive }) => (isActive ? 'activo' : '')}>
            {m.texto}
          </NavLink>
        ))}
      </nav>

      <main className="contenido">
        <Outlet />
      </main>
    </div>
  );
}