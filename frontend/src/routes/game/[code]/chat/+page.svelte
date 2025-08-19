<script>
  import { onMount } from 'svelte';
  import { page } from '$app/stores';
  import { Send } from 'lucide-svelte';
  import TopBar from '$lib/components/TopBar.svelte';
  import SockJS from 'sockjs-client';
  import { Client } from '@stomp/stompjs';
  import { writable } from 'svelte/store';
  import { goto } from '$app/navigation'

  let client;
  let message = '';
  let refCallsUsed = writable(0);
  let isMobile = false;
  let chatWindow;
  let messages = [];
  let destination = "http://localhost:8080"

  $: if (messages.length && chatWindow) {
    chatWindow.scrollTo({
      top: chatWindow.scrollHeight,
      behavior: 'smooth'
    });
  }

  const gameCode   = $page.params.code;
  const playerName = sessionStorage.getItem('playerName');
  const topic      = sessionStorage.getItem("currentTopic");
  const p1Name     = sessionStorage.getItem("p1");
  const p2Name     = sessionStorage.getItem("p2");
  const p1Stance   = sessionStorage.getItem("p1Stance");
  const p2Stance   = sessionStorage.getItem("p2Stance");
  


  onMount(() => {
    const ua = navigator.userAgent;
    isMobile = /Android|iPhone|iPad|iPod|Opera Mini|IEMobile|WPDesktop/i.test(ua);
    client = new Client({
      webSocketFactory: () => new SockJS(`${destination}/ws`),
      debug: str => console.log('[STOMP]', str),
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('[WS] Connected to chat');

        client.subscribe(`/topic/${gameCode}/chat`, (msg) => {
          const data = JSON.parse(msg.body);
          messages = [...messages, data];

          checkFinaleRedirect().catch(console.error);
        });
      }
    });

    client.activate();
    fetch(`${destination}/api/chat/${gameCode}/refcalls?playerName=${playerName}`)
      .then(res => res.json())
      .then(data => {
        refCallsUsed = data.used || 0;
      });
  });

  const handleSend = async () => {
    if (!message.trim() || !client || !client.connected) return;

    const payload = {
      gameCode,
      sender: playerName,
      text: message.trim()
    };

    client.publish({
      destination: '/app/chat',
      body: JSON.stringify(payload)
    });

    message = '';
  };

  const checkFinaleRedirect = async () => {
    const [p1Res, p2Res] = await Promise.all([
      fetch(`${destination}/api/chat/${gameCode}/refcalls?playerName=${p1Name}`).then(r => r.json()),
      fetch(`${destination}/api/chat/${gameCode}/refcalls?playerName=${p2Name}`).then(r => r.json())
    ]);

    if (p1Res.used >= 2 && p2Res.used >= 2) {
      messages = [
        ...messages,
        { sender: "System", text: "Both players have used up their referee calls. Redirecting shortly..." }
      ];
      await new Promise(r => setTimeout(r, 5000));
      goto(`/game/${gameCode}/finale`);
    }
  };


  const handleRefereeCall = async () => {
    if (refCallsUsed >= 2) return;
    refCallsUsed += 1; 
    if (!topic || !p1Stance || !p2Stance) {
      alert("Missing context. Please go back and vote again.");
      return;
    }

    messages = [...messages, {
      sender: 'Referee',
      text: '🔍 Evaluating your point...'
    }];

    try {
      const res = await fetch(`${destination}/api/chat/${gameCode}/callref?` + new URLSearchParams({
        topic,
        p1Stance,
        p2Stance,
        playerName
      }));

      console.log('[AI CALL]', res.status);
    } catch (err) {
      console.error('[ERROR] Calling AI referee', err);
    }

    await checkFinaleRedirect();
  };
  


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
    justify-content: space-between;
    overflow: hidden;
    min-height: 100dvh;
  }

  .chat-window {
    flex: 1;
    width: 100%;
    max-width: 600px;
    padding: 1rem;
    overflow-y: auto;
    z-index: 1;
  }

  .message {
    background: white;
    border-radius: 12px;
    padding: 0.75rem 1rem;
    margin-bottom: 0.5rem;
    max-width: 80%;
    word-wrap: break-word;
    box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  }

  .you {
    align-self: flex-end;
    background: #fff;
  }

  .other {
    align-self: flex-start;
    background: #eee;
  }

  .chat-box {
    display: flex;
    width: 100%;
    max-width: 600px;
    padding: 1rem;
    gap: 0.5rem;
    z-index: 2;
  }

  .chat-input {
    flex: 1;
    padding: 0.75rem;
    font-size: 1rem;
    border-radius: 30px;
    border: 2px solid #ccc;
    font-family: 'Rubik', sans-serif;
  }

  .chat-button {
    padding: 0.75rem 1rem;
    background: #333333;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-family: 'Rubik', sans-serif;
    font-weight: bold;
  }

  .ref-button {
    margin: 1rem;
    padding: 0.75rem 1.5rem;
    background: #fff;
    border-radius: 10px;
    border: 2px dashed #000;
    font-weight: bold;
    cursor: pointer;
    font-family: 'Rubik', sans-serif;
    z-index: 2;
    transition: border 0.2s ease-in-out;
  }

  .ref-button:hover {
    border: 2px solid #000;
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

  .send-button {
    background-color: #333333;
    border: none;
    border-radius: 50%;
    width: 51px;
    height: 51px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: transform 0.2s ease-in-out;
  }

  .send-button:hover {
    transform: scale(1.1);
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
  <div class="shape circle"></div>
  <div class="shape triangle"></div>
  <div class="shape square"></div>

  <TopBar gameCode={gameCode} />

  <div class="chat-window" bind:this={chatWindow}>
    {#each messages as msg (msg.text + msg.sender + Math.random())}
      <div class="message {msg.sender === playerName ? 'you' : 'other'}">
        <strong>{msg.sender}:</strong> {msg.text}
      </div>
    {/each}
  </div>

  <button
    class="ref-button"
    on:click={handleRefereeCall}
    disabled={refCallsUsed >= 2}
    style="opacity: {refCallsUsed >= 2 ? 0.4 : 1}; cursor: {refCallsUsed >= 2 ? 'not-allowed' : 'pointer'};">
    Call AI Referee ({refCallsUsed}/2)
  </button>

  <div class="chat-box">
    <input
      class="chat-input"
      placeholder="Type a message..."
      bind:value={message}
      on:keydown={(e) => e.key === 'Enter' && handleSend()}
    />
    <button class="send-button" on:click={handleSend}>
      <Send size={20} color="white" />
    </button>
  </div>
</div>