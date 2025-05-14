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
  }
});
