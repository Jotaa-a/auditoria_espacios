import { useState } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext';

export default function Login() {
  const { autenticado, login } = useAuth();
  const navegar = useNavigate();
  const ubicacion = useLocation();
  const [correo, setCorreo] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  if (autenticado) return <Navigate to="/" replace />;

  async function enviar(e) {
    e.preventDefault();
    setError('');
    setCargando(true);
    try {
      await login(correo.trim(), password);
      navegar(ubicacion.state?.desde?.pathname ?? '/', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setCargando(false);
    }
  }

  return (
    <div className="login">
      <form className="tarjeta" onSubmit={enviar}>
        <h1>Iniciar sesión</h1>

        <label htmlFor="correo">Correo</label>
        <input id="correo" type="email" autoComplete="username" required
               value={correo} onChange={(e) => setCorreo(e.target.value)} />

        <label htmlFor="password">Contraseña</label>
        <input id="password" type="password" autoComplete="current-password" required
               value={password} onChange={(e) => setPassword(e.target.value)} />

        {error && <p className="error" role="alert">{error}</p>}

        <button className="btn btn-primario" disabled={cargando}>
          {cargando ? 'Entrando…' : 'Entrar'}
        </button>
      </form>
    </div>
  );

  return (
    <div className="login">
      <div className="login-marca">
        <img src="/logos/campuslands.png" alt="Campuslands" width="64" height="64" />
        <img src="/logos/prexxa.png" alt="Prexxa" width="64" height="64" />
      </div>

      <form className="tarjeta" onSubmit={enviar}>
        {/* ...el contenido del formulario queda igual... */}
      </form>
    </div>
  );
}