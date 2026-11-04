import { useState } from 'react';
import { api } from '../api/client';
import { Link } from 'react-router-dom';

export default function OlvidoPassword() {
    const [correo, setCorreo] = useState('');
    const [mensaje, setMensaje] = useState({ tipo: '', texto: '' });
    const [cargando, setCargando] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMensaje({ tipo: '', texto: '' });
        setCargando(true);

        try {
            await api.post('/api/usuarios/forgot-password', { correo });
            setMensaje({
                tipo: 'exito',
                texto: 'Si el correo está registrado, hemos enviado instrucciones de recuperación.'
            });
            setCorreo('');
        } catch (err) {
            setMensaje({ tipo: 'error', texto: err.message || 'Error al procesar la solicitud.' });
        } finally {
            setCargando(false);
        }
    };

    return (
        <div className="tarjeta" style={{ maxWidth: '400px', margin: '4rem auto' }}>
            <h2>Recuperar Contraseña</h2>
            <p className="suave" style={{ marginBottom: '1rem' }}>
                Ingresa tu correo electrónico y te enviaremos un token para restablecer tu contraseña.
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
                    <label>Correo electrónico</label>
                    <input
                        type="email"
                        required
                        value={correo}
                        onChange={(e) => setCorreo(e.target.value)}
                        placeholder="tucorreo@universidad.edu"
                    />
                </div>

                <button type="submit" className="btn btn-primario" disabled={cargando}>
                    {cargando ? 'Enviando...' : 'Enviar Token de Recuperación'}
                </button>
            </form>

            <div style={{ textAlign: 'center', marginTop: '1rem', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <Link to="/reset-password" style={{ color: 'var(--color-primario, #2563eb)', textDecoration: 'underline', fontSize: '0.9rem' }}>
                ¿Ya tienes un token? Ingresa aquí para restablecer
              </Link>
              <Link to="/login" className="suave" style={{ textDecoration: 'underline' }}>
                Volver al inicio de sesión
              </Link>
            </div>
        </div>
    );
}