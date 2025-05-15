<script>
  import { onMount } from 'svelte';
  import { page }      from '$app/stores';
  import { goto }      from '$app/navigation';
  import SockJS        from 'sockjs-client';
  import { Stomp }     from '@stomp/stompjs';
  import { scale }     from 'svelte/transition';
  import { cubicOut }  from 'svelte/easing';

  let client;
  const gameCode      = $page.params.code;
  const playerName    = sessionStorage.getItem('playerName');

  let topics          = [];
  let currentIndex    = 0;
  let waiting         = false;
  let showNoDebate    = false;

  onMount(() => {
    client = Stomp.over(() => new SockJS('http://localhost:8080/ws'));
    client.connect({}, () => {
      console.log('[WS] connected');

      client.subscribe(`/topic/${gameCode}`, ({ body }) => {
        const { type, topics: t } = JSON.parse(body);
        if (type === 'TOPIC_SYNC') {
          topics       = t;
          currentIndex = 0;
          waiting      = false;
        }
      });

      client.subscribe(`/topic/${gameCode}/topic-complete`, ({ body }) => {
        const { topicIndex, disagree } = JSON.parse(body);
        console.log('[TOPIC-COMPLETE]', topicIndex, disagree);

        if (topicIndex !== currentIndex) return;

        waiting = false;

        if (disagree) {
          return goto(`/game/${gameCode}/chat`);
        }

        if (currentIndex + 1 < topics.length) {
          currentIndex += 1;
        } else {
          showNoDebate = true;
        }
      });

      client.send(
        '/app/start-topics',
        {},
        JSON.stringify({ playerName, gameCode })
      );
    });

    return () => {
      client?.deactivate();
    };
  });

function sendDecision(vote) {
  if (!client?.connected) return;

  waiting = true;

  sessionStorage.setItem("currentTopic", topics[currentIndex]);
  sessionStorage.setItem("myStance", vote);

  const isCreator = sessionStorage.getItem("isCreator") === "true";

  let p1Stance, p2Stance;

  if (isCreator) {
    p1Stance = vote;
    p2Stance = vote === "agree" ? "disagree" : "agree";
    sessionStorage.setItem("p1", playerName);
  } else {
    p2Stance = vote;
    p1Stance = vote === "agree" ? "disagree" : "agree";
    sessionStorage.setItem("p2", playerName);
  }

  sessionStorage.setItem("p1Stance", p1Stance);
  sessionStorage.setItem("p2Stance", p2Stance);

  client.send(
    '/app/vote',
    {},
    JSON.stringify({
      gameCode,
      playerName,
      topicIndex: currentIndex,
      vote
    })
  );
}
</script>


<svelte:head>
  <link
    href="https://fonts.googleapis.com/css2?family=Rubik&display=swap"
    rel="stylesheet"
  />
</svelte:head>

<style>
  :global(html, body) {
    margin: 0;
    padding: 0;
    height: 100dvh;
    overflow: hidden;
    font-family: 'Rubik', sans-serif;
  }
  .background {
    height: 100dvh;
    width: 100vw;
    background: linear-gradient(135deg, #b7e32d, #9ccf00);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    position: relative;
  }
  .shape {
    position: absolute;
    opacity: 0.1;
    pointer-events: none;
  }

  .card {
    background: white;
    padding: 2rem 1.5rem;
    border-radius: 2rem;
    width: 85vw;
    max-width: 420px;
    min-height: 250px;
    box-shadow: 0 8px 30px rgba(0,0,0,0.2);
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    text-align: center;
    transition: transform .2s, opacity .2s;
  }
  .topic-text {
    font-size: 1.4rem;
    font-weight: 600;
    color: #222;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .buttons {
    display: flex;
    gap: 1rem;
    margin-top: 1rem;
  }
  .btn {
    flex: 1;
    padding: 1rem;
    border: none;
    border-radius: 1rem;
    font-weight: bold;
    cursor: pointer;
    transition: transform .2s;
  }
  .btn:hover { transform: scale(1.05); }
  .agree { background: #4caf50; color: white; }
  .disagree { background: #f44336; color: white; }
  .waiting {
    color: #555;
    margin-top: 1rem;
    font-size: 1rem;
  }

  /* no-debate pulsate */
  .no-debate {
    font-size: 1.6rem;
    font-weight: bold;
    color: #333;
    animation: loudPulse 1.5s ease-out;
  }
  .back {
    margin-top: 1.5rem;
    background: #000;
    color: #fff;
  }
  @keyframes loudPulse {
    0%   { transform: scale(1); }
    30%  { transform: scale(1.3); }
    60%  { transform: scale(0.95); }
    80%  { transform: scale(1.1); }
    100% { transform: scale(1); }
  }
		/* ─── floating‐shape animations ───────────────────────────────────────────── */
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

	/* ─── shape base styling ──────────────────────────────────────────────────── */
	.shape {
		position: absolute;
		opacity: 0.1;
		pointer-events: none;
	}

	/* ─ circle ─────────────────────────────────────────────────────────────────── */
	.circle {
		width: 200px;
		height: 200px;
		background: white;
		border-radius: 50%;
		top: 10%;
		left: 5%;
		animation: float1 8s ease-in-out infinite;
	}

	/* ─ triangle ───────────────────────────────────────────────────────────────── */
	.triangle {
		width: 0;
		height: 0;
		border-left: 120px solid transparent;
		border-right: 120px solid transparent;
		border-bottom: 200px solid white;
		bottom: 5%;
		right: 10%;
		animation: float2 10s ease-in-out infinite;
	}

	/* ─ square ─────────────────────────────────────────────────────────────────── */
	.square {
		width: 100px;
		height: 100px;
		background: white;
		bottom: 20%;
		left: 30%;
		animation: float3 7s ease-in-out infinite;
	}

</style>

<div class="background">
  <div class="shape circle"></div>
  <div class="shape triangle"></div>
  <div class="shape square"></div>

  {#if showNoDebate}
    <!-- everybody agreed on all topics -->
    <div class="card">
      <div in:scale={{ duration: 500, easing: cubicOut }} class="no-debate">
        Wow… I guess there’s nothing for you two to debate about.
      </div>
      <button class="btn back" on:click={() => goto('/')}>
        ← Back to Main Menu
      </button>
    </div>
  {:else}
    <!-- still have topics, or waiting for opponent -->
    <div class="card">
      <div class="topic-text">{topics[currentIndex]}</div>

      {#if waiting}
        <div class="waiting">Waiting for the other player…</div>
      {:else}
        <div class="buttons">
          <button class="btn disagree" on:click={() => sendDecision('disagree')}>
            ← Disagree
          </button>
          <button class="btn agree" on:click={() => sendDecision('agree')}>
            Agree →
          </button>
        </div>
      {/if}
    </div>
  {/if}
</div>
