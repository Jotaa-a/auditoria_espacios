import { useEffect, useState } from 'react';
import { api } from '../api/client';
import Dashboard from '../components/Dashboard'; 

export default function Admin() {
  const [seccion, setSeccion] = useState('dashboard'); 
  
  // Estados para Usuarios y Representantes
  const [usuarios, setUsuarios] = useState([]);
  const [formUsuario, setFormUsuario] = useState({ nombre: '', correo: '', password: '', rol: 'TECNICO' });
  
  // Estados para Espacios (incluyendo representanteId opcional)
  const [espacios, setEspacios] = useState([]);
  const [formEspacio, setFormEspacio] = useState({ id: null, nombre: '', representanteId: '' });
  const [editandoEspacioId, setEditandoEspacioId] = useState(null);

  // Estados para Puestos de Trabajo
  const [puestos, setPuestos] = useState([]);
  const [formPuesto, setFormPuesto] = useState({ espacioId: '', codigo: '' });
  const [filtroEspacioPuesto, setFiltroEspacioPuesto] = useState('');
  
  // Estados para Tipos de Hallazgo
  const [tiposHallazgo, setTiposHallazgo] = useState([]);

  const [error, setError] = useState('');
  const [exito, setExito] = useState('');
  const [ocupado, setOcupado] = useState(false);

  // Cargar datos según la sección seleccionada
  useEffect(() => {
    cargarDatos();
  }, [seccion, filtroEspacioPuesto]);

  const cargarDatos = async () => {
    setError('');
    setExito('');
    try {
      if (seccion === 'usuarios') {
        const data = await api.get('/api/usuarios');
        setUsuarios(data);
      } else if (seccion === 'espacios') {
        const [espaciosData, usuariosData] = await Promise.all([
          api.get('/api/espacios'),
          api.get('/api/usuarios')
        ]);
        setEspacios(espaciosData);
        setUsuarios(usuariosData);
      } else if (seccion === 'puestos') {
        const [espaciosData, puestosData] = await Promise.all([
          api.get('/api/espacios'),
          api.get(filtroEspacioPuesto ? `/api/puestos?espacioId=${filtroEspacioPuesto}` : '/api/puestos')
        ]);
        setEspacios(espaciosData);
        setPuestos(puestosData);
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

  // Seleccionar espacio para editar
  const iniciarEdicionEspacio = (esp) => {
    setEditandoEspacioId(esp.id);
    setFormEspacio({
      nombre: esp.nombre,
      representanteId: esp.representanteId || ''
    });
    setError('');
    setExito('');
  };

  // Cancelar edición
  const cancelarEdicionEspacio = () => {
    setEditandoEspacioId(null);
    setFormEspacio({ nombre: '', representanteId: '' });
  };

  // Crear o Actualizar Espacio
  const handleGuardarEspacio = async (e) => {
    e.preventDefault();
    setOcupado(true);
    setError('');
    setExito('');
    try {
      const payload = {
        nombre: formEspacio.nombre,
        representanteId: formEspacio.representanteId ? Number(formEspacio.representanteId) : null
      };

      if (editandoEspacioId) {
        await api.put(`/api/espacios/${editandoEspacioId}`, payload);
        setExito('Espacio actualizado exitosamente.');
      } else {
        await api.post('/api/espacios', payload);
        setExito('Espacio creado exitosamente.');
      }

      setFormEspacio({ nombre: '', representanteId: '' });
      setEditandoEspacioId(null);
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

  // Crear Puesto de Trabajo
  const handleCrearPuesto = async (e) => {
    e.preventDefault();
    setOcupado(true);
    setError('');
    setExito('');
    try {
      await api.post('/api/puestos', {
        espacioId: Number(formPuesto.espacioId),
        codigo: formPuesto.codigo.trim(),
        activo: true
      });
      setExito('Puesto de trabajo creado exitosamente.');
      setFormPuesto({ espacioId: '', codigo: '' });
      cargarDatos();
    } catch (err) {
      setError(err.message);
    } finally {
      setOcupado(false);
    }
  };

  // Desactivar Puesto de Trabajo
  const handleDesactivarPuesto = async (id) => {
    if (!window.confirm('¿Estás seguro de desactivar este puesto?')) return;
    setError('');
    try {
      await api.delete(`/api/puestos/${id}`);
      setExito('Puesto desactivado.');
      cargarDatos();
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <section className="pila">
      <div className="tarjeta">
        <h1>Panel de Administración</h1>
        <p className="suave">Gestiona las estadísticas, usuarios, espacios físicos, puestos de trabajo y tipos de hallazgos.</p>

        {/* Sub-pestañas de administración actualizadas */}
        <div style={{ display: 'flex', gap: '1rem', marginTop: '1rem', borderBottom: '1px solid #e5e7eb', paddingBottom: '0.5rem', flexWrap: 'wrap' }}>
          <button
            onClick={() => setSeccion('dashboard')}
            className={`btn ${seccion === 'dashboard' ? 'btn-primario' : 'btn-secundario'}`}
          >
            📊 Estadísticas
          </button>
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
            onClick={() => setSeccion('puestos')}
            className={`btn ${seccion === 'puestos' ? 'btn-primario' : 'btn-secundario'}`}
          >
            Puestos de Trabajo
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

      {/* SECCIÓN DASHBOARD / ESTADÍSTICAS */}
      {seccion === 'dashboard' && <Dashboard />}

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
          <form className="tarjeta" onSubmit={handleGuardarEspacio}>
            <h2>{editandoEspacioId ? 'Editar espacio o laboratorio' : 'Registrar nuevo espacio o laboratorio'}</h2>
            
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

            <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}>
              <button className="btn btn-primario" disabled={ocupado}>
                {ocupado ? 'Guardando...' : (editandoEspacioId ? 'Actualizar Espacio' : 'Crear Espacio')}
              </button>
              {editandoEspacioId && (
                <button
                  type="button"
                  className="btn btn-secundario"
                  onClick={cancelarEdicionEspacio}
                >
                  Cancelar
                </button>
              )}
            </div>
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
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button
                      type="button"
                      className="btn btn-secundario"
                      onClick={() => iniciarEdicionEspacio(esp)}
                    >
                      Editar
                    </button>
                    <button
                      type="button"
                      className="btn btn-secundario"
                      style={{ color: 'red', borderColor: 'red' }}
                      onClick={() => handleEliminarEspacio(esp.id)}
                    >
                      Eliminar
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}

      {/* SECCIÓN PUESTOS DE TRABAJO */}
      {seccion === 'puestos' && (
        <div className="pila">
          <form className="tarjeta" onSubmit={handleCrearPuesto}>
            <h2>Registrar nuevo puesto de trabajo</h2>
            
            <label>Espacio / Salón</label>
            <select
              required
              value={formPuesto.espacioId}
              onChange={(e) => setFormPuesto({ ...formPuesto, espacioId: e.target.value })}
            >
              <option value="">Selecciona un espacio...</option>
              {espacios.map((esp) => (
                <option key={esp.id} value={esp.id}>
                  {esp.nombre}
                </option>
              ))}
            </select>

            <label>Código del puesto</label>
            <input
              type="text"
              required
              maxLength={30}
              placeholder="Ej. SP_001"
              value={formPuesto.codigo}
              onChange={(e) => setFormPuesto({ ...formPuesto, codigo: e.target.value })}
            />

            <button className="btn btn-primario" disabled={ocupado} style={{ marginTop: '1rem' }}>
              {ocupado ? 'Registrando...' : 'Crear Puesto'}
            </button>
          </form>

          <div className="tarjeta">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '1rem' }}>
              <h2>Puestos existentes</h2>
              <div>
                <label style={{ marginRight: '0.5rem' }}>Filtrar por espacio:</label>
                <select
                  value={filtroEspacioPuesto}
                  onChange={(e) => setFiltroEspacioPuesto(e.target.value)}
                >
                  <option value="">Todos los espacios</option>
                  {espacios.map((esp) => (
                    <option key={esp.id} value={esp.id}>
                      {esp.nombre}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <ul className="lista" style={{ marginTop: '1rem' }}>
              {puestos.length === 0 ? (
                <p className="suave">No hay puestos registrados.</p>
              ) : (
                puestos.map((p) => (
                  <li key={p.id} className="puesto-cab" style={{ padding: '0.5rem 0', borderBottom: '1px solid #f3f4f6' }}>
                    <div>
                      <strong>{p.codigo}</strong>
                      <p className="suave">Estado: {p.activo ? '🟢 Activo' : '🔴 Inactivo'}</p>
                    </div>
                    {p.activo && (
                      <button
                        type="button"
                        className="btn btn-secundario"
                        style={{ color: 'red', borderColor: 'red' }}
                        onClick={() => handleDesactivarPuesto(p.id)}
                      >
                        Desactivar
                      </button>
                    )}
                  </li>
                ))
              )}
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