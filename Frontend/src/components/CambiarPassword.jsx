import { useState } from 'react';
import { api } from '../api/client';

export default function CambiarPassword() {
  const [formMiPassword, setFormMiPassword] = useState({
    passwordActual: '',
    nuevaPassword: '',
    confirmarPassword: ''
  });
  const [mensajePass, setMensajePass] = useState({ tipo: '', texto: '' });
  const [cargandoPass, setCargandoPass] = useState(false);

  const handleCambiarMiPassword = async (e) => {
    e.preventDefault();
    setMensajePass({ tipo: '', texto: '' });

    if (formMiPassword.nuevaPassword !== formMiPassword.confirmarPassword) {
      setMensajePass({ tipo: 'error', texto: 'Las nuevas contraseñas no coinciden.' });
      return;
    }

    if (formMiPassword.nuevaPassword.length < 8) {
      setMensajePass({ tipo: 'error', texto: 'La nueva contraseña debe tener al menos 8 caracteres.' });
      return;
    }

    setCargandoPass(true);
    try {
      await api.patch('/api/usuarios/me/password', {
        passwordActual: formMiPassword.passwordActual,
        nuevaPassword: formMiPassword.nuevaPassword
      });

      setMensajePass({ tipo: 'exito', texto: '¡Contraseña actualizada exitosamente!' });
      setFormMiPassword({ passwordActual: '', nuevaPassword: '', confirmarPassword: '' });
    } catch (err) {
      setMensajePass({ tipo: 'error', texto: err.message || 'Error al actualizar la contraseña.' });
    } finally {
      setCargandoPass(false);
    }
  };

  return (
    <div className="tarjeta" style={{ maxWidth: '500px', margin: '2rem auto' }}>
      <h2>Seguridad: Cambiar mi contraseña</h2>
      
      {mensajePass.texto && (
        <div style={{ 
          padding: '0.75rem', 
          marginBottom: '1rem', 
          borderRadius: '4px',
          backgroundColor: mensajePass.tipo === 'error' ? '#fee2e2' : '#dcfce7',
          color: mensajePass.tipo === 'error' ? '#991b1b' : '#166534'
        }}>
          {mensajePass.texto}
        </div>
      )}

      <form onSubmit={handleCambiarMiPassword} className="pila">
        <div>
          <label>Contraseña actual</label>
          <input
            type="password"
            required
            value={formMiPassword.passwordActual}
            onChange={(e) => setFormMiPassword({ ...formMiPassword, passwordActual: e.target.value })}
          />
        </div>

        <div>
          <label>Nueva contraseña (mínimo 8 caracteres)</label>
          <input
            type="password"
            required
            value={formMiPassword.nuevaPassword}
            onChange={(e) => setFormMiPassword({ ...formMiPassword, nuevaPassword: e.target.value })}
          />
        </div>

        <div>
          <label>Confirmar nueva contraseña</label>
          <input
            type="password"
            required
            value={formMiPassword.confirmarPassword}
            onChange={(e) => setFormMiPassword({ ...formMiPassword, confirmarPassword: e.target.value })}
          />
        </div>

        <button type="submit" className="btn btn-primario" disabled={cargandoPass}>
          {cargandoPass ? 'Actualizando...' : 'Actualizar Contraseña'}
        </button>
      </form>
    </div>
  );
}