CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(32) NOT NULL DEFAULT 'ROLE_USER'
        CHECK (rol IN ('ROLE_USER', 'ROLE_ADMIN')),
    avatar_url TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    email_verificado BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS categorias (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS productos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL UNIQUE,
    descripcion TEXT,
    precio NUMERIC(10, 2) NOT NULL CHECK (precio >= 0),
    stock INTEGER NOT NULL DEFAULT 0 CHECK (stock >= 0),
    estado VARCHAR(32) NOT NULL DEFAULT 'ACTIVO'
        CHECK (estado IN ('ACTIVO', 'INACTIVO', 'AGOTADO')),
    destacado BOOLEAN NOT NULL DEFAULT FALSE,
    categoria_id UUID NOT NULL REFERENCES categorias(id) ON DELETE RESTRICT,
    vendedor_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    imagen_principal_url TEXT,
    galeria JSONB DEFAULT '[]'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    deleted_at TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS archivos_media (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre_original VARCHAR(255) NOT NULL,
    nombre_storage VARCHAR(255) NOT NULL,
    ruta_storage VARCHAR(500) NOT NULL,
    bucket VARCHAR(50) NOT NULL,
    tipo_mime VARCHAR(120) NOT NULL,
    tamaño_bytes BIGINT NOT NULL CHECK (tamaño_bytes >= 0),
    entidad_tipo VARCHAR(50),
    entidad_id UUID,
    publico BOOLEAN NOT NULL DEFAULT TRUE,
    uploaded_by UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS interacciones_ia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID REFERENCES users(id) ON DELETE SET NULL,
    tipo_interaccion VARCHAR(40) NOT NULL DEFAULT 'CHAT_ASISTENTE'
        CHECK (tipo_interaccion IN ('CHAT_ASISTENTE', 'IA_GENERE_TEXTO',
            'IA_GENERE_IMAGEN', 'IA_CLASIFICACION_MEDIA', 'OTRO')),
    prompt TEXT NOT NULL,
    respuesta TEXT,
    modelo_ia VARCHAR(120),
    tokens_entrada INTEGER,
    tokens_salida INTEGER,
    costo_estimado NUMERIC(10, 6),
    estado VARCHAR(32) NOT NULL DEFAULT 'COMPLETADO'
        CHECK (estado IN ('PENDIENTE', 'COMPLETADO', 'ERROR', 'CANCELADO')),
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS logs_actividad (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    usuario_id UUID REFERENCES users(id) ON DELETE SET NULL,
    accion VARCHAR(150) NOT NULL,
    modulo VARCHAR(100) NOT NULL,
    ip VARCHAR(50),
    user_agent TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_vendedor ON productos(vendedor_id);
CREATE INDEX IF NOT EXISTS idx_productos_destacado ON productos(destacado);
CREATE INDEX IF NOT EXISTS idx_productos_estado ON productos(estado);
CREATE INDEX IF NOT EXISTS idx_productos_precio ON productos(precio);
CREATE INDEX IF NOT EXISTS idx_productos_deleted_at ON productos(deleted_at);
CREATE INDEX IF NOT EXISTS idx_archivos_entidad ON archivos_media(entidad_tipo, entidad_id);
CREATE INDEX IF NOT EXISTS idx_archivos_bucket ON archivos_media(bucket);
CREATE INDEX IF NOT EXISTS idx_ia_usuario ON interacciones_ia(usuario_id);
CREATE INDEX IF NOT EXISTS idx_ia_tipo ON interacciones_ia(tipo_interaccion);
CREATE INDEX IF NOT EXISTS idx_ia_created ON interacciones_ia(created_at);
CREATE INDEX IF NOT EXISTS idx_logs_usuario ON logs_actividad(usuario_id);
CREATE INDEX IF NOT EXISTS idx_logs_modulo ON logs_actividad(modulo);
CREATE INDEX IF NOT EXISTS idx_logs_created ON logs_actividad(created_at);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_users_updated_at ON users;
CREATE TRIGGER trg_users_updated_at BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS trg_categorias_updated_at ON categorias;
CREATE TRIGGER trg_categorias_updated_at BEFORE UPDATE ON categorias
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
DROP TRIGGER IF EXISTS trg_productos_updated_at ON productos;
CREATE TRIGGER trg_productos_updated_at BEFORE UPDATE ON productos
FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

INSERT INTO categorias (nombre, slug, descripcion, activo) VALUES
    ('Electrónica', 'electronica', 'Dispositivos y accesorios electrónicos', TRUE),
    ('Moda', 'moda', 'Ropa, calzado y complementos', TRUE),
    ('Hogar', 'hogar', 'Artículos para el hogar y decoración', TRUE),
    ('Deportes', 'deportes', 'Equipamiento deportivo y fitness', TRUE),
    ('Tecnología', 'tecnologia', 'Gadgets, ordenadores y periféricos', TRUE)
ON CONFLICT (nombre) DO NOTHING;
