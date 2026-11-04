import { useState } from 'react';
import { api } from '../api/client';
import { useNavigate, Link } from 'react-router-dom';

export default function RestablecerPassword() {
    const [form, setForm] = useState({ token: '', nuevaPassword: '', confirmarPassword: ''});
    const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
    const [cargando, setCargando] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensaje({ tipo: '', texto: ''});

        if (form.nuevaPassword !== form.confirmarPassword) {
            setMensaje({ tipo: 'error', texto: 'Las contraseñas no coinciden. '});
            return;
        }

        if (form.nuevaPassword.length < 8) {
            setMensaje({ tipo: 'error', texto: 'La nueva contraseña debe tener al menos 8 caracteres.'});
            return;
        }

        setCargando(true);
        try {
            await api.post('/api/usuarios/reset-password', {
                token: form.token.trim(),
                nuevaPassword: form.nuevaPassword
            });

            setMensaje({ tipo: 'exito', texto: '¡Contraseña restablecida con éxito! Redirigiendo...'});
            setTimeout(() => navigate('/login'), 3000);
        } catch (err) {
            setMensaje({ tipo: 'error', texto: err.message || 'Token inválido o expirado.' });
        } finally {
            setCargando(false);
        }
    };

    return (
    <div className="tarjeta" style={{ maxWidth: '400px', margin: '4rem auto' }}>
      <h2>Restablecer Contraseña</h2>
      <p className="suave" style={{ marginBottom: '1rem' }}>
        Introduce el token que recibiste en tu correo y tu nueva contraseña.
      </p>

      {mensaje.texto && (
        <div style={{ 
          padding: '0.75rem', 
          marginBottom: '1rem', 
          borderRadius: '4px',
          backgroundColor: mensaje.tipo === 'error' ? '#fee2e2' : '#dcfce7',
          color: mensaje.tipo === 'error' ? '#991b1b' : '#166534'
        }}>
          {mensaje.texto}
        </div>
      )}

      <form onSubmit={handleSubmit} className="pila">
        <div>
          <label>Token de recuperación</label>
          <input
            type="text"
            required
            value={form.token}
            onChange={(e) => setForm({ ...form, token: e.target.value })}
            placeholder="Pega aquí el token recibido"
          />
        </div>

        <div>
          <label>Nueva contraseña (mínimo 8 caracteres)</label>
          <input
            type="password"
            required
            value={form.nuevaPassword}
            onChange={(e) => setForm({ ...form, nuevaPassword: e.target.value })}
          />
        </div>

        <div>
          <label>Confirmar nueva contraseña</label>
          <input
            type="password"
            required
            value={form.confirmarPassword}
            onChange={(e) => setForm({ ...form, confirmarPassword: e.target.value })}
          />
        </div>

        <button type="submit" className="btn btn-primario" disabled={cargando}>
          {cargando ? 'Restableciendo...' : 'Guardar Nueva Contraseña'}
        </button>

        <div style={{ textAlign: 'center', marginTop: '1rem' }}>
          <Link to="/login" className="suave" style={{ textDecoration: 'underline' }}>
            Volver al inicio de sesión
          </Link>
        </div>
      </form>
    </div>
  );
}