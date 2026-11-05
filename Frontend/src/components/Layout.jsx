import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

const MENU = [
  { ruta: '/inspecciones', texto: 'Inspecciones', roles: ['ADMIN', 'TECNICO'], icono: '📋' },
  { ruta: '/comparacion', texto: 'Comparativa', roles: ['ADMIN', 'TECNICO', 'REPRESENTANTE'], icono: '📊' },
  { ruta: '/reportes', texto: 'Daños', roles: ['ADMIN', 'TECNICO', 'REPRESENTANTE'], icono: '⚠️' },
  { ruta: '/admin', texto: 'Administración', roles: ['ADMIN'], icono: '⚙️' },
  { ruta: '/auditoria', texto: 'Auditoría', roles: ['ADMIN'], icono: '🕒' },
];

const ETIQUETA_ROL = { 
  ADMIN: 'Administrador', 
  TECNICO: 'Técnico', 
  REPRESENTANTE: 'Representante' 
};

export default function Layout() {
  const { usuario, rol, logout } = useAuth();
  const location = useLocation();

  const menuFiltrado = MENU.filter((m) => m.roles.includes(rol));

  return (
      <div style={{ display: 'flex', minHeight: '100vh', backgroundColor: '#f8fafc' }}>      
      {/* Sidebar Lateral */}
      <aside style={{ 
        width: '250px', 
        backgroundColor: '#ffffff', 
        borderRight: '1px solid #e2e8f0',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'space-between',
        padding: '1.5rem 1rem',
        boxSizing: 'border-box'
      }}>
        <div>
          {/* Marca / Header */}
          <div style={{ 
              display: 'flex', 
              alignItems: 'center', 
              justifyContent: 'center', // Centra todo el contenido dentro de la tarjeta
              gap: '1rem',              // Separa la imagen del texto para que no estén pegados
              padding: '0.85rem 1.1rem', // Da respiro en los cuatro bordes de la tarjeta
              backgroundColor: '#f8fafc',
              borderRadius: '12px',
              border: '1px solid #e2e8f0',
              marginBottom: '1.25rem'
            }}>
            <img 
              src="/logos/LogoAuditoriaEspacios.jpg" 
              alt="Prexxa" 
              style={{ 
                height: '42px', 
                width: 'auto', 
                objectFit: 'contain',
                mixBlendMode: 'multiply'
              }} 
            />
         <div>
              <h1 style={{ fontSize: '1rem', fontWeight: '700', color: '#0f172a', margin: 0, lineHeight: 1.2 }}>
                Auditoría
              </h1>
              <span style={{ fontSize: '0.75rem', color: '#64748b', whiteSpace: 'nowrap' }}>
                Gestión de Espacios
              </span>
            </div>
          </div>

          {/* Menú Dinámico filtrado por Rol */}
          <nav style={{ display: 'flex', flexDirection: 'column', gap: '0.35rem' }}>
            {menuFiltrado.map((m) => {
              const activo = location.pathname === m.ruta;
              return (
                <NavLink
                  key={m.ruta}
                  to={m.ruta}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.75rem',
                    padding: '0.65rem 0.85rem',
                    borderRadius: '8px',
                    textDecoration: 'none',
                    fontSize: '0.875rem',
                    fontWeight: activo ? '600' : '500',
                    color: activo ? '#2563eb' : '#475569',
                    backgroundColor: activo ? '#eff6ff' : 'transparent',
                    transition: 'all 0.15s ease'
                  }}
                >
                  <span style={{ fontSize: '1rem' }}>{m.icono}</span>
                  {m.texto}
                </NavLink>
              );
            })}
          </nav>
        </div>

        {/* Sección de Usuario y Sesión */}
        <div style={{ borderTop: '1px solid #f1f5f9', paddingTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
          <div>
            <div style={{ fontSize: '0.875rem', fontWeight: '600', color: '#1e293b', whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>
              {usuario?.nombre || 'Usuario'}
            </div>
            <span style={{ 
              display: 'inline-block', 
              marginTop: '0.25rem',
              padding: '0.15rem 0.5rem', 
              fontSize: '0.7rem', 
              fontWeight: '600', 
              color: '#3b82f6', 
              backgroundColor: '#eff6ff', 
              borderRadius: '9999px' 
            }}>
              {ETIQUETA_ROL[rol] || rol}
            </span>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
            <NavLink 
              to="/mi-password" 
              style={({ isActive }) => ({
                fontSize: '0.8rem',
                color: isActive ? '#2563eb' : '#64748b',
                textDecoration: 'none',
                padding: '0.4rem 0.5rem',
                borderRadius: '6px',
                fontWeight: isActive ? '600' : '500',
                backgroundColor: isActive ? '#f1f5f9' : 'transparent'
              })}
            >
              🔑 Mi Contraseña
            </NavLink>

            <button
              onClick={logout}
              style={{
                textAlign: 'left',
                background: 'none',
                border: 'none',
                color: '#ef4444',
                fontSize: '0.8rem',
                fontWeight: '600',
                padding: '0.4rem 0.5rem',
                cursor: 'pointer',
                borderRadius: '6px'
              }}
            >
              🚪 Salir
            </button>
          </div>
        </div>
      </aside>

      {/* Área Principal de Contenido */}
      <main style={{ flex: 1, padding: '2rem', overflowY: 'auto', boxSizing: 'border-box' }}>
        <div style={{ maxWidth: '1200px', margin: '0 auto' }}>
          <Outlet />
        </div>
      </main>

    </div>
  );
}