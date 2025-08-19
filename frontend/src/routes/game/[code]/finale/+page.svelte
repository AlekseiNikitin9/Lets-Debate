<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { goto } from '$app/navigation';
  import TopBar from '$lib/components/TopBar.svelte';

  const gameCode = $page.params.code;
  const destination = "http://localhost:8080";

  // Names (for labels under the bar)
  const p1Name = sessionStorage.getItem('p1') || 'Player 1';
  const p2Name = sessionStorage.getItem('p2') || 'Player 2';

  let isMobile = false;
  let countdown = 5;
  let loading = true;
  let result = null; // { p1, p2, p1Score, p2Score, verdict }

  onMount(() => {
    const ua = navigator.userAgent;
    isMobile = /Android|iPhone|iPad|iPod|Opera Mini|IEMobile|WPDesktop/i.test(ua);

    const timer = setInterval(() => {
      countdown -= 1;
      if (countdown <= 0) {
        clearInterval(timer);
        evaluate();
      }
    }, 1000);
  });

  async function evaluate() {
    try {
      const res = await fetch(`${destination}/api/finale/${gameCode}/score`);
      result = await res.json();
    } catch (e) {
      console.error(e);
      result = {
        p1: p1Name, p2: p2Name,
        p1Score: 50, p2Score: 50,
        verdict: "Tie (fallback due to an error)."
      };
    } finally {
      loading = false;
    }
  }

  function backToStart() {
    // adjust if your landing route differs
    goto(`/`);
  }
</script>

<style>
  html, body { margin: 0; padding: 0; overflow: auto; font-family: 'Segoe UI', sans-serif; }

  .container {
    position: relative; width: 100%; max-width: 100vw; height: 100vh;
    background: #9CCF00; display: flex; flex-direction: column;
    align-items: center; justify-content: center; overflow: hidden; min-height: 100dvh;
  }

  .shape { position: absolute; z-index: 0; opacity: 0.15; }
  .circle  { width: 400px; height: 400px; background: white; border-radius: 50%;
             top: -100px; left: -100px; animation: float1 9.2s ease-in-out infinite; }
  .triangle{ width: 0; height: 0; border-left: 300px solid transparent; border-right: 300px solid transparent;
             border-bottom: 400px solid white; top: 100px; right: -100px; animation: float2 11.4s ease-in-out infinite; }
  .square  { width: 250px; height: 250px; background: white; bottom: -100px; left: 100px;
             transform: rotate(10deg); animation: float3 7.3s ease-in-out infinite; }

  .content { z-index: 1; width: 100%; max-width: 900px; padding: 2rem 1rem; margin-top: 24px;
             display: flex; flex-direction: column; align-items: center; text-align: center; }

  .title { font-weight: 800; font-size: clamp(28px, 4vw, 48px); margin: 1.5rem 0 0.25rem; letter-spacing: .5px; color: #333333;}
  .subtitle { font-size: clamp(14px, 2vw, 18px); opacity: .95; color: #333333;}

  .countdown {
    margin: 2.5rem 0 1rem; font-weight: 900; font-size: clamp(60px, 10vw, 120px);
    animation: pulse 1s ease-in-out infinite; color: #333333;
  }
  @keyframes pulse {
    0% { transform: scale(1); opacity: 0.9; }
    50% { transform: scale(1.1); opacity: 1; }
    100% { transform: scale(1); opacity: 0.9; }
  }

  .barWrap { width: 100%; max-width: 720px; margin-top: 2rem; background: rgba(255,255,255,.55);
             border-radius: 16px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,.15); }
  .bar { display: flex; height: 42px; }
  .p1 { background: #ffffff; }
  .p2 { background: #333333; color: #333333; }

  .labels { display: flex; justify-content: space-between; max-width: 720px; width: 100%; margin-top: 8px;
            font-weight: 700; color: #333333;}
  .label { display: flex; gap: 6px; align-items: center; }
  .dot { width: 10px; height: 10px; border-radius: 50%; display: inline-block; }
  .dot.p1 { background: #ffffff; border: 1px solid #000; }
  .dot.p2 { background: #333333;}

  .verdict { margin-top: 1.25rem; font-size: clamp(14px, 2vw, 18px); max-width: 720px; color: #333333;}

  /* OPTION A: absolutely position the actions wrapper and center it */
  .actions {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 10%;
    display: flex;
    justify-content: center;
    z-index: 2;
  }

  .btn {
    padding: .8rem 1.2rem; border-radius: 10px; border: 2px dashed #000; background: #fff; color: #333333;
    font-weight: 800; cursor: pointer;
    /* no absolute positioning here */
  }
  .btn:hover { border-style: solid; }

  @keyframes float1 { 0%{transform:translate(0,0) rotate(0)} 50%{transform:translate(25px,-20px) rotate(6deg)} 100%{transform:translate(0,0) rotate(0)} }
  @keyframes float2 { 0%{transform:translate(0,0) rotate(0)} 50%{transform:translate(-30px,25px) rotate(-5deg)} 100%{transform:translate(0,0) rotate(0)} }
  @keyframes float3 { 0%{transform:translate(0,0) rotate(0)} 50%{transform:translate(18px,-18px) rotate(4deg)} 100%{transform:translate(0,0) rotate(0)} }
</style>


<div class="container">
  <!-- floating background -->
  <div class="shape circle"></div>
  <div class="shape triangle"></div>
  <div class="shape square"></div>

  <TopBar gameCode={gameCode} />

  <div class="content">
    <div class="title">Final Evaluation</div>
    {#if loading}
      <div class="subtitle">Determining the winner…</div>
      <div class="countdown">{countdown}</div>
    {:else}
      <!-- Split bar -->
      <div class="barWrap">
        <div class="bar">
          <div class="p1" style="width: {result.p1Score}%"></div>
          <div class="p2" style="width: {result.p2Score}%"></div>
        </div>
      </div>

      <div class="labels">
        <div class="label"><span class="dot p1"></span>{result.p1 || p1Name}: {result.p1Score}</div>
        <div class="label"><span class="dot p2"></span>{result.p2 || p2Name}: {result.p2Score}</div>
      </div>

      <div class="verdict"><strong>Verdict:</strong> {result.verdict}</div>

      <div class="actions">
        <button class="btn" on:click={backToStart}>Back to landing</button>
      </div>
    {/if}
  </div>
</div>
