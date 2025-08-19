<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import TopBar from '$lib/components/TopBar.svelte';

  const gameCode = $page.params.code;
  let isMobile = false;

  onMount(() => {
    const ua = navigator.userAgent;
    isMobile = /Android|iPhone|iPad|iPod|Opera Mini|IEMobile|WPDesktop/i.test(ua);
  });
</script>

<style>
  html, body {
    margin: 0;
    padding: 0;
    overflow: auto;
    font-family: 'Segoe UI', sans-serif;
  }

  .container {
    position: relative;
    width: 100%;
    max-width: 100vw;
    height: 100vh;
    background: #9CCF00;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: flex-start;
    overflow: hidden;
    min-height: 100dvh;
  }

  .shape {
    position: absolute;
    z-index: 0;
    opacity: 0.15;
  }
  .circle {
    width: 400px;
    height: 400px;
    background: white;
    border-radius: 50%;
    top: -100px;
    left: -100px;
    animation: float1 9.2s ease-in-out infinite;
  }
  .triangle {
    width: 0;
    height: 0;
    border-left: 300px solid transparent;
    border-right: 300px solid transparent;
    border-bottom: 400px solid white;
    top: 100px;
    right: -100px;
    animation: float2 11.4s ease-in-out infinite;
  }
  .square {
    width: 250px;
    height: 250px;
    background: white;
    bottom: -100px;
    left: 100px;
    transform: rotate(10deg);
    animation: float3 7.3s ease-in-out infinite;
  }

  .content {
    z-index: 1;
    width: 100%;
    max-width: 800px;
    margin-top: 24px; /* space under TopBar */
    padding: 2rem 1rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
  }

  .headline {
    font-weight: 800;
    font-size: clamp(28px, 4vw, 48px);
    margin: 2rem 0 0.5rem;
    letter-spacing: 0.5px;
  }

  .sub {
    font-size: clamp(14px, 2vw, 18px);
    opacity: 0.9;
  }

  .mobile-padding {
    padding-bottom: 80px;
  }

  @keyframes float1 {
    0% { transform: translate(0, 0) rotate(0deg); }
    50% { transform: translate(25px, -20px) rotate(6deg); }
    100% { transform: translate(0, 0) rotate(0deg); }
  }
  @keyframes float2 {
    0% { transform: translate(0, 0) rotate(0deg); }
    50% { transform: translate(-30px, 25px) rotate(-5deg); }
    100% { transform: translate(0, 0) rotate(0deg); }
  }
  @keyframes float3 {
    0% { transform: translate(0, 0) rotate(0deg); }
    50% { transform: translate(18px, -18px) rotate(4deg); }
    100% { transform: translate(0, 0) rotate(0deg); }
  }
</style>

<div class="container {isMobile ? 'mobile-padding' : ''}">
  <!-- floating background -->
  <div class="shape circle"></div>
  <div class="shape triangle"></div>
  <div class="shape square"></div>

  <!-- shared top bar -->
  <TopBar gameCode={gameCode} />

  <!-- placeholder content for now -->
  <div class="content">
    <div class="headline">Final Evaluation</div>
    <div class="sub">Both players are here. We’ll set up the finale flow next.</div>
  </div>
</div>
