CREATE DATABASE IF NOT EXISTS auditoria_espacios
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE auditoria_espacios;

create table usuario (
	id				bigint auto_increment primary key,
	nombre			varchar(100) not null,
	correo			varchar(120) not null unique,
	password_hash	varchar(255) not null,
	rol				enum('ADMIN', 'TECNICO', 'REPRESENTANTE') not null,
	activo			boolean not null default true
);

create table espacio(
	id					bigint auto_increment primary key,
	nombre				varchar(80) not null unique,
	representante_id	bigint null,
	foreign key (representante_id) references usuario(id)
);

create table puesto_trabajo (
	id			bigint auto_increment primary key,
	espacio_id	bigint not null,
	codigo		varchar(30) not null,
	activo		boolean not null default true,
	unique 	(espacio_id, codigo),
	foreign key (espacio_id) references espacio(id)
);

create table componente (
	id			bigint auto_increment primary key,
	puesto_id	bigint not null,
	tipo 		enum('TORRE','PANTALLA','TECLADO','MOUSE','CABLE_PODER',
                  'CABLE_VIDEO','CABLE_USB','OTRO') not null,
	serial 		varchar(80) null,
	descripcion	varchar(150) null,
	foreign key (puesto_id) references puesto_trabajo(id)
);

create table inspeccion (
	id				bigint auto_increment primary key,
	espacio_id		bigint not null,
	tipo			enum('VIERNES_ENTREGA', 'LUNES_RECEPCION') not null,
	fecha			date not null,
	inspector_id 	bigint not null,
	estado			enuma('BORRADOR', 'CERRADA') not null default 'BORRADOR',
	observaciones	text null,
	creada_en		datetime not null default current_timestamp,
	unique (espacio_id, tipo, fecha),
	foreign key (espacio_id) references espacio(id),
	foreign key (inspector_id) references usuario(id)
);

create table inspeccion_detalle (
	id				bigint auto_increment primary key,
	inspeccion_id	bigint not null,
	puesto_id		bigint not null,
	resultado		enum('OK', 'CON_NOVEDADES') not null,
	unique (inspeccion_id, puesto_id),
	foreign key (inspeccion_id) references inspeccion(id),
	foreign key (puesto_id) references puesto_trabajo(id)
);

create table tipo_hallazgo (
	id		bigint auto_increment primary key,
	codigo 	varchar(40) not null unique,
	nombre	varchar(100) not null,
	categoria enum('LIMPIEZA','ORDEN','PERIFERICO','DANO_FISICO') not null
);

create table hallazgo (
	id					bigint auto_increment primary key,
	detalle_id			bigint not null,
	tipo_hallazgo_id	bigint not null,
	componente_id		bigint null,
	descripcion			varchar(500) null,
	foreign key (detalle_id) references inspeccion_detalle(id),
	foreign key (tipo_hallazgo_id) references tipo_hallazgo(id),
	foreign key (componente_id) references componente(id)
);

create table reporte_dano (
	id				bigint auto_increment primary key,
	componente_id	bigint not null,
	reportado_por	bigint not null,
	inspeccion_id	bigint null,
	descripcion		text not null,
	gravedad		enum('LEVE', 'MODERADO', 'GRAVE') not null,
	estado			enum('ABIERTO', 'EN_REVISION', 'RESUELTO') not null default 'ABIERTO',
	creado_en		datetime not null default current_timestamp,
	resuelto_en 	datetime null,
	foreign key (componente_id) references componente(id),
	foreign key (reportado_por) references usuario(id),
	foreign key (inspeccion_id) references inspeccion(id)
);

create table evidencia (
	id				bigint auto_increment primary key,
	ruta_archivo	varchar(255) not null,
	hallazgo_id		bigint null,
	reporte_id		bigint null,
	subida_en		datetime not null default current_timestamp,
	foreign key (hallazgo_id) references hallazgo(id),
	foreign key (reporte_id) references reporte_dano(id),
	check (hallazgo_id is not null or reporte_id is not null)
);

create table auditoria_log (
	id			bigint auto_increment primary key,
	usuario_id	bigint null,
	accion		varchar(255) not null,
	entidad 	varchar(255) not null,
	entidad_id	bigint null,
	detalle		JSON null,
	fecha		datetime not null default current_timestamp,
	foreign key (usuario_id) references usuario(id)
);

insert into tipo_hallazgo (codigo, nombre, categoria) values
 ('BASURA',                  'Basura en el puesto',             'LIMPIEZA'),
 ('SUCIEDAD',                'Superficie o equipo sucio',       'LIMPIEZA'),
 ('SILLA_DESORDENADA',       'Silla mal acomodada',             'ORDEN'),
 ('CABLES_DESORDENADOS',     'Cables mal acomodados',           'ORDEN'),
 ('PERIFERICO_FALTANTE',     'Periférico faltante',             'PERIFERICO'),
 ('PERIFERICO_DESCONECTADO', 'Periférico desconectado',         'PERIFERICO'),
 ('PERIFERICO_CAMBIADO',     'Periférico distinto al original', 'PERIFERICO'),
 ('DANO_VISIBLE',            'Daño físico visible',             'DANO_FISICO'),
 ('OTRO',                    'Otro (ver descripción)',          'ORDEN');


select * from tipo_hallazgo;
show tables;

