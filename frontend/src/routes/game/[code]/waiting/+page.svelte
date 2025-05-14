<script>
  // ✅ Fix SockJS global reference
  window.global = globalThis;

  import { onMount } from 'svelte';
  import { goto } from '$app/navigation';
  import { page } from '$app/stores';
  import { Client } from '@stomp/stompjs';
  import SockJS from 'sockjs-client';
  import TopBar from '$lib/components/TopBar.svelte';

  $: gameCode = $page.params.code;

  let dots = '.';
  let interval;
  let gameCode = $page.params.code;
  let client;

  onMount(() => {
    // Animate loading dots
    let count = 1;
    interval = setInterval(() => {
      count = (count % 3) + 1;
      dots = '.'.repeat(count);
    }, 500);

    // ✅ Create STOMP client
    client = new Client({
      webSocketFactory: () => new SockJS('http://192.168.0.121:8080/ws'),
      reconnectDelay: 5000,
      debug: (str) => console.log('[STOMP]', str),
      onConnect: () => {
        console.log('🟢 WebSocket connected');

        // ✅ Subscribe only after connect
        client.subscribe(`/waiting/${gameCode}`, (message) => {
          try {
            const data = JSON.parse(message.body);
            console.log('[WebSocket] Received:', data);

            if (data.type === 'PLAYER_JOINED') {
              clearInterval(interval);
              goto(`/game/${gameCode}/topics`);
            }
          } catch (err) {
            console.error('❌ Error parsing WebSocket message:', err);
          }
        });
      },
      onStompError: (frame) => {
        console.error('❌ STOMP error:', frame);
      }
    });

    client.activate();

    return () => {
      clearInterval(interval);
      client.deactivate();
    };
  });
</script>




<style>
  @keyframes float1 {
    0%   { transform: translate(0, 0) rotate(0deg) scale(1); }
    50%  { transform: translate(25px, -20px) rotate(6deg) scale(1.05); }
    100% { transform: translate(0, 0) rotate(0deg) scale(1); }
  }

  @keyframes float2 {
    0%   { transform: translate(0, 0) rotate(0deg) scale(1); }
    50%  { transform: translate(-30px, 25px) rotate(-5deg) scale(1.04); }
    100% { transform: translate(0, 0) rotate(0deg) scale(1); }
  }

  @keyframes float3 {
    0%   { transform: translate(0, 0) rotate(0deg) scale(1); }
    50%  { transform: translate(18px, -18px) rotate(4deg) scale(1.06); }
    100% { transform: translate(0, 0) rotate(0deg) scale(1); }
  }

  html, body {
    margin: 0;
    padding: 0;
    overflow: hidden; /* ✅ prevents vertical scroll */
    height: 100%;
  }


  .shape {
    position: absolute;
    z-index: 0;
    opacity: 0.15;
    pointer-events: none;
  }

  .circle {
    width: 400px;
    height: 400px;
    background: white;
    border-radius: 50%;
    top: -100px;
    left: -100px;
    animation: float1 8s ease-in-out infinite;
  }

  .triangle {
    width: 0;
    height: 0;
    border-left: 300px solid transparent;
    border-right: 300px solid transparent;
    border-bottom: 400px solid white;
    top: 100px;
    right: -100px;
    animation: float2 10s ease-in-out infinite;
  }

  .square {
    width: 250px;
    height: 250px;
    background: white;
    bottom: -100px;
    left: 100px;
    transform: rotate(10deg);
    animation: float3 7s ease-in-out infinite;
  }

  .center-shape {
    position: absolute;
    width: 360px;
    height: 360px;
    background: rgba(255, 255, 255, 0.15);
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) rotate(25deg);
    z-index: 0;
    border-radius: 16px;
    animation: float2 9s ease-in-out infinite;
  }

  .waiting-screen {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100dvh;
    font-size: 1.8rem;
    font-family: 'Rubik', sans-serif;
    font-weight: bold;
    color: white;
    background-color: #9CCF00;
  }

  .waiting-container {
    position: relative;
    height: 100dvh;
    overflow: hidden; /* ✅ local containment if needed */
    display: flex;
    flex-direction: column;
    align-items: center;
    background-color: #9CCF00;
    font-family: 'Rubik', sans-serif;
    color: white;
  }


  .text {
    white-space: nowrap;
    font-size: 1.75rem;
    text-align: center;
  }

  .dots {
    width: 2ch; /* reserves space for up to 3 dots */
    text-align: left;
    display: inline-block;
    font-size: 2rem;
    margin-left: 0.25rem;
  }
  .waiting-container {
    position: relative;
    height: 100dvh;
    display: flex;
    flex-direction: column;
    background-color: #9CCF00;
    font-family: 'Rubik', sans-serif;
    color: white;
    overflow: hidden;
  }

  .waiting-center {
    flex: 1; /* fills remaining space */
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    position: relative;
  }
</style>

<div class="waiting-container">
  <TopBar {gameCode} />

  <div class="waiting-center">
    <div class="shape circle"></div>
    <div class="shape triangle"></div>
    <div class="shape square"></div>
    <div class="shape center-shape"></div>

    <span class="text">Waiting for the other player</span>
    <span class="dots">{dots}</span>
  </div>
</div>


