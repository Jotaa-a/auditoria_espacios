import { useState } from 'react';

export default function CargaMasivaEspacios() {
  const [archivo, setArchivo] = useState(null);
  const [cargando, setCargando] = useState(false);
  const [mensaje, setMensaje] = useState('');

  const handleFileChange = (e) => {
    setArchivo(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!archivo) return;

    const formData = new FormData();
    formData.append('file', archivo);

    setCargando(true);
    setMensaje('');

    try {
      // Ajusta la URL según tu configuración de fetch / axios y token JWT
      const respuesta = await fetch('/api/espacios/carga-masiva', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}` // Si usas JWT
        },
        body: formData
      });

      const resultado = await respuesta.text();
      if (respuesta.ok) {
        setMensaje(`${resultado}`);
      } else {
        setMensaje(`Error: ${resultado}`);
      }
    } catch (error) {
      setMensaje('Error de conexión con el servidor.');
    } finally {
      setCargando(false);
    }
  };

  return (
    <div className="tarjeta" style={{ marginTop: '1.5rem' }}>
      <h2>Carga Masiva de Salones / Puestos</h2>
      <p className="suave">Sube un archivo Excel con las columnas estructuradas para registrar múltiples espacios a la vez.</p>
      
      <form onSubmit={handleSubmit} className="pila">
        <input 
          type="file" 
          accept=".xlsx, .xls" 
          onChange={handleFileChange} 
        />
        <button type="submit" className="btn btn-primario" disabled={cargando || !archivo}>
          {cargando ? 'Procesando...' : 'Subir e Importar Excel'}
        </button>
      </form>

      {mensaje && <p style={{ marginTop: '0.75rem', fontWeight: '500' }}>{mensaje}</p>}
    </div>
  );
}