LINKS:

    RENDER- https://dashboard.render.com/
    NEON- https://console.neon.tech/app/projects/hidden-heart-81293763/branches/br-lucky-sky-bbvwvem9

## Conexión con el frontend

El catálogo está disponible en `https://tienda-jdr-backend.onrender.com/api/productos`.
El frontend en Vercel puede consumirlo mediante su proxy `/api`.

Para llamadas directas del navegador a Render, configurar `CORS_ORIGINS` con los
orígenes permitidos separados por comas, sin barra final ni rutas. Incluir
`http://localhost:5173` si se necesita desarrollo local y el dominio real de
producción de Vercel. La variable se configura en Render y requiere redesplegar.
Las credenciales de Neon siguen en `DB_URL`, `DB_USERNAME` y `DB_PASSWORD`.
