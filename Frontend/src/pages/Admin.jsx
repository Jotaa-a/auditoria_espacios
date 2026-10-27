import { useEffect, useState } from 'react';
import { api } from '../api/client';

export default function Admin() {
  const [seccion, setSeccion] = useState('usuarios'); // 'usuarios', 'espacios', 'tipos'
  
  // Estados para Usuarios y Representantes
  const [usuarios, setUsuarios] = useState([]);
  const [formUsuario, setFormUsuario] = useState({ nombre: '', correo: '', password: '', rol: 'TECNICO' });
  
  // Estados para Espacios (incluyendo representanteId opcional)
  const [espacios, setEspacios] = useState([]);
  const [formEspacio, setFormEspacio] = useState({ nombre: '', representanteId: '' });
  
  // Estados para Tipos de Hallazgo
  const [tiposHallazgo, setTiposHallazgo] = useState([]);

  const [error, setError] = useState('');
  const [exito, setExito] = useState('');
  const [ocupado, setOcupado] = useState(false);

  // Cargar datos según la sección seleccionada
  useEffect(() => {
    cargarDatos();
  }, [seccion]);

  const cargarDatos = async () => {
    setError('');
    setExito('');
    try {
      if (seccion === 'usuarios') {
        const data = await api.get('/api/usuarios');
        setUsuarios(data);
      } else if (seccion === 'espacios') {
        // Al entrar a espacios, cargamos tanto los espacios como los usuarios para obtener los representantes
        const [espaciosData, usuariosData] = await Promise.all([
          api.get('/api/espacios'),
          api.get('/api/usuarios')
        ]);
        setEspacios(espaciosData);
        setUsuarios(usuariosData);
      } else if (seccion === 'tipos') {
        const data = await api.get('/api/tipos-hallazgo');
        setTiposHallazgo(data);
      }
    } catch (e) {
      setError(e.message);
    }
  };

  // Filtrar solo los usuarios que tengan el rol de REPRESENTANTE
  const representantes = usuarios.filter((u) => u.rol === 'REPRESENTANTE');

  // Crear Usuario
  const handleCrearUsuario = async (e) => {
    e.preventDefault();
    setOcupado(true);
    setError('');
    setExito('');
    try {
      await api.post('/api/usuarios', formUsuario);
      setExito('Usuario creado exitosamente.');
      setFormUsuario({ nombre: '', correo: '', password: '', rol: 'TECNICO' });
      cargarDatos();
    } catch (err) {
      setError(err.message);
    } finally {
      setOcupado(false);
    }
  };

  // Crear Espacio (enviando el representanteId correcto para el EspacioRequest)
  const handleCrearEspacio = async (e) => {
    e.preventDefault();
    setOcupado(true);
    setError('');
    setExito('');
    try {
      const payload = {
        nombre: formEspacio.nombre,
        representanteId: formEspacio.representanteId ? Number(formEspacio.representanteId) : null
      };

      await api.post('/api/espacios', payload);
      setExito('Espacio creado exitosamente.');
      setFormEspacio({ nombre: '', representanteId: '' });
      cargarDatos();
    } catch (err) {
      setError(err.message);
    } finally {
      setOcupado(false);
    }
  };

  // Eliminar Espacio
  const handleEliminarEspacio = async (id) => {
    if (!window.confirm('¿Estás seguro de eliminar este espacio?')) return;
    setError('');
    try {
      await api.delete(`/api/espacios/${id}`);
      setExito('Espacio eliminado.');
      cargarDatos();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <section className="pila">
      <div className="tarjeta">
        <h1>Panel de Administración</h1>
        <p className="suave">Gestiona los usuarios, espacios físicos y tipos de hallazgos de la universidad.</p>

        {/* Sub-pestañas de administración */}
        <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem', borderBottom: '1px solid #e5e7eb', paddingBottom: '0.5rem' }}>
          <button
            onClick={() => setSeccion('usuarios')}
            className={`btn ${seccion === 'usuarios' ? 'btn-primario' : 'btn-secundario'}`}
          >
            Usuarios
          </button>
          <button
            onClick={() => setSeccion('espacios')}
            className={`btn ${seccion === 'espacios' ? 'btn-primario' : 'btn-secundario'}`}
          >
            Espacios y Aulas
          </button>
          <button
            onClick={() => setSeccion('tipos')}
            className={`btn ${seccion === 'tipos' ? 'btn-primario' : 'btn-secundario'}`}
          >
            Tipos de Hallazgo
          </button>
        </div>
      </div>

      {error && <p className="error" role="alert">{error}</p>}
      {exito && <p className="aviso" role="status" style={{ color: 'green' }}>{exito}</p>}

      {/* SECCIÓN USUARIOS */}
      {seccion === 'usuarios' && (
        <div className="pila">
          <form className="tarjeta" onSubmit={handleCrearUsuario}>
            <h2>Registrar nuevo usuario</h2>
            
            <label>Nombre completo</label>
            <input
              type="text"
              required
              value={formUsuario.nombre}
              onChange={(e) => setFormUsuario({ ...formUsuario, nombre: e.target.value })}
            />

            <label>Correo electrónico</label>
            <input
              type="email"
              required
              value={formUsuario.correo}
              onChange={(e) => setFormUsuario({ ...formUsuario, correo: e.target.value })}
            />

            <label>Contraseña temporal</label>
            <input
              type="password"
              required
              value={formUsuario.password}
              onChange={(e) => setFormUsuario({ ...formUsuario, password: e.target.value })}
            />

            <label>Rol</label>
            <select
              value={formUsuario.rol}
              onChange={(e) => setFormUsuario({ ...formUsuario, rol: e.target.value })}
            >
              <option value="ADMIN">Administrador</option>
              <option value="TECNICO">Técnico</option>
              <option value="REPRESENTANTE">Representante</option>
            </select>

            <button className="btn btn-primario" disabled={ocupado} style={{ marginTop: '1rem' }}>
              {ocupado ? 'Registrando...' : 'Crear Usuario'}
            </button>
          </form>

          <div className="tarjeta">
            <h2>Lista de usuarios registrados</h2>
            <ul className="lista" style={{ marginTop: '1rem' }}>
              {usuarios.map((u) => (
                <li key={u.id} className="puesto-cab" style={{ padding: '0.5rem 0', borderBottom: '1px solid #f3f4f6' }}>
                  <div>
                    <strong>{u.nombre}</strong> <span className="suave">({u.correo})</span>
                  </div>
                  <span className="insignia insignia-ok">{u.rol}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}

      {/* SECCIÓN ESPACIOS */}
      {seccion === 'espacios' && (
        <div className="pila">
          <form className="tarjeta" onSubmit={handleCrearEspacio}>
            <h2>Registrar nuevo espacio o laboratorio</h2>
            
            <label>Nombre del espacio</label>
            <input
              type="text"
              required
              placeholder="Ej. Laboratorio 302"
              value={formEspacio.nombre}
              onChange={(e) => setFormEspacio({ ...formEspacio, nombre: e.target.value })}
            />

            <label>Representante asignado (Opcional)</label>
            <select
              value={formEspacio.representanteId}
              onChange={(e) => setFormEspacio({ ...formEspacio, representanteId: e.target.value })}
            >
              <option value="">Sin representante</option>
              {representantes.map((rep) => (
                <option key={rep.id} value={rep.id}>
                  {rep.nombre} ({rep.correo})
                </option>
              ))}
            </select>

            <button className="btn btn-primario" disabled={ocupado} style={{ marginTop: '1rem' }}>
              {ocupado ? 'Guardando...' : 'Crear Espacio'}
            </button>
          </form>

          <div className="tarjeta">
            <h2>Espacios existentes</h2>
            <ul className="lista" style={{ marginTop: '1rem' }}>
              {espacios.map((esp) => (
                <li key={esp.id} className="puesto-cab" style={{ padding: '0.5rem 0', borderBottom: '1px solid #f3f4f6' }}>
                  <div>
                    <strong>{esp.nombre}</strong>
                    <p className="suave">
                      Representante: {esp.representanteNombre || 'No asignado'}
                    </p>
                  </div>
                  <button
                    type="button"
                    className="btn btn-secundario"
                    style={{ color: 'red', borderColor: 'red' }}
                    onClick={() => handleEliminarEspacio(esp.id)}
                  >
                    Eliminar
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}

      {/* SECCIÓN TIPOS DE HALLAZGO */}
      {seccion === 'tipos' && (
        <div className="tarjeta">
          <h2>Tipos de Hallazgo configurados</h2>
          <p className="suave" style={{ marginBottom: '1rem' }}>Estos son los tipos definidos en el sistema para clasificar las incidencias.</p>
          <ul className="lista">
            {tiposHallazgo.map((tipo) => (
              <li key={tipo.id} style={{ padding: '0.5rem 0', borderBottom: '1px solid #f3f4f6' }}>
                <strong>{tipo.nombre}</strong>
                {tipo.descripcion && <p className="suave">{tipo.descripcion}</p>}
              </li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}