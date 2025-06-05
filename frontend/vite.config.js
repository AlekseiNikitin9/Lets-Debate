import { defineConfig } from 'vite';
import { sveltekit } from '@sveltejs/kit/vite';
import tailwindcss from '@tailwindcss/vite';
import rollupNodePolyFill from 'rollup-plugin-node-polyfills';

export default defineConfig({
  plugins: [tailwindcss(), sveltekit()],
  define: {
    global: 'globalThis'  // 👈 this is the critical line
  },
  optimizeDeps: {
    include: ['sockjs-client'],
  },
  build: {
    rollupOptions: {
      plugins: [rollupNodePolyFill()]
    }
  },
  server: {
    host: true, // 👈 allows LAN access (binds to 0.0.0.0)
    port: 5173  // 👈 optional; change if you want a custom port
  }
});
