<script>
let code = '';
let name = '';
let stage = 'select';
let action = '';
let destination = "http://localhost:8080"
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { goto } from '$app/navigation';

const handleSelect = (type) => {
  if (type === 'join') {
		sessionStorage.setItem('isCreator', 'false');
    if (code.length !== 6) {
      alert('Please enter a 6-digit code');
      return;
    }
		
  }

  if (type === 'create') {
		sessionStorage.setItem('isCreator', 'true');
    // maybe later: check if a name was entered early, show invite code, etc.
  }

  action = type;
  stage = 'name';
};

const handleSubmitName = async () => {
  if (!name.trim()) {
    alert('Please enter your name');
    return;
  }

	sessionStorage.setItem("playerName", name);

  if (action === 'join') {
    const res = await fetch(`${destination}/api/game/${code}/join`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });

    if (res.ok) {
      // ✅ Create a STOMP client just to send the event
      const stompClient = new Client({
        webSocketFactory: () => new SockJS(`${destination}/ws`),
        onConnect: () => {
          // ✅ Send to server-side handler @MessageMapping("/joined")
          stompClient.publish({
            destination: '/app/joined',
            body: JSON.stringify({
              type: 'PLAYER_JOINED',
              playerName: name,
              gameCode: code
            })
          });
          stompClient.deactivate(); // optional: disconnect after sending
        }
      });

      stompClient.activate();
      goto(`/game/${code}/topics`);
    } else {
      alert('Join failed');
    }
  }

  if (action === 'create') {
    const res = await fetch(`${destination}/api/game/create`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });
    const data = await res.json();
    if (data.code) {
      goto(`/game/${data.code}/waiting`);
    } else {
      alert('Game creation failed');
    }
  }
};
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
    overflow-x: hidden;
  }

  .container {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
		height: 100vh;
		height: 100dvh;
    background: #9CCF00;
    font-family: 'Segoe UI', sans-serif;
    width: 100%;
    max-width: 100vw;
    overflow: hidden;
  }

  /* Background shapes */
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
		pointer-events: none;
		animation: float2 9s ease-in-out infinite;
	}


  .box {
    position: relative;
    z-index: 1;
    background: white;
    padding: 2rem;
    border-radius: 12px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.25);
    color: black;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
  }

  h1 {
    font-size: 2.5rem;
    margin-bottom: 2rem;
    color: white;
    z-index: 1;
  }

  .entry-wrapper {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1rem;
    width: 100%;
  }

  .input {
		font-family: 'Rubik', sans-serif;
    font-size: 1.2rem;
    padding: 0.75rem;
    border: 2px solid #ccc;
    border-radius: 6px;
    text-align: center;
    width: 200px;
		transition: transform .2s;
  }

  .button {
    font-size: 1.1rem;
		font-family: 'Rubik', sans-serif;
    padding: 0.75rem 2rem;
    border: none;
    border-radius: 8px;
    background-color: #000;
    color: white;
    cursor: pointer;
    width: 200px;
		transition: transform .2s;
  }

	.button:hover, .input:hover { 
		transform: scale(1.05);
	}

  .or {
    font-weight: bold;
    color: #aaa;
		font-family: 'Rubik', sans-serif;
    text-align: center;
  }

	.content {
		display: flex;
		flex-direction: column;
		align-items: center;
		justify-content: center;
		z-index: 1;
		position: relative;
		height: 100%;
		margin-top: -2rem;
	}

	.logo {
		width: 40vw;
		max-width: 250px;
		min-width: 120px;
		margin-bottom: -1.5rem;
		margin-top: -1rem;
		z-index: 1;
	}

	.logo-shape {
		position: absolute;
		width: 300px;
		height: 300px;
		background: rgba(255, 255, 255, 1); /* boosted visibility */
		top: 20%;
		left: 30%;
		transform: translate(-50%, -50%) rotate(20deg);
		z-index: 0;
		border-radius: 24px;
		animation: float3 10s ease-in-out infinite;
		pointer-events: none;
	}


  /* Responsive Layout */
  @media (min-width: 700px) {
    .entry-wrapper {
      flex-direction: row;
      align-items: center;
      justify-content: center;
    }

    .or {
      margin: 0 1rem;
    }

  }

</style>

<div class="container">
  <!-- Background shapes -->
  <div class="shape circle"></div>
  <div class="shape triangle"></div>
  <div class="shape square"></div>
  <div class="shape center-shape"></div>

  <div class="content">
    <div class="shape logo-shape"></div>
    <img src="/images/logo.png" alt="Let's Debate!" class="logo" />

    <div class="box">
      {#if stage === 'select'}
        <div class="entry-wrapper">
          <div style="display: flex; flex-direction: column; align-items: center; gap: 0.5rem;">
            <input
              class="input"
              placeholder="Enter Game Code"
              bind:value={code}
              maxlength="6"
            />
            <button class="button" on:click={() => handleSelect('join')}>Enter</button>
          </div>

          <div class="or">OR</div>

          <button class="button" on:click={() => handleSelect('create')}>Create Game</button>
        </div>
      {:else if stage === 'name'}
        <div class="entry-wrapper" style="flex-direction: column; align-items: center; gap: 1rem;">
          <div class="or">Enter your name</div>
          <input
            class="input"
            placeholder="e.g. Alex"
            bind:value={name}
          />
          <button class="button" on:click={handleSubmitName}>
            {action === 'join' ? 'Join Game' : 'Start Game'}
          </button>
        </div>
      {/if}
    </div>
  </div>
</div>
